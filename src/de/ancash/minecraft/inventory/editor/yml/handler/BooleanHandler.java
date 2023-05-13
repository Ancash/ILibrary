package de.ancash.minecraft.inventory.editor.yml.handler;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.editor.yml.BooleanEditor;
import de.ancash.minecraft.inventory.editor.yml.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.ValueEditor;
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;

public class BooleanHandler implements IValueHandler<Boolean> {

	public static final BooleanHandler INSTANCE = new BooleanHandler();

	protected BooleanHandler() {
	}

	@Override
	public boolean isValid(ConfigurationSection section, String key) {
		return section.isBoolean(key);
	}

	@Override
	public Boolean get(ConfigurationSection section, String s) {
		return section.getBoolean(s);
	}

	@SuppressWarnings("nls")
	@Override
	public ItemStack getAddItem() {
		return new ItemBuilder(XMaterial.REDSTONE_TORCH).setDisplayname("§7Add Boolean").build();
	}

	@Override
	public void set(ConfigurationSection section, String key, Boolean value) {
		section.set(key, value);
	}

	@Override
	public Class<?> getClazz() {
		return Boolean.class;
	}

	@Override
	public ItemStack getEditItem(ConfigurationSectionEditor editor, String key) {
		ItemStack item = IValueHandler.super.getEditItem(editor, key);
		item.setType(XMaterial.REDSTONE_TORCH.parseMaterial());
		return item;
	}

	@Override
	public void edit(ConfigurationSectionEditor editor, String key) {
		edit(editor.getFile(), editor, key, editor.getValueHandler(), editor.getId(),
				YamlEditor.createTitle(editor.getRoot(), editor.getCurrent(), key, editor.getHandler(key).getClazz(),
						32),
				() -> editor.getCurrent().getBoolean(key), b -> editor.getCurrent().set(key, b), editor::open,
				() -> editor.getCurrent().remove(key));
	}

	public void replaceValue(int[] arr, int find, int replace) {
		IntStream.range(0, arr.length).boxed().filter(pos -> arr[pos] == find).forEach(pos -> arr[pos] = replace);
	}

	@Override
	public String valueToString(ConfigurationSection section, String s) {
		return String.valueOf(get(section, s));
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof Boolean;
	}

	@Override
	public void edit(YamlEditor yfe, ValueEditor<?> parent, String key, List<IValueHandler<?>> valHandler, UUID id,
			String title, Supplier<Boolean> valSup, Consumer<Boolean> onEdit, Runnable onBack, Runnable onDelete) {
		BooleanEditor be = new BooleanEditor(id, title, parent, yfe, key, () -> onEdit.accept(!valSup.get()), valSup,
				onBack, onDelete);
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> be.open(), 1);
	}

	@Override
	public Boolean defaultValue() {
		return true;
	}

}
