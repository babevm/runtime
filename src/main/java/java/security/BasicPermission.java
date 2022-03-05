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
 *
 * <p>The BasicPermission class extends the Permission class, and can be used as
 * the base class for permissions that want to follow the same naming convention
 * as BasicPermission.
 *
 * <p>The name for a BasicPermission is the name of the given permission (for
 * example, "exit", "setFactory", "print.queueJob", etc). The naming convention
 * follows the hierarchical property naming convention. An asterisk may appear
 * by itself, or if immediately preceded by a "." may appear at the end of the
 * name, to signify a wildcard match. For example, "*" and "java.*" are valid,
 * while "*java", "a*b", and "java*" are not valid. This convention is assumed
 * but not enforced.
 *
 * <p>Unlike the J2SE, a BasicPermission may have actions. Actions are used in both
 * object equality and implies. Additionally, BasicPermission is concrete and
 * may be instantiated, unlike J2SE where it is abstract.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public class BasicPermission extends Permission {

	// does this permission have a wildcard at the end?
	private boolean _wildcard;

	// the name without the wildcard on the end
	private String _path;

	private void init(String name) {

		int len = name.length();

		char last = name.charAt(len - 1);

		// Is wildcard or ends with ".*"?
		if (last == '*' && (len == 1 || name.charAt(len - 2) == '.')) {
			_wildcard = true;
			if (len == 1) {
				_path = "";
			} else {
				_path = name.substring(0, len - 1);
			}
		} else {
			_path = name;
		}
	}

	public BasicPermission(String name) {
		this(name, 0);
	}

	public BasicPermission(String name, int actions) {
		super(name, actions);
		init(name);
	}

	public boolean implies(Permission p) {

		// must also be a BasicPermission
		if ((p == null) || (p.getClass() != getClass()))
			return false;

		BasicPermission that = (BasicPermission) p;

		boolean nameOkay = false;

		if (this._wildcard) {
			if (that._wildcard) {
				// one wildcard can imply another
				nameOkay = that._path.startsWith(_path);
			} else {
				// make sure ap.path is longer so a.b.* doesn't imply a.b
				nameOkay = (that._path.length() > this._path.length()) && that._path.startsWith(this._path);
			}
		} else {
			if (that._wildcard) {
				// a non-wildcard can't imply a wildcard
				return false;
			} else {
				nameOkay = this._path.equals(that._path);
			}
		}

		/*
		 * the name is okay so check the given permission actions are a subset of
		 * this permissions actions
		 */
		return (nameOkay && (this._actions & that._actions) == that._actions);
	}

	public boolean equals(Object obj) {

		if (obj == this)
			return true;

		if ((obj == null) || (obj.getClass() != getClass()))
			return false;

		BasicPermission bp = (BasicPermission) obj;

		return ((bp._actions == this._actions) && (_name.equals(bp._name)));
	}

	public int hashCode() {
		return this.getName().hashCode();
	}

	public PermissionCollection newPermissionCollection() {
		return new BasicPermissionCollection(BasicPermission.class);
	}

}
