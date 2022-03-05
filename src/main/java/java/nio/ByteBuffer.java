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
public final class ByteBuffer extends Buffer {

    private ByteOrder _order = ByteOrder.BIG_ENDIAN;

    protected byte[] _bytes;

    private boolean _isDirect;

    private ByteBuffer(int capacity, int limit, int position, int mark) {
    	super(capacity, limit, position, mark);
	}

    private ByteBuffer(byte[] bytes, int offset, int length) {
        this(length, offset + length , offset, Buffer.MARK_UNDEFINED);
        _bytes = bytes;
    }

    private ByteBuffer(byte[] bytes) {
        this(bytes.length, bytes.length, 0, Buffer.MARK_UNDEFINED);
        _bytes = bytes;
    }

    public static ByteBuffer allocateDirect(int capacity) {

		if (capacity < 0)
			throw new IllegalArgumentException();

		ByteBuffer bb = new ByteBuffer(new byte[capacity]);

		bb._isDirect = true;

		return bb;
	}

    public static ByteBuffer allocate(int capacity) {

    	if (capacity < 0)
    		throw new IllegalArgumentException();

        return new ByteBuffer(new byte[capacity]);
    }

    public static ByteBuffer wrap(byte[] array) {
        return new ByteBuffer(array);
    }

    public static ByteBuffer wrap(byte[] array, int offset, int length) {

    	if ( (offset < 0) || (offset > array.length) )
    		throw new IndexOutOfBoundsException();

    	if ( (length < 0) || (length > array.length - offset) )
    		throw new IndexOutOfBoundsException();

        return new ByteBuffer(array, offset, length);
    }

    public ByteBuffer get(byte[] dst, int offset, int length) {

    	if ((offset < 0) || (offset > dst.length))
    		throw new IndexOutOfBoundsException();

    	if ((length < 0) || (length > dst.length - offset ))
    		throw new IndexOutOfBoundsException();

    	if (length > remaining())
    	    throw new BufferUnderflowException();

    	System.arraycopy(_bytes, _arrayOffset + _position, dst, offset, length);
    	_position += length;

        return this;
    }

    public ByteBuffer get(byte[] dst) {
        return get(dst, 0, dst.length);
    }

    public ByteBuffer put(ByteBuffer src) {

    	if (src == this)
    		throw new IllegalArgumentException();

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

		int n = src.remaining();

		if (n > remaining())
		    throw new BufferOverflowException();

    	if (n > 0) {
            put(src.get(new byte[n]));
        }

        return this;
    }

    public ByteBuffer put(byte[] src, int offset, int length) {

    	if ((offset < 0) || (offset > src.length))
    		throw new IndexOutOfBoundsException();

    	if ((length < 0) || (length > src.length - offset ))
    		throw new IndexOutOfBoundsException();

    	if (length > remaining())
    	    throw new BufferOverflowException();

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

    	System.arraycopy(src, offset, _bytes, _arrayOffset + _position, length);
    	_position += length;

        return this;
    }

    public ByteBuffer put(byte[] src) {
        return put(src, 0, src.length);
    }

    public boolean hasArray() {
        return ( (true) && (!_isReadOnly)) ;
    }

    public byte[] array() {

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

        return _bytes;
    }

    public int arrayOffset() {

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

        return _arrayOffset;
    }

    public ByteOrder order() {
        return _order;
    }

    public ByteBuffer order(ByteOrder endian) {
        _order = endian;
        return this;
    }

    public byte get() {

    	if (_position >= _limit)
    		throw new BufferUnderflowException();

        return _bytes[_arrayOffset + _position++];
    }

    public ByteBuffer put(byte b) {

    	if (_position >= _limit)
    		throw new BufferOverflowException();

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

        _bytes[_arrayOffset + _position++] = b;
        return this;
    }

    public byte get(int index) {

    	if ((index < 0) || (index >= _limit))
    		throw new IndexOutOfBoundsException();

        return _bytes[_arrayOffset + index];
    }

