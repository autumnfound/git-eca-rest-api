package org.eclipsefoundation.git.eca.model;

import java.util.Objects;

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EclipseAccount [id=");
		builder.append(uid);
		builder.append(", name=");
		builder.append(name);
		builder.append(", mail=");
		builder.append(mail);
		builder.append(", eca=");
		builder.append(eca);
		builder.append(", isCommitter=");
		builder.append(isCommitter);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(eca, isCommitter, mail, name, uid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EclipseUser other = (EclipseUser) obj;
		return Objects.equals(eca, other.eca) && isCommitter == other.isCommitter && Objects.equals(mail, other.mail)
				&& Objects.equals(name, other.name) && uid == other.uid;
	}

	/**
	 * ECA for Eclipse accounts, representing whether users have signed the Eclipse
	 * Committer Agreement to enable contribution.
	 */
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class ECA {
		private boolean signed = false;
		private boolean canContributeSpecProject = false;

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

		@Override
		public int hashCode() {
			return Objects.hash(signed, canContributeSpecProject);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ECA other = (ECA) obj;
			return signed == other.signed && canContributeSpecProject == other.canContributeSpecProject;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("ECA [signed=");
			builder.append(signed);
			builder.append(", canContributeSpecProject=");
			builder.append(canContributeSpecProject);
			builder.append("]");
			return builder.toString();

		}
	}
}
