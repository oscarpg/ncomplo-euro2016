package org.jgayoso.ncomplo.web.beans;

import java.io.Serializable;

public class ResetPasswordBean implements Serializable {

  private static final long serialVersionUID = 1984212722109860979L;
  private String login;
  private String email;

  public ResetPasswordBean() {
    super();
  }

  public ResetPasswordBean(String login, String email) {
    super();
    this.login = login;
    this.email = email;
  }

  public String getLogin() {
    return login;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
