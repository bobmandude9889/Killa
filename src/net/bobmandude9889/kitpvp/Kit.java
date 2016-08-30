package net.bobmandude9889.kitpvp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class Kit {

	public String name;
	File file;
	ItemStack[] contents;
	ItemStack[] armor;

	public Kit(File file) {
		this.file = file;
		this.name = file.getName().substring(0,file.getName().length());
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadContents() {
		FileInputStream in;
		try {
			in = new FileInputStream(file);
			byte[] b = new byte[in.available()];
			in.read(b);
			String fileIn = new String(b);
			String[] b64 = fileIn.split(" ");
			contents = KitManager.itemStackArrayFromBase64(b64[0]);
			armor = KitManager.itemStackArrayFromBase64(b64[1]);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setContents(ItemStack[][] items) {
		try {
			PrintStream out = new PrintStream(file);
			out.print(KitManager.itemStackArrayToBase64(items[0]) + " ");
			out.print(KitManager.itemStackArrayToBase64(items[1]));
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		contents = items[0];
		armor = items[1];
	}

	public void applyKit(Player player) {
		PlayerInventory inv = player.getInventory();
		
		inv.clear();

		inv.setArmorContents(armor);
		inv.setContents(contents);
		
		for(PotionEffect eff : player.getActivePotionEffects()){
			player.removePotionEffect(eff.getType());
		}
		
		player.updateInventory();
	}

}
