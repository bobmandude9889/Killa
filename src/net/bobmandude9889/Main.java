package net.bobmandude9889;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import net.bobmandude9889.commandcooldown.PlayerCommandListener;
import net.bobmandude9889.commandcooldown.TimeManager;
import net.bobmandude9889.kitpvp.Kit;
import net.bobmandude9889.kitpvp.KitManager;
import net.bobmandude9889.kitpvp.KitSignManager;
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
		instance = this;

		KitManager.init();

		Bukkit.getPluginManager().registerEvents(new TimeManager(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerCommandListener(), this);
		Bukkit.getPluginManager().registerEvents(new KitSignManager(), this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch (command.getName().toLowerCase()) {
		case "fixall":
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
			break;
		case "setkit":
			if (sender instanceof Player && sender.hasPermission("kit.set")) {
				Player player = (Player) sender;
				try {
					PlayerInventory inv = player.getInventory();
					if (KitManager.getKit(args[0]) == null) {
						KitManager.createKit(args[0].toLowerCase(), new ItemStack[][] { inv.getContents(), inv.getArmorContents() });
						sender.sendMessage(ChatColor.GRAY + "Created kit " + ChatColor.GREEN + args[0].toLowerCase() + ChatColor.GRAY + " using current inventory.");
					} else {
						KitManager.setKit(args[0].toLowerCase(), new ItemStack[][] { inv.getContents(), inv.getArmorContents() });
						sender.sendMessage(ChatColor.GRAY + "Set kit " + ChatColor.GREEN + args[0].toLowerCase() + ChatColor.GRAY + " using current inventory.");
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					sender.sendMessage(ChatColor.WHITE + "/setkit <name>");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You can not use that command!");
			}
			break;
		case "kitsign":
			if (sender instanceof Player && sender.hasPermission("kit.set")) {
				try {
					switch (args[0]) {
					case "add":
						Kit kit = KitManager.getKit(args[1]);
						if (kit != null) {
							KitSignManager.instance.add.put((Player) sender, kit);
							sender.sendMessage(ChatColor.GREEN + "Click a sign to add the kit to it!");
						} else {
							sender.sendMessage(ChatColor.RED + "Unknown kit!");
						}
						break;
					case "remove":
						if (KitSignManager.instance.remove.contains((Player) sender)) {
							KitSignManager.instance.remove.remove((Player) sender);
							sender.sendMessage(ChatColor.RED + "Disable sign removal");
						} else {
							KitSignManager.instance.remove.add((Player) sender);
							sender.sendMessage(ChatColor.GREEN + "Click a sign to remove it!");
						}
						break;
					default:
						sender.sendMessage(ChatColor.WHITE + "/kitsign <add,remove> [<name>]");
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					sender.sendMessage(ChatColor.WHITE + "/kitsign <add,remove> [<name>]");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You can not use that command!");
			}
			break;
		case "kreload":
			if (sender.hasPermission("killa.reload")) {
				this.reloadConfig();
				sender.sendMessage(ChatColor.GREEN + "Reloaded configs!");
			}
			break;
		}
		return true;
	}

}
