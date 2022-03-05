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
public final class StackTraceElement {

	/* do not move or rearrange these fields - they are used by the VM */
	/* ********************************************** */
	private String className;
	private String methodName;
	private String fileName;
	private int lineNumber;
	/* ********************************************** */

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getFileName() {
		return fileName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public boolean isNativeMethod() {
		return lineNumber == -2;
	}

	public String toString() {
		String fileName = this.fileName != null ? this.fileName : "Unknown Source";
		return getClassName()
				+ "."
				+ methodName
				+ (isNativeMethod() ? "(Native Method)" : (fileName != null
						&& lineNumber >= 0 ? "(" + fileName + ":" + lineNumber
						+ ")" : (fileName != null ? "(" + fileName + ")"
						: "(Unknown Source)")));
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof StackTraceElement))
			return false;
		StackTraceElement e = (StackTraceElement) obj;
		return e.className.equals(className) && e.lineNumber == lineNumber
				&& eq(methodName, e.methodName) && eq(fileName, e.fileName);
	}

	private static boolean eq(Object a, Object b) {
		return a == b || (a != null && a.equals(b));
	}

	/**
	 * Returns a hash code value for this stack trace element.
	 */
	public int hashCode() {
		int result = 31 * className.hashCode() + methodName.hashCode();
		result = 31 * result + (fileName == null ? 0 : fileName.hashCode());
		result = 31 * result + lineNumber;
		return result;
	}

}