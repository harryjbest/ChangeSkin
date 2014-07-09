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
package me.clip.changeskin.config;

import java.util.Arrays;
import java.util.List;

import me.clip.changeskin.ChangeSkin;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigFile {

	private ChangeSkin plugin;

	public ConfigFile(ChangeSkin instance) {
		this.plugin = instance;
	}

	public void loadConfiguration() {
		FileConfiguration config = plugin.getConfig();
		config.options().header(
				"ChangeSkin version: " + plugin.getDescription().getVersion()
						+ " Configuration file\nCreated by: extended_clip"
						+ "\nforced_skins syntax is <skinOwner>,<perm>"
						+ "\nPlayers must have skin.force.<perm> to have the skin forced on join");
		config.addDefault("check_updates", true);
		config.addDefault("force_skin_on_join", false);
		config.addDefault("forced_skins", Arrays.asList(new String[] { "extended_clip,clip", "Notch,notch",
		"Dinnerbone,guy" }));
		config.addDefault("tell_forced_change", true);
		config.addDefault("use_blacklist", true);
		config.addDefault("blacklist_as_whitelist", false);
		config.addDefault("blacklist_skins",
				Arrays.asList(new String[] { "playerName1", "playerName2",
						"playerName3" }));
		config.addDefault("blacklist_playernames",
			Arrays.asList(new String[] { "playerName1", "playerName2",
				"playerName3" }));
		config.options().copyDefaults(true);
		plugin.saveConfig();
	}
	
	public boolean tellForcedChange() {
		return this.plugin.getConfig().getBoolean("tell_forced_change");
	}
	
	public boolean update() {
		return this.plugin.getConfig().getBoolean("check_updates");
	}
	
	public boolean useList() {
		return this.plugin.getConfig().getBoolean("use_blacklist");
	}
	
	public boolean forceSkin() {
		return this.plugin.getConfig().getBoolean("force_skin_on_join");
	}
	
	public List<String> forcedSkins() {
		return this.plugin.getConfig().getStringList("forced_skins");
	}

	public boolean isWhitelist() {
		return this.plugin.getConfig().getBoolean("blacklist_as_whitelist");
	}
	
	public List<String> getSkins() {
		return this.plugin.getConfig().getStringList("blacklist_skins");
	}
	
	public List<String> getPlayerNames() {
		return this.plugin.getConfig().getStringList("blacklist_playernames");
	}

}
