package com.ca.garbage;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import javax.annotation.Nonnull;

public class FastNavIntSet extends AbstractSet<Integer> implements NavigableSet<Integer>, Serializable {
	private static final int CHUNK_SIZE = 64;
	public static Comparator<Integer> FORWARD = Integer::compareTo;
	public static Comparator<Integer> REVERSE = (o1, o2) -> o2.compareTo(o1);
	private int size;
	private final boolean forward;
	@Nonnull
	private int[] values;

	private FastNavIntSet(int[] values, boolean forward) {
		this.forward = forward;
		this.values = new int[8];
		this.size = 0;
		if (values != null && values.length > 0) {
			addAll(values);
		}
	}

	private FastNavIntSet(FastNavIntSet fastNavIntSet, boolean forward) {
		this.values = Arrays.copyOf(fastNavIntSet.values, fastNavIntSet.size);
		this.size = fastNavIntSet.size;
		this.forward = forward;
	}

	public static FastNavIntSet create(int[] values, boolean forward) {
		return new FastNavIntSet(values, forward);
	}

	public static FastNavIntSet create() {
		return new FastNavIntSet((int[]) null, true);
	}

	public static FastNavIntSet create(int... values) {
		return create(values, true);
	}

	public static FastNavIntSet create(boolean forward) {
		return create(null, forward);
	}

