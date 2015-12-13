package com.ca.garbage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import junit.framework.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class FastNavIntSetTest {
	/**
	 * The number of elements to place in collections, arrays, etc.
	 */
	static final int SIZE = 20;

	/**
	 * Returns a new set of given size containing consecutive Integers 0 ... n.
	 */
	private FastNavIntSet populatedSet(int n) {
		FastNavIntSet q = FastNavIntSet.create();
		assertTrue(q.isEmpty());
		for (int i = n - 1; i >= 0; i -= 2) {
			assertTrue(q.add(new Integer(i)));
		}
		for (int i = (n & 1); i < n; i += 2) {
			assertTrue(q.add(new Integer(i)));
		}
		assertFalse(q.isEmpty());
		assertEquals(n, q.size());
		return q;
	}

	/**
	 * Returns a new set of first 5 ints.
	 */
	private FastNavIntSet set5() {
		FastNavIntSet q = FastNavIntSet.create(1, 2, 3, 4, 5);
		assertEquals(5, q.size());
		return q;
	}

	/**
	 * A new set has unbounded capacity
	 */
	@Test
	public void testConstructor1() {
		assertEquals(0, FastNavIntSet.create().size());
	}

	/**
	 * Initializing from null Collection throws NPE
	 */
	@Test(expected = NullPointerException.class)
	public void testConstructor3() {
		FastNavIntSet.fromCollection((Collection) null);
	}

	/**
	 * Initializing from Collection of null elements throws NPE
	 */
	@Test(expected = NullPointerException.class)
	public void testConstructor4() {
		FastNavIntSet.fromCollection(Arrays.asList(new Integer[SIZE]));
	}

	/**
	 * Initializing from Collection with some null elements throws NPE
	 */
	@Test(expected = NullPointerException.class)
	public void testConstructor5() {
		Integer[] ints = new Integer[SIZE];
		for (int i = 0; i < SIZE - 1; ++i) {
			ints[i] = i;
		}
		FastNavIntSet.fromCollection(Arrays.asList(ints));
	}

	/**
	 * Set contains all elements of collection used to initialize
	 */
	@Test
	public void testConstructor6() {
		int[] ints = new int[SIZE];
		for (int i = 0; i < SIZE; ++i) {
			ints[i] = i;
		}
		FastNavIntSet q = FastNavIntSet.create(ints);
		for (int i = 0; i < SIZE; ++i) {
			assertEquals((Integer) ints[i], q.pollFirst());
		}
	}

	/**
	 * The comparator used in constructor is used
	 */
	@Test
	public void testConstructor7() {
		FastNavIntSet q = FastNavIntSet.create(false);
		assertEquals(FastNavIntSet.REVERSE, q.comparator());
		Integer[] ints = new Integer[SIZE];
		for (int i = 0; i < SIZE; ++i) {
			ints[i] = i;
		}
		q.addAll(Arrays.asList(ints));
		for (int i = SIZE - 1; i >= 0; --i) {
			assertEquals(ints[i], q.pollFirst());
		}
	}

	/**
	 * isEmpty is true before add, false after
	 */
	@Test
	public void testEmpty() {
		FastNavIntSet q = FastNavIntSet.create();
		assertTrue(q.isEmpty());
		q.add(new Integer(1));
		assertFalse(q.isEmpty());
		q.add(new Integer(2));
		q.pollFirst();
		q.pollFirst();
		assertTrue(q.isEmpty());
	}

	/**
	 * size changes when elements added and removed
	 */
	@Test
	public void testSize() {
		FastNavIntSet q = populatedSet(SIZE);
		for (int i = 0; i < SIZE; ++i) {
			assertEquals(SIZE - i, q.size());
			q.pollFirst();
		}
		for (int i = 0; i < SIZE; ++i) {
			assertEquals(i, q.size());
			q.add(new Integer(i));
		}
	}

	/**
	 * add(null) throws NPE if nonempty
	 */
	@Test(expected = NullPointerException.class)
	public void testAddNull() {
		FastNavIntSet q = populatedSet(SIZE);
		q.add(null);
	}

	/**
	 * Add of comparable element fails
	 */
	@Test(expected = ClassCastException.class)
	public void testAdd() {
		NavigableSet q = FastNavIntSet.create();
		assertTrue(q.add(new Long(1)));
	}

	/**
	 * Add of duplicate element fails
	 */
	@Test
	public void testAddDup() {
		FastNavIntSet q = FastNavIntSet.create();
		assertTrue(q.add(0));
		assertFalse(q.add(0));
	}

	/**
	 * addAll(null) throws NPE
	 */
	@Test(expected = NullPointerException.class)
	public void testAddAll1() {
		NavigableSet<Integer> q = FastNavIntSet.create();
		q.addAll(null);
	}

	/**
	 * addAll of a collection with null elements throws NPE
	 */
	@Test(expected = NullPointerException.class)
	public void testAddAll2() {
		FastNavIntSet q = FastNavIntSet.create();
		Integer[] ints = new Integer[SIZE];
		q.addAll(Arrays.asList(ints));
	}

	/**
	 * addAll of a collection with any null elements throws NPE after possibly adding some elements
	 */
	@Test(expected = NullPointerException.class)
	public void testAddAll3() {
		FastNavIntSet q = FastNavIntSet.create();
		Integer[] ints = new Integer[SIZE];
		for (int i = 0; i < SIZE - 1; ++i) {
			ints[i] = new Integer(i);
		}
		q.addAll(Arrays.asList(ints));
	}

	/**
	 * Set contains all elements of successful addAll
	 */
	@Test
	public void testAddAll5() {
		Integer[] empty = new Integer[0];
		Integer[] ints = new Integer[SIZE];
		for (int i = 0; i < SIZE; ++i) {
			ints[i] = new Integer(SIZE - 1 - i);
		}
		FastNavIntSet q = FastNavIntSet.create();
		assertFalse(q.addAll(Arrays.asList(empty)));
		assertTrue(q.addAll(Arrays.asList(ints)));
		for (int i = 0; i < SIZE; ++i) {
			assertEquals(new Integer(i), q.pollFirst());
		}
	}

	/**
	 * pollFirst succeeds unless empty
	 */
	@Test
	public void testPollFirst() {
		FastNavIntSet q = populatedSet(SIZE);
		for (int i = 0; i < SIZE; ++i) {
			assertEquals((Integer) i, q.pollFirst());
		}
		assertNull(q.pollFirst());
	}

	/**
	 * pollLast succeeds unless empty
	 */
	@Test
	public void testPollLast() {
		FastNavIntSet q = populatedSet(SIZE);
		for (int i = SIZE - 1; i >= 0; --i) {
			assertEquals((Integer) i, q.pollLast());
		}
		assertNull(q.pollFirst());
	}

	/**
	 * remove(x) removes x and returns true if present
	 */
	@Test
	public void testRemoveElement() {
		FastNavIntSet q = populatedSet(SIZE);
		for (int i = 1; i < SIZE; i += 2) {
			assertTrue(q.contains(i));
			assertTrue(q.remove(i));
			assertFalse(q.contains(i));
			assertTrue(q.contains(i - 1));
		}
		for (int i = 0; i < SIZE; i += 2) {
			assertTrue(q.contains(i));
			assertTrue(q.remove(i));
			assertFalse(q.contains(i));
			assertFalse(q.remove(i + 1));
			assertFalse(q.contains(i + 1));
		}
		assertTrue(q.isEmpty());
	}

	/**
	 * contains(x) reports true when elements added but not yet removed
	 */
	@Test
	public void testContains() {
		FastNavIntSet q = populatedSet(SIZE);
		for (int i = 0; i < SIZE; ++i) {
			assertTrue(q.contains(i));
			q.pollFirst();
			assertFalse(q.contains(i));
		}
	}

	/**
	 * clear removes all elements
	 */
	@Test
	public void testClear() {
		FastNavIntSet q = populatedSet(SIZE);
		q.clear();
		assertTrue(q.isEmpty());
		assertEquals(0, q.size());
		q.add(1);
		assertFalse(q.isEmpty());
		q.clear();
		assertTrue(q.isEmpty());
	}

	/**
	 * containsAll(c) is true when c contains a subset of elements
	 */
	@Test
	public void testContainsAll() {
		FastNavIntSet q = populatedSet(SIZE);
		FastNavIntSet p = FastNavIntSet.create();
		for (int i = 0; i < SIZE; ++i) {
			assertTrue(q.containsAll(p));
			assertFalse(p.containsAll(q));
			p.add(new Integer(i));
		}
		assertTrue(p.containsAll(q));
	}

	/**
	 * retainAll(c) retains only those elements of c and reports true if changed
	 */
	@Test
	public void testRetainAll() {
		FastNavIntSet q = populatedSet(SIZE);
		FastNavIntSet p = populatedSet(SIZE);
		for (int i = 0; i < SIZE; ++i) {
			boolean changed = q.retainAll(p);
			if (i == 0) {
				assertFalse(changed);
			}
			else {
				assertTrue(changed);
			}

			assertTrue(q.containsAll(p));
			assertEquals(SIZE - i, q.size());
			p.pollFirst();
		}
	}

	/**
	 * removeAll(c) removes only those elements of c and reports true if changed
	 */
	@Test
	public void testRemoveAll() {
		for (int i = 1; i < SIZE; ++i) {
			FastNavIntSet q = populatedSet(SIZE);
			FastNavIntSet p = populatedSet(i);
			assertTrue(q.removeAll(p));
			assertEquals(SIZE - i, q.size());
			for (int j = 0; j < i; ++j) {
				Integer x = (Integer) (p.pollFirst());
				assertFalse(q.contains(x));
			}
		}
	}

	/**
	 * lower returns preceding element
	 */
	@Test
	public void testLower() {
		FastNavIntSet q = set5();
		Object e1 = q.lower(3);
		assertEquals(2, e1);

		Object e2 = q.lower(6);
		assertEquals(5, e2);

		Object e3 = q.lower(1);
		assertNull(e3);

		Object e4 = q.lower(0);
		assertNull(e4);
	}

	/**
	 * higher returns next element
	 */
	@Test
	public void testHigher() {
		FastNavIntSet q = set5();
		Object e1 = q.higher(3);
		assertEquals(4, e1);

		Object e2 = q.higher(0);
		assertEquals(1, e2);

		Object e3 = q.higher(5);
		assertNull(e3);

		Object e4 = q.higher(6);
		assertNull(e4);
	}

	/**
	 * floor returns preceding element
	 */
	@Test
	public void testFloor() {
		FastNavIntSet q = set5();
		Object e1 = q.floor(3);
		assertEquals(3, e1);

		Object e2 = q.floor(6);
		assertEquals(5, e2);

		Object e3 = q.floor(1);
		assertEquals(1, e3);

		Object e4 = q.floor(0);
		assertNull(e4);
	}

	/**
	 * ceiling returns next element
	 */
	@Test
	public void testCeiling() {
		FastNavIntSet q = set5();
		Object e1 = q.ceiling(3);
		assertEquals(3, e1);

		Object e2 = q.ceiling(0);
		assertEquals(1, e2);

		Object e3 = q.ceiling(5);
		assertEquals(5, e3);

		Object e4 = q.ceiling(6);
		assertNull(e4);
	}

	/**
	 * toArray contains all elements in sorted order
	 */
	@Test
	public void testToArray() {
		FastNavIntSet q = populatedSet(SIZE);
		Object[] o = q.toArray();
		for (int i = 0; i < o.length; i++) {
			assertSame(o[i], q.pollFirst());
		}
	}

	/**
	 * toArray(a) contains all elements in sorted order
	 */
	@Test
	public void testToArray2() {
		FastNavIntSet q = populatedSet(SIZE);
		Integer[] ints = new Integer[SIZE];
		Integer[] array = q.toArray(ints);
		assertSame(ints, array);
		for (int i = 0; i < ints.length; i++) {
			assertSame(ints[i], q.pollFirst());
		}
	}

	/**
	 * iterator iterates through all elements
	 */
	@Test
	public void testIterator() {
		FastNavIntSet q = populatedSet(SIZE);
		Iterator it = q.iterator();
		int i;
		for (i = 0; it.hasNext(); i++) {
			assertTrue(q.contains(it.next()));
		}
		assertEquals(i, SIZE);
		assertFalse(it.hasNext());
	}

	/**
	 * iterator iterates through all elements
	 */
	@Test
	public void testDescendingIterator() {
		FastNavIntSet q = populatedSet(SIZE);
		Iterator<Integer> it = q.descendingIterator();
		int i;
		int lastValue = Integer.MAX_VALUE;
		for (i = 0; it.hasNext(); i++) {
			int nextValue = it.next();
			assertTrue(String.format("%d > %d", lastValue, nextValue), lastValue > nextValue);
			lastValue = nextValue;
			assertTrue(q.contains(nextValue));
		}
		assertEquals(i, SIZE);
		assertFalse(it.hasNext());
	}

	/**
	 * iterator of empty set has no elements
	 */
	@Test
	public void testEmptyIterator() {
		assertFalse(FastNavIntSet.create().iterator().hasNext());
	}

	/**
	 * iterator.remove removes current element
	 */
	@Test
	public void testIteratorRemove() {
		final FastNavIntSet q = FastNavIntSet.create();
		q.add(new Integer(2));
		q.add(new Integer(1));
		q.add(new Integer(3));

		Iterator it = q.iterator();
		it.next();
		it.remove();

		it = q.iterator();
		assertEquals(it.next(), new Integer(2));
		assertEquals(it.next(), new Integer(3));
		assertFalse(it.hasNext());
	}

	/**
	 * toString contains toStrings of elements
	 */
	@Test
	public void testToString() {
		FastNavIntSet q = populatedSet(SIZE);
		String s = q.toString();
		for (int i = 0; i < SIZE; ++i) {
			assertTrue(s.contains(String.valueOf(i)));
		}
	}

	/**
	 * A deserialized serialized set has same elements
	 */
	@Test
	public void testSerialization() throws Exception {
		NavigableSet x = populatedSet(SIZE);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oout = new ObjectOutputStream(out);
		oout.writeObject(x);

		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
		NavigableSet y = (NavigableSet) in.readObject();

		assertNotSame(x, y);
		assertEquals(x.size(), y.size());
		assertEquals(x, y);
		assertEquals(y, x);
		while (!x.isEmpty()) {
			assertFalse(y.isEmpty());
			assertEquals(x.pollFirst(), y.pollFirst());
		}
		assertTrue(y.isEmpty());
	}

	/**
	 * subSet returns set with keys in requested range
	 */
	@Test
	public void testSubSetContents() {
		FastNavIntSet set = set5();
		SortedSet sm = set.subSet(2, 4);
		assertEquals(2, sm.first());
		assertEquals(3, sm.last());
		assertEquals(2, sm.size());
		assertFalse(sm.contains(1));
		assertTrue(sm.contains(2));
		assertTrue(sm.contains(3));
		assertFalse(sm.contains(4));
		assertFalse(sm.contains(5));
		Iterator i = sm.iterator();
		Object k;
		k = (Integer) (i.next());
		assertEquals(2, k);
		k = (Integer) (i.next());
		assertEquals(3, k);
		assertFalse(i.hasNext());
		Iterator j = sm.iterator();
		j.next();
		j.remove();
		assertFalse(set.contains(2));
		assertEquals(4, set.size());
		assertEquals(1, sm.size());
		assertEquals(3, sm.first());
		assertEquals(3, sm.last());
		assertTrue(sm.remove(3));
		assertTrue(sm.isEmpty());
		assertEquals(3, set.size());
	}

	@Test
	public void testSubSetContents2() {
		FastNavIntSet set = set5();
		SortedSet sm = set.subSet(2, 3);
		assertEquals(1, sm.size());
		assertEquals(2, sm.first());
		assertEquals(2, sm.last());
		assertFalse(sm.contains(1));
		assertTrue(sm.contains(2));
		assertFalse(sm.contains(3));
		assertFalse(sm.contains(4));
		assertFalse(sm.contains(5));
		Iterator i = sm.iterator();
		Object k;
		k = (Integer) (i.next());
		assertEquals(2, k);
		assertFalse(i.hasNext());
		Iterator j = sm.iterator();
		j.next();
		j.remove();
		assertFalse(set.contains(2));
		assertEquals(4, set.size());
		assertEquals(0, sm.size());
		assertTrue(sm.isEmpty());
		assertFalse(sm.remove(3));
		assertEquals(4, set.size());
	}

	/**
	 * headSet returns set with keys in requested range
	 */
	@Test
	public void testHeadSetContents() {
		FastNavIntSet set = set5();
		SortedSet sm = set.headSet(4);
		assertTrue(sm.contains(1));
		assertTrue(sm.contains(2));
		assertTrue(sm.contains(3));
		assertFalse(sm.contains(4));
		assertFalse(sm.contains(5));
		Iterator i = sm.iterator();
		Object k;
		k = (Integer) (i.next());
		assertEquals(1, k);
		k = (Integer) (i.next());
		assertEquals(2, k);
		k = (Integer) (i.next());
		assertEquals(3, k);
		assertFalse(i.hasNext());
		sm.clear();
		assertTrue(sm.isEmpty());
		assertEquals(2, set.size());
		assertEquals((Object) 4, set.first());
	}

	/**
	 * tailSet returns set with keys in requested range
	 */
	@Test
	public void testTailSetContents() {
		FastNavIntSet set = set5();
		SortedSet sm = set.tailSet(2);
		assertFalse(sm.contains(1));
		assertTrue(sm.contains(2));
		assertTrue(sm.contains(3));
		assertTrue(sm.contains(4));
		assertTrue(sm.contains(5));
		Iterator i = sm.iterator();
		Object k;
		k = (Integer) (i.next());
		assertEquals(2, k);
		k = (Integer) (i.next());
		assertEquals(3, k);
		k = (Integer) (i.next());
		assertEquals(4, k);
		k = (Integer) (i.next());
		assertEquals(5, k);
		assertFalse(i.hasNext());

		SortedSet ssm = sm.tailSet(4);
		assertEquals(4, ssm.first());
		assertEquals(5, ssm.last());
		assertTrue(ssm.remove(4));
		assertEquals(1, ssm.size());
		assertEquals(3, sm.size());
		assertEquals(4, set.size());
	}

	Random rnd = new Random(666);
	BitSet bs;

	/**
	 * Subsets of subsets subdivide correctly
	 */
	@Test
	public void testRecursiveSubSets() throws Exception {
		int setSize = 100;

		NavigableSet<Integer> set = FastNavIntSet.create();
		bs = new BitSet(setSize);

		populate(set, setSize);
		check(set, 0, setSize - 1, true);
		check(set.descendingSet(), 0, setSize - 1, false);

		mutateSet(set, 0, setSize - 1);
		check(set, 0, setSize - 1, true);
		check(set.descendingSet(), 0, setSize - 1, false);

		bashSubSet(set.subSet(0, true, setSize, false),
							 0, setSize - 1, true);
	}

	/**
	 * addAll is idempotent
	 */
	@Test
	public void testAddAll_idempotent() throws Exception {
		Set x = populatedSet(SIZE);
		Set y = FastNavIntSet.fromCollection(x);
		y.addAll(x);
		assertEquals(x, y);
		assertEquals(y, x);
	}

	void populate(NavigableSet<Integer> set, int limit) {
		for (int i = 0, n = 2 * limit / 3; i < n; i++) {
			int element = rnd.nextInt(limit);
			put(set, element);
		}
	}

	void mutateSet(NavigableSet<Integer> set, int min, int max) {
		int size = set.size();
		int rangeSize = max - min + 1;

		// Remove a bunch of entries directly
		for (int i = 0, n = rangeSize / 2; i < n; i++) {
			remove(set, min - 5 + rnd.nextInt(rangeSize + 10));
		}

		// Remove a bunch of entries with iterator
		for (Iterator<Integer> it = set.iterator(); it.hasNext();) {
			if (rnd.nextBoolean()) {
				bs.clear(it.next());
				it.remove();
			}
		}

		// Add entries till we're back to original size
		while (set.size() < size) {
			int element = min + rnd.nextInt(rangeSize);
			assertTrue(element >= min && element <= max);
			put(set, element);
		}
	}

	void mutateSubSet(NavigableSet<Integer> set, int min, int max) {
		int size = set.size();
		int rangeSize = max - min + 1;

		// Remove a bunch of entries directly
		for (int i = 0, n = rangeSize / 2; i < n; i++) {
			remove(set, min - 5 + rnd.nextInt(rangeSize + 10));
		}

		// Remove a bunch of entries with iterator
		for (Iterator<Integer> it = set.iterator(); it.hasNext();) {
			if (rnd.nextBoolean()) {
				bs.clear(it.next());
				it.remove();
			}
		}

		// Add entries till we're back to original size
		while (set.size() < size) {
			int element = min - 5 + rnd.nextInt(rangeSize + 10);
			if (element >= min && element <= max) {
				put(set, element);
			}
			else {
				try {
					set.add(element);
					Assert.fail();
				}
				catch (IllegalArgumentException success) {
				}
			}
		}
	}

	void put(NavigableSet<Integer> set, int element) {
		if (set.add(element)) {
			bs.set(element);
		}
	}

	void remove(NavigableSet<Integer> set, int element) {
		if (set.remove(element)) {
			bs.clear(element);
		}
	}

	void bashSubSet(NavigableSet<Integer> set,
									int min, int max, boolean ascending) {
		check(set, min, max, ascending);
		check(set.descendingSet(), min, max, !ascending);

		mutateSubSet(set, min, max);
		check(set, min, max, ascending);
		check(set.descendingSet(), min, max, !ascending);

		// Recurse
		if (max - min < 2) {
			return;
		}
		int midPoint = (min + max) / 2;

		// headSet - pick direction and endpoint inclusion randomly
		boolean incl = rnd.nextBoolean();
		NavigableSet<Integer> hm = set.headSet(midPoint, incl);
		if (ascending) {
			if (rnd.nextBoolean()) {
				bashSubSet(hm, min, midPoint - (incl ? 0 : 1), true);
			}
			else {
				bashSubSet(hm.descendingSet(), min, midPoint - (incl ? 0 : 1),
									 false);
			}
		}
		else if (rnd.nextBoolean()) {
			bashSubSet(hm, midPoint + (incl ? 0 : 1), max, false);
		}
		else {
			bashSubSet(hm.descendingSet(), midPoint + (incl ? 0 : 1), max,
								 true);
		}

		// tailSet - pick direction and endpoint inclusion randomly
		incl = rnd.nextBoolean();
		NavigableSet<Integer> tm = set.tailSet(midPoint, incl);
		if (ascending) {
			if (rnd.nextBoolean()) {
				bashSubSet(tm, midPoint + (incl ? 0 : 1), max, true);
			}
			else {
				bashSubSet(tm.descendingSet(), midPoint + (incl ? 0 : 1), max,
									 false);
			}
		}
		else if (rnd.nextBoolean()) {
			bashSubSet(tm, min, midPoint - (incl ? 0 : 1), false);
		}
		else {
			bashSubSet(tm.descendingSet(), min, midPoint - (incl ? 0 : 1),
								 true);
		}

		// subSet - pick direction and endpoint inclusion randomly
		int rangeSize = max - min + 1;
		int[] endpoints = new int[2];
		endpoints[0] = min + rnd.nextInt(rangeSize);
		endpoints[1] = min + rnd.nextInt(rangeSize);
		Arrays.sort(endpoints);
		boolean lowIncl = rnd.nextBoolean();
		boolean highIncl = rnd.nextBoolean();
		if (ascending) {
			NavigableSet<Integer> sm = set.subSet(
				endpoints[0], lowIncl, endpoints[1], highIncl);
			if (rnd.nextBoolean()) {
				bashSubSet(sm, endpoints[0] + (lowIncl ? 0 : 1),
									 endpoints[1] - (highIncl ? 0 : 1), true);
			}
			else {
				bashSubSet(sm.descendingSet(), endpoints[0] + (lowIncl ? 0 : 1),
									 endpoints[1] - (highIncl ? 0 : 1), false);
			}
		}
		else {
			NavigableSet<Integer> sm = set.subSet(
				endpoints[1], highIncl, endpoints[0], lowIncl);
			if (rnd.nextBoolean()) {
				bashSubSet(sm, endpoints[0] + (lowIncl ? 0 : 1),
									 endpoints[1] - (highIncl ? 0 : 1), false);
			}
			else {
				bashSubSet(sm.descendingSet(), endpoints[0] + (lowIncl ? 0 : 1),
									 endpoints[1] - (highIncl ? 0 : 1), true);
			}
		}
	}

	/**
	 * min and max are both inclusive. If max < min, interval is empty.
	 */
	void check(NavigableSet<Integer> set,
						 final int min, final int max, final boolean ascending) {
		class ReferenceSet {
			int lower(int element) {
				return ascending
							 ? lowerAscending(element) : higherAscending(element);
			}

			int floor(int element) {
				return ascending
							 ? floorAscending(element) : ceilingAscending(element);
			}

			int ceiling(int element) {
				return ascending
							 ? ceilingAscending(element) : floorAscending(element);
			}

			int higher(int element) {
				return ascending
							 ? higherAscending(element) : lowerAscending(element);
			}

			int first() {
				return ascending ? firstAscending() : lastAscending();
			}

			int last() {
				return ascending ? lastAscending() : firstAscending();
			}

			int lowerAscending(int element) {
				return floorAscending(element - 1);
			}

			int floorAscending(int element) {
				if (element < min) {
					return -1;
				}
				else if (element > max) {
					element = max;
				}

				// BitSet should support this! Test would run much faster
				while (element >= min) {
					if (bs.get(element)) {
						return element;
					}
					element--;
				}
				return -1;
			}

			int ceilingAscending(int element) {
				if (element < min) {
					element = min;
				}
				else if (element > max) {
					return -1;
				}
				int result = bs.nextSetBit(element);
				return (result > max) ? -1 : result;
			}

			int higherAscending(int element) {
				return ceilingAscending(element + 1);
			}

			private int firstAscending() {
				int result = ceilingAscending(min);
				return (result > max) ? -1 : result;
			}

			private int lastAscending() {
				int result = floorAscending(max);
				return (result < min) ? -1 : result;
			}
		}
		ReferenceSet rs = new ReferenceSet();

		// Test contents using containsElement
		int size = 0;
		for (int i = min; i <= max; i++) {
			boolean bsContainsI = bs.get(i);
			assertEquals(bsContainsI, set.contains(i));
			if (bsContainsI) {
				size++;
			}
		}
		assertEquals(size, set.size());

		// Test contents using contains elementSet iterator
		int size2 = 0;
		int previousElement = -1;
		for (int element : set) {
			assertTrue(bs.get(element));
			size2++;
			assertTrue(previousElement < 0 || (ascending
																				 ? element - previousElement > 0 : element - previousElement < 0));
			previousElement = element;
		}
		assertEquals(size2, size);

		// Test navigation ops
		for (int element = min - 1; element <= max + 1; element++) {
			assertEq(set.lower(element), rs.lower(element));
			assertEq(set.floor(element), rs.floor(element));
			assertEq(set.higher(element), rs.higher(element));
			assertEq(set.ceiling(element), rs.ceiling(element));
		}

		// Test extrema
		if (set.size() != 0) {
			assertEq(set.first(), rs.first());
			assertEq(set.last(), rs.last());
		}
		else {
			assertEq(rs.first(), -1);
			assertEq(rs.last(), -1);
			try {
				set.first();
				Assert.fail();
			}
			catch (NoSuchElementException success) {
			}
			try {
				set.last();
				Assert.fail();
			}
			catch (NoSuchElementException success) {
			}
		}
	}

	static void assertEq(Integer i, int j) {
		if (i == null) {
			assertEquals(j, -1);
		}
		else {
			assertEquals((int) i, j);
		}
	}

	static boolean eq(Integer i, int j) {
		return (i == null) ? j == -1 : i == j;
	}

}
