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
public abstract class Buffer {

	final int _capacity;

	static final int MARK_UNDEFINED = -1;

	int _limit;

	int _position;

	int _mark;

	int _arrayOffset;

	boolean _isReadOnly;

	Buffer(int capacity, int limit, int position, int mark) {
		_capacity = capacity;
		_limit = limit;
		_position = position;
		_mark = mark;
	}

	public final int capacity() {
		return _capacity;
	}

	public final Buffer clear() {
		_limit = _capacity;
		_position = 0;
		_mark = MARK_UNDEFINED;
		return this;
	}

	public final Buffer flip() {
		_limit = _position;
		_position = 0;
		_mark = MARK_UNDEFINED;
		return this;
	}

	public final boolean hasRemaining() {
		return _limit - _position > 0;
	}

	public boolean isReadOnly() {
		return _isReadOnly;
	}

	public final int limit() {
		return _limit;
	}

	public final Buffer limit(int newLimit) {

		if ((newLimit < 0) || (newLimit > _capacity)) {
			throw new IllegalArgumentException();
		}

		if (newLimit < _mark) {
			_mark = MARK_UNDEFINED;
		}

		if (_position > newLimit) {
			_position = newLimit;
		}
		_limit = newLimit;
		return this;
	}

	public final Buffer mark() {
		_mark = _position;
		return this;
	}

	public final int position() {
		return _position;
	}

	public final Buffer position(int newPosition) {

		if ((newPosition < 0) || (newPosition > _limit)) {
			throw new IllegalArgumentException();
		}

		if (newPosition <= _mark) {
			_mark = MARK_UNDEFINED;
		}
		_position = newPosition;
		return this;
	}

	public final int remaining() {
		return _limit - _position;
	}

	public final Buffer reset() {

		if (_mark == MARK_UNDEFINED) {
			throw new InvalidMarkException();
		}
		_position = _mark;
		return this;
	}

	public final Buffer rewind() {
		_position = 0;
		_mark = MARK_UNDEFINED;
		return this;
	}

	abstract boolean hasArray();
}