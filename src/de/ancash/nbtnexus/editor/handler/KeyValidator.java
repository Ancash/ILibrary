package de.ancash.nbtnexus.editor.handler;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.minecraft.inventory.editor.yml.IKeyValidator;
import de.ancash.minecraft.inventory.editor.yml.gui.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ValueEditor;
import de.ancash.minecraft.inventory.editor.yml.handler.BooleanHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ByteHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ConfigurationSectionHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.DoubleHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.IValueHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.IntegerHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ListHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.LongHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ShortHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.StringHandler;
import de.ancash.nbtnexus.NBTNexus;
import de.ancash.nbtnexus.NBTNexusItem;
import de.ancash.nbtnexus.NBTTag;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;

public class KeyValidator implements IKeyValidator {

	private final SerDeStructure structure = NBTNexus.getInstance().getStructure();

	@SuppressWarnings("nls")
	@Override
	public Duplet<String, String> validate(ValueEditor<?> cur, IValueHandler<?> type, String key) {
		ValueEditor<?> ve = ValidatorUtil.getOneBeforeItemRoot(cur);

		if (ve != null)
			ve = ve.getParent();
		else
			ve = cur;

		if (!ValidatorUtil.isItemRoot(ve))
			return Tuple.of(key, null);
		ConfigurationSectionEditor root = (ConfigurationSectionEditor) ve;
		ve = cur;

		while (!(ve instanceof ConfigurationSectionEditor) && ve.hasParent())
			ve = ve.getParent();

		String path = ((ConfigurationSectionEditor) ve).getCurrent().getCurrentPath() + "." + key;
		if (path.startsWith("."))
			path = path.replaceFirst(".", "");
		path = path.replaceFirst(root.getCurrent().getCurrentPath(), "");
		if (structure.containsKey(path) || (NBTNexusItem.NBT_NEXUS_ITEM_PROPERTIES_TAG.equals(key) && structure.containsKey(path = key))) {
			NBTTag def = structure.isMap(path) ? NBTTag.COMPOUND : (NBTTag) structure.get(path);
			NBTTag actual = NBTTag.getByHandler(type);
			if (actual == null || (actual == def))
				return Tuple.of(key, null);
			return Tuple.of(null, key + " must be " + def + ", not " + actual);
		}

		String ending = "";
		if (type instanceof DoubleHandler) {
			ending = NBTNexus.SPLITTER + NBTTag.DOUBLE.name();
		} else if (type instanceof LongHandler) {
			ending = NBTNexus.SPLITTER + NBTTag.LONG.name();
		} else if (type instanceof StringHandler) {
			ending = NBTNexus.SPLITTER + NBTTag.STRING.name();
		} else if (type instanceof ConfigurationSectionHandler) {
			ending = NBTNexus.SPLITTER + NBTTag.COMPOUND.name();
		} else if (type instanceof BooleanHandler) {
			ending = NBTNexus.SPLITTER + NBTTag.BOOLEAN.name();
		} else if (type instanceof ListHandler) {
			ending = NBTNexus.SPLITTER + NBTTag.LIST.name() + NBTNexus.SPLITTER + NBTTag.OBJECT;
		} else if (type instanceof ShortHandler) {
			ending = NBTNexus.SPLITTER + NBTTag.SHORT;
		} else if (type instanceof ByteHandler) {
			ending = NBTNexus.SPLITTER + NBTTag.BYTE;
		} else if (type instanceof IntegerHandler) {
			ending = NBTNexus.SPLITTER + NBTTag.INT;
		}

		if (!key.endsWith(ending))
			return Tuple.of(key.split(NBTNexus.SPLITTER_REGEX)[0] + ending, null);
		return Tuple.of(key, null);
	}
}
