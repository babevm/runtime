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

package java.lang;

import java.io.UnsupportedEncodingException;

import babe.i18n.Codec;

/**
 *
 * @author Greg McCreath
 * @since 0.0.1
 */
public final class String implements CharSequence, Comparable<String> {

	// ***********************************************************************

	// WARNING!!. Do not place any class fields before this array. For
	// efficiency, The VM assumes the position and existence of these fields.
	char[] _chars;

	/* the offset into the _char[] where this string starts */
	int _offset;

	/* the length of this string */
	int _length;

	// ***********************************************************************

	public String() {
		_chars = new char[0];
	}

	public String(byte[] bytes) {
		this(bytes, 0, bytes.length);
	}

	public String(byte[] bytes, int offset, int length) {
		_chars = Codec.decode(bytes, offset, length);
		_length = _chars.length;
	}

	public String(byte[] bytes, int offset, int length, String enc)
			throws UnsupportedEncodingException {
		_chars = Codec.decode(bytes, offset, length, enc);
		_length = _chars.length;
	}

	public String(byte[] bytes, String enc) throws UnsupportedEncodingException {
		this(bytes, 0, bytes.length, enc);
	}

	public String(char[] value) {
		this(value, 0, value.length);
	}

	/**
	 * Package protected constructor that allows the creation of a string from
	 * an existing char array. The char array content are not copied. This
	 * effectively wraps a String object around a portion of an array of char.
	 *
	 * @param offset
	 * @param length
	 * @param value
	 */
	String(int offset, int length, char value[]) {
		_chars = value;
		_offset = offset;
		_length = length;
	}

	public String(char value[], int offset, int length) {

		if (offset < 0)
			throw new StringIndexOutOfBoundsException(offset);

		if (length < 0)
			throw new StringIndexOutOfBoundsException(length);

        if (offset > value.length - length)
			throw new StringIndexOutOfBoundsException(offset + length);

		_chars = new char[length];
		_length = length;

		System.arraycopy(value, offset, _chars, 0, length);
	}

	public String(String value) {
		_length = value._length;
		_offset = value._offset;
		_chars = value._chars;
	}

	public String(StringBuffer buffer) {
		_chars = buffer._chars;
		_length = buffer._length;
		buffer._isShared = true;
	}

	public String(StringBuilder builder) {
		_chars = builder._chars;
		_length = builder._length;
		builder._isShared = true;
	}

	public byte[] getBytes() {
		return Codec.encode(_chars, _offset, _length);
	}

	public byte[] getBytes(String enc) throws UnsupportedEncodingException {
		return Codec.encode(_chars, _offset, _length, enc);
	}

	public static String valueOf(int i) {
		return Integer.toString(i);
	}

	public static String valueOf(float f) {
		return Float.toString(f);
	}

	public static String valueOf(double d) {
		return Double.toString(d);
	}

	public static String valueOf(long l) {
		return Long.toString(l);
	}

	public static String valueOf(Object o) {
		if (o == null)
			return "null";
		if (o instanceof String)
			return (String) o;
		return o.toString();
	}

	public static String valueOf(char c) {
		return Character.toString(c);
	}

	public static String valueOf(boolean b) {
		return Boolean.toString(b);
	}

	public static String valueOf(char[] data) {
		return new String(data, 0, data.length);
	}

	public static String valueOf(char[] data, int offset, int length) {
		return new String(data, offset, length);
	}

	public native String intern();

	public int length() {
		return _length;
	}

	public char[] toCharArray() {
		char ch[] = new char[_length];
		System.arraycopy(this._chars, _offset, ch, 0, _length);
		return ch;
	}

	public String toString() {
		return this;
	}

	public native char charAt(int i);

	public native boolean equals(Object obj);

    public boolean equalsIgnoreCase(String anotherString) {
        return (anotherString != null) && (anotherString._length == _length) &&
            regionMatches(true, 0, anotherString, 0, _length);
    }

    public native int hashCode();

	public boolean startsWith(String prefix) {
		return startsWith(prefix, 0);
	}

	public native boolean startsWith(String prefix, int offset);

	public boolean endsWith(String suffix) {
		return startsWith(suffix, _length - suffix._length);
	}

	public String substring(int beginIndex, int endIndex) {
		String retval;

		if (beginIndex < 0)
			throw new StringIndexOutOfBoundsException();

		if (endIndex > _length)
			throw new StringIndexOutOfBoundsException();

		if (beginIndex > endIndex)
			throw new StringIndexOutOfBoundsException();

		if ((beginIndex == 0) && (endIndex == _length))
			retval = this;
		else {
			// use private constructor that does not copy the string
			retval = new String(beginIndex + _offset, endIndex - beginIndex, _chars);
		}

		return retval;
	}

	public String substring(int beginIndex) throws IndexOutOfBoundsException {
		return substring(beginIndex, _length);
	}

	// TODO make native.
	public int compareTo(String str) {

		if (str == this)
			return 0;

		char[] tchars = _chars;
		char[] ochars = str._chars;

		int tlen = _length;
		int olen = str._length;

		int toff = _offset;
		int ooff = str._offset;

		int i = -1;

		int len = (tlen < olen) ? tlen : olen;

		while (++i < len) {
			char tc = tchars[i + toff];
			char oc = ochars[i + ooff];

			if (tc != oc)
				return tc - oc;
		}

		return tlen - olen;
	}

	public String concat(String str) {

		int olen = str._length;

		if (olen == 0)
			return this;

		int tlen = _length;

		int newLen = tlen + olen;

		// allocate a new char array for the total contents
		char buf[] = new char[newLen];
		// copy 'this' chars into new buffer
		getChars(0, tlen, buf, 0);
		// copy str chars into new buffer
		str.getChars(0, olen, buf, tlen);
		// create a new string with the new buffer
		return new String(buf, 0, newLen);
	}

	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {

		if (srcBegin < 0)
			throw new StringIndexOutOfBoundsException(srcBegin);

		if (srcEnd > _length)
			throw new StringIndexOutOfBoundsException(srcEnd);

		if (srcBegin > srcEnd)
			throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);

