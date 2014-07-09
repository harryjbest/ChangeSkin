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

import java.util.HashMap;

import me.clip.changeskin.ChangeSkin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	ChangeSkin plugin;
	private ConsoleCommands consolecmd;

	public Commands(ChangeSkin instance) {
		plugin = instance;
		consolecmd = new ConsoleCommands(instance);
	}

	private void sms(Player p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

	public static HashMap<String, String> disguised = new HashMap<String, String>();

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (!(sender instanceof Player)) {
			return consolecmd.onCommand(sender, cmd, label, args);
		}

		Player p = (Player) sender;

		if (args.length < 1) {
			sms(p, "&7Change&fSkin &av&f"
					+ plugin.getDescription().getVersion());
			sms(p, "&7Created by: &cextended_clip");
			sms(p, "&ause &7/skin help &afor help!");
			return true;
		}

		else if (args.length >= 1) {
			/*
			 * 
			 * Self
			 */
			if (args[0].equalsIgnoreCase("self")) {
				if (!p.hasPermission("skin.self")) {
					sms(p, "&cYou need &7skin.self &cto use that command!");
					return true;
				}
				if (args.length == 2) {
					
					String target = args[1];
					if (ChangeSkin.filter) {
					if (!p.hasPermission("skin.bypass.skins")) {
						if (plugin.isWhitelist()) {
							if (plugin.getSkins() != null
									&& !plugin.getSkins().contains(target)) {
								sms(p, "&cThat skin is not allowed!");
								return true;
							}
						} else {
							if (plugin.getSkins() != null
									&& plugin.getSkins().contains(target)) {
								sms(p, "&cThat skin is not allowed!");
								return true;
							}
						}
					}
					}
					plugin.getFactory().changeDisplay(p.getName(), target);
					disguised.put(p.getName(), target + ";" + p.getName());
					sms(p, "&aYour skin has been changed!");
					return true;
				}

				else if (args.length == 3) {
					if (!p.hasPermission("skin.name")) {
						sms(p, "&cYou need &7skin.name &cto use that command!");
						return true;
					}
					String target = args[1];
					String name = args[2];
					
					if (ChangeSkin.filter) {
					if (plugin.isWhitelist()) {
						if (!p.hasPermission("skin.bypass.skins")) {
							if (plugin.getSkins() != null
									&& !plugin.getSkins().contains(target)) {
								sms(p, "&cThat skin is not allowed!");
								return true;
							}
						}
						if (!p.hasPermission("skin.bypass.displaynames")) {
							if (plugin.getDisplayNames() != null
									&& !plugin.getDisplayNames().contains(stripColor(name))) {
								sms(p, "&cThat display name is not allowed!");
								return true;
							}
						}
					} else {
						if (!p.hasPermission("skin.bypass.skins")) {
							if (plugin.getSkins() != null
									&& plugin.getSkins().contains(target)) {
								sms(p, "&cThat skin is not allowed!");
								return true;
							}
						}
						if (!p.hasPermission("skin.bypass.displaynames")) {
							if (plugin.getDisplayNames() != null
									&& plugin.getDisplayNames().contains(stripColor(name))) {
								sms(p, "&cThat display name is not allowed!");
								return true;
							}
						}
					}
					}
					
					plugin.getFactory().changeDisplay(p, target, ChatColor.translateAlternateColorCodes('&', name));
					disguised.put(p.getName(), target + ";" + stripColor(name));
					sms(p, "&aYour skin and name have been changed!");
					return true;
				} else {
					sms(p,
							"&cIncorrect usage! &f/skin self <skinOwner> (displayName)");
					return true;
				}
			} 
			/*
			 * 
			 * Other
			 */
			else if (args[0].equalsIgnoreCase("other")) {
				if (args.length == 3) {
					if (!p.hasPermission("skin.others")) {
						sms(p, "&cYou need &7skin.others &cto use that command!");
						return true;
					}
					String target = args[1];
					Player t = null;
					for (Player online : Bukkit.getServer().getOnlinePlayers()) {
						if (online.getName().equalsIgnoreCase(target)) {
							t = online;
							break;
						}
					}
					if (t == null) {
						sms(p, "&7" + target + " &cis not online!");
						return true;
					}

					String skinOwner = args[2];
					if (ChangeSkin.filter) {
					if (!p.hasPermission("skin.bypass.skins")) {
						if (plugin.isWhitelist()) {
							if (plugin.getSkins() != null
									&& !plugin.getSkins().contains(skinOwner)) {
								sms(p, "&cThat skin is not allowed!");
								return true;
							}
						} else {
							if (plugin.getSkins() != null
									&& plugin.getSkins().contains(skinOwner)) {
								sms(p, "&cThat skin is not allowed!");
								return true;
							}
						}
					}
					}
					plugin.getFactory().changeDisplay(t.getName(), skinOwner);
					disguised.put(t.getName(), skinOwner + ";" + t.getName());
					sms(p, "&7" + t.getName() + "s &askin has been changed!");
					sms(t, "&aYour skin has been changed!");
					return true;
				}

				else if (args.length == 4) {
					if (!p.hasPermission("skin.others.name")) {
						sms(p, "&cYou need &7skin.others.name &cto use that command!");
						return true;
					}
					String target = args[1];
					Player t = null;
					for (Player online : Bukkit.getServer().getOnlinePlayers()) {
						if (online.getName().equalsIgnoreCase(target)) {
							t = online;
							break;
						}
					}
					if (t == null) {
						sms(p, "&7" + target + " &cis not online!");
						return true;
					}
					String skinOwner = args[2];
					String displayName = args[3];
					if (ChangeSkin.filter) {
					if (plugin.isWhitelist()) {
						if (!p.hasPermission("skin.bypass.skins")) {
							if (plugin.getSkins() != null
									&& !plugin.getSkins().contains(skinOwner)) {
								sms(p, "&cThat skin is not allowed!");
								return true;
							}
						}
						if (!p.hasPermission("skin.bypass.displaynames")) {
							if (plugin.getDisplayNames() != null
									&& !plugin.getDisplayNames().contains(
											stripColor(displayName))) {
								sms(p, "&cThat display name is not allowed!");
								return true;
							}
						}
					} else {
						if (!p.hasPermission("skin.bypass.skins")) {
							if (plugin.getSkins() != null
									&& plugin.getSkins().contains(skinOwner)) {
								sms(p, "&cThat skin is not allowed!");
								return true;
							}
						}
						if (!p.hasPermission("skin.bypass.displaynames")) {
							if (plugin.getDisplayNames() != null
									&& plugin.getDisplayNames().contains(
											stripColor(displayName))) {
								sms(p, "&cThat display name is not allowed!");
								return true;
							}
						}
					}
					}
					
					plugin.getFactory()
							.changeDisplay(t, skinOwner, ChatColor.translateAlternateColorCodes('&', displayName));
					disguised.put(t.getName(), skinOwner + ";" + stripColor(displayName));
					sms(p, "&7" + t.getName()
							+ "s &askin and name has been changed!");
					sms(t, "&aYour skin and name has been changed!");
					return true;
				} else {
					sms(p,
							"&cIncorrect usage! &f/skin other <target> <skinOwner> (displayName)");
					return true;
				}

			} 
			/*
			 * 
			 * help
			 */
			else if (args[0].equalsIgnoreCase("help")) {
				sms(p, "&7Change&fSkin &aHelp");
				sms(p, "&a/skin self <skinOwner> (displayName)");
				sms(p, "&a/skin other <target> <skinOwner> (displayName)");
				sms(p, "&a/skin reset (target)");
				sms(p, "&a/skin list");
				sms(p, "&a/skin users");
				sms(p, "&a/skin reload");
				return true;
			} 
			/*
			 * 
			 * reload
			 */
			else if (args[0].equalsIgnoreCase("reload")) {
				if (!p.hasPermission("skin.reload")) {
					sms(p, "&cYou need &7skin.reload &cto use that command!");
					return true;
				}
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
				sms(p, "&aConfiguration successfully reloaded!");
				if (ChangeSkin.forceSkin) {
					for (Player on : Bukkit.getServer().getOnlinePlayers()) {
						plugin.setForcedSkin(on);
					}
					sms(p, "&aAll players have been refreshed to the forced skin if they had permission!");
				}
				
				return true;
			}
			/*
			 * 
			 * List
			 */
			else if (args[0].equalsIgnoreCase("list")) {
				if (!p.hasPermission("skin.self")) {
					sms(p, "&cYou need &7skin.self &cto use that command!");
					return true;
				}
				if (ChangeSkin.filter) {
				if (plugin.isWhitelist()) {
					sms(p, "&aThe following skins and userNames are allowed:");
				} else {
					sms(p,
							"&aThe following skins and userNames are &cnot allowed&a:");
				}
				if (plugin.getSkins() == null || plugin.getSkins().isEmpty()) {
					sms(p, "&aSkins&a: &7None");
				} else {
					sms(p,
							"&aSkins: "
									+ plugin.getSkins().toString()
											.replace("[", "&7")
											.replace("]", "")
											.replace(",", "&a,&7"));
				}
				if (plugin.getDisplayNames() == null
						|| plugin.getDisplayNames().isEmpty()) {
					sms(p, "&aDisplay Names&a: &7None");
				} else {
					sms(p, "&aDisplay Names: "
							+ plugin.getSkins().toString().replace("[", "&7")
									.replace("]", "").replace(",", "&a,&7"));
				}
				}
				else {
					sms(p, "&aThere are no skins or display names being filtered.");
				}
				return true;
			}
			/*
			 * 
			 * Reset
			 */
			else if (args[0].equalsIgnoreCase("reset")) {
				if (args.length == 1) {
					if (!p.hasPermission("skin.self")) {
						sms(p, "&cYou need &7skin.self &cto use that command!");
						return true;
					}
				
					plugin.getFactory().removeChanges(p);
					if (disguised.containsKey(p.getName())) {
						disguised.remove(p.getName());
					}
					sms(p, "&aYour skin and name have been reset!");
					return true;
				}

				else if (args.length == 2) {
					if (!p.hasPermission("skin.others.reset")) {
						sms(p,
								"&cYou need &7skin.others.reset &cto use that command!");
						return true;
					}
					String target = args[1];
					Player t = null;
					for (Player online : Bukkit.getServer().getOnlinePlayers()) {
						if (online.getName().equalsIgnoreCase(target)) {
							t = online;
							break;
						}
					}
					if (t == null) {
						sms(p, "&7" + target + " &cis not online!");
						return true;
					}
					
					plugin.getFactory().removeChanges(t);
					if (disguised.containsKey(t.getName())) {
						disguised.remove(t.getName());
					}
					sms(p, "&7" + t.getName()
							+ "s &askin and name have been reset!");
					sms(t, "&aYour skin and name have been refreshed!");
					return true;
				} else {
					sms(p, "&cIncorrect usage! &f/skin reset (target)");
					return true;
				}
			}
			/*
			 * 
			 * Users
			 */
			else if (args[0].equalsIgnoreCase("users")) {

				if (!p.hasPermission("skin.users")) {
					sms(p, "&cYou need &7skin.users &cto use that command!");
					return true;
				}
				if (disguised.isEmpty()) {
					sms(p, "&aThere are no players disguised!");
					return true;
				}
				if (disguised.keySet().size() == 1) {
					sms(p, "&aThere is &7" + disguised.keySet().size()
							+ " &aplayer disguised!");
				}
				else {
					sms(p, "&aThere are &7" + disguised.keySet().size()
							+ " &aplayers disguised!");
				}
				
				for (String d : disguised.keySet()) {
					String value = disguised.get(d);
					String[] parts = value.split(";");
					sms(p, "&aPlayer:&7" + d + " &aSkin:&7" + parts[0]
							+ " &aName:&7" + parts[1]);
					return true;
				}
			} 
			else {
				sms(p, "&cIncorrect usage!");
				sms(p, "&ause &7/skin help &afor help!");
			}
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
