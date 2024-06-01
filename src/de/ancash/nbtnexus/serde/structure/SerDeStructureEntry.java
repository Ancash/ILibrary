package de.ancash.nbtnexus.serde.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Keyed;
import org.bukkit.Registry;

import de.ancash.minecraft.inventory.editor.yml.handler.StringHandler;
import de.ancash.minecraft.inventory.editor.yml.suggestion.ValueSuggestion;
import de.ancash.nbtnexus.serde.ItemSerializer;

public class SerDeStructureEntry {

	public static final SerDeStructureEntry STRING = new SerDeStructureEntry(SerDeStructureKeySuggestion.STRING, null);
	public static final SerDeStructureEntry BYTE = new SerDeStructureEntry(SerDeStructureKeySuggestion.BYTE, null);
	public static final SerDeStructureEntry SHORT = new SerDeStructureEntry(SerDeStructureKeySuggestion.SHORT, null);
	public static final SerDeStructureEntry INT = new SerDeStructureEntry(SerDeStructureKeySuggestion.INT, null);
	public static final SerDeStructureEntry LONG = new SerDeStructureEntry(SerDeStructureKeySuggestion.LONG, null);
	public static final SerDeStructureEntry FLOAT = new SerDeStructureEntry(SerDeStructureKeySuggestion.FLOAT, null);
	public static final SerDeStructureEntry DOUBLE = new SerDeStructureEntry(SerDeStructureKeySuggestion.DOUBLE, null);
	public static final SerDeStructureEntry BOOLEAN = new SerDeStructureEntry(SerDeStructureKeySuggestion.BOOLEAN, null);
	public static final SerDeStructureEntry UUID = new SerDeStructureEntry(SerDeStructureKeySuggestion.UUID, SerDeStructureValueSuggestion.forUUID());

	protected final SerDeStructureKeySuggestion<?> key;
	protected final SerDeStructureValueSuggestion<?> value;

	public SerDeStructureEntry(SerDeStructureKeySuggestion<?> key, SerDeStructureValueSuggestion<?> value) {
		this.key = key;
		this.value = value;
	}

	public SerDeStructureKeySuggestion<?> getKey() {
		return key;
	}

	public SerDeStructureValueSuggestion<?> getValue() {
		return value;
	}

	public static <T extends Keyed> SerDeStructureEntry forRegistry(Registry<T> registry) {
		Iterator<T> iter = registry.iterator();
		List<ValueSuggestion<String>> suggestions = new ArrayList<ValueSuggestion<String>>();
		while (iter.hasNext()) {
			String ser = ItemSerializer.INSTANCE.serializeNamespacedKey(iter.next().getKey());
			suggestions.add(new ValueSuggestion<String>(StringHandler.INSTANCE, ser, ser));
		}
		return new SerDeStructureEntry(SerDeStructureKeySuggestion.forRegistry(registry), new SerDeStructureValueSuggestion<String>(suggestions));
	}

	public static <T extends Enum<T>> SerDeStructureEntry forEnum(Class<T> clazz) {
		return new SerDeStructureEntry(SerDeStructureKeySuggestion.forEnum(clazz), SerDeStructureValueSuggestion.forEnum(clazz));
	}
}
