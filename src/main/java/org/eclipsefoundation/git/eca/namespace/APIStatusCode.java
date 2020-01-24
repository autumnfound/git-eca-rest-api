package org.eclipsefoundation.git.eca.namespace;

import com.fasterxml.jackson.annotation.JsonValue;

public enum APIStatusCode {
	SUCCESS_DEFAULT(200), SUCCESS_COMMITTER(201), SUCCESS_CONTRIBUTOR(202), ERROR_DEFAULT(-401), ERROR_SIGN_OFF(-402),
	ERROR_SPEC_PROJECT(-403);

	private int code;

	private APIStatusCode(int code) {
		this.code = code;
	}

	@JsonValue
	public int getValue() {
		return code;
	}
}