    public ByteBuffer put(int index, byte b) {

    	if ((index < 0) || (index >= _limit))
        		throw new IndexOutOfBoundsException();

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

        _bytes[_arrayOffset + index] = b;
        return this;
    }

    public boolean isDirect() {
        return _isDirect;
    }

    public char getChar() {

    	if ( remaining() < 2)
    		throw new BufferUnderflowException();

    	int index = _position;

    	_position += 2;

        return getChar(index);
    }

    public ByteBuffer putChar(char value) {

    	if ( remaining() < 2)
    		throw new BufferOverflowException();

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

    	int index = _position;

    	_position += 2;

        putChar(index, value);
        return this;
    }

    public char getChar(int index) {

    	if ( (index < 0) || (index >= _limit-1))
    		throw new IndexOutOfBoundsException();

        return (char) getShort(index);
    }

    public ByteBuffer putChar(int index, char value) {
        return putShort(index, (short) value);
    }

    public short getShort() {

    	if (remaining() < 2 )
    		throw new BufferUnderflowException();

    	int index = _position;

    	_position += 2;

        return getShort(index);
    }

    public ByteBuffer putShort(short value) {

    	if (remaining() < 2 )
    		throw new BufferOverflowException();

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

    	int index = _position;

    	_position += 2;

        return putShort(index, value);
    }

    public short getShort(int index) {

    	if ( (index < 0) || (index >= _limit-1))
    		throw new IndexOutOfBoundsException();

    	int pos = _arrayOffset + index;

        if (_order == ByteOrder.LITTLE_ENDIAN) {
            return (short) ((_bytes[pos] & 0xff) + (_bytes[++pos] << 8));
        } else {
            return (short) ((_bytes[pos] << 8) + (_bytes[++pos] & 0xff));
        }
    }

    public ByteBuffer putShort(int index, short value) {

    	if ( (index < 0) || (index >= _limit-1))
    		throw new IndexOutOfBoundsException();

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

    	int pos = _arrayOffset + index;

    	if (_order == ByteOrder.LITTLE_ENDIAN) {
            _bytes[pos] = (byte) value;
            _bytes[++pos] = (byte) (value >> 8);
        } else {
            _bytes[pos] = (byte) (value >> 8);
            _bytes[++pos] = (byte) value;
        }
        return this;
    }

    public int getInt() {

    	if (remaining() < 4)
    		throw new BufferUnderflowException();

    	int index = _position;

    	_position += 4;

        return getInt(index);
    }

    public ByteBuffer putInt(int value) {

    	if (remaining() < 4)
    		throw new BufferOverflowException();

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

    	int index = _position;

    	_position += 4;

        return putInt(index, value);
    }

    public int getInt(int index) {

    	if ( (index < 0) || (index >= _limit-3))
    		throw new IndexOutOfBoundsException();

    	int pos = _arrayOffset + index;

        if (_order == ByteOrder.LITTLE_ENDIAN) {
            return (_bytes[pos] & 0xff) + ((_bytes[++pos] & 0xff) << 8)
                    + ((_bytes[++pos] & 0xff) << 16)
                    + ((_bytes[++pos] & 0xff) << 24);
        } else {
            return (_bytes[pos] << 24) + ((_bytes[++pos] & 0xff) << 16)
                    + ((_bytes[++pos] & 0xff) << 8)
                    + (_bytes[++pos] & 0xff);
        }
    }

    public ByteBuffer putInt(int index, int value) {

    	if ( (index < 0) || (index >= _limit-3))
    		throw new IndexOutOfBoundsException();

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

    	int pos = _arrayOffset + index;

        if (_order == ByteOrder.LITTLE_ENDIAN) {
            _bytes[pos] = (byte) value;
            _bytes[++pos] = (byte) (value >> 8);
            _bytes[++pos] = (byte) (value >> 16);
            _bytes[++pos] = (byte) (value >> 24);
        } else {
            _bytes[pos] = (byte) (value >> 24);
            _bytes[++pos] = (byte) (value >> 16);
            _bytes[++pos] = (byte) (value >> 8);
            _bytes[++pos] = (byte) value;
        }
        return this;
    }

