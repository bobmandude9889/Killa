package net.bobmandude9889;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import net.bobmandude9889.CommandCooldown.PlayerCommandListener;
import net.bobmandude9889.CommandCooldown.TimeManager;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.item.Items;

public class Main extends JavaPlugin {

	public static Main instance;
	
	public static boolean crash = false;

	public static void main(String[] args) {

	}

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		Bukkit.getPluginManager().registerEvents(new TimeManager(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerCommandListener(), this);
		instance = this;
		Logger logger = (Logger) LogManager.getRootLogger();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("fixall")) {
			if (sender instanceof Player && sender.hasPermission("fixall.use")) {
				Player player = (Player) sender;
				for (ItemStack item : player.getInventory().getContents()) {
					if (item != null && Items.itemByType(item.getType()).isDurable()) {
						item.setDurability((short) 0);
					}
				}
				for (ItemStack item : player.getInventory().getArmorContents()) {
					if (item != null && Items.itemByType(item.getType()).isDurable()) {
						item.setDurability((short) 0);
					}
				}
				player.sendMessage(ChatColor.GREEN + "All items have been fixed!");
			} else {
				sender.sendMessage(ChatColor.RED + "You can not use that command!");
			}
		}
		return true;
	}

}
