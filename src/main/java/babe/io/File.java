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

package babe.io;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * TODO: Permissions
 *
 * File access to native platform files. Provides a thin-ish layer onto native
 * file access used by the VM itself. Those astute members of the audience will note
 * that lack of the usual Java 'stream' metaphor. This is raw bytes to and from
 * the OS file system.
 *
 * In many ways, this class is modelled on the file io functionality present in ISO C.
 * The file open modes from POSIX are used. These map onto ISO C awkwardly, but
 * they are a good model.
 *
 * A note about file names: All operations using file names assume the given
 * contain on US ASCII characters (0-127 base 10). Conversion of a Java String
 * filename to a native platform filename does not involve unicode encoding - it
 * simply drops the upper byte of each char in the String.
 *
 * @author Greg McCreath
 * @since 0.0.1
 */
public final class File {

	/** The seek offset is set to the specified number of offset bytes. */
	public static final int SEEK_SET = 0;
	/** The seek offset is set to its current location plus offset bytes. */
	public static final int SEEK_CUR = 1;
	/** The offset is set to the size of the file plus offset bytes. */
	public static final int SEEK_END = 2;

	/**
	 * For opening files in read only mode. CREATE/TRUNCATE/APPEND will be
	 * ignored. The file must exist.
	 */
	public static final int READ = 0x0000;
	/**
	 * For opening files in write only mode. If any of the
	 * CREATE/TRUNCATE/APPEND flags are specified the file will be created if it
	 * does not exist - otherwise the file must exist.
	 */
	public static final int WRITE = 0x0001;
	/**
	 * For opening files in read write mode. If any of the
	 * CREATE/TRUNCATE/APPEND flags are specified the file will be created if it
	 * does not exist - otherwise the file must exist.
	 */
	public static final int READ_WRITE = 0x0002;

	/**
	 * for write open modes, create the file if it does not exist. If the file
	 * exists this flag has no effect.
	 */
	public static final int CREATE = 0x0100;

	/**
	 * for write open modes, create the file if it does not exist, or truncate
	 * an existing file on open and set file position to the beginning of the
	 * file
	 */
	public static final int TRUNCATE = 0x0200;

	/**
	 * for write open modes, create the file if it does not exist and set the
	 * file position to the end of the file. All write are at end of the file.
	 */
	public static final int APPEND = 0x0400;

	// *******************************************************************
	// VM assumes existence and sequence of the following instance fields.
	// *******************************************************************

	/** native file handle. Is -1 when not open */
	private int _native_handle = -1;

	/**
	 * flags used to open the file. Used for sanity checking during native read
	 * write operations.
	 */
	private int _flags;

	/**
	 * used by the VM to ensure that data is appropriately flush between write
	 * and read operations. Set to true after a write - causes a native flush
	 * before usage of other methods that may be affected by buffered data.
	 */
	private boolean flush_pending = false;

	// *******************************************************************

	/** Private constructor - forces use of static Open() method */
	private File(int handle, int flags) {
		_native_handle = handle;
		_flags = flags;
	};

	/**
	 * Open a file of the given name using the given flags. The file name will
	 * be converted to a non-unicode C string internally for use on the platform
	 * so do not use non ASCII characters in the file name.
	 *
	 * The flags are TODO (more ...). Note that the native layer performs no
	 * sanity checking on the flags. So get them right.
	 *
	 * @param filename
	 *            the name of the platform file to open.
	 * @param flags
	 * @return a File object representing a native file.
	 * @throws IOException
	 *             if any error occurs. Specifically <code>FileNotFoundException</code> if
	 *             the file cannot be opened for any reason. This may include
	 *             attempting to open a read only file in write mode, as well as
	 *             the file just plain not existing if CREATE is not specified.
	 */
	public static File open(String filename, int flags) throws IOException {

		int handle = open0(filename, flags);

		return new File(handle, flags);
	}

	/** native function to do a real file open */
	private static native int open0(String filename, int flags) throws FileNotFoundException;

	/**
	 * Close a file. Attempting to close a file that is already closed has no
	 * effect.
	 *
	 * @throws IOException
	 *             if any error occurs.
	 */
	public native void close() throws IOException;

