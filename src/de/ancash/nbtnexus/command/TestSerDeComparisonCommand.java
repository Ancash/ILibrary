package de.ancash.nbtnexus.command;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.ancash.misc.MathsUtils;
import de.ancash.nbtnexus.NBTNexus;
import de.ancash.nbtnexus.serde.ItemDeserializer;
import de.ancash.nbtnexus.serde.ItemSerializer;
import de.ancash.nbtnexus.serde.SerializedItem;

public class TestSerDeComparisonCommand extends NBTNexusSubCommand {

	@SuppressWarnings("nls")
	public static final String PERMISSION = "nbtn.tsdc";

	@SuppressWarnings("nls")
	public TestSerDeComparisonCommand(NBTNexus pl) {
		super(pl, "tsdc");
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
		String yaml = null;
		long l = System.nanoTime();
		for (int i = 0; i < 1000; i++) {
			try {
				yaml = ItemSerializer.INSTANCE.serializeItemStackToYaml(
						ItemDeserializer.INSTANCE.deserializeJsonToItemStack(ItemSerializer.INSTANCE.serializeItemStackToJson(
								ItemDeserializer.INSTANCE.deserializeYamlToItemStack(ItemSerializer.INSTANCE.serializeItemStackToYaml(item)))));
			} catch (IOException e) {
				player.sendMessage("§cCould not serialize to yaml");
				e.printStackTrace();
				return true;
			}
		}
		player.sendMessage(
				"§eSerialized item->yaml->item->json->item->yaml in " + ((System.nanoTime() - l) / 1000000d) / 1000 + " ms avg (1000 iters)");
		l = System.nanoTime();
		String copy = yaml;
		Bukkit.getScheduler().runTaskAsynchronously(pl.pl, () -> {
			long ll = System.nanoTime();
			SerializedItem a = SerializedItem.of(item);
			SerializedItem b = SerializedItem.of(ItemDeserializer.INSTANCE.deserializeYamlToItemStack(copy));
			for (int i = 1; i < 10000; i++) {
				a.areEqual(b);
			}
			if (a.areEqual(b)) {
				player.sendMessage(
						"§aComparison successful! " + MathsUtils.round((System.nanoTime() - ll) / 1000000d / 10000, 6) + "ms avg (10.000 iters)");
			} else {
				player.sendMessage("§cComparison failed! See console for the data");
				try {
					Bukkit.getConsoleSender().sendMessage(copy);
					Bukkit.getConsoleSender().sendMessage(ItemSerializer.INSTANCE.serializeItemStackToYaml(item));
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		});
		return true;
	}
}
