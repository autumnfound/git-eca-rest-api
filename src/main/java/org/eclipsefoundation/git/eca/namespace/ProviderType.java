/*******************************************************************************
 * Copyright (C) 2020 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
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
