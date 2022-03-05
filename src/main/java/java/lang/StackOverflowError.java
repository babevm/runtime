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
 * Thrown when a stack overflow occurs because an application recurses too
 * deeply.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public class StackOverflowError extends VirtualMachineError {

	public StackOverflowError() {
		super();
	}

	public StackOverflowError(String message) {
		super(message);
	}


}
