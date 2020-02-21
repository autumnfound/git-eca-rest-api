/*******************************************************************************
 * Copyright (C) 2020 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipsefoundation.git.eca.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipsefoundation.git.eca.namespace.ProviderType;

/**
 * Represents a request to validate a list of commits.
 * 
 * @author Martin Lowe
 *
 */
public class ValidationRequest {
	private String repoUrl;
	private List<Commit> commits;
	private ProviderType provider;

	/**
	 * @return the repoUrl
	 */
	public String getRepoUrl() {
		return repoUrl;
	}

	/**
	 * @param repoUrl the repoUrl to set
	 */
	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}

	/**
	 * @return the commits
	 */
	public List<Commit> getCommits() {
		return new ArrayList<>(commits);
	}

	/**
	 * @param commits the commits to set
	 */
	public void setCommits(List<Commit> commits) {
		this.commits = new ArrayList<>(commits);
	}

	/**
	 * @return the provider
	 */
	public ProviderType getProvider() {
		return provider;
	}

	/**
	 * @param provider the provider to set
	 */
	public void setProvider(ProviderType provider) {
		this.provider = provider;
	}
}
