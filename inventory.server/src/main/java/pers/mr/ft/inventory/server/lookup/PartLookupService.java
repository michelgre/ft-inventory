package pers.mr.ft.inventory.server.lookup;

import org.eclipse.scout.rt.server.jdbc.lookup.AbstractSqlLookupService;

import pers.mr.ft.inventory.shared.lookup.IPartLookupService;

public class PartLookupService extends AbstractSqlLookupService<Long> implements IPartLookupService {
  @Override
  protected String getConfiguredSqlSelect() {
    return "" +
        "SELECT id, part_label FROM v_parts_page " +
        "WHERE 1=1 " +
        "<key> AND id = :key </key>" +
        "<text> AND UPPER(part_label) LIKE UPPER(:text || '%') </text> " +
        "<all></all>";
  }
}
