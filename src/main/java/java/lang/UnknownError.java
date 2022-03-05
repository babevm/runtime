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
 * Thrown when an unknown but serious exception has occurred in the Virtual
 * Machine.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public class UnknownError extends VirtualMachineError {

	public UnknownError() {
		super();
	}

	public UnknownError(String message) {
		super(message);
	}

}
