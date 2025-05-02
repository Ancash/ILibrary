package de.ancash.nbtnexus.manipulation;

import static de.ancash.nbtnexus.serde.access.MapAccessUtil.get;
import static de.ancash.nbtnexus.serde.access.MapAccessUtil.remove;
import static de.ancash.nbtnexus.serde.access.MapAccessUtil.set;

import java.util.Optional;

import de.ancash.nbtnexus.serde.SerializedItem;

public class ReplaceManipulation implements IManipulation {

	private final String path;
	private final Object def;

	public ReplaceManipulation(String path, Object def) {
		this.path = path;
		this.def = def;
	}

	@Override
	public ManipulationType getType() {
		return ManipulationType.REPLACE;
	}

	@Override
	public void manipulate(SerializedItem base, SerializedItem factor, Object... opts) {
		Optional<Object> val = getValue(factor);
		if (!val.isPresent())
			if (def == null)
				remove(base.getMap(), path);
			else
				set(base.getMap(), path, def);
		else
			set(base.getMap(), path, val.get());
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public Optional<Object> getValue(SerializedItem s) {
		return Optional.ofNullable(get(s.getMap(), path));
	}
}