package net.bobmandude9889.kitpvp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import net.bobmandude9889.Main;
import net.md_5.bungee.api.ChatColor;

public class KitSignManager implements Listener {

	public static KitSignManager instance;

	public HashMap<Player, Kit> add;
	public List<Player> remove;

	public List<KitSign> signs;

	private File configFile;
	private YamlConfiguration config;

	public KitSignManager() {
		instance = this;
		add = new HashMap<Player, Kit>();
		remove = new ArrayList<Player>();

		configFile = new File(Main.instance.getDataFolder(), "kits.yml");
		config = YamlConfiguration.loadConfiguration(configFile);
		
		signs = new ArrayList<KitSign>();
		loadSigns();
	}

	private void loadSigns() {
		List<String> rawSigns = config.getStringList("signs");
		if(rawSigns != null){
			for(String sign : rawSigns){
				KitSign kSign = KitSign.deserialize(sign);
				signs.add(kSign);
			}
		}
	}

	private void saveSigns() {
		List<String> rawSigns = new ArrayList<String>();
		for (KitSign sign : signs) {
			rawSigns.add(sign.serialize());
		}
		config.set("signs", rawSigns);
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (add.containsKey(e.getPlayer()) && isSign(e.getClickedBlock().getType())) {
			e.setCancelled(true);
			Kit kit = add.get(e.getPlayer());
			KitSign sign = new KitSign(e.getClickedBlock().getLocation(), kit);
			signs.add(sign);
			add.remove(e.getPlayer());
			saveSigns();
			e.getPlayer().sendMessage(ChatColor.GREEN + "Sign added!");
		} else if (remove.contains(e.getPlayer()) && isSign(e.getClickedBlock().getType())) {
			KitSign sign = getKitSign(e.getClickedBlock());
			if (sign != null) {
				signs.remove(sign);
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "Sign removed");
				remove.remove(e.getPlayer());
			}
		} else if (isSign(e.getClickedBlock().getType())) {
			KitSign sign = getKitSign(e.getClickedBlock());
			if (sign != null) {
				sign.kit.applyKit(e.getPlayer());
				e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("got_kit_message").replace("{kit}", sign.kit.name)));
				e.setCancelled(true);
			}
		}
	}

	private KitSign getKitSign(Block block) {
		for (KitSign sign : signs) {
			if (sign.location.getX() == block.getLocation().getX() && sign.location.getY() == block.getLocation().getY() && sign.location.getZ() == block.getLocation().getZ() && sign.location.getWorld().getName().equals(block.getLocation().getWorld().getName())) {
				return sign;
			}
		}
		return null;
	}

	private boolean isSign(Material material) {
		return material.equals(Material.SIGN_POST) || material.equals(Material.WALL_SIGN);
	}

}
