package de.ancash.nbtnexus.serde.structure;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import de.ancash.minecraft.inventory.editor.yml.handler.StringHandler;
import de.ancash.minecraft.inventory.editor.yml.suggestion.ValueSuggestion;

public class SerDeStructureValueSuggestion<T> {

	protected final Set<ValueSuggestion<T>> suggestions;

	@SafeVarargs
	public SerDeStructureValueSuggestion(ValueSuggestion<T>... suggestions) {
		this(Arrays.asList(suggestions));
	}

	public SerDeStructureValueSuggestion(Collection<ValueSuggestion<T>> suggestions) {
		this.suggestions = Collections.unmodifiableSet(new HashSet<>(suggestions));
	}

	public Set<ValueSuggestion<T>> getSuggestions() {
		return suggestions;
	}

	public static <T extends Enum<T>> SerDeStructureValueSuggestion<String> forEnum(Class<T> clazz) {
		return new SerDeStructureValueSuggestion<String>(Arrays.asList(clazz.getEnumConstants()).stream()
				.map(e -> new ValueSuggestion<>(StringHandler.INSTANCE, e.name(), e.name())).collect(Collectors.toList()));
	}

	public static <T extends Enum<T>> SerDeStructureValueSuggestion<String> forEnum(T[] enums) {
		return new SerDeStructureValueSuggestion<String>(Arrays.asList(enums).stream()
				.map(e -> new ValueSuggestion<>(StringHandler.INSTANCE, e.name(), e.name())).collect(Collectors.toList()));
	}

	public static <T extends Enum<T>> SerDeStructureValueSuggestion<String> forUUID() {
		UUID rnd = UUID.randomUUID();
		return new SerDeStructureValueSuggestion<String>(new ValueSuggestion<String>(StringHandler.INSTANCE, rnd.toString(), rnd.toString())) {
			@Override
			public Set<ValueSuggestion<String>> getSuggestions() {
				UUID u = UUID.randomUUID();
				return new HashSet<>(Arrays.asList(new ValueSuggestion<String>(StringHandler.INSTANCE, u.toString(), u.toString())));
			}
		};
	}
}
