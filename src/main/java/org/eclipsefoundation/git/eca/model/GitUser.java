package org.eclipsefoundation.git.eca.model;

/**
 * Basic object representing a Git users data required for verification.
 * 
 * @author Martin Lowe
 *
 */
public class GitUser {
	private String name;
	private String mail;

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [name=");
		builder.append(name);
		builder.append(", mail=");
		builder.append(mail);
		builder.append("]");
		return builder.toString();
	}

}