    public long getLong() {

    	if (remaining() < 8)
    		throw new BufferUnderflowException();

    	int index = _position;

    	_position += 8;

        return getLong(index);
    }

    public ByteBuffer putLong(long value) {

    	if (remaining() < 8)
    		throw new BufferOverflowException();

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

    	int index = _position;

    	_position += 8;

        return putLong(index, value);
    }

    public long getLong(int index) {

    	if ( (index < 0) || (index >= _limit-7))
    		throw new IndexOutOfBoundsException();

    	int pos = _arrayOffset + index;

        if (_order == ByteOrder.LITTLE_ENDIAN) {
            return (_bytes[pos] & 0xff) + ((_bytes[++pos] & 0xff) << 8)
                    + ((_bytes[++pos] & 0xff) << 16)
                    + ((_bytes[++pos] & 0xffL) << 24)
                    + ((_bytes[++pos] & 0xffL) << 32)
                    + ((_bytes[++pos] & 0xffL) << 40)
                    + ((_bytes[++pos] & 0xffL) << 48)
                    + (((long) _bytes[++pos]) << 56);
        } else {
            return (((long) _bytes[pos]) << 56)
                    + ((_bytes[++pos] & 0xffL) << 48)
                    + ((_bytes[++pos] & 0xffL) << 40)
                    + ((_bytes[++pos] & 0xffL) << 32)
                    + ((_bytes[++pos] & 0xffL) << 24)
                    + ((_bytes[++pos] & 0xff) << 16)
                    + ((_bytes[++pos] & 0xff) << 8)
                    + (_bytes[++pos] & 0xffL);
        }
    }

    public ByteBuffer putLong(int index, long value) {

    	if ( (index < 0) || (index >= _limit-7))
    		throw new IndexOutOfBoundsException();

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

    	int pos = _arrayOffset + index;

        if (_order == ByteOrder.LITTLE_ENDIAN) {
            _bytes[pos] = (byte) value;
            _bytes[++pos] = (byte) (value >> 8);
            _bytes[++pos] = (byte) (value >> 16);
            _bytes[++pos] = (byte) (value >> 24);
            _bytes[++pos] = (byte) (value >> 32);
            _bytes[++pos] = (byte) (value >> 40);
            _bytes[++pos] = (byte) (value >> 48);
            _bytes[++pos] = (byte) (value >> 56);
        } else {
            _bytes[pos] = (byte) (value >> 56);
            _bytes[++pos] = (byte) (value >> 48);
            _bytes[++pos] = (byte) (value >> 40);
            _bytes[++pos] = (byte) (value >> 32);
            _bytes[++pos] = (byte) (value >> 24);
            _bytes[++pos] = (byte) (value >> 16);
            _bytes[++pos] = (byte) (value >> 8);
            _bytes[++pos] = (byte) value;
        }
        return this;
    }

    public ByteBuffer duplicate() {
        ByteBuffer bb = new ByteBuffer(_capacity, _limit, _position, _mark);
        bb._bytes = _bytes;
        bb._isReadOnly = _isReadOnly;  /* direct to avoid method call opcode */
        return bb;
    }

    public ByteBuffer asReadOnlyBuffer() {
        ByteBuffer bb = new ByteBuffer(_capacity, _limit, _position, _mark);
        bb._bytes = _bytes;
        bb._isReadOnly = true; /* direct to avoid method call opcode */
        return bb;
    }

    public ByteBuffer slice() {
    	int rem = _limit - _position; /* recalc to avoid method call opcode */
        ByteBuffer bb = new ByteBuffer(rem, rem, 0, -1);
        bb._bytes = _bytes;
        bb._isReadOnly = _isReadOnly;
        bb._arrayOffset = _position + _arrayOffset;
        return bb;
    }

    public ByteBuffer compact() {

    	if (_isReadOnly)
    		throw new ReadOnlyBufferException();

    	int len = _limit - _position;

    	System.arraycopy(_bytes, _arrayOffset + _position, _bytes, _arrayOffset, len);

    	_limit = _capacity;
    	_position = len;

    	return this;

    }

}