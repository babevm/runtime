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

package babe.i18n.codecs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import babe.i18n.Codec;
import babe.i18n.CodecException;

/**
 * A codec for the ISO-8859-1 standard.  ISO-8859-1 is a single byte encoder so in that
 * sense, this is nice and easy.  For encoding, chars are cast to bytes and put into an array.
 * For decoding, it is the opposite.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public class ISO_8859_1 extends Codec {

	public int decode(ByteBuffer src, CharBuffer dst) throws CodecException {

		byte[] sa = src.array();
		int sp = src.arrayOffset() + src.position();
		int sl = src.arrayOffset() + src.limit();
		assert (sp <= sl);
		sp = (sp <= sl ? sp : sl);

		char[] da = dst.array();
		int dp = dst.arrayOffset() + dst.position();
		int dl = dst.arrayOffset() + dst.limit();
		assert (dp <= dl);
		dp = (dp <= dl ? dp : dl);

		try {
			while (sp < sl) {
				byte b = sa[sp];
				if (dp >= dl)
					return Codec.OVERFLOW;
				da[dp++] = (char) (b & 0xff);
				sp++;
			}
			return Codec.UNDERFLOW;
		} finally {
			src.position(sp - src.arrayOffset());
			dst.position(dp - dst.arrayOffset());
		}
	}

	public int decodeSize(ByteBuffer src) throws CodecException {
		// ISO 8859 is a single byte encoding. nr in = nr out
		return src.remaining();
	}

	public int encode(CharBuffer src, ByteBuffer dst) throws CodecException {

		char[] sa = src.array();
		int sp = src.arrayOffset() + src.position();
		int sl = src.arrayOffset() + src.limit();
		assert (sp <= sl);
		sp = (sp <= sl ? sp : sl);

		byte[] da = dst.array();
		int dp = dst.arrayOffset() + dst.position();
		int dl = dst.arrayOffset() + dst.limit();
		assert (dp <= dl);
		dp = (dp <= dl ? dp : dl);

		try {
			while (sp < sl) {
				char c = sa[sp];
				if (c <= '\u00FF') {
					if (dp >= dl)
						return Codec.OVERFLOW;
					da[dp++] = (byte) c;
					sp++;
					continue;
				}
			}
			return Codec.UNDERFLOW;
		} finally {
			src.position(sp - src.arrayOffset());
			dst.position(dp - dst.arrayOffset());
		}
	}

	public int encodeSize(CharBuffer src) throws CodecException {
		// ISO 8859 is a single byte encoding. nr in = nr out
		return src.remaining();
	}

}
