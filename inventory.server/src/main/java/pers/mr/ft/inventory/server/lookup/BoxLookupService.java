package pers.mr.ft.inventory.server.lookup;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.server.jdbc.ISqlService;
import org.eclipse.scout.rt.server.jdbc.lookup.AbstractSqlLookupService;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;

import pers.mr.ft.inventory.shared.lookup.IBoxLookupService;

public class BoxLookupService extends AbstractSqlLookupService<Long> implements IBoxLookupService {
  @Override
  protected String getConfiguredSqlSelect() {
    return "" +
        "SELECT id, label FROM box " +
        "WHERE 1=1 " +
        "<key> AND id = :key </key>" +
        "<text> AND UPPER(label) LIKE UPPER(:text || '%') </text> " +
        "<all></all>";
  }

  /*
   * Si la clé d'une recherche par texte est un nombre, on considère que c'est l'id de la boite et
   * on fait une recherche par clé.
   * Attention, l'appelant ajoute un * en fin de chaine, à ignorer dans ce cas.
   */
  @Override
  public List<ILookupRow<Long>> getDataByText(ILookupCall<Long> call) {
    // change wildcards in text to db specific wildcards
    if (call.getText() != null) {
      String s = call.getText();
      
      // Vérifie si c'est un entier éventuellement terminé par *
      Pattern p = Pattern.compile("^([0-9]+)\\*?$");
      Matcher m = p.matcher(s);
      if (m.matches()) {
        String numberString = m.group(1);
        Long key = Long.parseLong(numberString);
        call.setKey(key);
        String sql = getConfiguredSqlSelect();
        return execLoadLookupRows(sql, filterSqlByKey(sql), call);
      }
      String sqlWildcard = BEANS.get(ISqlService.class).getSqlStyle().getLikeWildcard();
      call.setText(s.replace(call.getWildcard(), sqlWildcard));
    }
    String sql = getConfiguredSqlSelect();
    return execLoadLookupRows(sql, filterSqlByText(sql), call);
  }

}
