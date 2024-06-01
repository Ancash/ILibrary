package de.ancash.nbtnexus.editor.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import de.ancash.minecraft.inventory.editor.yml.IListEditorListener;
import de.ancash.minecraft.inventory.editor.yml.gui.ListEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ValueEditor;
import de.ancash.minecraft.inventory.editor.yml.handler.IValueHandler;

public class ListTypeValidator implements IListEditorListener {

	@SuppressWarnings({ "unchecked", "nls" })
	@Override
	public void onInit(ListEditor arg0) {
		if (arg0.getList().isEmpty())
			return;
		Set<IValueHandler<?>> handler = new HashSet<>();
		for (Object o : arg0.getList()) {
			for (IValueHandler<?> ivh : arg0.getHandler())
				if (ivh.isValid(o)) {
					handler.add(ivh);
					break;
				}
		}
		if (handler.size() != 1)
			throw new IllegalArgumentException(
					"list has elements of different types: " + arg0.getList().stream().map(io -> io.getClass()).collect(Collectors.toList()));
		arg0.setHandler(new ArrayList<>(handler));
	}

	@Override
	public void onInsert(ListEditor cur, IValueHandler<?> arg1) {
		if (cur.getList().size() != 1)
			return;
		ValueEditor<?> ve = ValidatorUtil.getOneBeforeItemRoot(cur);

		if (ve != null)
			ve = ve.getParent();
		else
			ve = cur;

		if (!ValidatorUtil.isItemRoot(ve))
			return;
		cur.setHandler(Arrays.asList(arg1));
	}

	@Override
	public void onDelete(ListEditor arg0, IValueHandler<?> arg1, Object arg2) {
		if (arg0.getList().isEmpty())
			arg0.setHandler(arg0.getYamlEditor().getValHandler());
	}
}
