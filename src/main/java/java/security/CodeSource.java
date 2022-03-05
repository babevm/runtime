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
 * <p>
 * A CodeSource represents the origin of code as a location and a set
 * certificates used to verify the code at that location.
 *
 * Unlike J2SE which uses a <code>URL</code> to define the location of a
 * CodeSource object, here we simply use a String.
 *
 * <p>
 * CodeSource objects are immutable.
 *
 * @author Greg McCreath
 * @since 0.0.1
 */
public final class CodeSource {

	/*
	 * VM assumes existence and order of the following members - do not move or
	 * amend.
	 */
	private String _location;
	private Certificate[] _certificates;

	/* ***** */

	/**
	 * Creates a new CodeSource for a given location and set of certificates.
	 *
	 * The source name of "main" is reserved for system use - attempting to
	 * create a CodeSource with a source of "main" will throw an
	 * {@link IllegalArgumentException}.
	 *
	 * The source may not be <code>null</code> or empty.
	 *
	 * @param source the location that the code originates from.
	 * @param certificates the set of certificates of the code.  Not yet implemented.
	 */
	public CodeSource(String source, Certificate[] certificates) {

		if ((source == null) || source.length() == 0)
			throw new IllegalArgumentException("null or empty source");

		if (source.equals("main"))
			throw new IllegalArgumentException("Illegal source name \"main\"");

		_location = source;
		_certificates = certificates;
	}

	/**
	 * Returns the CodeSource location
	 *
	 * @return the location
	 */
	public String getLocation() {
		return _location;
	}

	/**
	 * Returns the CodeSource certificates.
	 *
	 * @return the certificates.
	 */
	public Certificate[] getCertificates() {
		return (_certificates == null) ? null : _certificates.clone();
	}

	/**
	 * Tests object equality. CodeSource equality if the locations and
	 * certificates match.
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (!(obj instanceof CodeSource))
			return false;

		CodeSource cs = (CodeSource) obj;

		if (_location == null) {
			if (cs._location != null)
				return false;
		} else {
			if (!_location.equals(cs._location))
				return false;
		}

		return matchCertificates(cs);
	}

	private boolean matchCertificates(CodeSource cs) {
		// TODO - when certificates are implemented, make sure the CodeSource
		//  "equals" takes them into account.
		return true;
	}

}
