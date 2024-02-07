package de.ancash.minecraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.ancash.ILibrary;
import de.ancash.minecraft.inventory.search.SearchGUI;
import de.ancash.minecraft.inventory.search.impl.MaterialQueryProcessor;

public class MaterialSearchCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] args) {
		if (!(arg0 instanceof Player))
			return true;
		SearchGUI s = new SearchGUI(ILibrary.getInstance(), ((Player) arg0).getUniqueId(), "test");
		s.addQueryProcessor(new MaterialQueryProcessor(x -> arg0.sendMessage(x.toString())));
		return true;
	}

}
