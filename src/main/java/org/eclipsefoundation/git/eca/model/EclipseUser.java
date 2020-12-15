/**
 * Copyright (C) 2020 Eclipse Foundation
 *
 * <p>This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.git.eca.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Represents a users Eclipse Foundation account
 *
 * @author Martin Lowe
 */
public class EclipseUser {
  private int uid;
  private String name;
  private String mail;
  private ECA eca;
  private boolean isCommitter;
  // this field is internal for tracking bot stubs
  @JsonIgnore private boolean isBot;

  /**
   * Create a bot user stub when there is no real Eclipse account for the bot.
   *
   * @param user the Git user that was detected to be a bot.
   * @return a stubbed Eclipse user bot object.
   */
  public static EclipseUser createBotStub(GitUser user) {
    EclipseUser stub = new EclipseUser();
    stub.setName(user.getName());
    stub.setMail(user.getMail());
    stub.setEca(new ECA());
    stub.setBot(true);
    return stub;
  }

  /** @return the id */
  public int getId() {
    return uid;
  }

  /** @param id the id to set */
  public void setId(int uid) {
    this.uid = uid;
  }

  /** @return the name */
  public String getName() {
    return name;
  }

  /** @param name the name to set */
  public void setName(String name) {
    this.name = name;
  }

  /** @return the mail */
  public String getMail() {
    return mail;
  }

  /** @param mail the mail to set */
  public void setMail(String mail) {
    this.mail = mail;
  }

  /** @return the eca */
  public ECA getEca() {
    return eca;
  }

  /** @param eca the eca to set */
  public void setEca(ECA eca) {
    this.eca = eca;
  }

  /** @return the isCommitter */
  public boolean isCommitter() {
    return isCommitter;
  }

  /** @param isCommitter the isCommitter to set */
  public void setCommitter(boolean isCommitter) {
    this.isCommitter = isCommitter;
  }

  /** @return the isBot */
  public boolean isBot() {
    return isBot;
  }

  /** @param isBot the isBot to set */
  private void setBot(boolean isBot) {
    this.isBot = isBot;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("EclipseUser [uid=");
    builder.append(uid);
    builder.append(", name=");
    builder.append(name);
    builder.append(", mail=");
    builder.append(mail);
    builder.append(", eca=");
    builder.append(eca);
    builder.append(", isCommitter=");
    builder.append(isCommitter);
    builder.append(", isBot=");
    builder.append(isBot);
    builder.append("]");
    return builder.toString();
  }

  /**
   * ECA for Eclipse accounts, representing whether users have signed the Eclipse Committer
   * Agreement to enable contribution.
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

    /** @return the signed */
    public boolean isSigned() {
      return signed;
    }

    /** @param signed the signed to set */
    public void setSigned(boolean signed) {
      this.signed = signed;
    }

    /** @return the canContributeSpecProject */
    public boolean isCanContributeSpecProject() {
      return canContributeSpecProject;
    }

    /** @param canContributeSpecProject the canContributeSpecProject to set */
    public void setCanContributeSpecProject(boolean canContributeSpecProject) {
      this.canContributeSpecProject = canContributeSpecProject;
    }
  }
}
