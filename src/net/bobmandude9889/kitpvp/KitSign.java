package net.bobmandude9889.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class KitSign {

	public Location location;
	public Kit kit;
	
	public KitSign(Location location, Kit kit) {
		this.location = location;
		this.kit = kit;
	}
	
	public String serialize(){
		return kit.name 
				+ "," 
				+ location.getX() 
				+ "," 
				+ location.getBlockY() 
				+ "," 
				+ location.getBlockZ() 
				+ ","
				+ location.getWorld().getName();
	}
	
	public static KitSign deserialize(String rawSign){
		String[] split = rawSign.split(",");
		Kit kit = KitManager.getKit(split[0]);
		
		Location location = new Location(Bukkit.getWorld(split[4]),Double.parseDouble(split[1]),Double.parseDouble(split[2]),Double.parseDouble(split[3]));
		
		return new KitSign(location, kit);
	}
	
}
