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

package java.security;

/**
 * <p>A provider interface of {@link PolicyEntry} objects for the system security
 * policy maintained by {@link Policy}.
 *
 * <p>A PolicyLoader implementation has just a single task - to provide an array of
 * PolicyEntry objects that define the system security policy.
 *
 * <p>A PolicyLoader implementation must provide a no-args constructor.
 *
 * <p>The Policy object will call the {@link #getPolicyEntries()} of the installed
 * policy loader in a privileged manner. That is, the code executing in
 * getPolicyEntries() is executed as system code regardless of what is actually
 * on the java execution stack.
 *
 * <p>As the policy loader provides highly sensitive information, implementations
 * must protect themselves against unauthorised access. In order to provide this
 * protection, both the no-args constructor and getPolicyEntries() method must
 * perform a permissions check. Failure to do so may allow unpermitted code to
 * inspect the system security policy, which, event though the security policy
 * is immutable, by all account is probably not a good idea..
 *
 * <ul>
 * <li>The constructor must perform a {@link SecurityPermission} permission check of name
 * "createPolicyLoader".
 * <li>The getPolicyEntries method must perform a {@link SecurityPermission}
 * permission check of name "getPolicyEntries".
 * </ul>
 *
 * To illustrate the anticipated usage, an example "all permissions" implementation
 * follows:
 *
 * <pre>
 * public final class AllPermissionsPolicyLoader implements PolicyLoader {
 *
 * 	public AllPermissionsPolicyLoader() {
 * 		AccessController.checkPermission(new SecurityPermission(&quot;createPolicyLoader&quot;));
 * 	}
 *
 * 	public PolicyEntry[] getPolicyEntries() {
 * 		AccessController.checkPermission(new SecurityPermission(&quot;getPolicyEntries&quot;));
 * 		return new PolicyEntry[] { new PolicyEntry(null, SecurityConstants.ALL_PERMISSIONS) };
 * 	}
 * }
 *
 * </pre>
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public interface PolicyLoader {

	/**
	 * Returns the array of PolicyEntry objects that define the system security
	 * policy. This method may be called more than once - certainly it will be
	 * called as the security policy is first established, but it may also be
	 * called after a {@link Policy#refresh()} has been performed.
	 *
	 * @return an array of PolicyEntry objects. A <code>null</code> return value
	 *         is not valid - if no PolicyEntry objects are to be returned,
	 *         implementation must return an empty array.
	 */
	public PolicyEntry[] getPolicyEntries();

}
