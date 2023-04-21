package de.ancash.minecraft.commands;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import de.ancash.minecraft.inventory.editor.YamlFileEditor;

public class FEditCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] args) {
		if (!(arg0 instanceof Player))
			return true;
		Player p = (Player) arg0;
		if (args.length != 1)
			return false;
		File file = new File(args[0]);
		if (!file.exists()) {
			p.sendMessage(args[0] + " does not exist");
			return true;
		}
		if (file.isDirectory()) {
			p.sendMessage(args[0] + " is a directory");
			return true;
		}
		try {
			YamlFileEditor editor = new YamlFileEditor(file, p);
			editor.open();
		} catch (IOException | InvalidConfigurationException e) {
			p.sendMessage(e.getLocalizedMessage());
			e.printStackTrace();
			return true;
		}

		return true;
	}

}
