/* *****************************************************************
 *
 * Copyright 2022 Montera Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *******************************************************************/

package java.util;

/**
 * This class implements a hash map, which maps keys to values. Any non-<code>null</code>
 * object can be used as a key or as a value.
 * <p>
 * To successfully store and retrieve objects from a hashtable, the objects used
 * as keys must implement the <code>hashCode</code> method and the
 * <code>equals</code> method.
 * <p>
 * An instance of <code>Hashtable</code> has two parameters that affect its
 * efficiency: its <i>capacity</i> and its <i>load factor</i>. When the number
 * of entries in the HashTable exceeds the product of the load factor and the
 * current capacity, the capacity is increased by calling the
 * <code>rehash</code> method.
 * <p>
 * If many entries are to be made into a <code>Hashtable</code>, creating it
 * with a sufficiently large capacity may allow the entries to be inserted more
 * efficiently than letting it perform automatic rehashing as needed to grow the
 * _table.
 * <p>
 * Unlike the J2SE Hashtable, this is NOT thread safe.
 * <p>
 *
 * @author Greg McCreath
 * @since 0.0.1
 */
public class Hashtable<K,V> {

	/**
	 * The hash _table data.
	 */
	private HashtableEntry _table[];

	/**
	 * The total number of entries in the hash _table.
	 */
	private int _count;

	/**
	 * Rehashes the _table when _count exceeds this _threshold.
	 */
	private int _threshold;

	/**
	 * The load factor for the hashtable. The default load factor is
	 * 75%.
	 */
	private static final int _loadFactorPercent = 75;

	/** keep track of how many times the map has been modified */
	private int _modCount;

