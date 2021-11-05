package pers.mr.ft.inventory.server.pages;

import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.server.ServerSession;
import pers.mr.ft.inventory.shared.pages.IMissingPartsService;
import pers.mr.ft.inventory.shared.pages.MissingPartsTablePageData;

public class MissingPartsService implements IMissingPartsService {
  @Override
  public MissingPartsTablePageData getMissingPartsTableData(SearchFilter filter) {
    MissingPartsTablePageData pageData = new MissingPartsTablePageData();
    
    // TODO: Choix de langue dans la session
    String userLanguage = ServerSession.get().getSessionLanguage();
    String defaultLanguage =  ServerSession.get().getDefaultLanguage();

    SQL.selectInto(
        "SELECT box_id, box_label, bin_id, model_id, " + 
        " part_id, year_number, COALESCE(lt.label, part.label), " +
        " 'icons/?image=' || p.ft_icon, " +
        " count, kit_count, count - kit_count, part.color_id, " +
        " part.cost, part.cost * (kit_count - count) "+
        "FROM v_parts_by_box part " +
        " LEFT JOIN multilingual_label lt ON lt.id = part.title_id AND lt.langcode = :userLanguage " +
        " JOIN part p ON p.id = part.part_id " +
        "WHERE count < kit_count "+
        "ORDER BY box_id, bin_id, part_id " +
        "INTO :boxId, :boxLabel, :bin, :model, :partId, :partNumber, :part, :icon, :boxCount, :kitCount, :deltaCount, :color, :partValue, :value ",
        pageData, 
        new NVPair("userLanguage", userLanguage));
    return pageData;
  }
}
