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

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Represents a users Eclipse Foundation account
 * 
 * @author Martin Lowe
 *
 */
public class EclipseUser {
	private int uid;
	private String name;
	private String mail;
	private ECA eca;
	private boolean isCommitter;

	/**
	 * @return the id
	 */
	public int getId() {
		return uid;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int uid) {
		this.uid = uid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the mail
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * @param mail the mail to set
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * @return the eca
	 */
	public ECA getEca() {
		return eca;
	}

	/**
	 * @param eca the eca to set
	 */
	public void setEca(ECA eca) {
		this.eca = eca;
	}

	/**
	 * @return the isCommitter
	 */
	public boolean isCommitter() {
		return isCommitter;
	}

	/**
	 * @param isCommitter the isCommitter to set
	 */
	public void setCommitter(boolean isCommitter) {
		this.isCommitter = isCommitter;
	}

	/**
	 * ECA for Eclipse accounts, representing whether users have signed the Eclipse
	 * Committer Agreement to enable contribution.
	 */
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class ECA {
		private boolean signed;
		private boolean canContributeSpecProject;
		
		public ECA() {
			this(false, false);
		}
		
		public ECA(boolean signed, boolean canContributeSpecProject) {
			this.signed = signed;
			this.canContributeSpecProject = canContributeSpecProject;
		}

		/**
		 * @return the signed
		 */
		public boolean isSigned() {
			return signed;
		}

		/**
		 * @param signed the signed to set
		 */
		public void setSigned(boolean signed) {
			this.signed = signed;
		}

		/**
		 * @return the canContributeSpecProject
		 */
		public boolean isCanContributeSpecProject() {
			return canContributeSpecProject;
		}

		/**
		 * @param canContributeSpecProject the canContributeSpecProject to set
		 */
		public void setCanContributeSpecProject(boolean canContributeSpecProject) {
			this.canContributeSpecProject = canContributeSpecProject;
		}
	}
}
