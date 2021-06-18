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

public enum APIStatusCode {
	SUCCESS_DEFAULT(200), SUCCESS_COMMITTER(201), SUCCESS_CONTRIBUTOR(202), ERROR_DEFAULT(-401), ERROR_SIGN_OFF(-402),
	ERROR_SPEC_PROJECT(-403), ERROR_AUTHOR(-404), ERROR_COMMITTER(-405), ERROR_PROXY_PUSH(-406);

	private int code;

	private APIStatusCode(int code) {
		this.code = code;
	}

	@JsonValue
	public int getValue() {
		return code;
	}
}
