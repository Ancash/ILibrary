package de.ancash.minecraft.inventory.editor.yml;

import java.util.Optional;

public abstract class AbstractInputValidator<T> {

	public abstract Optional<String> isValid(ValueEditor<T> editor, T t);

	@SuppressWarnings("unchecked")
	public Optional<String> isValidUnchecked(ValueEditor<?> editor, Object t) {
		return isValid((ValueEditor<T>) editor, (T) t);
	}

	public abstract boolean isOfInterest(ValueEditor<?> ve);
}
