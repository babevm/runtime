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

/**
 * @author Greg McCreath
 * @since 0.0.1
 */
public class StringBuilder implements CharSequence {

	public char[] _chars;

	int _length = 0;

	boolean _isShared = false;

	public StringBuilder() {
		_chars = new char[16];
	}

	public StringBuilder(int length) {
		_chars = new char[length];
	}

	public StringBuilder(String str) {
        this(str._length + 16);
		append(str);
	}

    public StringBuilder(CharSequence seq) {
        this(seq.length() + 16);
        append(seq);
    }

	public StringBuilder append(boolean b) {
		return append(Boolean.toString(b));
	}

	public StringBuilder append(char c) {
		return append(Character.toString(c));
	}

	public StringBuilder append(char str[]) {
		return append(new String(str));
	}

	public StringBuilder append(char str[], int offset, int length) {
		return append(new String(str, offset, length));
	}

	public StringBuilder append(byte b) {
		return append(Integer.toString(b));
	}

	public StringBuilder append(int i) {
		return append(Integer.toString(i));
	}

	public StringBuilder append(long l) {
		return append(Long.toString(l));
	}

	public StringBuilder append(float f) {
		return append(Float.toString(f));
	}

	public StringBuilder append(double d) {
		return append(Double.toString(d));
	}

	public StringBuilder append(Object obj) {
		if (obj == null)
			return append("null");
		return append(obj.toString());
	}

	public StringBuilder append(CharSequence s) {
		if (s == null)
			s = "null";
		if (s instanceof String)
			return this.append((String) s);
		if (s instanceof StringBuilder)
			return this.append((StringBuilder) s);
		return this.append(s, 0, s.length());
	}

	public StringBuilder append(CharSequence s, int start, int end) {
		if (s == null)
			s = "null";

		if ((start < 0) || (end < 0) || (start > end) || (end > s.length()))
			throw new IndexOutOfBoundsException();

		int len = end - start;
		if (len == 0)
			return this;

		int newLength = _length + len;
		if (newLength > _chars.length)
			ensureCapacity(newLength);
	    else
	    	if (_isShared) unShare();

		for (int i = start; i < end; i++)
			_chars[_length++] = s.charAt(i);

		_length = newLength;
		return this;
	}

	private native StringBuilder insert0(int position, char chars[], int offset, int length);

    public native StringBuilder append(String s);

    public native void setLength(int newlength);

	public String toString() {
		return new String(this);
	}

	public int capacity() {
		return _chars.length - _length;
	}

	public int length() {
		return _length;
	}

	public native void ensureCapacity(int minimumCapacity);

	public char charAt(int index) {
		if ((index >= _length) || (index < 0))
			throw new IndexOutOfBoundsException();

		return _chars[index];
	}

	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		if ((srcBegin < 0) || (srcEnd < 0) || (srcBegin > srcEnd)
				|| (srcEnd > _length)
				|| (dstBegin + srcEnd - srcBegin > _length)) {
			System
					.arraycopy(_chars, srcBegin, dst, dstBegin, srcEnd
							- srcBegin);
		}
	}

	public void setCharAt(int index, char ch) {

		if ((index >= _length) || (index < 0))
			throw new IndexOutOfBoundsException();

		if (_isShared)
			unShare();

		_chars[index] = ch;
	}

	public StringBuilder deleteCharAt(int index) {

		if ((index < 0) || (index >= _length))
			throw new StringIndexOutOfBoundsException();

		if (_isShared)
			unShare();

		System.arraycopy(_chars, index + 1, _chars, index, _length - index - 1);
		_length--;
		return this;
	}

	public StringBuilder delete(int start, int end) {

		/* only up to the end */
		if (end > _length)
			end = length();

		/* range check */
		if ((start < 0) || (start > _length) || (start > end))
			throw new StringIndexOutOfBoundsException();

		int len = end - start;
		if (len > 0) {
			if (_isShared)
				unShare();
			System.arraycopy(_chars, start + len, _chars, start, _length - end);
			_length -= len;
		}
		return this;
	}

	public StringBuilder insert(int offset, String str) {
		return insert0(offset, str._chars, str._offset, str._length);
	}

	public StringBuilder insert(int offset, Object obj) {
		return insert(offset, obj.toString());
	}

	public StringBuilder insert(int offset, char[] chars) {
		return insert0(offset, chars, 0, chars.length);
	}

	public StringBuilder insert(int offset, boolean b) {
		return insert(offset, Boolean.toString(b));
	}

	public StringBuilder insert(int offset, char c) {
		return insert0(offset, new char[] { c }, 0, 1);
	}

	public StringBuilder insert(int offset, int i) {
		return insert(offset, Integer.toString(i));
	}

	public StringBuilder insert(int offset, long l) {
		return insert(offset, Long.toString(l));
	}

	public StringBuilder reverse() {
		int n = _length - 1;
		for (int j = (n - 1) >> 1; j >= 0; --j) {
			char temp = _chars[j];
			_chars[j] = _chars[n - j];
			_chars[n - j] = temp;
		}
		return this;
	}

	void unShare() {
		_chars = _chars.clone();
		_isShared = false;
	}

	public CharSequence subSequence(int start, int end) {
		String s = new String(0, _length, _chars);
		_isShared = true;
		return s;
	}

}
