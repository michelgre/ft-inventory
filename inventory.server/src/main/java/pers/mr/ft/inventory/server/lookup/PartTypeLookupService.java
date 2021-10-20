package pers.mr.ft.inventory.server.lookup;

import org.eclipse.scout.rt.server.jdbc.lookup.AbstractSqlLookupService;

import pers.mr.ft.inventory.shared.lookup.IPartTypeLookupService;

public class PartTypeLookupService extends AbstractSqlLookupService<Long> implements IPartTypeLookupService {
  @Override
  protected String getConfiguredSqlSelect() {
    return "" +
        "SELECT id, name FROM part_type " +
        "WHERE 1=1 " +
        "<key> AND id = :key </key>" +
        "<text> AND UPPER(name) LIKE UPPER(:text || '%') </text> " +
        "<all></all>";
  }

}
