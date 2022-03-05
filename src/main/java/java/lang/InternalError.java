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
 * Thrown to indicate some unexpected internal error has occurred in
 * the Java Virtual Machine.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public class InternalError extends VirtualMachineError {

	public InternalError() {
		super();
	}

	public InternalError(String message) {
		super(message);
	}

}
