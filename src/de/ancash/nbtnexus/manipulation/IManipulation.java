package de.ancash.nbtnexus.manipulation;

import java.util.Optional;

import de.ancash.nbtnexus.serde.SerializedItem;

public interface IManipulation {

	public ManipulationType getType();

	public String getPath();

	public Optional<Object> getValue(SerializedItem s);

	public void manipulate(SerializedItem base, SerializedItem factor, Object... opts);
}
