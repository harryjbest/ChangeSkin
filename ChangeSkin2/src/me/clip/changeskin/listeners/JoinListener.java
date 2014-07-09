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
package me.clip.changeskin.listeners;

import me.clip.changeskin.ChangeSkin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

	ChangeSkin plugin;

	public JoinListener(ChangeSkin changeSkin) {
		plugin = changeSkin;
	}

	private void sms(Player p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (ChangeSkin.forceSkin) {
		plugin.setForcedSkin(p);
		}
		
		if (ChangeSkin.checkUpdates) {
			if (p.hasPermission("skin.updates") && ChangeSkin.update) {
				sms(p, "&aAn update is available: &7" + ChangeSkin.uName
						+ "&a, a &7" + ChangeSkin.uType + " &afor &f"
						+ ChangeSkin.uVersion + " &aavailable at &b"
						+ ChangeSkin.uLink);
			}
		}
	}
	

}
