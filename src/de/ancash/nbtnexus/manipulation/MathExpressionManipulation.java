package de.ancash.nbtnexus.manipulation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.ancash.nbtnexus.NBTNexus;
import de.ancash.nbtnexus.NBTTag;
import de.ancash.nbtnexus.serde.SerializedItem;
import de.ancash.nbtnexus.serde.access.MapAccessUtil;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import redempt.crunch.functional.EvaluationEnvironment;

public class MathExpressionManipulation implements IManipulation {

	private static final EvaluationEnvironment env = new EvaluationEnvironment();
	private static final Map<Class<?>, Function<Number, Object>> casterCache = new HashMap<>();
	private static final Map<Class<?>, Integer> primitiveOrdinal = new HashMap<>();

	static {
		env.setVariableNames(IntStream.range(1, 10).boxed().map(s -> "$" + s).collect(Collectors.toList()).toArray(String[]::new));
		env.addFunction("min", 2, (d) -> Math.min(d[0], d[1]));
		env.addFunction("max", 2, (d) -> Math.min(d[0], d[1]));
		for (int i = 2; i < 10; i++) {
			env.addFunction("avg" + i, i, d -> IntStream.range(0, d.length).boxed().mapToDouble(a -> d[0]).sum() / d.length);
			env.addFunction("max" + i, i, (d) -> Math.min(d[0], d[1]));
		}
		casterCache.put(Short.class, n -> n.shortValue());
		casterCache.put(Byte.class, n -> n.byteValue());
		casterCache.put(Integer.class, n -> n.intValue());
		casterCache.put(Long.class, n -> n.longValue());
		casterCache.put(Float.class, n -> n.floatValue());
		casterCache.put(Double.class, n -> n.doubleValue());
		primitiveOrdinal.put(Byte.class, 1);
		primitiveOrdinal.put(Short.class, 2);
		primitiveOrdinal.put(Integer.class, 3);
		primitiveOrdinal.put(Long.class, 4);
		primitiveOrdinal.put(Float.class, -1);
		primitiveOrdinal.put(Double.class, -2);
	}

	public static CompiledExpression compileExpression(String s) {
		return Crunch.compileExpression(s, env);
	}

	private final String path;
	private final CompiledExpression expression;
	private final double def;
	private final Function<Number, Object> supplier;

	public MathExpressionManipulation(String path, String expression, double def) {
		this.path = path;
		this.expression = compileExpression(expression);
		this.def = def;
		if (path.endsWith(NBTNexus.SPLITTER + NBTTag.BYTE))
			supplier = o -> o.byteValue();
		else if (path.endsWith(NBTNexus.SPLITTER + NBTTag.SHORT))
			supplier = o -> o.shortValue();
		else if (path.endsWith(NBTNexus.SPLITTER + NBTTag.INT))
			supplier = o -> o.intValue();
		else if (path.endsWith(NBTNexus.SPLITTER + NBTTag.LONG))
			supplier = o -> o.longValue();
		else if (path.endsWith(NBTNexus.SPLITTER + NBTTag.FLOAT))
			supplier = o -> o.floatValue();
		else if (path.endsWith(NBTNexus.SPLITTER + NBTTag.DOUBLE))
			supplier = o -> o.doubleValue();
		else
			supplier = null;
	}

	@Override
	public ManipulationType getType() {
		return ManipulationType.MATH_EXPRESSION;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public Optional<Object> getValue(SerializedItem s) {
		return Optional.ofNullable(MapAccessUtil.get(s.getMap(), path));
	}

	@Override
	public void manipulate(SerializedItem base, SerializedItem factor, Object... opt) {
		Optional<Object> baseOpt = getValue(base);
		Optional<Object> factorOpt = getValue(factor);

		if ((baseOpt.isPresent() && !(baseOpt.get() instanceof Number)) || (factorOpt.isPresent() && !(factorOpt.get() instanceof Number)))
			throw new IllegalArgumentException("invalid number: base=" + baseOpt.orElse(def) + " factor=" + factorOpt.orElse(def));
		Number baseVal = (Number) baseOpt.orElse(def);
		Number factorVal = (Number) factorOpt.orElse(def);

		Function<Number, Object> caster = supplier;

		if (caster == null) {
			Class<?> baseClass = baseVal.getClass();
			Class<?> factorClass = factorVal.getClass();
			if (baseOpt.isPresent() && factorOpt.isPresent()) {
				int baseClassOrdinal = primitiveOrdinal.get(baseClass);
				int factorClassOrdinal = primitiveOrdinal.get(factorClass);

				if (baseClassOrdinal == factorClassOrdinal)
					caster = casterCache.get(factorClass);
				else if (baseClassOrdinal > 0 && factorClassOrdinal > 0)
					caster = casterCache.get(primitiveOrdinal.entrySet().stream()
							.filter(e -> e.getValue() == Math.max(baseClassOrdinal, factorClassOrdinal)).findAny().get().getKey());
				else if (baseClassOrdinal < 0 && factorClassOrdinal < 0)
					caster = casterCache.get(primitiveOrdinal.entrySet().stream()
							.filter(e -> e.getValue() == Math.min(baseClassOrdinal, factorClassOrdinal)).findAny().get().getKey());
				else
					throw new IllegalStateException("incompatible primitive types " + baseClass + " <=> " + factorClass);
			}
		}
		Object calcVal = caster.apply(expression.evaluate(baseVal.doubleValue(), factorVal.doubleValue()));
		MapAccessUtil.set(base.getMap(), path, calcVal);
	}
}
