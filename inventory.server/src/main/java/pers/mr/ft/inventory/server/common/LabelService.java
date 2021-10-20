package pers.mr.ft.inventory.server.common;

import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.server.jdbc.SQL;

import pers.mr.ft.inventory.server.ServerSession;
import pers.mr.ft.inventory.shared.common.ILabelService;

public class LabelService implements ILabelService {

  @Override
  public String load(Long id, String lang, boolean useDefault) {
    String defaultLanguage =  ServerSession.get().getDefaultLanguage();
    
    Object[][] rows = SQL.select("SELECT label FROM multilingual_label WHERE id = :id AND langcode = :lang",
        new NVPair("id", id),
        new NVPair("lang", lang));
    if (rows.length==0 && useDefault) {
      rows = SQL.select("SELECT label FROM multilingual_label WHERE id = :id AND langcode = :lang",
          new NVPair("id", id),
          new NVPair("lang", defaultLanguage));
    }
    if (rows.length==0) {
      return "";
    }
    else {
      return (String) rows[0][0];
    }
  }

  @Override
  public Long save(Long id, String lang, String value) {
    Object[][] rows = new Object[0][];
    
    if (id>0) {
      rows = SQL.select("SELECT version FROM multilingual_label WHERE id = :id AND langcode = :lang",
        new NVPair("id", id),
        new NVPair("lang", lang));
    }
    if (rows.length==0) {
      // N'existe pas
      if (id == 0) {
        // Le label n'existe pas non plus
        SQL.insert("INSERT INTO multilingual_label (langcode, label, version) VALUES (:lang, :value, :version)" , 
            new NVPair("lang", lang),
            new NVPair("value", value),
            new NVPair("version", 1));
        rows = SQL.select("SELECT LASTVAL()");
        id = (Long) rows[0][0];
      }
      else {
        // Le label existe mais pas dans cette langue
        SQL.insert("INSERT INTO multilingual_label (id, langcode, label, version) VALUES (:id, :lang, :value, :version)" , 
            new NVPair("id", id),
            new NVPair("lang", lang),
            new NVPair("value", value),
            new NVPair("version", 1)
            );
      }
    }
    else {
      // Existe
      Long version = (Long) rows[0][0] + 1;
      SQL.update("UPDATE multilingual_label SET label = :value, version = :version WHERE id = :id AND langcode = :lang",
          new NVPair("id", id),
          new NVPair("lang", lang),
          new NVPair("value", value),
          new NVPair("version", version)
          );
    }
    return id;
  }

}
