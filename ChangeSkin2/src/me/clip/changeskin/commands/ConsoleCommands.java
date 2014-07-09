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
package me.clip.changeskin.commands;

import me.clip.changeskin.ChangeSkin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConsoleCommands implements CommandExecutor{
	
	private ChangeSkin plugin;
	
	public ConsoleCommands(ChangeSkin instance) {
		plugin = instance;
	}
	
	private void sms(CommandSender s, String msg) {
		s.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String label,
			String[] args) {

		if ((s instanceof Player)) {
			return true;
		}
		
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("help")) {
				sms(s, "&7Force&fSkin &aversion &f" + plugin.getDescription().getVersion());
				sms(s, "&7created by: &cextended_clip");
				sms(s, "&a/skin set <target> <skinOwner> (displayName)");
				sms(s, "&a/skin reset <target>");
				sms(s, "&a/skin list");
				sms(s, "&a/skin reload");
				return true;
			}
			else if (args[0].equalsIgnoreCase("reload")) {
				plugin.reloadConfig();
				plugin.saveConfig();
				ChangeSkin.filter = plugin.getConfigFile().useList();
				ChangeSkin.isWhitelist = plugin.getConfigFile().isWhitelist();
				ChangeSkin.skins = plugin.getConfigFile().getSkins();
				ChangeSkin.displayNames = plugin.getConfigFile().getPlayerNames();
				ChangeSkin.forceSkin = plugin.getConfigFile().forceSkin();
				ChangeSkin.tellChange = plugin.getConfigFile().tellForcedChange();
				ChangeSkin.checkUpdates = plugin.getConfigFile().update();
				plugin.loadForcedSkins();
				sms(s, "&aConfiguration successfully reloaded!");
				if (ChangeSkin.forceSkin) {
					for (Player on : Bukkit.getServer().getOnlinePlayers()) {
						plugin.setForcedSkin(on);
					}
					sms(s, "&aAll players have been refreshed to the forced skin if they had permission!");
				}
				
				return true;
			}
			else if (args[0].equalsIgnoreCase("set")) {
				if (args.length == 3) {
					String target = args[1];
					Player t = null;
					for (Player online : Bukkit.getServer().getOnlinePlayers()) {
						if (online.getName().equalsIgnoreCase(target)) {
							t = online;
							break;
						}
					}
					if (t == null) {
						sms(t, "&7" + target + " &cis not online!");
						return true;
					}
					String skinOwner = args[2];
					plugin.getFactory().changeDisplay(t, skinOwner, null);
					Commands.disguised.put(t.getName(), skinOwner + ";" + t.getName());
					sms(s, "&7" + t.getName() + "s &askin has been changed!");
					sms(t, "&aYour skin has been changed!");
					return true;
				}
				else if (args.length == 4) {
					String target = args[1];
					Player t = null;
					for (Player online : Bukkit.getServer().getOnlinePlayers()) {
						if (online.getName().equalsIgnoreCase(target)) {
							t = online;
							break;
						}
					}
					if (t == null) {
						sms(s, "&7" + target + " &cis not online!");
						return true;
					}
					String skinOwner = args[2];
					String displayName = args[3];
					
					plugin.getFactory()
							.changeDisplay(t, skinOwner, ChatColor.translateAlternateColorCodes('&', displayName));
					Commands.disguised.put(t.getName(), skinOwner + ";" + stripColor(displayName));
					sms(s, "&7" + t.getName()
							+ "s &askin and name has been changed!");
					sms(t, "&aYour skin and name has been changed!");
					return true;
				} else {
					sms(s,
							"&cIncorrect usage! &f/skin set <target> <skinOwner> (displayName)");
					return true;
				}

			} 
			else if (args[0].equalsIgnoreCase("reset")) {
				if (args.length == 2) {
					
					String target = args[1];
					Player t = null;
					for (Player online : Bukkit.getServer().getOnlinePlayers()) {
						if (online.getName().equalsIgnoreCase(target)) {
							t = online;
							break;
						}
					}
					if (t == null) {
						sms(s, "&7" + target + " &cis not online!");
						return true;
					}

					plugin.getFactory().removeChanges(t);
					if (Commands.disguised.containsKey(t.getName())) {
						Commands.disguised.remove(t.getName());
					}
					sms(s, "&7" + t.getName()
							+ "s &askin and name have been reset!");
					sms(t, "&aYour skin and name have been reset!");
					return true;
				} else {
					sms(s, "&cIncorrect usage! &f/skin reset <target>");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("list")) {

				if (Commands.disguised.isEmpty() || Commands.disguised == null) {
					sms(s, "&aThere are no players disguised!");
					return true;
				}
				sms(s, "&aThere are &7" + Commands.disguised.keySet().size()
						+ " &aplayers disguised!");
				for (String d : Commands.disguised.keySet()) {
					String value = Commands.disguised.get(d);
					String[] parts = value.split(";");
					sms(s, "&aPlayer:&7" + d + " &aSkin:&7" + parts[0]
							+ " &aName:&7" + parts[1]);
					return true;
				}
			}

			else {
				sms(s, "&cIncorrect usage!");
				sms(s, "&ause &7/skin help &afor help!");
			}
		
		}
		else {
			sms(s, "&cIncorrect usage!");
			sms(s, "&ause &7/skin help &afor help!");
		}

		return true;
	}
	public String stripColor(String oldMsg) {
		return oldMsg.replace("§0", "").replace("§1", "").replace("§2", "")
				.replace("§3", "").replace("§4", "").replace("§5", "")
				.replace("§6", "").replace("§7", "").replace("§8", "")
				.replace("§9", "").replace("§a", "").replace("§b", "")
				.replace("§c", "").replace("§d", "").replace("§e", "")
				.replace("§f", "").replace("&0", "").replace("&1", "")
				.replace("&2", "").replace("&3", "").replace("&4", "")
				.replace("&5", "").replace("&6", "").replace("&7", "")
				.replace("&8", "").replace("&9", "").replace("&a", "")
				.replace("&b", "").replace("&c", "").replace("&d", "")
				.replace("&e", "").replace("&f", "").replace("&k", "")
				.replace("&l", "").replace("&m", "").replace("&n", "")
				.replace("&o", "").replace("&r", "").replace("§k", "")
				.replace("§l", "").replace("§m", "").replace("§n", "")
				.replace("§o", "").replace("§r", "");
	}
}
