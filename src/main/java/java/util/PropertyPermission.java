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

package java.util;

import java.security.BasicPermission;
import java.security.BasicPermissionCollection;
import java.security.Permission;
import java.security.PermissionCollection;

/**
 * Security permission for protection of resources that may be read to or
 * written from supporting the basic actions of:
 *
 * <ul>
 * <li>{@link Permission#ACTION_READ}
 * <li>{@link Permission#ACTION_WRITE}
 * </ul>
 *
 * @author Greg McCreath
 *
 */
public final class PropertyPermission extends BasicPermission {

	/* all the actions support by this permission */
	private static final int ALL = (Permission.ACTION_READ | Permission.ACTION_WRITE);

	public PropertyPermission(String name, int actions) {
		super(name, actions);

		if ((actions & ALL) != actions)
			throw new IllegalArgumentException("invalid actions");

	}

	public PermissionCollection newPermissionCollection() {
		return new BasicPermissionCollection(PropertyPermission.class);
	}

}
