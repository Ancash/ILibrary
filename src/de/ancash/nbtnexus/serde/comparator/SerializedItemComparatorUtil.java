package de.ancash.nbtnexus.serde.comparator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import de.ancash.libs.org.apache.commons.lang3.Validate;
import de.ancash.nbtnexus.NBTNexusItem;

public class SerializedItemComparatorUtil {

//	private static final MultiConsumerDisruptor<CompareEvent> mcd = new MultiConsumerDisruptor<>(CompareEvent::new,
//			1024, ProducerType.MULTI, new BlockingWaitStrategy(),
//			IntStream.range(0, 3).boxed().map(i -> new CompareEventHandler()).toArray(CompareEventHandler[]::new));
//
//	public static boolean compareMap(Map<String, Object> a, Map<String, Object> b, Set<String> ignoredKeys,
//			Set<String> ignoredOrder, String relativePath) {
//		CompareOperation co = new CompareOperation(a, b);
//		compareMap(co, ignoredKeys, ignoredOrder, relativePath);
//		while (!co.children.isEmpty() && !co.hasFailed()) {
//			LockSupport.parkNanos(1000);
//		}
//		return !co.hasFailed();
//	}
//
//	@SuppressWarnings("nls")
//	private static boolean compareMap(CompareOperation cur, Set<String> ignoredKeys, Set<String> ignoredOrder,
//			String relativePath) {
//		if (cur.hasFailed())
//			return false;
//		Validate.notNull(cur.a);
//		Validate.notNull(cur.b);
//		Validate.notNull(ignoredKeys);
//		Validate.notNull(ignoredOrder);
//		Validate.notNull(relativePath);
//		Validate.isTrue(!ignoredKeys.contains(NBTNexusItem.NBT_NEXUS_ITEM_PROPERTIES_TAG));
//
//		if (cur.aAsMap().containsKey(NBTNexusItem.NBT_NEXUS_ITEM_PROPERTIES_TAG)
//				|| cur.bAsMap().containsKey(NBTNexusItem.NBT_NEXUS_ITEM_PROPERTIES_TAG))
//			relativePath = "";
//
//		Set<String> keys = new HashSet<>();
//		keys.addAll(cur.aAsMap().keySet());
//		keys.addAll(cur.bAsMap().keySet());
//		for (String key : keys) {
//			String curp = null;
//			if (relativePath.isEmpty())
//				curp = key;
//			else
//				curp = String.join(".", relativePath, key);
//			if (ignoredKeys.contains(curp))
//				continue;
//			if (cur.aAsMap().containsKey(key) != cur.bAsMap().containsKey(key)) {
//				cur.fail();
//				return false;
//			}
//			String temp = curp;
//			CompareOperation child = cur.addChildren(cur.aAsMap().get(key), cur.bAsMap().get(key));
//			mcd.publishEvent((e, sq) -> {
//				e.mcd = mcd;
//				e.r = () -> compareObjects(child, ignoredKeys, ignoredOrder, temp);
//			});
//		}
//		return true;
//	}
//
//	private static boolean compareObjects(CompareOperation cur, Set<String> ignoredKeys, Set<String> ignoredOrder,
//			String relativePath) {
//		if (cur.hasFailed())
//			return false;
//		if ((cur.a != null) != (cur.b != null)) {
//			cur.fail();
//			return false;
//		}
//		if (cur.a == null) {
//			cur.finish();
//			return true;
//		}
//		Validate.notNull(ignoredKeys);
//		Validate.notNull(ignoredOrder);
//		Validate.notNull(relativePath);
//
//		if (cur.a instanceof Map != cur.b instanceof Map || cur.a instanceof List != cur.b instanceof List
//				|| cur.a.getClass().isArray() != cur.b.getClass().isArray()) {
//			cur.fail();
//			return false;
//		}
//		boolean b = true;
//		if (cur.a instanceof Map) {
//			b = compareMap(cur, ignoredKeys, ignoredOrder, relativePath);
//		} else if (cur.a instanceof List) {
//			b = compareList(cur, ignoredKeys, ignoredOrder, relativePath);
//		} else if (cur.a.getClass().isArray()) {
//			b = comparePrimitiveArrays(cur, ignoredKeys, ignoredOrder, relativePath);
//		} else if (!cur.a.equals(cur.b)) {
//			cur.fail();
//			b = false;
//		}
//		cur.finish();
//		return b;
//	}
//
//	private static boolean comparePrimitiveArrays(CompareOperation cur, Set<String> ignoredKeys,
//			Set<String> ignoredOrder, String relativePath) {
//		if (cur.hasFailed())
//			return false;
//		Validate.notNull(cur.a);
//		Validate.notNull(cur.b);
//		Validate.notNull(ignoredKeys);
//		Validate.notNull(ignoredOrder);
//		Validate.notNull(relativePath);
//		CompareOperation child = cur.addChildren(
//				Arrays.asList(
//						IntStream.range(0, Array.getLength(cur.a)).boxed().map(i -> Array.get(cur.a, i)).toArray()),
//				Arrays.asList(
//						IntStream.range(0, Array.getLength(cur.b)).boxed().map(i -> Array.get(cur.b, i)).toArray()));
//		mcd.publishEvent((e, seq) -> {
//			e.mcd = mcd;
//			e.r = () -> compareList(child,
//					ignoredKeys, ignoredOrder, relativePath);
//		});
//		cur.finish();
//		return true;
//	}
//
//	private static boolean compareList(CompareOperation cur, Set<String> ignoredKeys, Set<String> ignoredOrder,
//			String relativePath) {
//		if (cur.hasFailed())
//			return false;
//		Validate.notNull(ignoredKeys);
//		Validate.notNull(ignoredOrder);
//		Validate.notNull(relativePath);
//
//		if (cur.aAsList().size() != cur.bAsList().size()) {
//			cur.fail();
//			return false;
//		}
//
//		boolean ignoreOrder = ignoredOrder.contains(relativePath);
//		cur.a = new ArrayList<>(cur.aAsList());
//		cur.b = new ArrayList<>(cur.bAsList());
//
//		Iterator<Object> itera = cur.aAsList().iterator();
//		while (itera.hasNext()) {
//			if (cur.hasFailed())
//				return false;
//			Object oa = itera.next();
//			Iterator<Object> iterb = cur.bAsList().iterator();
//			while (iterb.hasNext()) {
//				Object ob = iterb.next();
//				if (compareObjects(cur.addChildren(oa, ob), ignoredKeys, ignoredOrder, relativePath)) {
//					iterb.remove();
//					break;
//				}
//				if (!ignoreOrder)
//					return false;
//			}
//			if (cur.aAsList().size() == cur.bAsList().size())
//				return false;
//			itera.remove();
//		}
//		cur.finish();
//		return true;
//	}

