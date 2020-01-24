package org.eclipsefoundation.git.eca.oauth;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth2.clientauthentication.ClientAuthentication;
import com.github.scribejava.core.oauth2.clientauthentication.RequestBodyAuthenticationScheme;

/**
 * Wrapper around the OAuth API for Scribejava. Enables OAuth2.0 binding to the
 * Eclipse Foundation OAuth server.
 * 
 * @author Martin Lowe
 *
 */
public class EclipseApi extends DefaultApi20 {

	@Override
	public String getAccessTokenEndpoint() {
		return "https://accounts.eclipse.org/oauth2/token";
	}

	@Override
	protected String getAuthorizationBaseUrl() {
		return null;
	}

	@Override
	public ClientAuthentication getClientAuthentication() {
		return RequestBodyAuthenticationScheme.instance();
	}

	private static class InstanceHolder {
		private static final EclipseApi INSTANCE = new EclipseApi();
	}

	public static EclipseApi instance() {
		return InstanceHolder.INSTANCE;
	}
}
