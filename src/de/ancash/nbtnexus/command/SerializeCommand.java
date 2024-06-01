package de.ancash.nbtnexus.command;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.ancash.nbtnexus.NBTNexus;
import de.ancash.nbtnexus.serde.ItemSerializer;

public class SerializeCommand extends NBTNexusSubCommand {

	@SuppressWarnings("nls")
	public static final String PERMISSION = "nbtn.serialize";

	@SuppressWarnings("nls")
	public SerializeCommand(NBTNexus pl) {
		super(pl, "s", "ser", "serialize");
	}

	@SuppressWarnings({ "deprecation", "nls" })
	@Override
	public Boolean apply(CommandSender arg0, String[] arg1) {
		if (!isPlayer(arg0))
			return false;
		Player player = (Player) arg0;
		if (!player.hasPermission(PERMISSION))
			return false;
		ItemStack item = player.getItemInHand();
		if (item == null || item.getType() == Material.AIR) {
			player.sendMessage("§cNo item in hand");
			return true;
		}
		try {
			String yaml = ItemSerializer.INSTANCE.serializeItemStackToYaml(item);
			player.sendMessage("§7-------------------Yaml-------------------");
			player.sendMessage(yaml);
			Bukkit.getConsoleSender().sendMessage(player.getDisplayName() + " serialized an item, yaml:");
			Bukkit.getConsoleSender().sendMessage(yaml);
		} catch (IOException e) {
			player.sendMessage("§cCould not serialize to yaml");
			e.printStackTrace();
			return true;
		}
		try {
			String json = ItemSerializer.INSTANCE.serializeItemStackToJson(item);
			player.sendMessage("§7-------------------Json-------------------");
			player.sendMessage(json);
			Bukkit.getConsoleSender().sendMessage(player.getDisplayName() + " serialized an item, json:");
			Bukkit.getConsoleSender().sendMessage(json);
		} catch (IOException e) {
			player.sendMessage("§cCould not serialize to json");
			e.printStackTrace();
			return true;
		}
		return true;
	}
}
