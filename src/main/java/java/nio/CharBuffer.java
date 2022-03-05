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

package java.nio;

/**
 * @author Greg McCreath
 * @since 0.0.1
 */
public final class CharBuffer extends Buffer {

    private ByteOrder _order;

    private char[] _chars;

    private CharBuffer(int capacity, int limit, int position, int mark) {
    	super(capacity, limit, position, mark);
        _order = ByteOrder.nativeOrder();
	}

    private CharBuffer(char[] chars, int offset, int length) {
        this(length, offset + length , offset, -1);
        _chars = chars;
    }

    private CharBuffer(char[] chars) {
    	this(chars, 0, chars.length);
    }

    public static CharBuffer allocate(int capacity) {

    	if (capacity < 0)
			throw new IllegalArgumentException();

        return new CharBuffer(new char[capacity]);
    }

    public static CharBuffer wrap(char[] array) {
        return new CharBuffer(array);
    }

    public static CharBuffer wrap(char[] array, int offset, int length) {

    	if ( (offset < 0) || (offset > array.length) )
    		throw new IndexOutOfBoundsException();

    	if ( (length < 0) || (length > array.length - offset) )
    		throw new IndexOutOfBoundsException();

        return new CharBuffer(array, offset, length);
    }

    public int arrayOffset() {

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

        return _arrayOffset;
    }

    public boolean hasArray() {
        return ( (true) && (!_isReadOnly)) ;
    }

    public char[] array() {

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

    	return _chars;
    }

    public char get() {

    	if (_position >= _limit)
    		throw new BufferUnderflowException();

        return _chars[_arrayOffset + _position++];
    }

    public CharBuffer put(char c) {

    	if (_position >= _limit)
    		throw new BufferOverflowException();

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

        _chars[_arrayOffset + _position++] = c;
        return this;
    }

    public char get(int index) {

    	if ((index < 0) || (index >= _limit))
    		throw new IndexOutOfBoundsException();

        return _chars[_arrayOffset + index];
    }


    public CharBuffer put(int index, char c) {

    	if ((index < 0) || (index >= _limit))
        		throw new IndexOutOfBoundsException();

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

        _chars[_arrayOffset + index] = c;
        return this;
    }

    public CharBuffer get(char[] dst, int offset, int length) {

    	if ((offset < 0) || (offset > dst.length))
    		throw new IndexOutOfBoundsException();

    	if ((length < 0) || (length > dst.length - offset ))
    		throw new IndexOutOfBoundsException();

    	if (length > remaining())
    	    throw new BufferUnderflowException();

    	System.arraycopy(_chars, _arrayOffset + _position, dst, offset, length);
    	_position += length;

        return this;
    }

    public CharBuffer get(char[] dst) {
        return get(dst, 0, dst.length);
    }

    public CharBuffer put(CharBuffer src) {

    	if (src == this)
    		throw new IllegalArgumentException();

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

		int n = src.remaining();

		if (n > remaining())
		    throw new BufferOverflowException();

    	if (n > 0) {
            put(src.get(new char[n]));
        }

        return this;
    }

    public CharBuffer put(char[] src, int offset, int length) {

    	if ((offset < 0) || (offset > src.length))
    		throw new IndexOutOfBoundsException();

    	if ((length < 0) || (length > src.length - offset ))
    		throw new IndexOutOfBoundsException();

    	if (length > remaining())
    	    throw new BufferOverflowException();

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

    	System.arraycopy(src, offset, _chars, _arrayOffset + _position, length);
    	_position += length;

        return this;
    }

    public CharBuffer put(char[] src) {
        return put(src, 0, src.length);
    }

    public char charAt(int index) {

    	if ( (index < 0) || (index > remaining()))
    			throw new IndexOutOfBoundsException();

    	return _chars[_arrayOffset + _position + index];

    }

    public ByteOrder order() {
        return _order;
    }

    public CharBuffer duplicate() {
        CharBuffer bb = new CharBuffer(_capacity, _limit, _position, _mark);
        bb._chars = _chars;
        bb._isReadOnly = _isReadOnly;  /* direct to avoid method call opcode */
        return bb;
    }

    public CharBuffer asReadOnlyBuffer() {
        CharBuffer bb = new CharBuffer(_capacity, _limit, _position, _mark);
        bb._chars = _chars;
        bb._isReadOnly = true; /* direct to avoid method call opcode */
        return bb;
    }

    public CharBuffer slice() {
    	int rem = _limit - _position; /* recalc to avoid method call opcode */
        CharBuffer bb = new CharBuffer(rem, rem, 0, -1);
        bb._chars = _chars;
        bb._isReadOnly = _isReadOnly;
        bb._arrayOffset = _position + _arrayOffset;
        return bb;
    }

    public CharBuffer compact() {

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

    	int len = _limit - _position;

    	System.arraycopy(_chars, _arrayOffset + _position, _chars, _arrayOffset, len);

    	_limit = _capacity;
    	_position = len;

    	return this;

    }

}
