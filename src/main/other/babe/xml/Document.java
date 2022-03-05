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

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import babe.i18n.Codec;

/**
 *
 * A navigable representation of XML bytes. The Document class uses a
 * node array produced by a {@link Parser} to navigate and get String
 * names/values etc from the XML byte data. Unicode decoding is done with the
 * document prologue's named encoding if it has one - otherwise ISO-8859-1 is
 * used.
 *
 * Navigation is performed by moving from a given node to another node using
 * relative movements such as 'next sibling', 'first child' and so on. The
 * 'relativity' specifiers are :
 *
 * <ul>
 * <li>REF_IDENTITY
 * <li>REF_PARENT
 * <li>REF_NEXT_SIBLING
 * <li>REF_PREVIOUS_SIBLING
 * <li>REF_FIRST_CHILD
 * <li>REF_LAST_CHILD
 * <li>REF_FIRST_ATTRIBUTE
 * <li>REF_FORWARD_READ
 * </ul>
 *
 * ... and the method to move around it is {@link #getNodeId(int, short)}.
 * Generally, navigation will start with the root node which is obtained using
 * {@link #getRootNodeId()}.
 *
 * All navigation operations will return -1 if the navigation fails or is
 * invalid - like you are asking to the last child and there are no children -
 * or the next sibling and your current node is the last sibling.
 *
 * What this is not: Typical XML navigation.
 *
 * This is intended to be as small and as fast as possible while still providing
 * reasonable functionality and safety.
 *
 * Focus on : fast parsing and indexing for fast reading. Designed to be easily
 * translatable to C if speed requires it.
 *
 * Node names and values: <table border="1" summary="Describes node type, node
 * name, node value, and attributes">
 * <tr>
 * <th>Node Type</th>
 * <th>Node Name</th>
 * <th>Node Value</th>
 * <th>Attributes</th>
 * </tr>
 * <tr>
 * <td>NODE_ELEMENT</td>
 * <td>tag name</td>
 * <td>null</td>
 * <td>YES/NO</td>
 * </tr>
 * <tr>
 * <td>NODE_ATTRIBUTE</td>
 * <td>name of attribute</td>
 * <td>value of attribute</td>
 * <td>NO</td>
 * </tr>
 * <tr>
 * <td>NODE_TEXT</td>
 * <td><code>"#text"</code></td>
 * <td>content of the text node</td>
 * <td>NO</td>
 * </tr>
 * <tr>
 * <td>NODE_CDATA_SECTION</td>
 * <td><code>"#cdata-section"</code></td>
 * <td>content of the CDATA section</td>
 * <td>NO</td>
 * </tr>
 * <tr>
 * <td>NODE_ENTITY_REFERENCE</td>
 * <td>name of entity referenced</td>
 * <td>null</td>
 * <td>NO</td>
 * </tr>
 * <tr>
 * <td>NODE_ENTITY</td>
 * <td>entity name</td>
 * <td>null</td>
 * <td>NO</td>
 * </tr>
 * <tr>
 * <td>NODE_PROCESSING_INSTRUCTION</td>
 * <td>target</td>
 * <td>entire content excluding the target starting at first non-whitespace
 * character after the target and ending at the character before the final '?'.</td>
 * <td>NO</td>
 * </tr>
 * <tr>
 * <td>NODE_COMMENT</td>
 * <td><code>"#comment"</code></td>
 * <td>content of the comment</td>
 * <td>NO</td>
 * </tr>
 * <tr>
 * <td>NODE_DOCUMENT</td>
 * <td><code>"#document"</code></td>
 * <td>null</td>
 * <td>NO</td>
 * </tr>
 * <tr>
 * <td>NODE_DOCUMENT_TYPE</td>
 * <td>document type name</td>
 * <td>null</td>
 * <td>NO</td>
 * </tr>
 * <tr>
 * <td>NODE_NOTATION</td>
 * <td>notation name</td>
 * <td>null</td>
 * <td>NO</td>
 * </tr>
 * </table>
 *
 *
 * <h2>Some XML spec compatibility notes:</h2>
 *
 * <ul>
 * <li>No whitespace normalisation occurs.
 * <li>End of line CR/LF bytes into a single LF is not supported. The general
 * recommendation is that XML documents used on this platform are stripped of
 * unnecessary whitespace before deployment.
 * <li>Any char value &lt;= ' ' is considered whitespace.
 * <li>When getting the value of a node, entity reference resolution is only
 * performed for NODE_ATTRIBUTE and NODE_TEXT types.
 * <li>Nested entity references are not supported. The value for an entity may
 * contain another entity reference but the inner entity reference will not be
 * expanded.
 * </ul>
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public class Document {

	/** Node is an element */
	static final short NODE_ELEMENT = 1;

	/** Node is an attribute */
	static final short NODE_ATTRIBUTE = 2;

	/** Node is text */
	static final short NODE_TEXT = 3;

	/** Node is a CDATA section */
	static final short NODE_CDATA_SECTION = 4;

	/** Node is an entity reference */
	static final short NODE_ENTITY_REFERENCE = 5;

	/** Node is an entity */
	static final short NODE_ENTITY = 6;

	/** Node is a processing instruction */
	static final short NODE_PROCESSING_INSTRUCTION = 7;

	/** Node is a comment */
	static final short NODE_COMMENT = 8;

	/** Node is a document */
	static final short NODE_DOCUMENT = 9;

	/** Node is a document type */
	static final short NODE_DOCUMENT_TYPE = 10;

	/** Node is a notation */
	static final short NODE_NOTATION = 11;

	/**
	 * Special node type which indicates the closing of the current element
	 * (used in forward reading)
	 */
	static final short NODE_FORWARD_ELEMENT_CLOSE = 12;

	/** Used to reference the node itself (can be used on attributes) */
	static final short REF_IDENTITY = 1;

	/** Used to reference the node's parent (can also be used on attributes) */
	static final short REF_PARENT = 2;

	/**
	 * Used to reference the node's next sibling (can also be used on
	 * attributes)
	 */
	static final short REF_NEXT_SIBLING = 3;

	/**
	 * Used to reference the node's previous sibling (can also be used on
	 * attributes)
	 */
	static final short REF_PREVIOUS_SIBLING = 4;

	/** Used to reference the node's first child */
	static final short REF_FIRST_CHILD = 5;

	/** Used to reference the node's last child */
	static final short REF_LAST_CHILD = 6;

	/** Used to reference the node's first attribute (only on an element) */
	static final short REF_FIRST_ATTRIBUTE = 7;

	/**
	 * Used to reference the next node: special reference mode used to read the
	 * tree linearly
	 */
	static final short REF_FORWARD_READ = 8;

	/** Used to position a node before reference node */
	static final short POSITION_BEFORE = 1;
	/** Used to position a node after reference node */
	static final short POSITION_AFTER = 2;
	/** Used to position a node to replace reference node */
	static final short POSITION_REPLACE = 3;
	/** Used to position a node after the last of the reference's node children */
	static final short POSITION_APPEND_CHILD = 4;
	/** Used to append a node just after another one in textual XML order */
	static final short POSITION_FORWARD_WRITE = 5;

	String _encoding;
	byte[] _xml;
	int[] _nodes;

	/* TODO .. better if the key on this way not a String ... less GC */
	private Hashtable<String, String> _entities = new Hashtable<String, String>();

	static String[] names = { "", "NODE_ELEMENT", "NODE_ATTRIBUTE", "NODE_TEXT", "NODE_CDATA_SECTION", "NODE_ENTITY_REFERENCE", "NODE_ENTITY", "NODE_PROCESSING_INSTRUCTION", "NODE_COMMENT", "NODE_DOCUMENT", "NODE_DOCUMENT_TYPE", "NODE_NOTATION", "NODE_FORWARD_ELEMENT_CLOSE" };

	/**
	 * Creates a new XML document object.
	 *
	 * @param xml
	 *            the byte array that contains the XML bytes of the document.
	 * @param nodes
	 *            the int array of nodes for the bytes contained in the
	 *            <code>xml</code> argument.
	 * @param encoding
	 *            and optional encoding for the document. Specifying
	 *            <code>null</code> cause cause the Document object to attempt
	 *            to obtain the encoding from the XML prologue - if one exists.
	 *            If null and no prologue exists, the platform default encoding
	 *            will be used.
	 * @param entities
	 */
	public Document(byte[] xml, int[] nodes, String encoding) throws XMLException {
		_xml = xml;
		_nodes = nodes;

		// use the encoding if it is supplied, otherwise parse it
		if ((encoding == null) || encoding.equals(""))
			parseEncoding();
		else
			_encoding = encoding;

		parseEntities();
	}

	private void parseEncoding() {
		// TODO parse the XML prolog if there is one and get the encoding, if
		// there is one.
		_encoding = "ISO-8859-1";
	}

	private void parseEntities() throws XMLException {

		/* find the DOCTYPE node and scan its entities. */

		int nodeId = getDocumentTypeNodeId();

		if (nodeId != -1) {

			// get the first child
			nodeId = getNodeId(nodeId, Document.REF_FIRST_CHILD);

			// while there are children of the document node ...
			while (nodeId != -1) {

				/* get each NODE_ENTITY 'value' and add it to map of entities. */
				if ((_nodes[nodeId] >> 27) == Document.NODE_ENTITY) {

					/*
					 * note that according to the XML spec., ENTITY nodes have a
					 * 'null' value. So we cannot just call getNodeValue() here
					 * to get the value of the entity node. We'll get it
					 * manually by get the text between the delimiting quotes
					 * (or apostrophes) - very much like getting the value of an
					 * attribute.
					 */

					char delimiter;

					int idx = getNodeOffset(nodeId);

					char c = (char) _xml[idx];

					// find the first delimiter
					while ((c != '"') && (c != '\'')) {
						c = (char) _xml[idx++];
					}

					delimiter = c; // remember the delimiter
					int start = idx++; // remember the start

					// find the end delimiter
					while ((c = (char) _xml[idx++]) != delimiter) ;

					String name = getNodeName(nodeId);
					String value;
					try {
						value = new String(_xml, start, (idx - 1) - start, _encoding);
					} catch (UnsupportedEncodingException e) {
						value = "";
					}
					//_entities.put(new CharArray(name), value);
					_entities.put(name, value);
				}

				// get the next child node of the document type node (if any).
				nodeId = getNodeId(nodeId, Document.REF_NEXT_SIBLING);
			}
		}
	}

	/**
	 * Creates a string from the XML bytes using the document encoding.
	 *
	 * @param offset
	 *            the offset into the XML bytes the string starts.
	 * @param length
	 *            the length of the to-be-created string.
	 * @param scanEntities
	 *            if <code>true</code>, this method will resolve entity
	 *            references in the string. If <code>false</code>, it will
	 *            not.
	 * @return the String value of the bytes at offset/length - or null if an
	 *         encoding exception occurs.
	 */
	private String getString(int offset, int length, boolean scanEntities) {

		String s = null;

		char[] _scratchChars;
		char[] _valueChars;

		StringBuffer sb = new StringBuffer();

		try {
			if (scanEntities) {

				int x = 0;

				_scratchChars = Codec.decode(_xml, offset, length, _encoding);
				_valueChars = new char[_scratchChars.length];

				for (int i = 0; i < _scratchChars.length; i++, x++) {

					char c = _scratchChars[i];

					if (c == '&') {

						// &lt; replacement
						if ((_scratchChars[i + 1] == 'l') && (_scratchChars[i + 2] == 't') && (_scratchChars[i + 3] == ';')) {
							i += 3;
							_valueChars[x] = '<';
							continue;
						}

						// &gt; replacement
						if ((_scratchChars[i + 1] == 'g') && (_scratchChars[i + 2] == 't') && (_scratchChars[i + 3] == ';')) {
							i += 3;
							_valueChars[x] = '>';
							continue;
						}
						// &amp; replacement
						if ((_scratchChars[i + 1] == 'a') && (_scratchChars[i + 2] == 'm') && (_scratchChars[i + 3] == 'p') && (_scratchChars[i + 4] == ';')) {
							i += 4;
							_valueChars[x] = '&';
							continue;
						}

						// &quot; replacement
						if ((_scratchChars[i + 1] == 'q') && (_scratchChars[i + 2] == 'u') && (_scratchChars[i + 3] == 'o') && (_scratchChars[i + 4] == 't') && (_scratchChars[i + 5] == ';')) {
							i += 5;
							_valueChars[x] = '"';
							continue;
						}

						// &apos; replacement
						if ((_scratchChars[i + 1] == 'a') && (_scratchChars[i + 2] == 'p') && (_scratchChars[i + 3] == 'o') && (_scratchChars[i + 4] == 's') && (_scratchChars[i + 5] == ';')) {
							i += 5;
							_valueChars[x] = '\'';
							continue;
						}

						sb.append(_valueChars, 0, x);
						x = 0;

						/*
						 * if we have arrived here the entity is not an inbuilt
						 * one - we'll have to get it from the hashtable. We'll
						 * use a scratch CharArray to look into the map.
						 */

						/* find the end of the entity ref (it is the ';'). */
						{
							int start = ++i;

							while ((c = _scratchChars[i]) != ';')
								i++;

							String entityName = new String(_scratchChars, start, i-start);
							sb.append(_entities.get(entityName));
						}

					} else
						_valueChars[x] = c;

				}

				// on exiting the loop, append to the SB all remaining converted
				// chars
				sb.append(_valueChars, 0, x - 1);

				s = sb.toString();

			} else
				s = new String(_xml, offset, length, _encoding);
		} catch (UnsupportedEncodingException e) {
		}

		return s;
	}

	/**
	 * Gets the id of the root node of the document. The 'root node' is defined
	 * at the first node in the node list with a type of NODE_ELEMENT.
	 *
	 * @return the node id of the root node, or -1 if no NODE_ELEMENT is in the
	 *         node list.
	 *
	 */
	public int getRootNodeId() {

		int nrNodes;
		int[] nodes;
		int elemType = Document.NODE_ELEMENT;

		nodes = _nodes; // avoid field lookup bytecode in loop
		nrNodes = nodes[0]; // how many nodes are there ..

		for (int i = 1; i <= nrNodes; i++) {

			// if the node type is NODE_ELEMENT
			if ((nodes[i] >> 27) == elemType)
				return i;
		}

		return -1;

	}

	/**
	 * Gets the id of the document type node node of the document. The search
	 * will only go up to the first element node of the document.
	 *
	 * @return the node id of the document type node, or -1 if no
	 *         NODE_DOCUMENT_TYPE is in the node list.
	 *
	 */
	public int getDocumentTypeNodeId() {

		int nrNodes;
		int[] nodes;
		int docType = Document.NODE_DOCUMENT_TYPE;
		int elemType = Document.NODE_ELEMENT;

		nodes = _nodes; // avoid field lookup bytecode in loop
		nrNodes = nodes[0]; // how many nodes are there ..

		for (int i = 1; i <= nrNodes; i++) {

			int type = nodes[i] >> 27;

			// if the node type is NODE_DOCUMENT_TYPE cool.
			if (type == docType)
				return i;

			// if the node type is NODE_ELEMENT finished the search
			if (type == elemType)
				break;
		}

		return -1;

	}

	/**
	 * Gets the document node id of the document or -1 if no document node
	 * exists.
	 *
	 * @return as above
	 */
	public int getDocumentNodeId() {

		if (_nodes.length > 1) {

			// the first node will be a document node
			int i = _nodes[1];

			// if so, return it, if not -1.
			return (i >> 27 == Document.NODE_DOCUMENT) ? i : -1;
		}

		return -1;
	}

	/**
	 *
	 * Gets a node relative to another given node.
	 *
	 * @param refNodeId
	 * @param refType
	 * @return the node id of the requested node. Note that the node Id is only
	 *         deemed to be valid until an operation is performed on the
	 *         document that changes its structure. After that all held node ids
	 *         should be considered invalid.
	 *
	 * If the request is valid but the node cannot be found, for example like a
	 * request for a parent of the root, of the previous sibling of the first
	 * child and so a -1 will be returned.
	 *
	 * @throws IllegalArgumentException
	 *             if the node is not a valid node or the refType is not one of
	 *             the above
	 */
	public int getNodeId(int refNodeId, short refType) {

		int refNode;
		int nodeCount = _nodes[0];

		// a little sanity checking
		if ((refNodeId < 0) || (refNodeId > nodeCount))
			throw new java.lang.IllegalArgumentException("Invalid refNodeId");

		int[] nodes = _nodes; // avoid field lookup bytecode
		refNode = nodes[refNodeId];

		int refNodeType = refNode >> 27;
		int refNodeDepth = (refNode & 0x7E00000) >> 21; // following six bytes

		// default a search var to the ref node id.
		int searchNodeId = refNodeId;

		switch (refType) {

		/* Used to reference the node itself (can be used on attributes) */
		case Document.REF_IDENTITY:
			return refNodeId;
			/*
			 * Used to reference the node's parent (can also be used on
			 * attributes)
			 */
		case Document.REF_PARENT: {

			/*
			 * if refnode is an attribute, search backwards for the first
			 * element encountered.
			 */
			if (refNodeType == Document.NODE_ATTRIBUTE) {

				while ((nodes[--searchNodeId] >> 27) != Document.NODE_ELEMENT)
					;

				return searchNodeId;

			} else {

				/*
				 * otherwise search backwards until a depth of current depth-1
				 * is encountered that is not an attribute or we hit the
				 * beginning of nodes.
				 */

				refNodeDepth--; // calc here to avoid recalc in loop

				while (--searchNodeId > 0) {

					int n = nodes[searchNodeId];
					int ndepth = (n & 0x7E00000) >> 21;

					if ((ndepth == refNodeDepth) && ((n >> 27) != Document.NODE_ATTRIBUTE))
						return searchNodeId;
				}

				return -1;
			}
		}

			/*
			 * Used to reference the node's next sibling (can also be used on
			 * attributes)
			 */
		case Document.REF_NEXT_SIBLING: {

			/*
			 * if refnode is an attribute, the next node will be its next
			 * sibling. -1 if it is not a attribute.
			 */

			if (refNodeType == Document.NODE_ATTRIBUTE) {

				return ((nodes[++searchNodeId] >> 27) == Document.NODE_ATTRIBUTE) ? searchNodeId : -1;

			} else {

				/*
				 * otherwise search forward for another node at the same depth
				 * that is not an attribute. Encountering end of nodes or a node
				 * of a lesser depth means no next sibling.
				 */

				int parentDepth = refNodeDepth - 1; // avoid re-calc in loop.

				while (++searchNodeId <= nodeCount) {

					int n = nodes[searchNodeId];
					int ndepth = (n & 0x7E00000) >> 21;

					if (ndepth <= parentDepth)
						break;

					if ((ndepth == refNodeDepth) && ((n >> 27) != Document.NODE_ATTRIBUTE))
						return searchNodeId;
				}

				return -1;
			}
		}

			/*
			 * Used to reference the node's previous sibling (can also be used
			 * on attributes)
			 */
		case Document.REF_PREVIOUS_SIBLING: {

			/*
			 * if refnode is an attribute, the prev node will be its prev
			 * sibling. -1 if it is not and attribute.
			 */

			if (refNodeType == Document.NODE_ATTRIBUTE) {

				return ((nodes[--searchNodeId] >> 27) == Document.NODE_ATTRIBUTE) ? searchNodeId : -1;

			} else {

				/*
				 * otherwise, search backwards for a node of the same depth that
				 * is not an attribute. Encountering beginning of nodes or a
				 * lesser depth means no prev sibling.
				 */

				int parentDepth = refNodeDepth - 1; // avoid re-calc in loop.

				while (--searchNodeId > 0) {

					int n = nodes[searchNodeId];
					int ndepth = (n & 0x7E00000) >> 21;

					if (ndepth <= parentDepth)
						break;

					if ((ndepth == refNodeDepth) && ((n >> 27) != Document.NODE_ATTRIBUTE))
						return searchNodeId;
				}

				return -1;
			}
		}

			/* Used to reference the node's first child */
		case Document.REF_FIRST_CHILD: {

			// attributes do not have children ... return -1
			if (refNodeType == Document.NODE_ATTRIBUTE)
				return -1;

			/*
			 * otherwise, search for the next non-attribute node. If it has a
			 * depth of one greater than the reference node then it is a child
			 * of the reference node.
			 */

			int n;

			while (((n = nodes[++searchNodeId]) >> 27) == Document.NODE_ATTRIBUTE)
				;

			return ((n & 0x7E00000) >> 21 == (refNodeDepth + 1)) ? searchNodeId : -1;
		}

			/* Used to reference the node's last child */
		case Document.REF_LAST_CHILD: {

			// attributes do not have children ... return -1
			if (refNodeType == Document.NODE_ATTRIBUTE)
				return -1;

			/*
			 * The last child is the last non-attribute node we find when
			 * searching forwards before encountering end of nodes, or a depth
			 * equal to or less than the ref node (which would make it a
			 * sibling, or the next level up).
			 */

			int parentDepth = refNodeDepth - 1; // avoid re-calc in loop.
			int childDepth = refNodeDepth + 1; // avoid re-calc in loop.
			int lastNonAttrNode = -1;

			while (++searchNodeId <= nodeCount) {

				int n = nodes[searchNodeId];

				int ndepth = (n & 0x7E00000) >> 21;

				if (ndepth <= parentDepth)
					break;

				// remember the last non-attribute node we find at 'child' depth
				if ((ndepth == childDepth) && ((n >> 27) != Document.NODE_ATTRIBUTE))
					lastNonAttrNode = searchNodeId;

			}

			return lastNonAttrNode;
		}

			/*
			 * Used to reference the node's first attribute (only on an
			 * element). Returns -1 if reference nmode is not an element, or the
			 * reference element has no attributes
			 */
		case Document.REF_FIRST_ATTRIBUTE: {

			/*
			 * the first attribute of an element (if it has one) will be the
			 * very next node after the element.
			 */

			// if the node is not an element ...
			if (refNodeType != Document.NODE_ELEMENT)
				return -1;

			// if we are on the last node ..
			if (searchNodeId == nodeCount)
				return -1;

			// return the next node if it is an attribute, -1 otherwise
			return (nodes[++searchNodeId] >> 27 == Document.NODE_ATTRIBUTE) ? searchNodeId : -1;
		}
			/*
			 * Used to reference the next node: special reference mode used to
			 * read the tree linearly
			 */
		case Document.REF_FORWARD_READ:
			// return the next node, unless we are on the last one
			return (searchNodeId++ == nodeCount) ? -1 : searchNodeId;

		default:
			throw new java.lang.IllegalArgumentException("Invalid refType");
		}
	}

	/**
	 * Gets an attribute for a given element node.
	 *
	 * @param elementNode
	 *            the element node to find an attribute of.
	 * @param qname
	 *            the full qname of the attribute. Yes, if the attribute name
	 *            you are seeking is namespaced, include the namespace.
	 * @return the node id of the attribute with the given name if it exists, or
	 *         <code>-1</code> otherwise. <code>-1</code> will also be
	 *         returned if the <code>elementNodeId</code> argument is an
	 *         invalid node or not an element node.
	 * @throws XMLException
	 *             if any XML error occurs or an encoding error occurs.
	 */
	public int getAttributeByName(int elementNodeId, String qname) throws XMLException {

		int attr;
		int len;
		byte[] strBytes;
		byte[] xml = _xml;

		/*
		 * To find the attribute by name we get the encoded bytes of the
		 * requested name and then loop through all attributes for the given
		 * element node and do a byte-by-byte comparison of the data at the
		 * attribute offset for the length of the encoded qname
		 */

		// get the encoded bytes for the string qname.
		try {
			strBytes = qname.getBytes(_encoding);
			len = strBytes.length;
		} catch (UnsupportedEncodingException e) {
			throw new XMLException("Unsupported Encoding");
		}

		/*
		 * get the first attribute of the element elementNode. If error or no
		 * attributes return a -1
		 */
		attr = getNodeId(elementNodeId, Document.REF_FIRST_ATTRIBUTE);

		/*
		 * loop through each attribute comparing the name. Stop when we find
		 * one. Some people might "tut tut" at the usage of the break label here -
		 * but there is no better and cleaner way to manage two loops. It also
		 * produces less bytecode than the alternative "having a boolean and
		 * checking for found = true" etc. Have another look at this code and
		 * you'll see if is about as efficient a loop as it can be. So there.
		 */
		loop: while (attr != -1) {

			// use lower 21 bits of node as the offset
			int offset = _nodes[attr] & 0x1FFFFF;

			for (int i = 0; i < len; i++, offset++) {
				if (strBytes[i] != xml[offset]) {
					attr = getNodeId(attr, Document.REF_NEXT_SIBLING);
					continue loop;
				}
			}

			return attr;
		}

		return attr;
	}

	/**
	 * Gets the name of the given node. Names are determined as per XML 1.0
	 * specification and are summarised in the table above in the class javadoc.
	 *
	 * @param nodeId the node Id to get the name of.
	 * @return the name of the node as per XML 1.0 spec.
	 */
	public String getNodeName(int nodeId) throws XMLException {

		int start;
		char c;
		byte[] xml = _xml;
		int nodeCount = _nodes[0];

		// a little sanity checking
		if ((nodeId < 0) || (nodeId > nodeCount))
			throw new java.lang.IllegalArgumentException("Invalid nodeId");

		int node = _nodes[nodeId];

		int nodeType = node >> 27;
		int idx = node & 0x1FFFFF;

		try {

			switch (nodeType) {

			case Document.NODE_ELEMENT:

				/*
				 * An element name is delimited by a '>' or whitespace. If we
				 * encounter a ':', we'll move the start of the name to the
				 * position following the colon. The search starts at one past
				 * the node offset as element nodes have their offset pointing
				 * to the initial '<'.
				 */

				start = ++idx; // first char of name is one past the '<'.

				c = (char) xml[idx];

				while ((c != '>') && (c > ' ')) {
					if (c == ':')
						start = idx;
					c = (char) xml[idx++];
				}

				return new String(xml, start, (idx - 1) - start, _encoding);

			case Document.NODE_ATTRIBUTE:

				/*
				 * Attribute names starts at the beginning of the attribute node
				 * and ends when a '=' or whitespace is encountered. If we
				 * encounter a ':', we'll mark the start of the name right after
				 * it.
				 */

				start = idx; // first char of name is exactly on node offset.

				c = (char) xml[idx];

				while ((c != '=') && (c > ' ')) {
					if (c == ':')
						start = idx;
					c = (char) xml[idx++];
				}

				return new String(xml, start, (idx - 1) - start, _encoding);

			case Document.NODE_TEXT:
				/* as per XML spec */
				return "#text";
			case Document.NODE_CDATA_SECTION:
				/* as per XML spec */
				return "#cdata-section";
			case Document.NODE_ENTITY_REFERENCE:

				/*
				 * the name of the entity being referenced is one past the
				 * offset (to move past the '&') and continues until an ';'.
				 * Encountering whitespace in the name causes an exception.
				 */

				start = ++idx; // first char of name is exactly on node offset.

				c = (char) xml[idx];

				while (c != ';') {
					c = (char) xml[idx++];
					if (c < ' ')
						throw new XMLException("Whitespace encountered in entity name");
				}

				return new String(xml, start, (idx - 1) - start, _encoding);

			case Document.NODE_ENTITY:

				/*
				 * the name of an entity starts at the first non-whitespace
				 * character after the offset + 9 ('!ENTITY ') and stops at the
				 * first whitespace after that.
				 */

				start = idx += 9;

				while ((c = (char) xml[idx++]) <= ' ')
					;

				start = idx - 1;

				while ((c = (char) xml[idx++]) > ' ')
					;

				return new String(xml, start, (idx - 1) - start, _encoding);

			case Document.NODE_PROCESSING_INSTRUCTION:

				/*
				 * PI name (is the PI target) starts two chars past the node
				 * offset (for '<?') and ends at '?' or whitespace.
				 */

				start = idx += 2; // first char of name is after the '<?'.

				c = (char) xml[idx];

				while ((c != '?') && (c > ' ')) {
					c = (char) xml[idx++];
				}

				return new String(xml, start, (idx - 1) - start, _encoding);

			case Document.NODE_COMMENT:
				/* as per XML spec */
				return "#comment";
			case Document.NODE_DOCUMENT:
				/* as per XML spec */
				return "#document";
			case Document.NODE_DOCUMENT_TYPE:

				/*
				 * DTD name starts at the node offset plus '<!DOCTYPE ' (+10)
				 * plus any spaces and ends at '?' or whitespace.
				 */

				idx += 10;
				while ((c = (char) xml[idx++]) <= ' ')
					;

				start = idx - 1;

				while ((c != '>') && (c > ' ') && (c != '[')) {
					c = (char) xml[idx++];
				}

				return new String(xml, start, (idx - 1) - start, _encoding);

			case Document.NODE_NOTATION:
				/*
				 * the name of an notation starts at the first non-whitespace
				 * character after the offset + 11 ('!NOTATION ') and stops at
				 * the first non-character after that.
				 */

				start = idx += 11;
				while ((c = (char) xml[idx++]) <= ' ')
					;

				start = idx - 1;

				while ((c = (char) xml[idx++]) > ' ')
					;

				return new String(xml, start, (idx - 1) - start, _encoding);
			}
		} catch (UnsupportedEncodingException e) {
			throw new XMLException("Unsupported Encoding");
		}

		return null;
	}

	/**
	 * Gets the value of the given node. Value are determined as per XML 1.0
	 * specification.
	 *
	 *
	 * @param nodeId the node Id to get the value of
	 * @return the node value as per XML 1.0 spec
	 */
	public String getNodeValue(int nodeId) throws XMLException {

		int start;
		char c;
		byte[] xml = _xml;
		int nodeCount = _nodes[0];

		// a little sanity checking
		if ((nodeId < 0) || (nodeId > nodeCount))
			throw new java.lang.IllegalArgumentException("Invalid nodeId");

		int node = _nodes[nodeId];

		int nodeType = node >> 27;
		int idx = node & 0x1FFFFF;
		boolean hasEntities = false;

		try {
			switch (nodeType) {

			case Document.NODE_ELEMENT:
				return null;
			case Document.NODE_ATTRIBUTE:

				/*
				 * to get the value of an attribute we find the '=' and then
				 * determine the value delimiter. it will be the first
				 * apostrophe or quote we come to. Whichever it is, the value of
				 * the attribute will start one after it, and will end one
				 * before we find the next occurrence of that delimiter.
				 */

				char delimiter;

				// find the '='
				while ((c = (char) xml[idx++]) != '=')
					;

				// first the first delimiter afterwards
				while ((c != '"') && (c != '\'')) {
					c = (char) xml[idx++];
				}

				delimiter = c; // remember the delimiter
				start = idx; // remember the start

				// find the end delimiter
				while ((c = (char) xml[idx++]) != delimiter) {
					if (c == '&')
						hasEntities = true;
				}

				/*
				 * create String and perform entity processing if text has any
				 * entities
				 */
				return getString(start, (idx - 1) - start, hasEntities);
			case Document.NODE_TEXT:

				/*
				 * The value of a TEXT node begins at the offset and ends when a '<'
				 * is reached (text nodes are delimited by markup).
				 */

				start = idx;

				while ((c = (char) xml[idx++]) != '<') {
					if (c == '&')
						hasEntities = true;
				}

				/*
				 * create String and perform entity processing if text has any
				 * entities
				 */
				return getString(start, (idx - 1) - start, hasEntities);

			case Document.NODE_CDATA_SECTION:

				/*
				 * The value of a CDATA node begins at the offset + 9
				 * ('![CDATA[') and ends when a ']]>' is reached
				 */

				start = idx + 9;

				// spin till we hit the end.
				while (true) {
					if (((c = (char) xml[idx++]) == ']') && ((c = (char) xml[idx++]) == ']') && ((c = (char) xml[idx++]) == '>'))
						break;
				}

				/* create String and ignore all entities in CDATA */
				return new String(xml, start, (idx - 3) - start, _encoding);

			case Document.NODE_ENTITY_REFERENCE:
				return null;
			case Document.NODE_ENTITY:
				return null;
			case Document.NODE_PROCESSING_INSTRUCTION:
				/*
				 * This is from the first non white space character after the
				 * target to the character immediately preceding the '?>'. We
				 * need to first find the end of the target, then the beginning
				 * of anything else - and then find the '?>'.
				 */

				// find first whitespace after the offset
				while ((c = (char) xml[idx++]) > ' ')
					;

				// now find start of non-whitespace
				while ((c = (char) xml[idx++]) <= ' ')
					;

				// we're now at the start of non-whitespace
				start = idx - 1;

				// spin till we hit the end.
				while (true) {
					if (((c = (char) xml[idx++]) == '?') && ((c = (char) xml[idx++]) == '>'))
						break;
				}

				/* create String and ignore all entities in PI */
				return new String(xml, start, (idx - 2) - start, _encoding);

			case Document.NODE_COMMENT:

				/*
				 * The value of a COMMENT node begins at the offset + 4 ('!--')
				 * and ends when a '-->' is reached
				 */

				start = idx + 4;

				// spin till we hit the end.
				while (true) {
					if (((c = (char) xml[idx++]) == '-') && ((c = (char) xml[idx++]) == '-') && ((c = (char) xml[idx++]) == '>'))
						break;
				}

				/* create String and ignore all entities in comment */
				return new String(xml, start, (idx - 3) - start, _encoding);

			case Document.NODE_DOCUMENT:
				return null;
			case Document.NODE_DOCUMENT_TYPE:
				return null;
			case Document.NODE_NOTATION:
				return null;
			}
		} catch (UnsupportedEncodingException e) {
			throw new XMLException("Unsupported Encoding");
		}

		return null;
	}

	/**
	 * Scan the node tree that has nodeId as the root and return
	 * <code>maxHits</code> node ids with a matching name and namespace.
	 *
	 * @param nodeId
	 *            the node id that will the start of the search tree.
	 * @param namespaceURI
	 *            the namespace to qualify the <code>localName</code>.
	 *            <code>null</code> if no namespace.
	 * @param localName
	 *            the name of the element to search for.
	 * @param maxHits
	 *            the maximum number of node ids to find. -1 if unlimited.
	 * @throws XMLException
	 */
	void getElementsByTagName(int nodeId, String namespaceURI, String localName, int maxHits)
			throws XMLException {
		// TODO
	}

	/**
	 * Displays to the console the details of each node in a document's node
	 * array.
	 */
	public void dumpNodeArray() {

		for (int x = 1; x <= _nodes[0]; x++) {

			int type = getNodeType(x);
			int depth = (_nodes[x] & 0x7E00000) >> 21;
			int offset = _nodes[x] & 0x1FFFFF;

			System.out
					.println("Node: " + x + " \t" + Document.names[type] + " \t offset: " + offset + "\t depth: " + depth);
		}

	}

	/**
	 * Displays to the console the names of each node in a document's node
	 * array.
	 */
	public void dumpNodeNames() throws XMLException {

		for (int x = 1; x <= _nodes[0]; x++) {

			int node = _nodes[x];
			int type = node >> 27;

			System.out
					.println("Node: " + x + " \t" + Document.names[type] + " \t name: " + getNodeName(x));
		}

	}

	/**
	 * Displays to the console the names of each node in a document's node
	 * array.
	 */
	public void debugDumpNodePrefixes() throws XMLException {

		for (int x = 1; x <= _nodes[0]; x++) {

			int node = _nodes[x];
			int type = node >> 27;

			if ((type == NODE_ELEMENT) || (type == NODE_ATTRIBUTE))
				System.out
						.println("Node: " + x + " \t" + Document.names[type] + " \t prefix: " + getNodePrefix(x));
		}

	}

	/**
	 * Displays to the console the names of each node in a document's node
	 * array.
	 */
	public void debugDumpNodeValues() throws XMLException {

		for (int x = 1; x <= _nodes[0]; x++) {

			int node = _nodes[x];
			int type = node >> 27;

			// if ((type == NODE_ELEMENT) || (type == NODE_ATTRIBUTE))
			System.out
					.println("Node: " + x + " \t" + Document.names[type] + " \t value: " + getNodeValue(x));
		}

	}

	/**
	 * Gets the node type of a given node Id.
	 *
	 * @param nodeId
	 *            the Id D of the node to get the type of.
	 * @return a node type of the node Id. Node types are described in the
	 *         {@link Document} class as field names beginning with
	 *         <code>NODE_</code>.
	 */
	public int getNodeType(int nodeId) {

		// a little sanity checking
		if ((nodeId < 0) || (nodeId > _nodes[0]))
			throw new java.lang.IllegalArgumentException("Invalid nodeId");

		// node type is store in bits 27-31.
		return _nodes[nodeId] >> 27;

	}

	/**
	 * Gets the node type of a given node Id.
	 *
	 * @param nodeId
	 *            the Id of the node to get the type of.
	 * @return a node type of the node Id. Node types are described in the
	 *         {@link Document} class as field names beginning with
	 *         <code>NODE_</code>.
	 */
	private int getNodeOffset(int nodeId) {

		// a little sanity checking
		if ((nodeId < 0) || (nodeId > _nodes[0]))
			throw new java.lang.IllegalArgumentException("Invalid nodeId");

		return _nodes[nodeId] & 0x1FFFFF;
	}

	/**
	 * Gets the namespace prefix of a given node. Only <code>NODE_ELEMENT</code>
	 * and <code>NODE_ATTRIBUTE</code> type nodes are permitted. Attempting to
	 * get the prefix of other node types will cause an XMLException.
	 *
	 * @param nodeId
	 *            the id of the node to get the prefix of.
	 * @return the namespace prefix of the given node, or <code>null</code> if
	 *         it has none.
	 * @throws XMLException
	 */
	public String getNodePrefix(int nodeId) throws XMLException {

		int nodeCount = _nodes[0];

		// a little sanity checking
		if ((nodeId < 0) || (nodeId > nodeCount))
			throw new java.lang.IllegalArgumentException("Invalid nodeId");

		byte[] xml = _xml;

		int node = _nodes[nodeId];

		int nodeType = node >> 27;

		int idx = node & 0x1FFFFF;

		char c;

		/* node an element or attribute? Error */
		if ((nodeType != Document.NODE_ELEMENT) && (nodeType != Document.NODE_ATTRIBUTE))
			throw new IllegalArgumentException("Only ELEMENTS/ATTRIBUTES have prefix");

		/*
		 * attribute names start 1 on the offset, element names start one past
		 * it
		 */
		int nameStart = (nodeType == Document.NODE_ELEMENT) ? ++idx : idx;

		c = (char) xml[idx];

		/* loop looking for a ':' char - when we find one we've got a prefix. */
		while ((c != '>') && (c > ' ')) {
			if (c == ':') {
				try {
					return new String(xml, nameStart, (idx - 1) - nameStart, _encoding);
				} catch (UnsupportedEncodingException e) {
					throw new XMLException("Unsupported Encoding");
				}
			}
			c = (char) xml[idx++];
		}

		return null;

	}

}
