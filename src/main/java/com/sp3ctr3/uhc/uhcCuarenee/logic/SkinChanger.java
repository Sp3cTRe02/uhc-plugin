package com.sp3ctr3.uhc.uhcCuarenee.logic;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class SkinChanger {

    private static final Random random = new Random();
    private static final Map<Player, Set<ProfileProperty>> originalSkins = new HashMap<>();
    private static final Map<UUID, UUID> playerSkinAssignments = new HashMap<>();
    private static final Map<UUID, Set<ProfileProperty>> preChangeSkins = new HashMap<>();
    private static ProtocolManager protocolManager;

    public static void initialize() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public static void assignRandomSkinsToAll() {
        try {
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            
            if (players.size() < 2) {
                return;
            }

            playerSkinAssignments.clear();
            

            preChangeSkins.clear();
            for (Player p : players) {
                PlayerProfile prof = p.getPlayerProfile();
                preChangeSkins.put(p.getUniqueId(), new HashSet<>(prof.getProperties()));
            }
            
            List<UUID> availableSkinUUIDs = new ArrayList<>();
            for (Player player : players) {
                availableSkinUUIDs.add(player.getUniqueId());
            }
            
            Collections.shuffle(availableSkinUUIDs, random);
            
            Set<UUID> assignedSkins = new HashSet<>();
            
            for (Player currentPlayer : players) {
                UUID playerUUID = currentPlayer.getUniqueId();
                UUID assignedSkinUUID = null;
                
                for (UUID skinUUID : availableSkinUUIDs) {
                    if (!skinUUID.equals(playerUUID) && !assignedSkins.contains(skinUUID)) {
                        assignedSkinUUID = skinUUID;
                        assignedSkins.add(skinUUID);
                        break;
                    }
                }
                
                if (assignedSkinUUID == null) {
                    for (UUID skinUUID : availableSkinUUIDs) {
                        if (!skinUUID.equals(playerUUID)) {
                            assignedSkinUUID = skinUUID;
                            assignedSkins.add(skinUUID);
                            break;
                        }
                    }
                }
                
                if (assignedSkinUUID != null) {
                    playerSkinAssignments.put(playerUUID, assignedSkinUUID);
                }
            }
            
            Map<UUID, Integer> skinUsageCount = new HashMap<>();
            for (UUID skinUUID : playerSkinAssignments.values()) {
                skinUsageCount.put(skinUUID, skinUsageCount.getOrDefault(skinUUID, 0) + 1);
            }
            
            boolean hasDuplicates = false;
            for (Map.Entry<UUID, Integer> entry : skinUsageCount.entrySet()) {
                Player p = Bukkit.getPlayer(entry.getKey());
                if (entry.getValue() > 1) {
                    hasDuplicates = true;
                } else {
                }
            }
            
            if (hasDuplicates) {
                return;
            }
            
            for (Player player : players) {
                applySkinFromAssignment(player);
            }

            preChangeSkins.clear();
        } catch (Exception e) {
        }
    }
    
    private static void applySkinFromAssignment(Player target) {
        try {
            UUID assignedUUID = playerSkinAssignments.get(target.getUniqueId());
            
            
            if (assignedUUID == null) {
                return;
            }
            
            Player skinSource = Bukkit.getPlayer(assignedUUID);
            if (skinSource == null) {
                return;
            }

            PlayerProfile targetProfile = target.getPlayerProfile();
            
            if (!originalSkins.containsKey(target)) {
                Set<ProfileProperty> originalProps = new HashSet<>(targetProfile.getProperties());
                originalSkins.put(target, originalProps);
            }

            Set<ProfileProperty> sourceProps = preChangeSkins.get(assignedUUID);
            if (sourceProps == null || sourceProps.isEmpty()) {
                PlayerProfile sourceProfile = skinSource.getPlayerProfile();
                sourceProps = new HashSet<>(sourceProfile.getProperties());
            }
            
            targetProfile.clearProperties();
            sourceProps.forEach(targetProfile::setProperty);
            target.setPlayerProfile(targetProfile);

            double currentHealth = target.getHealth();
            target.damage(0.1);
            Bukkit.getScheduler().runTaskLater(
                Bukkit.getPluginManager().getPlugin("uhc-cuarenee"),
                () -> target.setHealth(currentHealth),
                2L
            );

            refreshPlayer(target);
        } catch (Exception e) {
        }
    }

    @Deprecated
    public static void changeTabSkinForAll(Player target) {
        assignRandomSkinsToAll();
    }

    private static void refreshPlayer(Player player) {
        if (protocolManager == null) {
            return;
        }

        try {
            PacketContainer removePlayer = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
            removePlayer.getUUIDLists().write(0, Collections.singletonList(player.getUniqueId()));

            PacketContainer addPlayer = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
            addPlayer.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
            
            PlayerProfile currentProfile = player.getPlayerProfile();
            WrappedGameProfile wrappedProfile = new WrappedGameProfile(player.getUniqueId(), player.getName());
            
            
            for (ProfileProperty prop : currentProfile.getProperties()) {
                wrappedProfile.getProperties().put(prop.getName(), 
                    new WrappedSignedProperty(prop.getName(), prop.getValue(), prop.getSignature()));
            }
            PlayerInfoData playerInfoData = new PlayerInfoData(
                wrappedProfile,
                player.getPing(),
                EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()),
                WrappedChatComponent.fromText(player.getDisplayName())
            );
            addPlayer.getPlayerInfoDataLists().write(1, Collections.singletonList(playerInfoData));

            PacketContainer destroyEntity = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            destroyEntity.getIntLists().write(0, Collections.singletonList(player.getEntityId()));

            for (Player viewer : Bukkit.getOnlinePlayers()) {
                if (!viewer.equals(player)) {
                    try {
                        protocolManager.sendServerPacket(viewer, removePlayer);
                        
                        protocolManager.sendServerPacket(viewer, destroyEntity);
                        
                        Bukkit.getScheduler().runTaskLater(
                            Bukkit.getPluginManager().getPlugin("uhc-cuarenee"),
                            () -> {
                                try {
                                    protocolManager.sendServerPacket(viewer, addPlayer);
                                    
                                    viewer.hidePlayer(Bukkit.getPluginManager().getPlugin("uhc-cuarenee"), player);
                                    viewer.showPlayer(Bukkit.getPluginManager().getPlugin("uhc-cuarenee"), player);
                                } catch (Exception e) {
                                }
                            },
                            1L
                        );
                    } catch (Exception e) {
                    }
                }
            }
            
        } catch (Exception e) {
        }
    }

    public static void changeTabSkin(Player target, Player viewer) {
    }

    public static void restoreOriginalSkin(Player target) {
        try {
            Set<ProfileProperty> originalProps = originalSkins.get(target);
            
            if (originalProps == null) {
                return;
            }

            PlayerProfile targetProfile = target.getPlayerProfile();
            
            targetProfile.clearProperties();
            
            originalProps.forEach(targetProfile::setProperty);
            
            target.setPlayerProfile(targetProfile);

            refreshPlayer(target);
            
            originalSkins.remove(target);
        } catch (Exception e) {
        }
    }

    public static void clearAll() {
        for (Player player : new ArrayList<>(originalSkins.keySet())) {
            restoreOriginalSkin(player);
        }
        originalSkins.clear();
        playerSkinAssignments.clear();
    }
}