	@SuppressWarnings("nls")
	public static boolean compareMap(Map<String, Object> a, Map<String, Object> b, Set<String> ignoredKeys, Set<String> ignoredOrder,
			String relativePath) {
		Validate.notNull(a);
		Validate.notNull(b);
		Validate.notNull(ignoredKeys);
		Validate.notNull(ignoredOrder);
		Validate.notNull(relativePath);
		Validate.isTrue(!ignoredKeys.contains(NBTNexusItem.NBT_NEXUS_ITEM_PROPERTIES_TAG));

		if (a.containsKey(NBTNexusItem.NBT_NEXUS_ITEM_PROPERTIES_TAG) || b.containsKey(NBTNexusItem.NBT_NEXUS_ITEM_PROPERTIES_TAG))
			relativePath = "";

		Set<String> keys = new HashSet<>();
		keys.addAll(a.keySet());
		keys.addAll(b.keySet());
		for (String key : keys) {
			String curp = null;
			if (relativePath.isEmpty())
				curp = key;
			else
				curp = String.join(".", relativePath, key);
			if (ignoredKeys.contains(curp))
				continue;
			if (a.containsKey(key) != b.containsKey(key))
				return false;
			if (!compareObjects(a.get(key), b.get(key), ignoredKeys, ignoredOrder, curp))
				return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static boolean compareObjects(Object oa, Object ob, Set<String> ignoredKeys, Set<String> ignoredOrder, String relativePath) {
		if ((oa != null) != (ob != null))
			return false;
		if (oa == null)
			return true;
		Validate.notNull(ignoredKeys);
		Validate.notNull(ignoredOrder);
		Validate.notNull(relativePath);

		if (oa instanceof Map != ob instanceof Map)
			return false;

		if (oa instanceof List != ob instanceof List)
			return false;

		if (oa.getClass().isArray() != ob.getClass().isArray())
			return false;

		if (oa instanceof Map)
			return compareMap((Map<String, Object>) oa, (Map<String, Object>) ob, ignoredKeys, ignoredOrder, relativePath);

		if (oa instanceof List)
			return compareList((List<Object>) oa, (List<Object>) ob, ignoredKeys, ignoredOrder, relativePath);

		if (oa.getClass().isArray())
			return compareArrays(oa, ob, ignoredKeys, ignoredOrder, relativePath);

		return oa.equals(ob);
	}

	public static boolean compareArrays(Object oa, Object ob, Set<String> ignoredKeys, Set<String> ignoredOrder, String relativePath) {
		Validate.notNull(oa);
		Validate.notNull(ob);
		Validate.notNull(ignoredKeys);
		Validate.notNull(ignoredOrder);
		Validate.notNull(relativePath);
		return compareList(Arrays.asList(IntStream.range(0, Array.getLength(oa)).boxed().map(i -> Array.get(oa, i)).toArray()),
				Arrays.asList(IntStream.range(0, Array.getLength(ob)).boxed().map(i -> Array.get(ob, i)).toArray()), ignoredKeys, ignoredOrder,
				relativePath);
	}

	public static boolean compareList(List<Object> a, List<Object> b, Set<String> ignoredKeys, Set<String> ignoredOrder, String relativePath) {
		Validate.notNull(ignoredKeys);
		Validate.notNull(ignoredOrder);
		Validate.notNull(relativePath);

		if (a.size() != b.size())
			return false;

		boolean ignoreOrder = ignoredOrder.contains(relativePath);
		a = new ArrayList<>(a);
		b = new ArrayList<>(b);

		Iterator<Object> itera = a.iterator();
		while (itera.hasNext()) {
			Object oa = itera.next();
			Iterator<Object> iterb = b.iterator();
			while (iterb.hasNext()) {
				Object ob = iterb.next();
				if (compareObjects(oa, ob, ignoredKeys, ignoredOrder, relativePath)) {
					iterb.remove();
					break;
				}
				if (!ignoreOrder)
					return false;
			}
			if (a.size() == b.size())
				return false;
			itera.remove();
		}
		return true;
	}

}
