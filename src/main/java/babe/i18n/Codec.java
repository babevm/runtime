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

package babe.i18n;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * A facility for encoding and decoding bytes to chars and vice versa. Modelled
 * a little on the Charset class from J2SE nio but a lot smaller and simpler.
 * For example, whereas the J2SE has CharsetEncode and CharsetDecoder classes -
 * we do not - all encoders and decoders are subclasses of this Codec class.
 *
 * Also, the results of encoding or decoding in J2SE are static objects in the
 * CoderResult class - here they are int - to make it easier to put an encoder
 * into native code.
 *
 * This Codec class serves two purposes. Firstly it has a number of static
 * helper methods for quickly converting bytes/chars without have to jump
 * through hoops with ByteBuffers and so on. Converting between byte[] and
 * char[] will, no doubt, be the most common usage and, no doubt, with the
 * default charset.
 *
 * All codecs are subclasses of this Codec class. Codec implementers override
 * the methods <code>encode</code>, <code>decode</code>,
 * <code>encodeSize</code>, and <code>decodeSize</code> as they require.
 *
 * While Coded subclasses may do the heavy lifting, Buffers are used to carry
 * the data around. All buffers in this VM are backed by arrays, so rather than
 * using Streams we use Buffers to get some block-at-a-time speed.
 *
 * New codecs are registered by setting a System property named after the codec
 * using the following format:
 *
 * <pre>
 * Codec.CODEC_PROPERTY_PREFIX
 * </code>
 * +name
 * </pre>
 *
 * Where 'name' is the name of your codec and the value of this System property
 * is the full class name of the Codec subclass that is doing the business.
 *
 * The VM has a 'default' codec. This may be changed to setting the System
 * property <code>Codec.CODEC_DEFAULT_PROPERTY</code> to the name of a encoder
 * to use. Like all other codecs, the actual implementing class name will be
 * found using the above method, so you also have to register the System
 * property for the class name as well. Get it? To change the default encoding
 * you set two system properties.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public class Codec {

	public static final int UNDERFLOW = 0;
	public static final int OVERFLOW = 1;

	public static String DEFAULT_ENCODING;

	public static final String CODEC_DEFAULT_PROPERTY = "babe.i18n.codec.default";
	public static final String CODEC_PROPERTY_PREFIX = "babe.i18n.codec.";

	private static String _lastRequestedCodecName;
	private static Codec _lastRequestedCodec;

	static {

		// make sure the system property for the default codec is set */
		System.setProperty("babe.i18n.codec.ISO_8859_1", "babe.i18n.codecs.ISO_8859_1");

		// establish the default codec. If it is not defined use the ISO-8859-1
		// codec.
		DEFAULT_ENCODING = System.getProperty(CODEC_DEFAULT_PROPERTY);
		if (DEFAULT_ENCODING == null) {
			DEFAULT_ENCODING = "ISO_8859_1";
		}
	}

	/**
	 * Get a Codec for the given name. If a codec cannot be found an
	 * UnsupportedEncodingException is thrown. The codec name is normalised name
	 * using {@link #normaliseEncodingName(String)}.
	 *
	 * This 'normalised' name is then appended to the
	 * Codec.CODEC_PROPERTY_PREFIX to arrive at a name that will be the key of a
	 * System property. That system property will contain the full name of the
	 * implementing class. One of them is instantiated and returned.
	 *
	 * @param name -
	 *            the String name of the codec to use
	 * @return a Codec object that implements the requested codec.
	 * @throws UnsupportedEncodingException
	 */
	public static final Codec forName(String name)
			throws UnsupportedEncodingException {

		Codec codec;
		String classname;

		if (name == _lastRequestedCodecName)
			return _lastRequestedCodec;

		classname = System.getProperty(CODEC_PROPERTY_PREFIX + normaliseEncodingName(name));

		if (classname == null)
			throw new UnsupportedEncodingException(name);

		try {
			codec = (Codec) Class.forName(classname).newInstance();
		} catch (Exception e) {
			if (e instanceof ClassNotFoundException) {
				throw new UnsupportedEncodingException(name);
			}
			throw new RuntimeException("Error loading codec: " + name)
					.initCause(e);
		}

		return codec;
	}

	/**
	 * Given a codec name, normalise it by turning '-' or ':' into '_' and
	 * uppercasing it.
	 *
	 * @param name the codec name to normalise.
	 * @return the normalised name.
	 */
	public static final String normaliseEncodingName(String name) {

		// turn '-' or ':' into '_' an uppercase.

		String newName = name.replace('-', '_').replace(':', '_').toUpperCase();

		if (newName.equals("US_ASCII"))
			newName = "ISO_8859_1";

		return newName;
	}

	/**
	 * Decode a byte array using the default codec.
	 *
	 * @param src
	 * @param offset
	 * @param length
	 * @return
	 */
	public static final char[] decode(byte[] src, int offset, int length) {
		try {
			return decode(src, offset, length, DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Missing default encoding");
		}
	}

	/**
	 *
	 * Decode a byte array using a given codec name.
	 *
	 * @param src
	 * @param offset
	 * @param length
	 * @param enc
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static final char[] decode(byte[] src, int offset, int length,
			String enc) throws UnsupportedEncodingException {

		Codec codec;

		if ((offset < 0) || (length < 0) || (offset > src.length - length))
			throw new IndexOutOfBoundsException(Integer.toString(offset));

		if (enc == _lastRequestedCodecName) {
			codec = _lastRequestedCodec;
		} else {
			codec = _lastRequestedCodec = forName(enc);
			_lastRequestedCodecName = enc;
		}

		ByteBuffer bytebuffer = ByteBuffer.wrap(src, offset, length);
		int decodeSize = codec.decodeSize(bytebuffer);

		CharBuffer charbuffer = CharBuffer.allocate(decodeSize);

		codec.decode(bytebuffer, charbuffer);

		return charbuffer.array();
	}

	/**
	 *
	 * Encode a char array using the default codec.
	 *
	 * @param src
	 * @param offset
	 * @param length
	 * @return
	 */
	public static final byte[] encode(char[] src, int offset, int length) {
		try {
			return encode(src, offset, length, DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Missing default encoding");
		}
	}

	/**
	 *
	 * Encode a char array using a given codec name.
	 *
	 * @param src
	 * @param offset
	 * @param length
	 * @param enc
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static final byte[] encode(char[] src, int offset, int length,
			String enc) throws UnsupportedEncodingException {

		Codec codec;

		if ((offset < 0) || (length < 0) || (offset > src.length - length))
			throw new IndexOutOfBoundsException(Integer.toString(offset));

		if (enc == _lastRequestedCodecName) {
			codec = _lastRequestedCodec;
		} else {
			codec = _lastRequestedCodec = forName(enc);
			_lastRequestedCodecName = enc;
		}

		CharBuffer charbuffer = CharBuffer.wrap(src, offset, length);
		int encodeSize = codec.encodeSize(charbuffer);

		ByteBuffer bytebuffer = ByteBuffer.allocate(encodeSize);

		codec.encode(charbuffer, bytebuffer);

		return bytebuffer.array();
	}

	/**
	 * Default implementation of encoding the contents of a CharBuffer to
	 * a ByteBuffer.  Throws a CodecException to say it is not supported.
	 *
	 * @param src -
	 *            the CharBuffer where the char come from ...
	 * @param dst -
	 *            the ByteBuffer where the bytes go to ...
	 * @return either Codec.UNDERFLOW or Codec.OVERFLOW depending on how the
	 *         buffers where filled. Refer to the J2SE documentation for when
	 *         these occur.
	 *
	 * @throws CodecException
	 *             if anything at all goes wrong.
	 */
	public int encode(CharBuffer src, ByteBuffer dst) throws CodecException {
		throw new CodecException("encode not supported");
	}

	/**
	 * Given a CharBuffer, calculate how many bytes are required to hold the
	 * encoded contents.  This default implementation throws a CodecException.
	 *
	 * @param src -
	 * @return size of byte[] required to hold encoded contents.
	 * @throws CodecException
	 */
	public int encodeSize(CharBuffer src) throws CodecException {
		throw new CodecException("encode not supported");
	}

	/**
	 * Default implementation of decoding the contents of a ByteBuffer to
	 * a CharBuffer.  Throws a CodecException to say it is not supported.
	 *
	 * @param src
	 * @param dst
	 * @return
	 * @throws CodecException
	 */
	public int decode(ByteBuffer src, CharBuffer dst) throws CodecException {
		throw new CodecException("decode not supported");
	}

	/**
	 * Given a ByteBuffer, calculate how many chars are required to hold the
	 * decoded contents.  This default implementation throws a CodecException.
	 *
	 * @param src
	 * @return
	 * @throws CodecException
	 */
	public int decodeSize(ByteBuffer src) throws CodecException {
		throw new CodecException("decode not supported");
	}

}
