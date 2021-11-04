package pers.mr.ft.inventory.shared.model;

import java.io.Serializable;

public class Document implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id = null;
  private String name = null;
  private String lang = null;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  
  public String getLang() {
    return lang;
  }
  public void setLang(String lang) {
    this.lang = lang;
  }
  public Document(Long id, String name) {
    this.id = id;
    this.name = name;
  }
  public Document(Long id, String name, String lang) {
    this.id = id;
    this.name = name;
    this.lang = lang;
  }
}
