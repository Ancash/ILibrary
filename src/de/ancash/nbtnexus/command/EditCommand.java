package de.ancash.nbtnexus.command;

import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.ancash.minecraft.inventory.editor.yml.YamlEditor;
import de.ancash.nbtnexus.NBTNexus;
import de.ancash.nbtnexus.editor.handler.HandlerMapper;
import de.ancash.nbtnexus.editor.handler.KeySuggester;
import de.ancash.nbtnexus.editor.handler.KeyValidator;
import de.ancash.nbtnexus.editor.handler.ListTypeValidator;
import de.ancash.nbtnexus.editor.handler.ValueSuggester;
import de.ancash.nbtnexus.editor.handler.ValueValidator;
import de.ancash.nbtnexus.serde.ItemDeserializer;
import de.ancash.nbtnexus.serde.ItemSerializer;

public class EditCommand extends NBTNexusSubCommand {

	@SuppressWarnings("nls")
	public static final String PERMISSION = "nbtn.edit";

	@SuppressWarnings("nls")
	public EditCommand(NBTNexus pl) {
		super(pl, "edit");
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
		String yaml;
		try {
			yaml = ItemSerializer.INSTANCE.serializeItemStackToYaml(item);
		} catch (IOException e) {
			player.sendMessage("§cCould not serialize to yaml");
			e.printStackTrace();
			return true;
		}
		YamlEditor editor;
		try {
			editor = new YamlEditor(yaml, player, ye -> onSave(ye, player));
		} catch (IOException | InvalidConfigurationException e) {
			player.sendMessage("§cCould not load editor");
			e.printStackTrace();
			return true;
		}
		editor.addValidator(new ValueValidator());
		editor.setHandlerMapper(new HandlerMapper());
		editor.setKeyValidator(new KeyValidator());
		editor.addValueSuggester(new ValueSuggester());
		editor.addKeySuggester(new KeySuggester());
		editor.setListTypeValidator(new ListTypeValidator());
		editor.open();
		return true;
	}

	@SuppressWarnings({ "deprecation", "nls" })
	private void onSave(YamlEditor editor, Player player) {
		String yml;
		try {
			yml = editor.getYamlFile().saveToString();
		} catch (IOException e) {
			player.sendMessage("§cCould not save yaml");
			e.printStackTrace();
			return;
		}
		player.setItemInHand(ItemDeserializer.INSTANCE.deserializeYamlToItemStack(yml));
		player.sendMessage("§aItem edited!");
	}
}
