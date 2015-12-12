package com.ca.garbage;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;
import javax.annotation.Nonnull;

public class FastNavIntSet implements NavigableSet<Integer>
{
	private static final int CHUNK_SIZE = 64;

	@Nonnull private int[] values;
	private int size;
	private final Comparator<Integer> comparator;

	private FastNavIntSet(int[] values, int size, Comparator<Integer> comparator)
	{
		this.comparator = comparator;
		this.values = Arrays.copyOf(values, size);

		if (comparator == null)
		{
			Arrays.sort(this.values, 0, size);
		}
		else
		{
			Collections.sort(new IntListWrapper(), comparator);
		}
		this.size = size;
	}

	private FastNavIntSet(FastNavIntSet fastNavIntSet, Comparator<Integer> comparator)
	{
		this.values = Arrays.copyOf(fastNavIntSet.values, fastNavIntSet.size);
		this.size = fastNavIntSet.size;
		this.comparator = comparator;
		if (this.comparator != null)
		{
			Collections.sort(new IntListWrapper(), comparator);
		}
	}

	public static FastNavIntSet create(int[] values, Comparator<Integer> comparator)
	{
		if (values == null)
		{
			return new FastNavIntSet(values, 10, comparator);
		}
		else
		{
			return new FastNavIntSet(values, values.length, comparator);
		}
	}

	public static FastNavIntSet create(int... values)
	{
		return create(values, null);
	}

	public static FastNavIntSet create(Comparator<Integer> comparator)
	{
		return create(null, comparator);
	}

	public static FastNavIntSet fromIntegerArray(Integer[] values, Comparator<Integer> comparitor)
	{
		int[] newValues = new int[values.length];
		for (int i = 0; i < values.length; i++)
		{
			Integer value = values[i];
			if (value == null)
			{
				throw new NullPointerException("Cannot contain null!");
			}
			newValues[i] = value;
		}
		return create(newValues, comparitor);
	}

	public static FastNavIntSet fromIntegerArray(Integer[] values)
	{
		return fromIntegerArray(values, null);
	}

	public static FastNavIntSet fromCollection(Collection<Integer> values, Comparator<Integer> comparator)
	{
		return fromIntegerArray(values.toArray(new Integer[values.size()]), comparator);
	}

	public static FastNavIntSet fromCollection(Collection<Integer> values)
	{
		return fromCollection(values, null);
	}

	public static FastNavIntSet fromCollection(FastNavIntSet values, Comparator<Integer> comparator)
	{
		return new FastNavIntSet(values, comparator);
	}

	public static FastNavIntSet fromCollection(FastNavIntSet values)
	{
		return fromCollection(values, null);
	}

	@Override
	public boolean add(Integer e)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean addAll(Collection<? extends Integer> c)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Integer ceiling(Integer e)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Comparator<? super Integer> comparator()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean contains(Object o)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Iterator<Integer> descendingIterator()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public NavigableSet<Integer> descendingSet()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FastNavIntSet other = (FastNavIntSet) obj;
		for (int i = 0; i < size; i++)
		{
			if (other.values[i] != this.values[i])
				return false;
		}
		return true;
	}

	@Override
	public Integer first()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Integer floor(Integer e)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		for (int i = 0; i < size; i++)
		{
            hash = 31 * hash + values[i];
		}
		return hash;
	}

	@Override
	public NavigableSet<Integer> headSet(Integer toElement, boolean inclusive)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public SortedSet<Integer> headSet(Integer toElement)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Integer higher(Integer e)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isEmpty()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Iterator<Integer> iterator()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Integer last()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Integer lower(Integer e)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Integer pollFirst()
	{
		return values[0];
	}

	@Override
	public Integer pollLast()
	{
		return values[size];
	}

	@Override
	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int size()
	{
		return size;
	}

	@Override
	public FastNavIntSet subSet(Integer fromElement, boolean fromInclusive, Integer toElement, boolean toInclusive)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public FastNavIntSet subSet(Integer fromElement, Integer toElement)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public FastNavIntSet tailSet(Integer fromElement, boolean inclusive)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public FastNavIntSet tailSet(Integer fromElement)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Object[] toArray()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	/**
	 *
	 * @param value
	 * @return Integer.MIN_VALUE if value less that lowest,
	 *			Integer.MAX_VALUE if value is larger than largest,
	 *			lower index on no match,
	 *			negative index on match
	 */
	private int findIndex(int value)
	{
		if (comparator == null)
		{
			return findIndexNoComparitor(value);
		}
		else
		{
			return findIndexComparitor(value);
		}
	}

	private int findIndexNoComparitor(int value)
	{
		if (value > values[size - 1])
			return Integer.MAX_VALUE;
		else if (value == values[size - 1])
			return size - 1;
		if (value < values[0])
			return Integer.MIN_VALUE;
		else if (value == values[0])
			return 0;

		if (CHUNK_SIZE > size)
		{
			for (int i = 1; i < size; i++)
			{
			}
		}
		return -1;
	}

	private int findIndexComparitor(int value)
	{
		int compare = comparator.compare(value, values[size - 1]);
		if (compare == 0)
			return size - 1;
		else if (compare > 0)
			return Integer.MAX_VALUE;

		compare = Integer.compare(value, values[0]);
		if (compare < 0)
			return Integer.MIN_VALUE;
		else if (compare == 0)
			return 0;

		if (CHUNK_SIZE > size)
		{
			for (int i = 1; i < size; i++)
			{
			}
		}
		return -1;
	}

	/**
	 *
	 * @param value
	 * @return Integer.MIN_VALUE if value less that lowest,
	 *			Integer.MAX_VALUE if value is larger than largest,
	 *			lower index on no match,
	 *			negative index on match
	 */
	private int searchChunk(int value, int chunkStart)
	{
		int chunkEnd = CHUNK_SIZE + chunkStart > size ? size : CHUNK_SIZE + chunkStart;
		for (int i = chunkStart; i < chunkEnd; i++)
		{
		}
	}

	private class IntListWrapper extends AbstractList<Integer>
	{
		@Override
		public Integer get(int index)
		{
			return values[index];
		}

		@Override
		public int size()
		{
			return size;
		}

		@Override
		public Integer set(int index, Integer element)
		{
			int v = values[index];
			values[index] = element;
			return v;
		}
	};


}
