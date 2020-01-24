package org.eclipsefoundation.git.eca.service;

/**
 * Used to generate OAuth tokens for use with internal services rather than
 * bolted on introspection. This is required over the (now deprecated) Elytron
 * plugin or the OIDC plugin as those plugins work with requests to validate
 * incoming rather than outgoing requests.
 * 
 * @author Martin Lowe
 *
 */
public interface OAuthService {

	/**
	 * Retrieve an access token for the service from the Eclipse API for internal
	 * usage.
	 * 
	 * @return current access token, or null if none could be retrieved for current
	 *         API credentials/settings.
	 */
	String getToken();
}
