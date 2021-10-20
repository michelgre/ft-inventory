package pers.mr.ft.inventory.server.lookup;

import org.eclipse.scout.rt.server.jdbc.lookup.AbstractSqlLookupService;

import pers.mr.ft.inventory.shared.lookup.IBoxTypeLookupService;

public class BoxTypeLookupService extends AbstractSqlLookupService<Long> implements IBoxTypeLookupService {
  @Override
  protected String getConfiguredSqlSelect() {
    return "" +
        "SELECT id, description FROM boxtype " +
        "WHERE 1=1 " +
        "<key> AND id = :key </key>" +
        "<text> AND UPPER(description) LIKE UPPER(:text || '%') </text> " +
        "<all></all>";
  }
}
