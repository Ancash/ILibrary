package de.ancash.nbtnexus.serde.structure;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Keyed;
import org.bukkit.Registry;

import de.ancash.libs.org.apache.commons.lang3.Validate;
import de.ancash.nbtnexus.NBTTag;

public class SerDeStructureKeySuggestion<T> {

	public static final SerDeStructureKeySuggestion<Byte> BYTE = new SerDeStructureKeySuggestion<>(NBTTag.BYTE);
	public static final SerDeStructureKeySuggestion<Short> SHORT = new SerDeStructureKeySuggestion<>(NBTTag.SHORT);
	public static final SerDeStructureKeySuggestion<Integer> INT = new SerDeStructureKeySuggestion<>(NBTTag.INT);
	public static final SerDeStructureKeySuggestion<Long> LONG = new SerDeStructureKeySuggestion<>(NBTTag.LONG);
	public static final SerDeStructureKeySuggestion<Float> FLOAT = new SerDeStructureKeySuggestion<>(NBTTag.FLOAT);
	public static final SerDeStructureKeySuggestion<Double> DOUBLE = new SerDeStructureKeySuggestion<>(NBTTag.DOUBLE);
	public static final SerDeStructureKeySuggestion<String> STRING = new SerDeStructureKeySuggestion<>(NBTTag.STRING);
	public static final SerDeStructureKeySuggestion<Boolean> BOOLEAN = new SerDeStructureKeySuggestion<>(NBTTag.BOOLEAN);
	public static final SerDeStructureKeySuggestion<String> UUID = new SerDeStructureKeySuggestion<String>(NBTTag.STRING, u -> {
		try {
			return java.util.UUID.fromString(u) != null;
		} catch (Exception e) {
			return false;
		}
	});

	protected final NBTTag type;
	protected final Function<T, Boolean> validator;

	public SerDeStructureKeySuggestion(NBTTag type) {
		this(type, o -> true);
	}

	public SerDeStructureKeySuggestion(NBTTag type, Function<T, Boolean> validator) {
		Validate.notNull(type);
		Validate.notNull(validator);
		this.type = type;
		this.validator = validator;
	}

	@SuppressWarnings("unchecked")
	public boolean isValid(Object o) {
		return validator.apply((T) o);
	}

	public NBTTag getType() {
		return type;
	}

	public static <T extends Enum<T>> SerDeStructureKeySuggestion<String> forEnum(Class<T> clazz) {
		return new SerDeStructureKeySuggestion<String>(NBTTag.STRING, e -> {
			try {
				return Enum.valueOf(clazz, e) != null;
			} catch (IllegalArgumentException ex) {
				return false;
			}
		});
	}
	
	public static <T extends Enum<T>> SerDeStructureKeySuggestion<String> forStringCollection(Collection<String> col) {
		return new SerDeStructureKeySuggestion<String>(NBTTag.STRING, col::contains);
	}

	public static <T extends Keyed> SerDeStructureKeySuggestion<String> forRegistry(Registry<T> registry) {
		return new SerDeStructureKeySuggestion<String>(NBTTag.STRING, s -> registry.match(s) != null);
	}

	@SuppressWarnings("nls")
	public static String[] splitArray(Object[] o) {
		return Arrays.asList(o).stream().collect(Collectors.toList()).toString().replaceAll("(.{1,150})\\s+", "$1\n").split("\n");
	}
}
