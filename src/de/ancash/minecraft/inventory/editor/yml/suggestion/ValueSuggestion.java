package de.ancash.minecraft.inventory.editor.yml.suggestion;

import de.ancash.libs.org.apache.commons.lang3.Validate;
import de.ancash.minecraft.inventory.editor.yml.handler.IValueHandler;

public class ValueSuggestion<T> {

	protected final IValueHandler<T> type;
	protected final T suggestions;
	protected final String abbr;

	@SuppressWarnings("nls")
	public ValueSuggestion(IValueHandler<T> handler, T suggestions, String abbr) {
		Validate.notNull(handler, "no type");
		this.suggestions = suggestions;
		this.type = handler;
		this.abbr = abbr;
	}

	public String getAbbreviation() {
		return abbr;
	}

	public T getSuggestion() {
		return suggestions;
	}

	public IValueHandler<?> getType() {
		return type;
	}
}
