package net.bobmandude9889.commandcooldown;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.bobmandude9889.Main;

public class TimeManager implements Listener {

	private static HashMap<Player, HashMap<String, Long>> useTimes;

	private static File configFile;
	private static YamlConfiguration config;
	
	public TimeManager() {
		useTimes = new HashMap<Player, HashMap<String, Long>>();

		configFile = new File(Main.instance.getDataFolder(),"times.yml");
		config = YamlConfiguration.loadConfiguration(configFile);
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			loadPlayer(player);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		loadPlayer(e.getPlayer());
	}

	public static void loadPlayer(Player player) {
		System.out.println("load player " + player.getUniqueId());
		config.createSection("players");
		ConfigurationSection players = config.getConfigurationSection("players");
		HashMap<String, Long> times = new HashMap<String, Long>();
		if (players.contains(player.getUniqueId().toString())) {
			ConfigurationSection playerSect = players.getConfigurationSection(player.getUniqueId().toString());
			for (String command : playerSect.getKeys(false)) {
				times.put(command, playerSect.getLong(command));
			}
		}
		useTimes.put(player, times);
	}

	public static void saveTimes() {
		ConfigurationSection players = config.getConfigurationSection("players");
		for(Player player : useTimes.keySet()){
			players.createSection(player.getUniqueId().toString());
			ConfigurationSection playerSect = players.getConfigurationSection(player.getUniqueId().toString());
			HashMap<String, Long> times = useTimes.get(player);
			for(String key : times.keySet()){
				playerSect.set(key, times.get(key));
			}
		}
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setUseTime(Player player, String command) {
		useTimes.get(player).put(command, System.currentTimeMillis());
		saveTimes();
	}

	public static long getUseTime(Player player, String command) {
		HashMap<String,Long> playerTimes = useTimes.get(player);
		if(playerTimes.containsKey(command))
			return useTimes.get(player).get(command);
		return 0;
	}

}