	/**
	 * Read bytes from the file at the current position into the destination
	 * byte array.
	 *
	 * @param dst -
	 *            the byte array buffer where the data will be written.
	 * @param offset -
	 *            the offset into the byte array where the writing will start
	 * @param count -
	 *            the number of bytes to read. The actual number of bytes read
	 *            may differ from this requested value.
	 * @return the number of bytes read, or -1 if the end of the file was
	 *         reached by the previous read.
	 * @throws IOException if any IO error occurs inlcuding attempting to read from
	 * 			a write-only file.
     * @exception  NullPointerException If <code>dst</code> is <code>null</code>.
     * @exception  IndexOutOfBoundsException If <code>offset</code> is negative,
     * <code>count</code> is negative, or <code>count</code> is greater than
     * <code>dst.length - offset</code>	 */
	public native int read(byte[] dst, int offset, int count)
			throws IOException;

	/**
	 * Write bytes from the given byte array to the file.
	 *
	 * @param src -
	 *            the byte array to write bytes from.
	 * @param offset -
	 *            the offset into the byte offset where wring will start from.
	 * @param count -
	 *            the number of bytes to write
	 * @throws IOException
	 *             if any error occurs including attempting to write to a read-only file.
     * @exception  NullPointerException If <code>src</code> is <code>null</code>.
     * @exception  IndexOutOfBoundsException If <code>offset</code> is negative,
     * <code>count</code> is negative, or <code>count</code> is greater than
     * <code>dst.length - offset</code>.
	 */
	public native void write(byte[] src, int offset, int count)
			throws IOException;

	/**
	 * Set the current file position pointer. This is the position that the next
	 * read or write operation will operate on.
	 *
	 * @param offset -
	 *            the offset number of bytes relative to <code>origin</code>
	 *            to set the position to.
	 * @param origin -
	 *            origin can be <code>SEEK_SET</code>, <code>SEEK_CUR</code>,
	 *            or <code>SEEK_END</code> to specify that offset is to be
	 *            measured from the beginning, from the current position, or
	 *            from the end of the file respectively.
	 * @throws IOException
	 *             if any error occurs
	 */
	public native void setPosition(int offset, int origin) throws IOException;

	/**
	 * Gets the current file position.
	 *
	 * @return the current file position.
	 *
	 * @throws IOException
	 *             if any error occurs.
	 */
	public native int getPosition() throws IOException;

	/**
	 * Gets the size of the open file in bytes.
	 *
	 * @return the size of the open file in bytes.
	 *
	 * @throws IOException
	 *             if any error occurs.
	 */
	public native int sizeOf() throws IOException;

	/**
	 * Renames a file to a new name.
	 *
	 * @param oldname
	 *            the current name of the file. Note that the filename will be
	 *            converted internally to a C string on ASCII only characters.
	 * @param newname
	 *            the new name of the file. Note that the filename will be
	 *            converted internally to a C string with ASCII only characters.
	 * @throws IOException
	 *             if any error occurs.
	 */
	public native static void rename(String oldname, String newname)
			throws IOException;

	/**
	 * Delete the file with the given filename.
	 *
	 * @param filename
	 *            the file name to delete. Note that the filename will be
	 *            converted internally to a C string with ASCII only characters.
	 * @throws IOException
	 *             if any error occurs
	 */
	public native static void remove(String filename) throws IOException;

	/**
	 * Reports if a given file name exists.
	 *
	 * @param filename
	 *            the file name to check. Note that the filename will be
	 *            converted internally to a C string with ASCII only characters.
	 * @return true if a file of the given name exists.
	 *
	 * @throws IOException
	 *             if any error occurs.
	 */
	public native static boolean exists(String filename) throws IOException;

	/**
	 * Truncate the file to a given length. Some platforms may treat a newLength
	 * value that is larger than the current size of the file as an error - some
	 * may just do nothing. Be wary.
	 *
	 * Indeed, some platforms may not actually support truncation of files (ANSI C
	 * file IO has no 'truncate') - so developers should not assume that issuing
	 * a truncate and not receiving an error means the file has actually been
	 * truncated. The platform is permitted to ignore the truncate request.
	 *
	 * @param newlength -
	 *            the new length of the file.
	 * @throws IOException
	 *             if any error occurs.
	 */
	public native void truncate(int newlength) throws IOException;

	// TODO fileList
//	public native static String[] fileList(String startsWithFilter)
//			throws IOException;

}
