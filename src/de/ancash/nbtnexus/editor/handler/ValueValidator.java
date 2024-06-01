package de.ancash.nbtnexus.editor.handler;

import java.util.Optional;

import de.ancash.minecraft.inventory.editor.yml.AbstractInputValidator;
import de.ancash.minecraft.inventory.editor.yml.gui.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ValueEditor;
import de.ancash.nbtnexus.NBTNexus;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;
import de.ancash.nbtnexus.serde.structure.SerDeStructureEntry;

public class ValueValidator extends AbstractInputValidator<Object> {

	private final SerDeStructure structure = NBTNexus.getInstance().getStructure();

	@Override
	public boolean isOfInterest(ValueEditor<?> cur) {
		ConfigurationSectionEditor root = ValidatorUtil.getItemRoot(cur);
		if (root == null || !ValidatorUtil.isItemRoot(root))
			return false;
		String path = ValidatorUtil.getPath(root, cur);
		return structure.containsKey(path) && (structure.isEntry(path) || (structure.isList(path) && structure.getList(path).getEntry() != null));
	}

	@SuppressWarnings("nls")
	@Override
	public Optional<String> isValid(ValueEditor<Object> cur, Object arg1) {
		ConfigurationSectionEditor root = ValidatorUtil.getItemRoot(cur);
		String path = ValidatorUtil.getPath(root, cur);
		SerDeStructureEntry entry = structure.isList(path) ? structure.getList(path).getEntry() : structure.getEntry(path);
		Object cast = arg1;
		switch (entry.getKey().getType()) {
		case BYTE:
			cast = ((Number) arg1).byteValue();
			break;
		case SHORT:
			cast = ((Number) arg1).shortValue();
			break;
		case INT:
			cast = ((Number) arg1).intValue();
			break;
		case LONG:
			cast = ((Number) arg1).longValue();
			break;
		case FLOAT:
			cast = ((Number) arg1).floatValue();
			break;
		case DOUBLE:
			cast = ((Number) arg1).doubleValue();
			break;
		default:
			break;
		}
		return entry.getKey().isValid(cast) ? Optional.empty() : Optional.of("invalid: " + cast);
	}
}
