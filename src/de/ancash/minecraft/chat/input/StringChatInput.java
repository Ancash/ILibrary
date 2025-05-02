package de.ancash.minecraft.chat.input;

import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.lambda.Lambda;
import de.ancash.minecraft.input.IStringInput;

public class StringChatInput implements Listener, IStringInput {

	protected final Player player;
	protected int inputs = 1;
	protected int allowedMisInputs = Integer.MAX_VALUE;
	protected Consumer<String> onComplete;
	private Function<String, Duplet<Boolean, String>> isValid;
	protected Runnable onAllChancesUsed;
	protected final JavaPlugin pl;
	protected String initialInputMessage;

	public StringChatInput(JavaPlugin pl, Player player) {
		this(pl, player, null);
	}

	public StringChatInput(JavaPlugin pl, Player player, Consumer<String> c) {
		this(pl, player, null, (a) -> Tuple.of(true, null));
	}

	public StringChatInput(JavaPlugin pl, Player player, Consumer<String> c, Function<String, Duplet<Boolean, String>> isValid) {
		this.player = player;
		this.pl = pl;
		this.onComplete = c;
		this.isValid = isValid;
	}

	@Override
	public StringChatInput onComplete(Consumer<String> onInput) {
		this.onComplete = onInput;
		return this;
	}

	@Override
	public StringChatInput isValid(Function<String, Duplet<Boolean, String>> isValid) {
		this.isValid = isValid;
		return this;
	}

	public StringChatInput setInitialInputMessage(String s) {
		this.initialInputMessage = s;
		return this;
	}

	public int remainingInputs() {
		return inputs;
	}

	public StringChatInput setAllowedMisInputs(int i) {
		this.allowedMisInputs = i;
		return this;
	}

	public StringChatInput setInputs(int i) {
		this.inputs = i;
		return this;
	}

	public StringChatInput onAllChancesUsed(Runnable r) {
		this.onAllChancesUsed = r;
		return this;
	}

	@Override
	public void start() {
		Bukkit.getPluginManager().registerEvents(this, pl);
		Lambda.of(initialInputMessage).execIf(Lambda.notNull(), player::sendMessage);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public synchronized void onChat(AsyncPlayerChatEvent event) {
		if (!event.getPlayer().equals(player))
			return;
		if (inputs <= 0 || allowedMisInputs < 0) {
			HandlerList.unregisterAll(this);
			return;
		}

		event.setCancelled(true);
		Duplet<Boolean, String> valid = isValid.apply(event.getMessage());
		if (valid.getFirst()) {
			inputs--;
			onComplete.accept(event.getMessage());
		} else {
			Lambda.of(valid.getSecond()).execIf(Lambda.notNull(), event.getPlayer()::sendMessage);
			if ((allowedMisInputs--) < 0)
				Lambda.of(onAllChancesUsed).ifPresent(Runnable::run);
		}
	}
}
