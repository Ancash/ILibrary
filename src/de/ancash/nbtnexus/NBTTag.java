package de.ancash.nbtnexus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.ancash.minecraft.inventory.editor.yml.handler.BooleanHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ByteHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ConfigurationSectionHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.DoubleHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.FloatHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.IValueHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.IntegerHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ListHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.LongHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ShortHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.StringHandler;
import de.tr7zw.nbtapi.NBTType;

public enum NBTTag {

	BYTE(ByteHandler.INSTANCE, NBTType.NBTTagByte, Byte.class),
	COMPOUND(ConfigurationSectionHandler.INSTANCE, NBTType.NBTTagCompound, Map.class, HashMap.class, LinkedHashMap.class),
	DOUBLE(DoubleHandler.INSTANCE, NBTType.NBTTagDouble, Double.class), FLOAT(FloatHandler.INSTANCE, NBTType.NBTTagFloat, Float.class),
	INT(IntegerHandler.INSTANCE, NBTType.NBTTagInt, Integer.class), STRING(StringHandler.INSTANCE, NBTType.NBTTagString, String.class),
	SHORT(ShortHandler.INSTANCE, NBTType.NBTTagShort, Short.class), LONG(LongHandler.INSTANCE, NBTType.NBTTagLong, Long.class),
	BYTE_ARRAY(null, NBTType.NBTTagByteArray, Byte[].class, byte[].class), INT_ARRAY(null, NBTType.NBTTagIntArray, Integer[].class, int[].class),
	LIST(ListHandler.INSTANCE, NBTType.NBTTagList, List.class, ArrayList.class, LinkedList.class), ITEM_STACK(null, null),
	ITEM_STACK_ARRAY(null, null), ITEM_STACK_LIST(null, null), UUID(null, null), BOOLEAN(BooleanHandler.INSTANCE, null, Boolean.class),
	END(null, NBTType.NBTTagEnd), OBJECT(null, null);

	private static final Map<NBTType, NBTTag> byType = new HashMap<>();
	private static final Map<Class<?>, NBTTag> byClazz = new HashMap<>();
	private static final Map<IValueHandler<?>, NBTTag> byHandler = new HashMap<>();

	static {
		for (NBTTag val : values()) {
			if (val.type != null)
				byType.put(val.type, val);
			if (val.clazz != null && !val.clazz.isEmpty())
				val.clazz.forEach(c -> byClazz.put(c, val));
			if (val.handler != null)
				byHandler.put(val.handler, val);
		}
	}

	public static NBTTag getByNBTType(NBTType type) {
		return byType.get(type);
	}

	public static NBTTag getByClazz(Class<?> c) {
		return byClazz.get(c);
	}

	public static NBTTag getByHandler(IValueHandler<?> handler) {
		return byHandler.get(handler);
	}

	private final NBTType type;
	private final List<Class<?>> clazz;
	private final IValueHandler<?> handler;

	private NBTTag(IValueHandler<?> handler, NBTType type, Class<?>... clazz) {
		this.type = type;
		this.handler = handler;
		this.clazz = Collections.unmodifiableList(Arrays.asList(clazz));
	}

	public IValueHandler<?> getHandler() {
		return handler;
	}

	public List<Class<?>> getClazz() {
		return clazz;
	}

	public NBTType getType() {
		return type;
	}
}