	/**
	 * Constructs a new, empty hashtable with the specified initial capacity.
	 *
	 * @param initialCapacity the initial capacity of the hashtable.
	 * @exception IllegalArgumentException if the initial capacity is less than
	 *                zero
	 */
	public Hashtable(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException();
		}
		if (initialCapacity == 0) {
			initialCapacity = 11;
		}
		_table = new HashtableEntry[initialCapacity];
		_threshold = (int) ((initialCapacity * _loadFactorPercent) / 100);
	}

	/**
	 * Constructs a new, empty hashtable with a default capacity and load
	 * factor.
	 */
	public Hashtable() {
		this(11);
	}

	/**
	 * Returns the number of keys in this hashtable.
	 *
	 * @return the number of keys in this hashtable.
	 */
	public int size() {
		return _count;
	}

	/**
	 * Tests if this hashtable maps no keys to values.
	 *
	 * @return <code>true</code> if this hashtable maps no keys to values;
	 *         <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
		return _count == 0;
	}

	/**
	 * Returns an Iterator of the keys in this hashtable.
	 *
	 * @return an Iterator of the keys in this hashtable.
	 * @see java.util.Enumeration
	 * @see java.util.Hashtable#elements()
	 */
	public Enumeration<K> keys() {
		return new HashtableEnumerator(_table, true);
	}

	/**
	 * Returns an Iterator of the values in this hashtable. Use the Iterator
	 * methods on the returned object to fetch the elements sequentially.
	 *
	 * @return an Iterator of the values in this hashtable.
	 * @see java.util.Enumeration
	 * @see java.util.Hashtable#keys()
	 */
	public Enumeration<V> elements() {
		return new HashtableEnumerator(_table, false);
	}

	/**
	 * Tests if some key maps into the specified value in this hashtable. This
	 * operation is more expensive than the <code>containsKey</code> method.
	 *
	 * @param value a value to search for.
	 * @return <code>true</code> if some key maps to the <code>value</code>
	 *         argument in this hashtable; <code>false</code> otherwise.
	 * @exception NullPointerException if the value is <code>null</code>.
	 * @see java.util.Hashtable#containsKey(java.lang.Object)
	 */
	public boolean contains(V value) {
		if (value == null) {
			throw new NullPointerException();
		}

		HashtableEntry tab[] = _table;
		for (int i = tab.length; i-- > 0;) {
			for (HashtableEntry e = tab[i]; e != null; e = e.next) {
				if (e.value.equals(value)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Tests if the specified object is a key in this hashtable.
	 *
	 * @param key possible key.
	 * @return <code>true</code> if the specified object is a key in this
	 *         hashtable; <code>false</code> otherwise.
	 * @see java.util.Hashtable#contains(java.lang.Object)
	 */
	public boolean containsKey(K key) {
		HashtableEntry tab[] = _table;
		int hash = key.hashCode();
		int index = (hash & 0x7FFFFFFF) % tab.length;
		for (HashtableEntry e = tab[index]; e != null; e = e.next) {
			if ((e.hash == hash) && e.key.equals(key)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the value to which the specified key is mapped in this hashtable.
	 *
	 * @param key a key in the hashtable.
	 * @return the value to which the key is mapped in this hashtable;
	 *         <code>null</code> if the key is not mapped to any value in this
	 *         hashtable.
	 * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
	 */
	public V get(K key) {
		HashtableEntry tab[] = _table;
		int hash = key.hashCode();
		int index = (hash & 0x7FFFFFFF) % tab.length;
		for (HashtableEntry e = tab[index]; e != null; e = e.next) {
			if ((e.hash == hash) && e.key.equals(key)) {
				return (V) e.value;
			}
		}
		return null;
	}

	/**
	 * Rehashes the contents of the hashtable into a hashtable with a larger
	 * capacity. This method is called automatically when the number of keys in
	 * the hashtable exceeds this hashtable's capacity and load factor.
	 *
	 * @TODO - could be native.
	 *
	 */
	protected void rehash() {
		int oldCapacity = _table.length;
		HashtableEntry oldTable[] = _table;

		int newCapacity = oldCapacity * 2 + 1;
		HashtableEntry newTable[] = new HashtableEntry[newCapacity];

		_threshold = (int) ((newCapacity * _loadFactorPercent) / 100);
		_table = newTable;

		for (int i = oldCapacity; i-- > 0;) {
			for (HashtableEntry old = oldTable[i]; old != null;) {
				HashtableEntry e = old;
				old = old.next;

				int index = (e.hash & 0x7FFFFFFF) % newCapacity;
				e.next = newTable[index];
				newTable[index] = e;
			}
		}
	}

	/**
	 * Maps the specified <code>key</code> to the specified <code>value</code>
	 * in this hashtable. Neither the key nor the value can be <code>null</code>.
	 * <p>
	 * The value can be retrieved by calling the <code>get</code> method with
	 * a key that is equal to the original key.
	 *
	 * @param key the hashtable key.
	 * @param value the value.
	 * @return the previous value of the specified key in this hashtable, or
	 *         <code>null</code> if it did not have one.
	 * @exception NullPointerException if the key or value is <code>null</code>.
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @see java.util.Hashtable#get(java.lang.Object)
	 */
	public V put(K key, V value) {
		// Make sure the value is not null
		if (value == null) {
			throw new NullPointerException();
		}

		// Makes sure the key is not already in the hashtable.
		HashtableEntry tab[] = _table;
		int hash = key.hashCode();
		int index = (hash & 0x7FFFFFFF) % tab.length;
		for (HashtableEntry e = tab[index]; e != null; e = e.next) {
			if ((e.hash == hash) && e.key.equals(key)) {
				V old = (V) e.value;
				e.value = value;
				return old;
			}
		}

		if (_count >= _threshold) {
			// Rehash the _table if the _threshold is exceeded
			rehash();
			return put(key, value);
		}

		// Creates the new entry.
		HashtableEntry e = new HashtableEntry();
		e.hash = hash;
		e.key = key;
		e.value = value;
		e.next = tab[index];
		tab[index] = e;
		_count++;
		_modCount++;
		return null;
	}

	/**
	 * Removes the key (and its corresponding value) from this hashtable. This
	 * method does nothing if the key is not in the hashtable.
	 *
	 * @param key the key that needs to be removed.
	 * @return the value to which the key had been mapped in this hashtable, or
	 *         <code>null</code> if the key did not have a mapping.
	 */
	public V remove(K key) {
		HashtableEntry tab[] = _table;
		int hash = key.hashCode();
		int index = (hash & 0x7FFFFFFF) % tab.length;
		for (HashtableEntry e = tab[index], prev = null; e != null; prev = e, e = e.next) {
			if ((e.hash == hash) && e.key.equals(key)) {
				if (prev != null) {
					prev.next = e.next;
				} else {
					tab[index] = e.next;
				}
				_count--;
				_modCount++;
				return (V) e.value;
			}
		}
		return null;
	}

	/**
	 * Clears this hashtable so that it contains no keys.
	 *
	 * @TODO - could be native.
	 */
	public void clear() {
		HashtableEntry tab[] = _table;
		for (int index = tab.length; --index >= 0;)
			tab[index] = null;
		_modCount++;
		_count = 0;
	}

	/**
	 * Returns a rather long string representation of this hashtable.
	 *
	 * @return a string representation of this hashtable.
	 */
	public String toString() {
		int max = size() - 1;
		StringBuilder buf = new StringBuilder();
		Enumeration k = keys();
		Enumeration e = elements();
		buf.append("{");

		for (int i = 0; i <= max; i++) {
			String s1 = k.nextElement().toString();
			String s2 = e.nextElement().toString();
			buf.append(s1 + "=" + s2);
			if (i < max) {
				buf.append(", ");
			}
		}
		buf.append("}");
		return buf.toString();
	}

	class HashtableEnumerator implements Enumeration {
		boolean keys;

		int index;

		HashtableEntry table[];

		HashtableEntry entry;

		int mod = _modCount;

		HashtableEnumerator(HashtableEntry table[], boolean keys) {
			this.table = table;
			this.keys = keys;
			this.index = table.length;
		}

		public boolean hasMoreElements() {
			// if the map has changed while we're iterating, throw this
			// ...
			if (mod != _modCount)
				throw new ConcurrentModificationException();

			if (entry != null) {
				return true;
			}
			while (index-- > 0) {
				if ((entry = table[index]) != null) {
					return true;
				}
			}
			return false;
		}

		public Object nextElement() {
			// if the map has changed while we're iterating, throw this
			// ...
			if (mod != _modCount)
				throw new ConcurrentModificationException();

			if (entry == null) {
				while ((index-- > 0) && ((entry = table[index]) == null))
					;
			}
			if (entry != null) {
				HashtableEntry e = entry;
				entry = e.next;
				return keys ? e.key : e.value;
			}
			throw new NoSuchElementException();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}


}

/**
 * HashMap collision list.
 *
 * @author Greg McCreath
 * @since 0.0.1
 */
class HashtableEntry {
	int hash;

	Object key;

	Object value;

	HashtableEntry next;
}
