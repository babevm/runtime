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

import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.SecurityManager;

/**
 *
 * @author Greg McCreath
 * @since 0.0.1
 */
public final class ClassLoader {

	/*
	 * Do not move/delete ANY of these fields. VM assumes their existence and
	 * order.
	 */
	private ClassLoader _parent = null;

	/*
	 * The number of Class objects held by this Classloader - used by the VM to
	 * grow the _classes array if need be.
	 */
	private int _nr_classes;

	/*
	 * the Class objects that this ClassLoader owns. Managed and expanded by the
	 * VM as required.
	 */
	private Class<?> _classes[];

	/*
	 * the paths the VM will use to find classes for this classloader. Managed
	 * by the VM.
	 */
	private String[] _classPath;

	/* the class loader's ProtectionDomain */
	private ProtectionDomain _protectionDomain;

	/* ******************************************************************/

	/**
	 * Creates a new ClassLoader object using the system ClassLoader as a
	 * parent.
	 *
	 * Callers must have the "createClassLoader" SecurityPermission.
	 */
	public ClassLoader() {
		this(getSystemClassLoader());
	}

	/**
	 * Creates a new ClassLoader object as as child of the given parent. The
	 * class search path of the parent will be assumed.
	 *
	 * Callers must have the "createClassLoader" SecurityPermission.
	 *
	 * @param parent a given parent ClassLoader.
	 */
	public ClassLoader(ClassLoader parent) {
		this(parent, null, null);
	}

	/**
	 * <p>
	 * Creates a new ClassLoader object as as child of the given parent. If not
	 * <code>null</code>, the given protection domain will become the new
	 * classloader's ProtectionDomain.
	 *
	 * <p>
	 * If not <code>null</code>, the given path will be the class path for the
	 * new ClassLoader.
	 *
	 * <p>
	 * If the given protection main is <code>null</code>, the new ClassLoader
	 * object will have a protection domain created for with with a CodeSource
	 * will have a location that is the concatenation of all the non-null
	 * elements in its classpath separated by the vertical bar ("|") character.
	 *
	 * . *
	 * <p>
	 * Callers must have the "createClassLoader" SecurityPermission.
	 *
	 * @param parent a given parent ClassLoader. If <code>null</code>, the
	 *            system class loader will be used.
	 * @param domain
	 * @param paths
	 */
	public ClassLoader(ClassLoader parent, ProtectionDomain domain, String[] paths) {
		SecurityManager.checkPermission(SecurityManager.CREATE_CLASSLOADER_PERMISSION);

		_parent = (parent != null) ? parent : getSystemClassLoader();
		_classes = new Class[3];

		_classPath = ((paths == null) || (paths.length == 0)) ? parent._classPath : paths;

		if (domain != null) {
			/* if a domain is provided.. */
			_protectionDomain = domain;
		} else {
			/*
			 * if no alias, join the classpath segments together to form a
			 * location string.
			 */
			StringBuilder sb = new StringBuilder();
			for (String s : _classPath) {
				if (s != null)
					if (sb._length > 0)
						sb.append("|");
				sb.append(s);
			}
			_protectionDomain = new ProtectionDomain(new CodeSource(sb.toString(), null), null);
		}
	}

	/**
	 * Returns the system class loader. The system class loader is the class
	 * loader that loads the main java class.
	 *
	 * @return the system java class
	 */
	native static ClassLoader getSystemClassLoader();

	/**
	 * Returns the ProtectionDomain for this classloader.
	 *
	 * @return the ProtectionDomain.
	 */
	ProtectionDomain getProtectionDomain() {
		return _protectionDomain;
	}

}
