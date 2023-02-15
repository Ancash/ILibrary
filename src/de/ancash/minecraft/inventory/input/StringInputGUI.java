package de.ancash.minecraft.inventory.input;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.lambda.Lambda;
import de.ancash.minecraft.input.IStringInput;
import net.wesjd.anvilgui.AnvilGUI;

public class StringInputGUI implements IStringInput {

	protected final StringInputGUI instance = this;
	private ItemStack left, right;
	private String title, text;
	private final Player player;
	private final JavaPlugin plugin;
	private Consumer<String> onComplete;
	private Function<String, Duplet<Boolean, String>> isValid;

	public StringInputGUI(JavaPlugin plugin, Player player) {
		this(plugin, player, null, (chars) -> Tuple.of(true, null));
	}

	public StringInputGUI(JavaPlugin plugin, Player player, Consumer<String> onComplete) {
		this(plugin, player, onComplete, (chars) -> Tuple.of(true, null));
	}

	public StringInputGUI(JavaPlugin plugin, Player player, Consumer<String> onComplete,
			Function<String, Duplet<Boolean, String>> isValid) {
		this.player = player;
		this.plugin = plugin;
		this.onComplete = onComplete;
		this.isValid = isValid;
	}

	@Override
	public StringInputGUI onComplete(Consumer<String> onComplete) {
		this.onComplete = onComplete;
		return this;
	}

	@Override
	public StringInputGUI isValid(Function<String, Duplet<Boolean, String>> isValid) {
		this.isValid = isValid;
		return this;
	}

	public StringInputGUI setLeft(ItemStack left) {
		this.left = left;
		return this;
	}

	public StringInputGUI setRight(ItemStack right) {
		this.right = right;
		return this;
	}

	public StringInputGUI setTitle(String title) {
		this.title = title;
		return this;
	}

	public StringInputGUI setText(String text) {
		this.text = text;
		return this;
	}

	@Override
	public void start() {
		open();
	}

	public void open() {
		new AnvilGUI.Builder().itemLeft(left).itemRight(right).title(title).text(text).plugin(plugin).preventClose()
				.onComplete(complete -> {
					Duplet<Boolean, String> valid = isValid.apply(complete.getText());
					if (!valid.getFirst())
						return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText(valid.getSecond()));
					Lambda.of(onComplete).execIf(Lambda.notNull(), c -> c.accept(text));
					return Arrays.asList(AnvilGUI.ResponseAction.close());
				}).open(player);
	}
}