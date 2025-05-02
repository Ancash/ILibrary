package de.ancash.nbtnexus.command;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.ancash.nbtnexus.NBTNexus;

public class NBTNexusCommand implements CommandExecutor {

	private final NBTNexus plugin;
	private final List<String> toSend;
	private final Map<String, NBTNexusSubCommand> subCmds = new HashMap<>();

	@SuppressWarnings({ "unchecked" })
	public NBTNexusCommand(NBTNexus plugin) {
		this.plugin = plugin;
		toSend = (List<String>) this.plugin.pl.getDescription().getCommands().get("nbtn").get("usage");
	}

	public void addSubCommand(NBTNexusSubCommand s) {
		for (String str : s.getSubCommand())
			subCmds.put(str, s);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (args.length == 0) {
			toSend.forEach(str -> sender.sendMessage(str));
			return true;
		}

		String command = args[0].toLowerCase(Locale.ENGLISH);

		if (subCmds.containsKey(command) && subCmds.get(command).apply(sender, args))
			return true;

		toSend.forEach(str -> sender.sendMessage(str));
		return true;
	}
}