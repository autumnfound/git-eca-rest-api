/*******************************************************************************
 * Copyright (C) 2020 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipsefoundation.git.eca.service.impl;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipsefoundation.git.eca.oauth.EclipseApi;
import org.eclipsefoundation.git.eca.service.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
 * Default implementation for requesting an OAuth request token. The reason that
 * this class is implemented over the other implementations baked into Quarkus
 * 
 * @author Martin Lowe
 *
 */
@Singleton
public class DefaultOAuthService implements OAuthService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOAuthService.class);

	@ConfigProperty(name = "oauth2.client-id")
	String id;
	@ConfigProperty(name = "oauth2.client-secret")
	String secret;
	@ConfigProperty(name = "oauth2.scope")
	String scope;

	// service reference (as we only need one)
	private OAuth20Service service;

	// token state vars
	private long expirationTime;
	private String accessToken;

	/**
	 * Create an OAuth service reference.
	 */
	@PostConstruct
	void createServiceRef() {
		this.service = new ServiceBuilder(id).apiSecret(secret).scope(scope).build(EclipseApi.instance());
	}

	@Override
	public String getToken() {
		// lock on the class instance to stop multiple threads from requesting new
		// tokens at the same time
		synchronized (this) {
			if (accessToken == null || System.currentTimeMillis() >= expirationTime) {
				// clear access token
				this.accessToken = null;
				try {
					OAuth2AccessToken requestToken = service.getAccessTokenClientCredentialsGrant();
					if (requestToken != null) {
						this.accessToken = requestToken.getAccessToken();
						this.expirationTime = System.currentTimeMillis()
								+ TimeUnit.SECONDS.toMillis(requestToken.getExpiresIn().longValue());
					}
				} catch (IOException e) {
					LOGGER.error("Issue communicating with OAuth server for authentication", e);
				} catch (InterruptedException e) {
					LOGGER.error("Authentication communication was interrupted before completion", e);
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					LOGGER.error("Error while retrieving access token for request", e);
				}
			}
		}
		return accessToken;
	}

}
