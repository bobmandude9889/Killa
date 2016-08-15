package net.bobmandude9889.CommandCooldown;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.bobmandude9889.Main;
import net.md_5.bungee.api.ChatColor;

public class PlayerCommandListener implements Listener {

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		String command = e.getMessage();
		command = command.split(" ")[0];
		command = command.substring(1);

		ConfigurationSection sect = Main.instance.getConfig().getConfigurationSection("commands");
		for (String s : sect.getKeys(false)) {
			if (command.equalsIgnoreCase(s)) {
				attemptCommand(s, player, e);
			} else {
				List<String> aliases = Bukkit.getPluginCommand(s).getAliases();
				for (String alias : aliases) {
					if (alias.equalsIgnoreCase(command)) {
						attemptCommand(s, player, e);
						break;
					}
				}
			}
		}
	}

	private void attemptCommand(String command, Player player, PlayerCommandPreprocessEvent e) {
		ConfigurationSection sect = Main.instance.getConfig().getConfigurationSection("commands");
		Long lastUse = TimeManager.getUseTime(player, command);
		Long cooldown = sect.getLong(command) * 1000;
		if(System.currentTimeMillis() - lastUse < cooldown){
			Long timeRemaining = (cooldown - (System.currentTimeMillis() - lastUse) + 1000) / 1000;
			player.sendMessage(ChatColor.GRAY + "Please wait " + ChatColor.RED + timeRemaining + " second" + (timeRemaining != 1 ? "s" : "") + ChatColor.GRAY + " before using " + ChatColor.RED + "/" + command);
			e.setCancelled(true);
		} else {
			TimeManager.setUseTime(player, command);
		}
			
	}

}
