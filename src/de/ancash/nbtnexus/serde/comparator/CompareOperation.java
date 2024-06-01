package de.ancash.nbtnexus.serde.comparator;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CompareOperation {

	protected static final AtomicInteger cnt = new AtomicInteger();

	protected final AtomicBoolean failed;
	protected Object a;
	protected Object b;
	protected final CompareOperation parent;
	protected final Set<CompareOperation> children = Collections.synchronizedSet(new HashSet<>());
	protected final int id = cnt.incrementAndGet();

	public CompareOperation(Object a, Object b) {
		this(a, b, null);
	}

	public CompareOperation(Object a, Object b, CompareOperation parent) {
		this(a, b, parent, new AtomicBoolean());
	}

	private CompareOperation(Object a, Object b, CompareOperation parent, AtomicBoolean failed) {
		this.a = a;
		this.failed = failed;
		this.b = b;
		if (parent == null)
			this.parent = this;
		else
			this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> aAsMap() {
		return (Map<String, Object>) a;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> bAsMap() {
		return (Map<String, Object>) b;
	}

	@SuppressWarnings("unchecked")
	protected List<Object> aAsList() {
		return (List<Object>) a;
	}

	@SuppressWarnings("unchecked")
	protected List<Object> bAsList() {
		return (List<Object>) b;
	}

	protected boolean hasFailed() {
		return failed.get();
	}

	protected CompareOperation addChildren(Object a, Object b) {
		CompareOperation co = new CompareOperation(a, b, parent, failed);
		parent.children.add(co);
		return co;
	}

	protected void fail() {
		failed.set(true);
	}

	protected void finish() {
		if (parent != null)
			parent.children.remove(this);
	}

	@Override
	public int hashCode() {
		return id;
	}
}
