package de.ancash.minecraft;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.BiFunction;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MinecraftLoggerUtil {

	private static final boolean appendPrefix = !Bukkit.getVersion().toLowerCase().contains("spigot");

	public static void enableDebugging(JavaPlugin plugin) {
		enableDebugging(plugin, (pl, record) -> record.getLevel().intValue() < Level.INFO.intValue(), (pl, record) -> {
			if (record.getLevel().intValue() < Level.INFO.intValue()) {
				StringBuilder builder = new StringBuilder();
				builder.append("\b\b\b\b\b\b\b");
				builder.append(record.getLevel().toString());
				builder.append("]: ");

				if (appendPrefix) {
					builder.append("[");
					builder.append(plugin.getName());
					builder.append("] ");
				}
				builder.append(record.getMessage());
				return builder.toString();
			} else {
				return record.getMessage();
			}
		});
	}

	public static void enableDebugging(JavaPlugin plugin, BiFunction<JavaPlugin, LogRecord, Boolean> handle,
			BiFunction<JavaPlugin, LogRecord, String> format) {
		plugin.getLogger().setLevel(Level.ALL);
		plugin.getLogger().setFilter(new Filter() {

			@Override
			public boolean isLoggable(LogRecord record) {
				if (!handle.apply(plugin, record))
					return true;
				println(format.apply(plugin, record));
				return false;
			}
		});
	}

	public static void disableDebugging(JavaPlugin plugin) {
		plugin.getLogger().setFilter(null);
	}

	public static void println() {
		System.out.println();
	}

	public static void println(String str) {
		println(str, Charset.forName("UTF-8"));
	}

	public static void println(String str, Charset set) {
		print(str, set);
		println();
	}

	public static void print(String str) {
		println(str, Charset.forName("UTF-8"));
	}

	public static void print(String str, Charset set) {
		try {
			System.out.write(str.getBytes(set));
		} catch (IOException e) {
			System.err.println("Could not print '" + str + "': " + e);
		}
	}
}