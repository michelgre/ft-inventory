package pers.mr.ft.inventory.server.pages;

import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.shared.pages.DocumentsTablePageData;
import pers.mr.ft.inventory.shared.pages.IDocumentsService;

public class DocumentsService implements IDocumentsService {
  @Override
  public DocumentsTablePageData getDocumentsTableData(SearchFilter filter) {
    DocumentsTablePageData pageData = new DocumentsTablePageData();
    
    String query = "SELECT id, datenbank_id, part_number, year, title, ft_title, language, (SELECT COUNT(*) FROM doc_part WHERE doc_id = id), LENGTH(content) " +
        " FROM document " +
        " WHERE 1 = 1" +
        " INTO :{page.id}, :{page.dbId}, :{page.part}, :{page.year}, :{page.name}, :{page.fTDBName}, :{page.lang}, :{page.usedInCount}, :{page.size}"
        ;
    SQL.selectInto(query, new NVPair("page", pageData), new NVPair("filter", filter.getFormData()));
    return pageData;
  }

  @Override
  public void delete(Long documentId) {
    SQL.delete("DELETE from document WHERE id = :documentId", new NVPair("documentId", documentId));    
  }
}
