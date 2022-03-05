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
 * Thrown to indicate that an attempt has been made to store the wrong type of object into an array
 * of objects. For example, the following code generates an ArrayStoreException:

<code>
     Object x[] = new String[3];
     x[0] = new Integer(0);
</code>

 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public class ArrayStoreException extends RuntimeException {

	public ArrayStoreException() {
		super();
	}

	public ArrayStoreException(String message) {
		super(message);
	}

}