	public static FastNavIntSet fromIntegerArray(Integer[] values, boolean forward) {
		int[] newValues = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			newValues[i] = values[i];
		}
		return create(newValues, forward);
	}

	public static FastNavIntSet fromIntegerArray(Integer[] values) {
		return fromIntegerArray(values, true);
	}

	public static FastNavIntSet fromCollection(Collection<Integer> values, boolean forward) {
		FastNavIntSet set = create(forward);
		set.addAll(values);
		return set;
	}

	public static FastNavIntSet fromCollection(Collection<Integer> values) {
		return fromCollection(values, true);
	}

	public static FastNavIntSet fromCollection(FastNavIntSet values, boolean forward) {
		return new FastNavIntSet(values, forward);
	}

	public static FastNavIntSet fromCollection(FastNavIntSet values) {
		return fromCollection(values, true);
	}

	@Override
	public boolean add(Integer e) {
		int insertionIndex = findIndex(e);
		if (insertionIndex >= 0 && insertionIndex != Integer.MAX_VALUE) {
			return false;
		}
		if (insertionIndex == Integer.MAX_VALUE) {
			insertionIndex = size;
		}
		else if (insertionIndex == Integer.MIN_VALUE) {
			insertionIndex = 0;
		}
		else {
			insertionIndex = (insertionIndex * -1) - 1;
		}

		++size;
		int len = values.length;
		if (len >= size) {
			for (int i = size - 1; i > insertionIndex; --i) {
				values[i] = values[i - 1];
			}
			values[insertionIndex] = e;
		}
		else {
			int newLen = 8;
			if (len >= newLen) {
				newLen = len + len / 2;
			}
			int[] newValues = new int[newLen];
			for (int i = 0, j = 0; i < size; i++, j++) {
				if (i != insertionIndex) {
					newValues[i] = values[j];
				}
				else {
					newValues[i] = e;
					--j;
				}
			}
			values = newValues;
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		int[] collectionValues = new int[c.size()];
		int i = 0;
		for (Integer value : c) {
			collectionValues[i] = value;
			++i;
		}
		return addAll(collectionValues);
	}

	public final boolean addAll(int[] toArray) {
		Arrays.sort(toArray);
		int[] newValues = new int[size + toArray.length];
		int newSize = 0;
		for (int i = 0, j = 0, k = 0; j < size || k < toArray.length; ++i) {
			if (j == size) {
				newValues[i] = toArray[k];
				++k;
				++newSize;
			}
			else if (k == toArray.length) {
				newValues[i] = values[j];
				++j;
				++newSize;
			}
			else {
				newValues[i] = Math.min(toArray[k], values[j]);
				++newSize;
				while (k < toArray.length && toArray[k] == newValues[i]) {
					++k;
				}
				while (j < size && values[j] == newValues[i]) {
					++j;
				}
			}
		}
		boolean changes = size != newSize;
		values = newValues;
		size = newSize;
		return changes;
	}

	@Override
	public Integer ceiling(Integer e) {
		return ceilingOrFloor(e, !forward);
	}

	@Override
	public void clear() {
		size = 0;
	}

	@Override
	public Comparator<? super Integer> comparator() {
		if (forward) {
			return FORWARD;
		}
		else {
			return REVERSE;
		}
	}

	@Override
	public boolean contains(Object o) {
		if (o instanceof Integer) {
			int index = findIndex((Integer) o);
			if (index < 0) {
				return false;
			}
			else {
				return index != Integer.MAX_VALUE;
			}
		}
		else {
			return false;
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		SortedSet sortedSet;
		if (c instanceof SortedSet) {
			sortedSet = (SortedSet) c;
		}
		else {
			sortedSet = FastNavIntSet.fromCollection((Collection<Integer>) c);
		}
		if (c.size() > size) {
			return false;
		}
		Iterator iterator = sortedSet.iterator();
		Iterator<Integer> thisIterator;
		if ((sortedSet.comparator() == null
				 || sortedSet.comparator().compare(1, 2) < 0) && forward) {
			thisIterator = new FastNavSetAscendingIterator();
		}
		else {
			thisIterator = new FastNavSetDecendingIterator();
		}
		while (iterator.hasNext() && thisIterator.hasNext()) {
			int valToFind = (int) iterator.next();
			int currentVal = thisIterator.next();
			while (currentVal != valToFind && thisIterator.hasNext()) {
				currentVal = thisIterator.next();
			}
			if (currentVal != valToFind) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Iterator<Integer> descendingIterator() {
		if (!forward) {
			return new FastNavSetAscendingIterator();
		}
		else {
			return new FastNavSetDecendingIterator();
		}
	}

	@Override
	public NavigableSet<Integer> descendingSet() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final FastNavIntSet other = (FastNavIntSet) obj;
		if (other.forward != this.forward) {
			return false;
		}
		for (int i = 0; i < size; i++) {
			if (other.values[i] != this.values[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Integer first() {
		return lastOrFirst(!forward);
	}

	@Override
	public Integer floor(Integer e) {
		return ceilingOrFloor(e, forward);
	}

	private Integer ceilingOrFloor(Integer e, boolean isFloor) {
		if (size == 0) {
			return null;
		}
		int index = findIndex(e);
		if (index == Integer.MIN_VALUE) {
			return isFloor ? null : values[0];
		}
		if (index == Integer.MAX_VALUE) {
			return isFloor ? values[size - 1] : null;
		}
		if (index >= 0) {
			return e;
		}
		else {
			index = (index * -1) - 1;
			if (isFloor) {
				index = index - 1;
			}
			return ((index >= 0) && (index < size)) ? values[index] : null;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		for (int i = 0; i < size; i++) {
			hash = 31 * hash + values[i];
		}
		if (forward) {
			hash *= 23;
		}
		return hash;
	}

	@Override
	public NavigableSet<Integer> headSet(Integer toElement, boolean inclusive
	) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public SortedSet<Integer> headSet(Integer toElement) {
		return headSet(toElement, true);
	}

	@Override
	public Integer higher(Integer e) {
		return getLowerOrHigher(e, !forward);
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Iterator<Integer> iterator() {
		if (forward) {
			return new FastNavSetAscendingIterator();
		}
		else {
			return new FastNavSetDecendingIterator();

		}
	}

	private class FastNavSetAscendingIterator implements Iterator<Integer> {
		private int index = 0;

		@Override
		public boolean hasNext() {
			return index != size;
		}

		@Override
		public Integer next() {
			int currIndex = index;
			++index;
			return values[currIndex];
		}

		@Override
		public void remove() {
			if (index != 0) {
				removeAtIndex(index - 1);
				--index;
			}
			else {
				throw new IllegalStateException("next has not been called");
			}
		}
	}

	private class FastNavSetDecendingIterator implements Iterator<Integer> {
		private int index = size - 1;

		@Override
		public boolean hasNext() {
			return index != -1;
		}

		@Override
		public Integer next() {
			int currIndex = index;
			--index;
			return values[currIndex];
		}

		@Override
		public void remove() {
			if (index != size - 1) {
				removeAtIndex(index + 1);
			}
			else {
				throw new IllegalStateException("Next has not been called");
			}
		}
	}

	@Override
	public Integer last() {
		return lastOrFirst(forward);
	}

	private Integer lastOrFirst(boolean getLast) throws NoSuchElementException {
		if (isEmpty()) {
			throw new NoSuchElementException("Set is empty");
		}
		if (getLast) {
			return values[size - 1];
		}
		else {
			return values[0];
		}
	}

	@Override
	public Integer lower(Integer e) {
		return getLowerOrHigher(e, forward);
	}

	private Integer getLowerOrHigher(Integer e, boolean lower) {
		int index = findIndex(e);
		if (index == Integer.MAX_VALUE) {
			return lower ? values[size - 1] : null;
		}
		else if (index == Integer.MIN_VALUE) {
			return lower ? null : values[0];
		}
		if (index < 0) {
			index = (index * -1) - 1;
			if (lower) {
				index--;
			}
		}
		else {
			if (lower) {
				index--;
			}
			else {
				index++;
			}
		}
		return ((index >= 0) && (index < size)) ? values[index] : null;
	}

	@Override
	public Integer pollFirst() {
		if (isEmpty()) {
			return null;
		}
		return poll(!forward);
	}

	@Override
	public Integer pollLast() {
		if (isEmpty()) {
			return null;
		}
		return poll(forward);
	}

	private int poll(boolean fromBack) {
		int out;
		if (fromBack) {
			out = values[size - 1];
			--size;
		}
		else {
			out = values[0];
			removeAtIndex(0);
		}
		return out;
	}

	private void removeAtIndex(int index) {
		if (index == size - 1) {
			--size;
		}
		else {
			for (int i = index; i < size - 1; i++) {
				values[i] = values[i + 1];
			}
			--size;
		}
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof Integer) {
			int index = findIndex((Integer) o);
			if (index >= 0 && index != Integer.MAX_VALUE) {
				removeAtIndex(index);
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public FastNavIntSet subSet(Integer fromElement, boolean fromInclusive, Integer toElement, boolean toInclusive) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public FastNavIntSet subSet(Integer fromElement, Integer toElement) {
		return subSet(fromElement, true, toElement, false);
	}

	@Override
	public FastNavIntSet tailSet(Integer fromElement, boolean inclusive) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public FastNavIntSet tailSet(Integer fromElement) {
		return tailSet(fromElement, true);
	}

	@Override
	public Object[] toArray() {
		Integer[] out = new Integer[size];
		for (int i = 0; i < size; i++) {
			out[i] = values[i];
		}
		return out;
	}

	/**
	 *
	 * @param value
	 * @return Integer.MIN_VALUE if value less that lowest, Integer.MAX_VALUE if value is larger than largest, lower index
	 * on no match, negative index -1 on match
	 */
	private int findIndex(int value) {
		if (size == 0) {
			return -1;
		}
		int low = 0;
		int high = ((size - 1) / CHUNK_SIZE);

		int chunkCheck = Integer.MIN_VALUE;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			chunkCheck = searchChunk(value, mid * CHUNK_SIZE);

			if (chunkCheck == Integer.MAX_VALUE) {
				low = mid + 1;
			}
			else if (chunkCheck == Integer.MIN_VALUE) {
				high = mid - 1;
			}
			else {
				return chunkCheck; // key found
			}
		}
		return chunkCheck;  // key not found.
	}

	/**
	 *
	 * @param value
	 * @return Integer.MIN_VALUE if value less that lowest, Integer.MAX_VALUE if value is larger than largest, lower index
	 * on no match, negative index on match
	 */
	private int searchChunk(int value, int chunkStart) {
		int[] vals = values;
		if (value == vals[chunkStart]) {
			return chunkStart;
		}
		if (value < vals[chunkStart]) {
			return Integer.MIN_VALUE;
		}
		int chunkEnd = CHUNK_SIZE + chunkStart > size - 1 ? size - 1 : CHUNK_SIZE + chunkStart;
		if (vals[chunkEnd] == value) {
			return chunkEnd;
		}
		if (value > vals[chunkEnd]) {
			return Integer.MAX_VALUE;
		}
		return binarySearch(vals, chunkStart, chunkEnd, value);
	}

	private static int binarySearch(final int[] a, int fromIndex, int toIndex,
																	int key) {
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			int midVal = a[mid];

			if (midVal < key) {
				low = mid + 1;
			}
			else if (midVal > key) {
				high = mid - 1;
			}
			else {
				return mid; // key found
			}
		}
		return -(low + 1);  // key not found.
	}
}
