/*
 * This class handles changing a players skin/displayname
 * Resource can be found at:
 * http://forums.bukkit.org/threads/class-change-the-skin-of-a-player-without-changing-the-name-yes-its-possible-no-spout.273251/
 * 
 * @author Comphenix
 *
 */
package me.clip.changeskin.factory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
 
import me.clip.changeskin.ChangeSkin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
 
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
 

public class SkinFactory {
	
	private ChangeSkin plugin; 
    private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final int WORKER_THREADS = 4;
 
    private ProtocolManager protocolManager;
    private ConcurrentMap<String, String> skinNames = Maps.newConcurrentMap();
    private ConcurrentMap<String, String> displayNames = Maps.newConcurrentMap();
 
    private Cache<String, String> profileCache = CacheBuilder.newBuilder().
        maximumSize(500).
        expireAfterWrite(4, TimeUnit.HOURS).
        build(new CacheLoader<String, String>() {
            public String load(String name) throws Exception {
                return getProfileJson(name);
            };
        });
 
    public SkinFactory(ChangeSkin instance) {
    	plugin = instance;
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.getAsynchronousManager().registerAsyncHandler(
          new PacketAdapter(instance, ListenerPriority.NORMAL,

                PacketType.Play.Server.NAMED_ENTITY_SPAWN,
             
                PacketType.Play.Server.ENTITY_EFFECT,
                PacketType.Play.Server.ENTITY_EQUIPMENT,
                PacketType.Play.Server.ENTITY_METADATA,
                PacketType.Play.Server.UPDATE_ATTRIBUTES,
                PacketType.Play.Server.ATTACH_ENTITY,
                PacketType.Play.Server.BED) {
           
            @Override
            public void onPacketSending(PacketEvent event) {

                if (event.getPacketType() != PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
                    return;
                }
             
                Player toDisplay = (Player) event.getPacket().getEntityModifier(event).read(0);
                String skinName = skinNames.get(toDisplay.getName());
                String displayName = displayNames.get(toDisplay.getName());
             
                if (skinName == null && displayName == null) {
                    return;
                }
                StructureModifier<WrappedGameProfile> profiles = event.getPacket().getGameProfiles();
                WrappedGameProfile original = profiles.read(0);
                WrappedGameProfile result = new WrappedGameProfile(
                    extractUUID(original.getName()),
                    displayName != null ? displayName : original.getName()
                );
             
                updateSkin(result, skinName != null ? skinName : result.getName());
                profiles.write(0, result);
            }
        }).start(WORKER_THREADS);
    }
 
    @SuppressWarnings("deprecation")
    private UUID extractUUID(final String playerName) {
        return Bukkit.getOfflinePlayer(playerName).getUniqueId();
    }
 
    private String getProfileJson(String name) throws IOException {
        final URL url = new URL(PROFILE_URL + extractUUID(name).toString().replace("-", ""));
        final URLConnection uc = url.openConnection();
 
        return CharStreams.toString(new InputSupplier<InputStreamReader>() {
            @Override
            public InputStreamReader getInput() throws IOException {
                return new InputStreamReader(uc.getInputStream(), Charsets.UTF_8);
            }
        });
    }
 
    private void updateSkin(WrappedGameProfile profile, String skinOwner) {
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(profileCache.get(skinOwner));
            JSONArray properties = (JSONArray) json.get("properties");
         
            for (int i = 0; i < properties.size(); i++) {
                JSONObject property = (JSONObject) properties.get(i);
                String name = (String) property.get("name");
                String value = (String) property.get("value");
                String signature = (String) property.get("signature");
             
                profile.getProperties().put(name, new WrappedSignedProperty(name, value, signature));
                }
        } catch (Exception e) {
            plugin.getLogger().info("Cannot fetch profile for " + skinOwner);
        }
    }
 
    public void changeDisplay(String string, String toSkin) {
        changeDisplay(string, toSkin, null);
    }
 
    public void changeDisplay(Player player, String toSkin, String toName) {
        if (updateMap(skinNames, player.getName(), toSkin) |
            updateMap(displayNames, player.getName(), toName)) {
            refreshPlayer(player);
        }
    }
 
    public void changeDisplay(String playerName, String toSkin, String toName) {
        if (updateMap(skinNames, playerName, toSkin) |
            updateMap(displayNames, playerName, toName)) {
            refreshPlayer(playerName);
        }
    }
 
    public void removeChanges(Player player) {
        changeDisplay(player.getName(), null, null);
    }
 
    public void removeChanges(String playerName) {
        changeDisplay(playerName, null, null);
    }
 
    private <T, U> boolean updateMap(Map<T, U> map, T key, U value) {
        if (value == null) {
            return map.remove(key) != null;
        } else {
            return !Objects.equal(value, map.put(key, value));
        }
    }
 
    @SuppressWarnings("deprecation")
    private void refreshPlayer(String name) {
        Player player = Bukkit.getPlayer(name);
     
        if (player != null) {
            refreshPlayer(player);
        }
    }
 
    private void refreshPlayer(Player player) {
        protocolManager.updateEntity(player, protocolManager.getEntityTrackers(player));
    }
}