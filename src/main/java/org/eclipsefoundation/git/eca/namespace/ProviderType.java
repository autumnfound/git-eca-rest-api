
package org.eclipsefoundation.git.eca.namespace;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents a provider that can submit commits for validation. This is used
 * for matching properly on some legacy fields.
 * 
 * @author Martin Lowe
 *
 */
public enum ProviderType {
	GITHUB, GITLAB, GERRIT;

	/**
	 * @return human-friendly name of the ProviderType
	 */
	@JsonValue
	public String getValue() {
		return name().toLowerCase();
	}
}