package de.ancash.nbtnexus.command;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.ancash.nbtnexus.NBTNexus;

public abstract class NBTNexusSubCommand implements BiFunction<CommandSender, String[], Boolean> {

	private final String[] subCmd;
	protected final NBTNexus pl;

	public NBTNexusSubCommand(NBTNexus pl, String... str) {
		this.subCmd = Stream.of(str).map(s -> s.toLowerCase(Locale.ENGLISH)).toArray(String[]::new);
		this.pl = pl;
	}

	public String[] getSubCommand() {
		return subCmd;
	}

	public boolean isPlayer(CommandSender sender) {
		if (sender instanceof Player)
			return true;
		return false;
	}
}