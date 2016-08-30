package net.bobmandude9889.kitpvp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import net.bobmandude9889.Main;

public class KitManager {

	public static File kitFolder;

	public static List<Kit> kits;

	public static void init() {
		kitFolder = new File(Main.instance.getDataFolder(), "kits");

		kitFolder.mkdir();
		
		kits = new ArrayList<Kit>();

		for (File file : kitFolder.listFiles()) {
			Kit kit = new Kit(file);
			kit.loadContents();
			kits.add(kit);
		}
	}

	public static void setKit(String name, ItemStack[][] contents) {
		for (Kit kit : kits) {
			if (kit.name.equals(name)) {
				kit.setContents(contents);
				break;
			}
		}
	}

	public static void createKit(String name, ItemStack[][] contents) {
		File file = new File(kitFolder, name);
		Kit kit = new Kit(file);
		kit.setContents(contents);
		kits.add(kit);
	}

	public static String[] playerInventoryToBase64(PlayerInventory playerInventory) throws IllegalStateException {
		// get the main content part, this doesn't return the armor
		String content = itemStackArrayToBase64(playerInventory.getContents());
		String armor = itemStackArrayToBase64(playerInventory.getArmorContents());

		return new String[] { content, armor };
	}

	public static Kit getKit(String name) {
		for (Kit kit : kits) {
			if (kit.name.equalsIgnoreCase(name))
				return kit;
		}
		return null;
	}

	public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			// Write the size of the inventory
			dataOutput.writeInt(items.length);

			// Save every element in the list
			for (int i = 0; i < items.length; i++) {
				dataOutput.writeObject(items[i]);
			}

			// Serialize that array
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}

	public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack[] items = new ItemStack[dataInput.readInt()];

			// Read the serialized inventory
			for (int i = 0; i < items.length; i++) {
				items[i] = (ItemStack) dataInput.readObject();
			}

			dataInput.close();
			return items;
		} catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}

}