		System.arraycopy(_chars, srcBegin + _offset, dst, dstBegin, srcEnd - srcBegin);
	}

	public int indexOf(int ch) {
		return indexOf(ch, 0);
	}

	public native int indexOf(int ch, int fromIndex);

	public int lastIndexOf(int ch) {
		return lastIndexOf(ch, _length - 1);
	}

	public native int lastIndexOf(int ch, int fromIndex);

	public int indexOf(String str) {
		return indexOf(str, 0);
	}

	// TODO make native.
	public int indexOf(String str, int fromIndex) {

		if (str == null)
			throw new NullPointerException();

		char[] tchars = _chars;
		int tlen = _length;
		int toff = _offset;

		char[] ochars = str._chars;
		int olen = str._length;
		int ooff = str._offset;

		// and empty string always returns zero
		int limit = tlen - olen;

		if (fromIndex < 0)
			fromIndex = 0;
		if (fromIndex > tlen)
			return (olen == 0 ? tlen : -1);

		if (fromIndex > limit)
			return -1;

		if (olen == 0)
			return fromIndex;

		int tindex = fromIndex + toff;

		int tlimit = limit + toff;
		int olimit = ooff + olen;

		char oc = ochars[ooff];

		while (tindex <= tlimit) {

			char tc = tchars[tindex];

			// if we find a matching start char, we'll see if the rest of the
			// chars match
			if (tc == oc) {
				int oi = ooff;
				for (int ti = tindex; oi < olimit; ti++) {
					if (tchars[ti] != ochars[oi])
						break;
					oi++;
				}

				if (oi == olimit)
					return tindex - toff;

			}

			tindex++;
		}

		return -1;
	}

	public boolean regionMatches(int toffset, String other, int ooffset, int len) {
		return regionMatches(false, toffset, other, ooffset, len);
	}

	public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) {

		char[] tchars = _chars;
		int tlen = _length;
		int toff = _offset;

		char[] ochars = other._chars;
		int olen = other._length;
		int ooff = other._offset;

		int tindex = toffset + toff;
		int oindex = ooffset + ooff;

		if ((toffset < 0) || (ooffset < 0) || (toffset > tlen - len) || (ooffset > olen - len))
			return false;

		while (len-- > 0) {

			char tc = tchars[tindex++];
			char oc = ochars[oindex++];

			if (tc == oc)
				continue;

			if (ignoreCase) {

				tc = Character.toUpperCase(tc);
				oc = Character.toUpperCase(oc);

				if (tc == oc)
					continue;

			}

			return false;
		}

		return true;
	}

	public String replace(char oldChar, char newChar) {
		char chars[] = _chars;
		int len = _length;
		int offset = _offset;
		int pos = -1;

		// first check if we need to replace any.
		while (++pos < len) {
			if (chars[pos + offset] == oldChar)
				break;
		}

		// if the pos is less than the length we have found and old char so
		// we'll set about replacing it.

		if (pos < len) {

			/* copy the string */
			char[] newchars = new char[_length];
			System.arraycopy(chars, offset, newchars, 0, len);

			while (pos < len) {
				if (chars[pos + offset] == oldChar)
					newchars[pos] = newChar;
				pos++;
			}

			return new String(0, len, newchars);
		}


		return this;
	}

	public String toLowerCase() {
		char chars[] = _chars;
		int len = _length;
		int offset = _offset;
		int pos = -1;

		// first check if we need to lowercase any. If not return this string.
		while (++pos < len) {
			char c = chars[pos + offset];
			if (c != Character.toLowerCase(c))
				break;
		}

		// if the pos is less than the length we have found an uppercase char so
		// we'll set about replacing the rest.
		if (pos < len) {

			/* copy the string */
			char[] newchars = new char[len];
			System.arraycopy(chars, offset, newchars, 0, len);

			while (pos < len) {
				char c = chars[pos + offset];
				char uc = Character.toLowerCase(c);
				if (c != uc)
					newchars[pos] = uc;
				pos++;
			}

			return new String(0, len, newchars);
		}

		return this;
	}

	public String toUpperCase() {
		char chars[] = _chars;
		int len = _length;
		int offset = _offset;
		int pos = -1;

		// first check if we need to uppercase any. If not return this string.
		while (++pos < len) {
			char c = chars[pos + offset];
			if (c != Character.toUpperCase(c))
				break;
		}

		// if the pos is less than the length we have found a lowercase char so
		// we'll set about replacing the rest.
		if (pos < len) {

			/* copy the string */
			char[] newchars = new char[len];
			System.arraycopy(chars, offset, newchars, 0, len);

			while (pos < len) {
				char c = chars[pos + offset];
				char uc = Character.toUpperCase(c);
				if (c != uc)
					newchars[pos] = uc;
				pos++;
			}

			return new String(0, len, newchars);
		}

		return this;
	}

	public String trim() {

		char[] chars = _chars;
		int len = _length;
		int offset = _offset;
		int st = 0;

		// count forwards past any whitespace
		while ((st < len) && (chars[st + offset] <= ' ')) {
			st++;
		}

		// and backwards ...
		while ((st < len) && (chars[offset + len - 1] <= ' ')) {
			len--;
		}

		return ((st > 0) || (len < _length)) ? substring(st, len) : this;
	}

	public CharSequence subSequence(int beginIndex, int endIndex) {
		return this.substring(beginIndex, endIndex);
	}

}
