package pers.mr.ft.inventory.client.pages;

import java.util.List;
import java.util.Set;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.OpenUriAction;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.dnd.IDNDSupport;
import org.eclipse.scout.rt.client.ui.dnd.ResourceListTransferObject;
import org.eclipse.scout.rt.client.ui.dnd.TransferObject;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.client.ui.messagebox.IMessageBox;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxes;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.client.ClientSession;
import pers.mr.ft.inventory.client.Desktop;
import pers.mr.ft.inventory.client.forms.BoxForm;
import pers.mr.ft.inventory.client.forms.DocumentForm;
import pers.mr.ft.inventory.client.pages.DocumentsTablePage.Table;
import pers.mr.ft.inventory.shared.forms.IDocumentService;
import pers.mr.ft.inventory.shared.pages.DocumentsTablePageData;
import pers.mr.ft.inventory.shared.pages.IBoxService;
import pers.mr.ft.inventory.shared.pages.IDocumentsService;

@Data(DocumentsTablePageData.class)
public class DocumentsTablePage extends AbstractPageWithTable<Table> {
  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Documents");
  }

  @Override
  protected void execLoadData(SearchFilter filter) {
    DocumentsTablePageData pageData = BEANS.get(IDocumentsService.class).getDocumentsTableData(filter);
    importPageData(pageData);
  }

  public class Table extends AbstractTable {

    public FTDBNameColumn getFTDBNameColumn() {
      return getColumnSet().getColumnByClass(FTDBNameColumn.class);
    }

    public DbIdColumn getDbIdColumn() {
      return getColumnSet().getColumnByClass(DbIdColumn.class);
    }

    public PartColumn getPartColumn() {
      return getColumnSet().getColumnByClass(PartColumn.class);
    }

    public YearColumn getYearColumn() {
      return getColumnSet().getColumnByClass(YearColumn.class);
    }

    public UsedInCountColumn getUsedInCountColumn() {
      return getColumnSet().getColumnByClass(UsedInCountColumn.class);
    }

    public LangColumn getLangColumn() {
      return getColumnSet().getColumnByClass(LangColumn.class);
    }

    public NameColumn getNameColumn() {
      return getColumnSet().getColumnByClass(NameColumn.class);
    }

    public IdColumn getIdColumn() {
      return getColumnSet().getColumnByClass(IdColumn.class);
    }

    @Order(1000)
    public class IdColumn extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Ident");
      }

      @Override
      protected int getConfiguredWidth() {
        return 60;
      }
    }

    @Order(2000)
    public class NameColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Name");
      }

      @Override
      protected int getConfiguredWidth() {
        return 320;
      }
    }

    @Order(3000)
    public class FTDBNameColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("FTDBName");
      }

      @Override
      protected int getConfiguredWidth() {
        return 320;
      }
      
      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
    }

    @Order(4000)
    public class DbIdColumn extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("FTDBId");
      }

      @Override
      protected int getConfiguredWidth() {
        return 60;
      }
    }

    @Order(5000)
    public class PartColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("PartNumber");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(5500)
    public class LangColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Lang");
      }

      @Override
      protected int getConfiguredWidth() {
        return 60;
      }
    }

    
    @Order(6000)
    public class YearColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Year");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(7000)
    public class UsedInCountColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("UsedInCount");
      }

      @Override
      protected int getConfiguredWidth() {
        return 70;
      }
    }
    
    
    
    @Order(1000)
    public class OpenMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Open");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
      }

      @Override
      protected void execAction() {
        IDocumentService service = BEANS.get(IDocumentService.class);
        IdColumn col = getIdColumn();
        for (Long documentId: col.getSelectedValues()) {
          BinaryResource doc = service.loadContent(documentId);
          
          // TODO: Cas où pas de contenu à voir
          ClientSession.get().getDesktop().openUri(doc, OpenUriAction.DOWNLOAD);
        }
      }
    }

    @Order(3000)
    public class EditMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Editer");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
      }

      @Override
      protected void execAction() {
        Long docId = getIdColumn().getSelectedValue();
        DocumentForm form = ((Desktop) ClientSession.get().getDesktop()).findDocumentForm(docId, new DocumentFormListener());
      }
    }

    @Order(5000)
    public class CreateMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Create");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
      }

      @Override
      protected void execAction() {
      }
    }

    @Order(6000)
    public class DeleteMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Delete");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
      }

      @Override
      protected void execAction() {
        List<ITableRow> rows = getSelectedRows();
        int res = MessageBoxes.createYesNo()
                          .withHeader(TEXTS.get("TitleConfirmDelete"))
                          .withBody(TEXTS.get("ConfirmDeleteDocuments", String.valueOf(rows.size())))
                          .withYesButtonText(TEXTS.get("Delete"))
                          .show(IMessageBox.NO_OPTION);
        if (res==IMessageBox.YES_OPTION) {
          IDocumentsService service = BEANS.get(IDocumentsService.class);
          for (ITableRow row: rows) {
            Long documentId = getIdColumn().getValue(row);
            service.delete(documentId);
          }
          reloadPage();
        }
      }
    }

    
    @Override
    protected Class<? extends IMenu> getConfiguredDefaultMenu() {
      return EditMenu.class;
    }
    @Override
    protected int getConfiguredDropType() {
      return IDNDSupport.TYPE_FILE_TRANSFER;
    }
    @Override
    protected void execDrop(ITableRow row, TransferObject t) {
      if (row==null) {
        // Création de nouveau(x) document(s) ?
        if (t instanceof ResourceListTransferObject) {
          ResourceListTransferObject rlto = (ResourceListTransferObject) t;
          for (BinaryResource document : rlto.getResources()) {
            DocumentForm form = new DocumentForm();
            form.addFormListener(new DocumentFormListener());
            form.startNew(document);
          }
        }
      }
      super.execDrop(row, t);
    }
  }
  
  private class DocumentFormListener implements FormListener {
    @Override
    public void formChanged(FormEvent e) {
      // reload page to reflect new/changed data after saving any changes
      if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
        reloadPage();
      }
    }
  }
}
