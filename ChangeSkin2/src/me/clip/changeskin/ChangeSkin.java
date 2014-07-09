/* This file is a class of ChangeSkin
 * @author extended_clip
 * 
 * 
 * ChangeSkin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * 
 * ChangeSkin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package me.clip.changeskin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import me.clip.changeskin.commands.Commands;
import me.clip.changeskin.config.ConfigFile;
import me.clip.changeskin.factory.SkinFactory;
import me.clip.changeskin.listeners.JoinListener;
import me.clip.changeskin.metrics.MetricsLite;
import me.clip.changeskin.updater.Updater;
import me.clip.changeskin.updater.Updater.ReleaseType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ChangeSkin extends JavaPlugin {

	private SkinFactory factory;
	private Commands commands;
	private ConfigFile config;

	// options
	public static boolean checkUpdates;
	public static boolean forceSkin;
	public static boolean filter;
	public static boolean isWhitelist;
	public static List<String> skins;
	public static List<String> displayNames;
	public static HashMap<String, String> forcedSkins = new HashMap<String, String>();
	public static boolean tellChange;

	// updater
	public static boolean update = false;
	public static String uName = "";
	public static ReleaseType uType = null;
	public static String uVersion = "";
	public static String uLink = "";

	@Override
	public void onEnable() {
		if (hookProtocolLib()) {
			getLogger().info("Hooked into ProtocolLib!");
			initialize();
		} else {
			getLogger().info(
					"Could not hook into ProtocolLib! Disabling ForceSkin!");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {

	}

	private void initialize() {
		config = new ConfigFile(this);
		factory = new SkinFactory(this);
		commands = new Commands(this);
		loadConfig();
		getCommand("skin").setExecutor(commands);
		registerEvents();
		checkUpdates();
		startMetricsLite();
	}

	private void checkUpdates() {
		// if the server has it disabled, don't check at all
		if (checkUpdates) {
			Updater updater = new Updater(this, 81957, this.getFile(),
					Updater.UpdateType.NO_DOWNLOAD, false);

			update = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
			uName = updater.getLatestName();
			uVersion = updater.getLatestGameVersion();
			uType = updater.getLatestType();
			uLink = updater.getLatestFileLink();

			if (update == true) {
				getLogger()
						.info("An update is available: " + uName + ", a "
								+ uType + " for " + uVersion + " available at "
								+ uLink);
			} else {
				getLogger().info(
						"You are running " + this.getDescription().getVersion()
								+ ", the latest version of ChangeSkin!");
			}

		}
	}

	private void startMetricsLite() {
		try {
			MetricsLite ml = new MetricsLite(this);
			ml.start();
		} catch (IOException e) {
		}
	}

	public void loadForcedSkins() {
		List<String> before = config.forcedSkins();
		for (String s : before) {
			if (!s.contains(",")) {
				continue;
			}
			String[] list = s.split(",");
			String skin = list[0].trim();
			String perm = "skin.force." + list[1].trim();
			forcedSkins.put(perm, skin);
		}

	}

	private void loadConfig() {
		config.loadConfiguration();
		filter = config.useList();
		isWhitelist = config.isWhitelist();
		skins = config.getSkins();
		displayNames = config.getPlayerNames();
		forceSkin = config.forceSkin();
		tellChange = config.tellForcedChange();
		checkUpdates = config.update();
		loadForcedSkins();
	}

	private void registerEvents() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new JoinListener(this), this);
	}

	private boolean hookProtocolLib() {
		if (Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
			return false;
		}
		return true;
	}

	public ConfigFile getConfigFile() {
		return this.config;
	}

	public SkinFactory getFactory() {
		return this.factory;
	}

	public boolean isWhitelist() {
		return isWhitelist;
	}

	public List<String> getSkins() {
		return skins;
	}

	public List<String> getDisplayNames() {
		return displayNames;
	}

	public void setForcedSkin(final Player p) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				boolean changed = false;
				for (String perm : ChangeSkin.forcedSkins.keySet()) {
					if (p.hasPermission(perm)) {
						changed = true;
						Commands.disguised.put(
								p.getName(),
								ChangeSkin.forcedSkins.get(perm) + ";"
										+ p.getName());
						getFactory().changeDisplay(p.getName(),
								ChangeSkin.forcedSkins.get(perm));

					}
				}
				if (tellChange && changed == true) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&aYour skin has been changed!"));
				}
			}
		}, 20L);
	}

}
