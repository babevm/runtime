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

package babe.xml;

/**
 *
 * An XML parser. The job of the <code>Parser</code> is to consume a byte
 * representation of an XML document and return an array of <code>int</code>
 * representing the node structure of the XML document. The node types to be
 * represented in the node structure are defined in the {@link Document} class
 * as <code>NODE_</code>* fields.
 *
 * The original bytes of the XML document are unchanged.
 *
 * The integers contained in the resultant <code>int</code> node array are not
 * just ordinary integers - they contain multiple values within the single
 * <code>int</code> respectively representing the node type, depth, and byte
 * offset of the node with the original XML document.
 *
 * Each <code>int</code> in the node array thus is a complex <code>int</code>
 * representing these three pieces of information. The structure of each node is
 * as such:
 *
 * <pre>
 *
 *  | .... 32 bits ..												|
 *
 *  | | 4 bits| 6 bits    | 21 bits									|
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  | | TYPE  | DEPTH     | OFFSET									|
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 * </pre>
 *
 * The first bit is ignored as java has no notion of unsigned integers and using
 * a '1' in the top bit makes bit shifting awkward - so unfortunately we do not
 * use it.
 *
 * Thereafter, the three segments are represented as 4 bits for the node type
 * (these are the <code>NODE_</code>* constants defined in {@link Document},
 * 6 bits for the depth of the node, and 21 bits for the byte offset into the
 * XML document where the node begins.
 *
 * This structure does, of course, limit the size and depth of an XML document,
 * but remember we are talking about embedded java here - 4gb documents are not
 * the norm. In fact, this limits the size of an XML document to 2^21 offsets,
 * and 2^6 depth - which means a max document size of (approximately) 2mb with
 * an element depth of 63. Big enough I reckon.
 *
 * The resultant node array is intended for use by the {@link Document} class.
 * However, one of the main reasons for separating out parsing and navigation is to permit
 * the re-use of the node array index that this parser creates.  Indeed, the node array
 * could be persisted and used again later on the same document.
 *
 * <h2>Parsing</h2>
 *
 * Parsing is performed by the {@link #parse(byte[], int, int)} method. This
 * method is intended to operate either on a whole XML document at once by
 * calling this method just once once and passing the entire XML document in
 * byte form, or piecemeal by calling it repeatedly passing in segments of the
 * XML bytes.
 *
 * At any time, even during parsing, the {@link #getNodes()} may be called to retrieve
 * the current array of parsed nodes.  Note that is the responsibility of the parser to
 * maintain the node list. If subsequent parsing occurs there can be no
 * guarantee that a further call to {@link getNodes()} will return the same
 * array - it may have been replaced (because it was expanded).
 *
 * The parser does not know when it has reached the end of the document and
 * requires no semantic knowledge of the contents of the XML - it just examines
 * the structure.
 *
 * Reuse of the same parser object for another parse is enabled by the
 * {@link #reset()} method.
 *
 * The <code>offset</code> segment of a parsed node always begins at the first
 * byte of the node. For XML markup nodes such as elements, comments and CDATA,
 * the offset points to the '<code>&lt;</code>' that starts the markup. For
 * attribute nodes, the offset points to the first character of the attribute
 * name. For character nodes, the offset points to the first character.
 *
 * <h2>Look-ahead</h2>
 *
 * The parse returns the number of bytes it has read from the passed-in bytes.
 * Generally, this will be the same as the passed-in length - especially if the
 * bytes of an entire XML document are passed-in.
 *
 * However, in order for the parser to do its job effectively, it implements a
 * 'look-ahead' mechanism whereby in order to determine the type of node it is
 * dealing with (say - after a ! character) it needs to look ahead a number of
 * bytes. If the bytes passed into the <code>parse</code> method end on an
 * ambiguous boundary this means the parser cannot determine the next node type
 * from the bytes it has. In this case, the parser method will return <i>less</i>
 * than the number of bytes it has been requested to read. It is the
 * responsibility of the calling method to recognise this and re-feed the unread
 * bytes back into <code>parse</code> with the next lot of bytes.
 *
 * Of course, if you have a lame head and try to parse an XML document one byte
 * at a time by passing '1' as the length to the parse method, you will, of
 * course, never get there. The parsing processing will need more than 1 byte at
 * a time to do its job.
 *
 * <h2>Entity References</h2>
 *
 * The parser can be made to emit, or not emit, entity references as nodes in
 * the resultant node array. Typically, application developers do not want nodes
 * of type <code>NODE_ENTITY_REFERENCE</code> in the node array - it just muddles things.
 * However, if the true and exact nature of the XML document is to be
 * represented in the node array, set {@link #preserveEntityReferences} to true.
 *
 * <h2>Some implementation notes </h2>
 *
 * This parser is implemented as a state machine. The XML bytes are read
 * one-at-a-time and the parsing is given a state on what character it is
 * currently parsing. The parser also looks ahead to see what is coming. This is
 * not a full-on implementation of a XML grammar parser - much simpler than
 * that. The current state help to determine what the next state may be and also
 * perform some limited validations.
 *
 * This parser is designed to be absolutely as fast as it can be with no GC. For that
 * reason there are some limitations. Rather than converting all bytes to chars
 * via a unicode character reader, all bytes are assumed to be 'chars'. This
 * means that only 8 bit encodings such as UTF-8 and ISO-8858-*, and US-ASCII
 * are supported.
 *
 * Treating bytes in this manner means this parser requires no semantic
 * understanding of the characters or the encoding in order to create the node
 * structure - it simply whips through the bytes looking for XML related byte
 * sequences and notes their type, depth and offset.
 *
 * This parser only does rough checks for well formed-ness. When it
 * encounters something unexpected it will cause an exception, but it will not
 * check everything.
 *
 * The parser uses the <i>structure</i> of the document and not the contents to
 * create the node tree. So mismatching names at the start and end of an element
 * do not cause issues - the parser is interested only in the XML markup
 * delimiters.
 *
 * So, developers, make sure your XML documents are in good form before using
 * this parser. And certainly do not rely on it checking the validity of your
 * XML documents before using it in the next mars explorer. The behaviour of
 * this parser on badly formed document is undefined.
 *
 * Get it ... ? <b>THIS IS A NON-VALIDATING PARSER.</b>
 *
 * A few limitations and a narrowing of XML spec compatibility is good for performance.
 * In some testing on my development machine I found this parser to be approximately
 * 20 times faster (yes, 20) than a Sun Java SAX parser with an empty content handler.
 * So, keeping it simple gives you an order of magnitude speed difference.
 * Developers, when you look at the source you'll note:
 *
 * <ul>
 * <li>The entire parsing process occurs within a single method call. No
 * expensive method calls occur except to create a node in the node array.
 * <li>The XML bytes are traversed only once using a simple state machine.
 * <li>No knowledge of the contents of the document are required - not even the
 * encoding is used to convert any bytes to chars for any purpose.
 * <li>No temporary objects are created during parsing. No Strings, no
 * StringBuffers, no char arrays. GC will occur only when the node array needs
 * expansion, or when a String is created as a message when an exception
 * occurs - which kind of doesn't matter.
 * <li>Object instance field lookups are minimised by having fields copied into
 * local variables first.
 * <li>The input data is not considered like a stream where we are constantly
 * getting the next char from the stream for comparison. A byte array is much
 * faster.
 * </ul>
 *
 * All this adds up to a simple parser that does a simple job that could (if
 * required) be translated to C and placed in the native layer very easily. Yes,
 * easy conversion to C is another design goal.
 *
 * Looking at the code may be a lot to take in.  It may not be obvious why some
 * state changes are made.  But, the best way to understand the following code
 * is to create a small XMl document and step through the code to watch
 * it work.  it is a lot harder to explain all the state transition than it
 * is to observe them and understand.
 *
 * <h2>Some XML conformance notes:</h2>
 *
 * <ul>
 * <li>In XML 1.0, if white space appears before the XML prologue declaration,
 * it will be treated as a processing instruction. This parser will just eat all
 * spaces at the start of the document - so spaces before the XML declaration
 * are just ignored.
 * <li>Any char value &lt;= ' ' is considered whitespace.
 * </ul>
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public class Parser {

	private static final int STATE_DOCUMENT = 0;
	private static final int STATE_CHARACTERS = 1;
	private static final int STATE_MARKUP = 2;
	private static final int STATE_COMMENT = 3;
	private static final int STATE_PI = 4;
	private static final int STATE_CDATA = 5;
	private static final int STATE_OPEN_TAGxREAD_ELEM_NAME = 6;
	private static final int STATE_OPEN_TAGxELEM_NAME_READ = 7;
	private static final int STATE_OPEN_TAGxREAD_ATTR_NAME = 8;
	private static final int STATE_OPEN_TAGxATTR_NAME_READ = 9;
	private static final int STATE_OPEN_TAGxEQUAL_READ = 10;
	private static final int STATE_OPEN_TAGxREAD_ATTR_VALUE_SIMPLE_QUOTE = 11;
	private static final int STATE_OPEN_TAGxREAD_ATTR_VALUE_DOUBLE_QUOTE = 12;
	private static final int STATE_OPEN_TAGxEMPTY_TAG = 13;
	private static final int STATE_CLOSE_TAGxREAD_ELEM_NAME = 14;
	private static final int STATE_CLOSE_TAGxELEM_NAME_READ = 15;
	private static final int STATE_DTD = 16;
	private static final int STATE_DTD_INTERNAL = 17;
	private static final int STATE_DTD_MARKUP = 18;
	private static final int STATE_ENTITY_REFERENCE = 19;
	private static final int STATE_PROLOG = 20;
	private static final int STATE_ERROR = 100;

	/* to track the XML depth of the parsing */
	private int _parseDepth = 0;

	/* the array of parsed nodes */
	public int[] _nodes;

	/* the current index into the '_nodes' node array as the doc is being parsed.
	 * Starts at index 1, index 0 is for the count of nodes in the node array. */
	private int _nodeIndex = 1;

	/**
	 * The {@link #preserveEntityReferences} field determines whether the parser
	 * should emit entity references as nodes in the parsed-node array. Setting
	 * to <code>true</code> will result in nodes of type
	 * {@link Document#NODE_ENTITY_REFERENCE} appearing in the node list if an
	 * entity reference occurs in the XML document. Setting to
	 * <code>false</code> (default) will simply mean that entity references
	 * are not represented as nodes in the node array.
	 *
	 */
	public boolean preserveEntityReferences;

	/* storage for the parsing 'registers' between parse runs (if more than one
	 * is performed). Each of these will have a local equivalent in the parse()
	 * method */
	private int _xmlidx = 0;
	private int _state = STATE_DOCUMENT;
	private boolean _isChars = false;

	public Parser(int estimatedNodes) {
		reset(estimatedNodes);
	}

	/**
	 * Gets the int node array that represents the structure of the XML document
	 * as created by calling {@link #parse(byte[], int, int)}.
	 *
	 * The first element of the array is the number of nodes in the array.
	 *
	 * @return an int array of nodes, or if no nodes existed, an array with a
	 *         single element with the value zero in it.
	 */
	public int[] getNodes() {
		return _nodes;
	}

	/**
	 * Ready the parser for a second or subsequent document parsing. This will
	 * have the effect of creating a new empty nodes array ready to be populated
	 * by a call to {@link #parse(byte[], int, int)}. A call to
	 * {@link #getNodes()} immediately following this method called must return
	 * zero nodes.
	 *
	 * @param estimatedNodes
	 *            the initial size of the node array.
	 */
	public void reset(int estimatedNodes) {
		_xmlidx = 0;
		_state = STATE_DOCUMENT;
		_isChars = false;
		_nodeIndex = 1;
		_parseDepth = 0;
		_nodes = new int[estimatedNodes + 1];
	}

	// **************************************************************************************
	// Parsing
	// **************************************************************************************

	/**
	 * Sets the state of the parser to error and throw and {@link XMLException}
	 * with the given message.
	 *
	 * @param message
	 *            the error message to associated with the thrown exception
	 * @throws XMLException
	 */
	private void parsingError(String message) throws XMLException {
		_state = STATE_ERROR;
		throw new XMLException(message);
	}

	/**
	 *
	 * Creates a node in the node array of the given type and offset.
	 *
	 * @param type
	 *            the type of the node
	 * @param offset
	 *            the offset into the XML bytes this node occurs at - if an XML
	 *            document parse occurs across multiple calls to parse, this
	 *            offset will be cumulative.
	 *
	 * @throws XMLException
	 *             if the offset, or depth exceed the defined limits.
	 */
	private void createNode(int type, int offset) throws XMLException {

		/* 'no can do' for depth greater than 2^6. Only 6 bits allocated to depth. */
		if (_parseDepth > 63) // 63 = 0x3F = 6 bits
			parsingError("Max XML depth exceeded.");

		/*
		 * 'no can do' for offsets greater than 2^21 - only 21 bits allocated to
		 * depth
		 */
		if (offset > 2097151) // 2097151 = 0x1FFFFF = 21 bits
			parsingError("Max XML offset exceeded.");

		/*
		 * if the nodes array is not large enough to house the new node -
		 * enlarge it.
		 */
		if (_nodeIndex == _nodes.length) {
			int[] newArray = new int[_nodeIndex * 2];
			System.arraycopy(_nodes, 0, newArray, 0, _nodeIndex);
			_nodes = newArray;
		}

		/*
		 * a node is stored as an int. top bit is unused, 4 bits type, next 6
		 * bits is depth, and the remaining 21 bits is offset. this gives a max
		 * file size of 2^21 or just over 2mb and a max depth = 64.
		 */
		_nodes[_nodeIndex++] = (type << 27) + (_parseDepth << 21) + offset;
	}

	/**
	 * Parse a block of XML bytes. This method has the effect of populating the
	 * <code>int</code> array of nodes that can be retrieved by
	 * {@link #getNodes()}.
	 *
	 * @param bytes
	 *            the byte of the XML document to parse - this could be the
	 *            while document of a portion of it.
	 * @param offset
	 *            the offset into the <code>bytes</code> argument to start
	 *            read at.
	 * @param length
	 *            the number of bytes to read from the <code>bytes</code>
	 *            argument.
	 * @return the number of bytes read from the <code>bytes</code> argument.
	 *         This may be less than the requested number if the parser finishes
	 *         on an ambiguous character boundary. When less than the requested
	 *         number of bytes is read, the remaining bytes from the
	 *         <code>bytes</code> argument should be re-passed at the
	 *         beginning of the next array of to-be-read bytes. This return
	 *         value can effectively be ignored if the entire XML document is
	 *         passed in.
	 * @throws XMLException
	 */
	public int parse(byte[] buf, int offset, int len) throws XMLException {

		/* restore saved 'registers' from last parse run - if there was one */
		int xmlidx = _xmlidx;
		int state = _state;
		boolean isChars = _isChars;

		boolean preserveEntityRefs = preserveEntityReferences;

		boolean stopParsing = false;

		char c;

		// some sanity checking on array bounds
		if ((offset < 0) || (len < 0) || (buf.length - offset < len))
			throw new ArrayIndexOutOfBoundsException();

		// ensure we are in an okay state to continue.
		if (_state == STATE_ERROR)
			throw new IllegalStateException("Parser in error state");

		int buflimit = offset + len;
		int bufidx = offset;

		while (true) {

			/*
			 * If we have either reached the end of the buffer, or have to exit due
			 * to a look-head ambiguity with the remaining bytes in the byte[]. We return
			 * the number of bytes processed. This will either be the end of the
			 * buffer (we have read them all) or it will be the point in the
			 * byte[] that marks the start of the look-ahead ambiguity.
			 */
			if ((bufidx >= buflimit) || (stopParsing)) {

				bufidx = stopParsing ? bufidx : buflimit;
				_xmlidx += bufidx - offset;
				_isChars = isChars;
				_state = state;
				_nodes[0] = _nodeIndex - 1;

				return bufidx;
			}

			c = (char) buf[bufidx++];

			switch (state) {

			case STATE_DOCUMENT:

				/* emit a NODE_DOCUMENT node */
				createNode(Document.NODE_DOCUMENT, 0);

				/* close the hatch, we're going in ... */
				_parseDepth++;

				/*
				 * and now switch to a state where we are reading the in-between
				 * stuff of either markup or characters
				 */
				state = STATE_CHARACTERS;

				/** FALL THROUGH TO 'STATE_CHARACTERS' - NO CASE BREAK REQUIRED * */

			case STATE_CHARACTERS:

				/*
				 * Read characters until we hit the start of some markup or an
				 * entity. All other characters will be considered text.
				 */

				switch (c) {

				case '&':

					// are we keeping entity references?
					if (preserveEntityRefs) {

						// back to "not processing chars".
						isChars = false;

						createNode(Document.NODE_ENTITY_REFERENCE, xmlidx + (bufidx - offset - 1));
						state = STATE_ENTITY_REFERENCE;
					}
					break;

				case '<':

					// back to "not processing chars".
					isChars = false;

					state = STATE_MARKUP;
					break;

				case '>':

					throw new XMLException("> not expected at byte " + (bufidx - offset - 1));

				default:

					// emit a NODE_TEXT node if characters are read.
					if (!isChars) {
						createNode(Document.NODE_TEXT, xmlidx + (bufidx - offset - 1));
						isChars = true;
					}
				}

				break;

			case STATE_MARKUP:

				switch (c) {

				case '!': {

					/*
					 * the following tests are performed in ascending 'look-ahead' size
					 * order
					 */

					/*
					 * Test for enough room to check for start of comment. If not
					 * cause method exit
					 */
					if (bufidx + 1 >= buflimit) {
						bufidx--; // point back to '!' character
						stopParsing = true;
						break;
					}

					/* test for begin of NODE_COMMENT */
					if ((buf[bufidx] == '-') && (buf[bufidx + 1] == '-')) {
						createNode(Document.NODE_COMMENT, xmlidx + (bufidx - offset - 2));
						state = STATE_COMMENT;
						bufidx += 2;  // move past '--'
						break;
					}

					/*
					 * Test for enough room to check for start of CDATA. If not
					 * cause method exit.
					 */
					if (bufidx + 6 >= buflimit) {
						bufidx--; // point back to '!' character
						stopParsing = true;
						break;
					}

					/* test for start of NODE_CDATA */
					if ((buf[bufidx] == '[') && (buf[bufidx + 1] == 'C') && (buf[bufidx + 2] == 'D') && (buf[bufidx + 3] == 'A') && (buf[bufidx + 4] == 'T') && (buf[bufidx + 5] == 'A') && (buf[bufidx + 6] == '[')) {
						createNode(Document.NODE_CDATA_SECTION, xmlidx + (bufidx - offset - 2));
						state = STATE_CDATA;
						bufidx += 7;
						break;
					}

					/*
					 * Test for enough room to check for start of DOCTYPE. If not
					 * cause method exit
					 */
					if (bufidx + 7 >= buflimit) {
						bufidx--; // point back to '!' character
						stopParsing = true;
						break;
					}

					/* test for start of NODE_DOCUMENT_TYPE */
					if ((buf[bufidx] == 'D') && (buf[bufidx + 1] == 'O') && (buf[bufidx + 2] == 'C') && (buf[bufidx + 3] == 'T') && (buf[bufidx + 4] == 'Y') && (buf[bufidx + 5] == 'P') && (buf[bufidx + 6] == 'E') && (buf[bufidx + 7] == ' ')) {
						createNode(Document.NODE_DOCUMENT_TYPE, xmlidx + (bufidx - offset - 2));
						state = STATE_DTD;
						bufidx += 8;
						_parseDepth++;
						break;
					}

					throw new XMLException("Comment, CDATA, or DOCTYPE expected after '<!'");
				}

				case '/': {
					state = STATE_CLOSE_TAGxREAD_ELEM_NAME;
					break;
				}

				case '?': {

					/*
					 * Test for enough room to check for prolog. If not cause
					 * method exit.
					 */
					if (bufidx + 3 >= buflimit) {
						bufidx--; // point back to '?' character
						stopParsing = true;
						break;
					}

					if ((buf[bufidx] == 'x') && (buf[bufidx + 1] == 'm') && (buf[bufidx + 2] == 'l') && (buf[bufidx + 3] == ' ')) {
						state = STATE_PROLOG;
						bufidx += 4;
						break;
					}

					createNode(Document.NODE_PROCESSING_INSTRUCTION, xmlidx + (bufidx - offset - 2));
					state = STATE_PI;
					break;

				}

				default: {
					// we have an element and we are on the first character of
					// the element name.
					createNode(Document.NODE_ELEMENT, xmlidx + (bufidx - offset - 2));
					state = STATE_OPEN_TAGxREAD_ELEM_NAME;
				}

				}

				break;

			case STATE_COMMENT:

				if (c == '-') {

					/*
					 * Test for enough room to check for end of NODE_COMMENT. If
					 * not cause method exit.
					 */
					if (bufidx + 1 >= buflimit) {
						bufidx--; // point back to '-' character
						stopParsing = true;
						break;
					}

					if ((buf[bufidx] == '-') && (buf[bufidx + 1] == '>')) {
						state = STATE_CHARACTERS;
						bufidx += 2;
						break;
					}
				}

				break;

			case STATE_PI:

				if (c == '?') {

					/*
					 * Rest for enough room to check for end of NODE_PROCESSING_INSTRUCTION. If
					 * not cause method exit.
					 */
					if (bufidx >= buflimit) {
						bufidx--; // point back to '?' char
						stopParsing = true;
						break;
					}

					if (buf[bufidx] == '>') {
						state = STATE_CHARACTERS;
						bufidx++;
						break;
					}

				}

				break;

			case STATE_CDATA:

				if (c == ']') {

					/*
					 * Test room to check for end of NODE_PROCESSING_INSTRUCTION. If
					 * not cause method exit.
					 */
					if (bufidx + 1 >= buflimit) {
						bufidx--; // point back to ']' char
						stopParsing = true;
						break;
					}

					if ((buf[bufidx] == ']') && (buf[bufidx + 1] == '>')) {
						state = STATE_CHARACTERS;
						bufidx += 2;
						break;
					}

				}

				break;

			// OPEN_TAG:
			case STATE_OPEN_TAGxREAD_ELEM_NAME:

				if (c < '@') { // skip alpha chars to reduce comparisons.
					if (c == '>') {
						_parseDepth++;
						state = STATE_CHARACTERS;
						break;
					} else if (c == '/') {
						state = STATE_OPEN_TAGxEMPTY_TAG;
						break;
					} else if (c <= ' ') {
						// reached a space (or something, cannot be first
						// element name char or in the
						// element name, so we must be about to read an
						// attribute name or just some spaces.
						state = STATE_OPEN_TAGxELEM_NAME_READ;
						break;
					}
				}

				break;

			case STATE_OPEN_TAGxELEM_NAME_READ:

				if (c == '>') {
					_parseDepth++;
					state = STATE_CHARACTERS;
					break;
				} else if (c == '/') {
					state = STATE_OPEN_TAGxEMPTY_TAG;
				} else if (c > ' ') {
					createNode(Document.NODE_ATTRIBUTE, xmlidx + (bufidx - offset - 1));
					state = STATE_OPEN_TAGxREAD_ATTR_NAME;
				}
				break;

			case STATE_OPEN_TAGxREAD_ATTR_NAME:

				if (c < '@') { // skip alpha chars to reduce comparisons.
					if (c <= ' ') {
						state = STATE_OPEN_TAGxATTR_NAME_READ;
						break;
					} else if (c == '=') {
						state = STATE_OPEN_TAGxEQUAL_READ;
						break;
					}
				}

				break;

			case STATE_OPEN_TAGxATTR_NAME_READ:
				if (c == '=') {
					state = STATE_OPEN_TAGxEQUAL_READ;
				} else if (c > ' ') {
					parsingError("'=' expected after attribute name");
				}
				break;

			case STATE_OPEN_TAGxEQUAL_READ:

				if (c == '\'') {
					state = STATE_OPEN_TAGxREAD_ATTR_VALUE_SIMPLE_QUOTE;
				} else if (c == '\"') {
					state = STATE_OPEN_TAGxREAD_ATTR_VALUE_DOUBLE_QUOTE;
				} else if (c > ' ') {
					parsingError("Quotes expected");
				}

				break;

			case STATE_OPEN_TAGxREAD_ATTR_VALUE_SIMPLE_QUOTE:

				if (c == '\'') {
					state = STATE_OPEN_TAGxELEM_NAME_READ;
					break;
				}

				break;

			case STATE_OPEN_TAGxREAD_ATTR_VALUE_DOUBLE_QUOTE:

				if (c == '\"') {
					state = STATE_OPEN_TAGxELEM_NAME_READ;
					break;
				}

				break;

			case STATE_OPEN_TAGxEMPTY_TAG:
				if (c == '>') {
					state = STATE_CHARACTERS;
					break;
				} else {
					parsingError("'>' expected");
				}

				// CLOSE_TAG:
			case STATE_CLOSE_TAGxREAD_ELEM_NAME:

				if (c < '@') { // skip alpha chars to reduce comparisons.

					if (c == '>') {
						_parseDepth--;
						state = STATE_CHARACTERS;
						break;
					} else if (c <= ' ') {
						state = STATE_CLOSE_TAGxELEM_NAME_READ;
						break;
					}
				}

				break;

			case STATE_CLOSE_TAGxELEM_NAME_READ:

				if (c == '>') {
					_parseDepth--;
					state = STATE_CHARACTERS;
					break;
				} else if (c > ' ') {
					parsingError("'>' expected");
				}
				break;

			case STATE_DTD:

				if (c == '>') {
					_parseDepth--;
					state = STATE_CHARACTERS;
					break;
				} else if (c == '[') {
					state = STATE_DTD_INTERNAL;
				}
				break;

			case STATE_DTD_INTERNAL:

				if (c == '<') {
					state = STATE_DTD_MARKUP;
					break;
				}

				if (c == ']') {
					state = STATE_DTD;
				}

				break;

			case STATE_DTD_MARKUP:

				if (c == '!') {

					/*
					 * Test for enough room to test for start of ENTITY. If not
					 * cause method exit
					 */
					if (bufidx + 6 >= buflimit) {
						bufidx--; // point back to '!' character
						stopParsing = true;
						break;
					}

					if ((buf[bufidx] == 'E') && (buf[bufidx + 1] == 'N') && (buf[bufidx + 2] == 'T') && (buf[bufidx + 3] == 'I') && (buf[bufidx + 4] == 'T') && (buf[bufidx + 5] == 'Y') && (buf[bufidx + 6] == ' ')) {
						// create a ENTITY node and keep the current state
						createNode(Document.NODE_ENTITY, xmlidx + (bufidx - offset - 2));
						// move past space after <!ENTITY
						bufidx += 7;
						break;
					}

					/*
					 * Test for enough room to test for start of NOTATION. If
					 * not cause method exit
					 */
					if (bufidx + 8 >= buflimit) {
						bufidx--; // point back to '!' character
						stopParsing = true;
						break;
					}

					if ((buf[bufidx] == 'N') && (buf[bufidx + 1] == 'O') && (buf[bufidx + 2] == 'T') && (buf[bufidx + 3] == 'A') && (buf[bufidx + 4] == 'T') && (buf[bufidx + 5] == 'I') && (buf[bufidx + 6] == 'O') && (buf[bufidx + 7] == 'N') && (buf[bufidx + 8] == ' ')) {
						// create a NOTATION node and keep the current state
						createNode(Document.NODE_NOTATION, xmlidx + (bufidx - offset - 2));
						// move past space after <!NOTATION
						bufidx += 9;
						break;
					}

					throw new XMLException("ENTITY or NOTATION expected after '<!'");
				}

				if (c == '>') {
					state = STATE_DTD_INTERNAL;
				}

				break;

			case STATE_ENTITY_REFERENCE:

				if (c == ';') {
					state = STATE_CHARACTERS;
				}

				break;

			case STATE_PROLOG:

				if (c == '>') {
					state = STATE_CHARACTERS;
				}

				break;
			default:
				parsingError("State unknown: ");
			}

		}

	}

}
