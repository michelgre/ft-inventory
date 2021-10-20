package pers.mr.ft.inventory.client.pages;

import java.util.List;
import java.util.Set;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractDecimalColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractIntegerColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.basic.tree.ITreeNode;
import org.eclipse.scout.rt.client.ui.desktop.OpenUriAction;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.ISearchForm;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.client.ui.form.fields.decimalfield.IDecimalField;
import org.eclipse.scout.rt.client.ui.messagebox.IMessageBox;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxes;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.collection.OrderedCollection;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pers.mr.ft.inventory.client.ClientSession;
import pers.mr.ft.inventory.client.Desktop;
import pers.mr.ft.inventory.client.columns.AbstractHeightColumn;
import pers.mr.ft.inventory.client.columns.AbstractIdColumn;
import pers.mr.ft.inventory.client.columns.AbstractLabelColumn;
import pers.mr.ft.inventory.client.columns.AbstractLengthColumn;
import pers.mr.ft.inventory.client.columns.AbstractWidthColumn;
import pers.mr.ft.inventory.client.forms.BoxForm;
import pers.mr.ft.inventory.client.forms.BoxSearchForm;
import pers.mr.ft.inventory.client.pages.BoxTablePage.Table;
import pers.mr.ft.inventory.client.pages.BoxTablePage.Table.IdColumn;
import pers.mr.ft.inventory.shared.codetype.LocationCodeType;
import pers.mr.ft.inventory.shared.codetype.ModelCodeType;
import pers.mr.ft.inventory.shared.forms.BoxSearchFormData;
import pers.mr.ft.inventory.shared.lookup.BoxLookupCall;
import pers.mr.ft.inventory.shared.lookup.BoxTypeLookupCall;
import pers.mr.ft.inventory.shared.pages.BoxTablePageData;
import pers.mr.ft.inventory.shared.pages.IBoxService;
import pers.mr.ft.inventory.shared.pages.ILocationService;
import pers.mr.ft.inventory.shared.pages.IReportModelService;
import pers.mr.ft.inventory.shared.pages.LocationTablePageData;
import pers.mr.ft.inventory.shared.pages.LocationTablePageData.LocationTableRowData;
import pers.mr.ft.inventory.shared.pages.ReportModelTablePageData;
import pers.mr.ft.inventory.shared.pages.ReportModelTablePageData.ReportModelTableRowData;
import pers.mr.ft.inventory.shared.reports.IReportService;

@Data(BoxTablePageData.class)
public class BoxTablePage extends AbstractPageWithTable<Table> {
  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(BoxTablePage.class);
  
  // Pour que toutes les instances aient un user context différent
  // Le user context identifie les informations de configuration de la page, notamment
  // les préférences utilisateur et le contenu du formulaire de recherche.
  
  private Long parentId = null;
  private Boolean lotAchat = false;
  private Boolean given = false;
  private BoxSearchFormData initialSearchData = new BoxSearchFormData();
  private BoxSearchFormData mandatorySearchData = new BoxSearchFormData();

  public BoxTablePage withParentId(Long parentId) {
    this.parentId = parentId;
    return this;
  }
  
  private static String createPageId(String pageName) {    
    // Remarque: on ne peut pas donner à chaque page un n° de séquence car alors on ne peut plus
    // recharger la configuration depuis une session précédente.
    // => L'id de page doit être défini par une valeur fonctionnelle fournie au constructeur
    return pageName;
  }
  
  public BoxTablePage(String pageName) {
    super(createPageId(pageName)); // Pour que toutes les instances aient un user context différent 
  }
  
  @Override
  protected boolean getConfiguredLeaf() {
    return false;
  }
  
  @Override
  protected IPage<?> execCreateChildPage(ITableRow row) {
    IdColumn idColumn = getTable().getIdColumn();
    BoxTablePage page = null;
    
    IBoxService service = BEANS.get(IBoxService.class);
    Long boxId = idColumn.getValue(row);
    if (service.hasBins(boxId)) {
      page = new BoxTablePage("Boite_"+boxId).withParentId(boxId);
    }
    
    return page;
  }

  public boolean isLotAchat() {
    return lotAchat;
  }

  public BoxTablePage withLotAchat(boolean lotAchat) {
    this.lotAchat = lotAchat;
    return this;
  }

  public Boolean isGiven() {
    return given;
  }

  public BoxTablePage withGiven(Boolean given) {
    this.given = given;
    return this;
  }

  public BoxTablePage withMainBox(boolean mainBox, boolean mandatory) {
    (mandatory?mandatorySearchData:initialSearchData).getMainBox().setValue(mainBox);
    return this;
  }

  public BoxTablePage withBoxTypeId(Long boxTypeId, boolean mandatory) {
    (mandatory?mandatorySearchData:initialSearchData).getBoxType().setValue(boxTypeId);
    return this;
  }

  public BoxTablePage withLocation(Long boxLocation, boolean mandatory) {
    (mandatory?mandatorySearchData:initialSearchData).getLocation().setValue(boxLocation);
    return this;
  }

  public BoxTablePage withTitle(String title) {
    getCellForUpdate().setText(title);
    return this;
  }
  
  @Override
  protected String getConfiguredTitle() {
    if (lotAchat) {
      return TEXTS.get("BoughtLots");
    }
    else if (!given){
      return TEXTS.get("Boxes");
    } else {
      return TEXTS.get("GivenBoxes");
    }
  }

  @Override
  protected Class<? extends ISearchForm> getConfiguredSearchForm() {
    return BoxSearchForm.class;
  }
  
  @Override
  protected void execInitSearchForm() {
    super.execInitSearchForm();
    
    BoxSearchForm searchForm = (BoxSearchForm) this.getSearchFormInternal();
    
    searchForm.setInitialData(initialSearchData);
    searchForm.setMandatoryData(mandatorySearchData);
  }
  
  @Override
  protected void execInitTable() {
    super.execInitTable();
    getTable().getDiffPartCountColumn().setAggregationFunction("none");
    getTable().getLengthColumn().setAggregationFunction("none");
    getTable().getWidthColumn().setAggregationFunction("none");
    getTable().getHeightColumn().setAggregationFunction("none");
  }
  

  @Override
  protected void execInitPage() {
    if (lotAchat) {
      setLeaf(true);
    }
    else if (parentId==null) {
      setLeaf(false);
    }
    else {
      IBoxService service = BEANS.get(IBoxService.class);
      if (!service.hasSubBins(parentId)) {
        setLeaf(true);
      }
 
    }
  }
  
  @Override
  protected void execLoadData(SearchFilter filter) {
    importPageData(BEANS.get(IBoxService.class).getBoxTableData(filter, parentId, lotAchat, given));
  }

  @Override
  public List<ITreeNode> getChildNodes() {
    // TODO: Lister les emplacements (hiérarchique ?)
    return super.getChildNodes();
  }
  
  public class Table extends AbstractTable {

    @Override
    protected void execInitTable() {
      // Ne pas afficher les boites données par défaut
      /*
      ColumnUserFilterState state = new ColumnUserFilterState(getGivenColumn());
      Set<Object> values = new HashSet<>();
      values.add(0);
      state.setSelectedValues(values);
      getUserFilterManager().addFilter(state);  
      */    
    }
    
    public ReferentialColumn getReferentialColumn() {
      return getColumnSet().getColumnByClass(ReferentialColumn.class);
    }

    public LocationColumn getLocationColumn() {
      return getColumnSet().getColumnByClass(LocationColumn.class);
    }

    public ValueColumn getValueColumn() {
      return getColumnSet().getColumnByClass(ValueColumn.class);
    }

    public DiffPartCountColumn getDiffPartCountColumn() {
      return getColumnSet().getColumnByClass(DiffPartCountColumn.class);
    }

    public ModelColumn getModelColumn() {
      return getColumnSet().getColumnByClass(ModelColumn.class);
    }

    public IncludedInColumn getIncludedInColumn() {
      return getColumnSet().getColumnByClass(IncludedInColumn.class);
    }

    public LengthColumn getLengthColumn() {
      return getColumnSet().getColumnByClass(LengthColumn.class);
    }

    public WidthColumn getWidthColumn() {
      return getColumnSet().getColumnByClass(WidthColumn.class);
    }

    public HeightColumn getHeightColumn() {
      return getColumnSet().getColumnByClass(HeightColumn.class);
    }

    public DescriptionColumn getDescriptionColumn() {
      return getColumnSet().getColumnByClass(DescriptionColumn.class);
    }

    public GivenColumn getGivenColumn() {
      return getColumnSet().getColumnByClass(GivenColumn.class);
    }

    public BoxTypeColumn getBoxTypeColumn() {
      return getColumnSet().getColumnByClass(BoxTypeColumn.class);
    }

    public PartCountColumn getPartCountColumn() {
      return getColumnSet().getColumnByClass(PartCountColumn.class);
    }

    public LabelColumn getLabelColumn() {
      return getColumnSet().getColumnByClass(LabelColumn.class);
    }

    public IdColumn getIdColumn() {
      return getColumnSet().getColumnByClass(IdColumn.class);
    }

    @Override
    protected Class<? extends IMenu> getConfiguredDefaultMenu() {
      return EditMenu.class;
    }
    
    @Order(1000)
    public class EditMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Editer");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        Long boxId = getIdColumn().getSelectedValue();
        @SuppressWarnings("unused")
        BoxForm form = ((Desktop) ClientSession.get().getDesktop()).findBoxForm(boxId, new BoxFormListener());
      }
    }



    @Order(1500)
    public class CreateMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Create");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.EmptySpace, TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        BoxForm form = new BoxForm();
        form.setBoxId(0L);
        form.setLotAchat(lotAchat);
        form.addFormListener(new BoxFormListener());
        form.startNew();
      }
    }



    @Order(1750)
    public class CopyMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("CopyBox");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        BoxForm form = new BoxForm();
        form.setBoxId(getIdColumn().getSelectedValue());
        form.setLotAchat(lotAchat);
        form.addFormListener(new BoxFormListener());
        form.startNew();
      }
    }

    

    @Order(2000)
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
                          .withBody(TEXTS.get("ConfirmDeleteBoxes", String.valueOf(rows.size())))
                          .withYesButtonText(TEXTS.get("Delete"))
                          .show(IMessageBox.NO_OPTION);
        if (res==IMessageBox.YES_OPTION) {
          IBoxService service = BEANS.get(IBoxService.class);
          for (ITableRow row: rows) {
            Long boxId = getIdColumn().getValue(row);
            service.delete(boxId);
          }
          reloadPage();
        }
      }
    }

    abstract public class AbstractReportMenu extends AbstractMenu {

      abstract String getConfiguredType();
      
      @Override
      protected void injectActionNodesInternal(OrderedCollection<IMenu> actionNodes) {
        super.injectActionNodesInternal(actionNodes);
        
        IReportModelService service = BEANS.get(IReportModelService.class);
        ReportModelTablePageData pageData = service.getReportTableData(getConfiguredType(), null);
        for (ReportModelTableRowData rowData :pageData.getRows()) {
          String reportName = rowData.getName();
          Long reportId = rowData.getId();
          
          IMenu reportMenu = new AbstractMenu() {
            @Override
            public String getText() {
              return reportName;
            }
            
            @Override
            protected Set<? extends IMenuType> getConfiguredMenuTypes() {
              return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection, TableMenuType.EmptySpace);
            }

            @Override
            protected void execAction() {
              List<Long> selectedIds = getTable().getIdColumn().getSelectedValues();
              IReportService service = BEANS.get(IReportService.class);
              BinaryResource br = service.buildReport(reportId, selectedIds);
              ClientSessionProvider.currentSession().getDesktop().openUri(br, OpenUriAction.DOWNLOAD);
            }
          };
          actionNodes.addLast(reportMenu);
        }
      }
      
    }

    @Order(3000)
    public class ReportMenu extends AbstractReportMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Report");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection, TableMenuType.EmptySpace);
      }
/*
      @Override
      protected void execAction() {
        IReportService service = BEANS.get(IReportService.class);        
        BinaryResource br = service.buildReport(getIdColumn().getSelectedValues());
        ClientSessionProvider.currentSession().getDesktop().openUri(br, OpenUriAction.DOWNLOAD);
      }
      */

      @Override
      String getConfiguredType() {
        return "R";
      }
    }


    @Order(4000)
    public class LabelsMenu extends AbstractReportMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Labels");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection, TableMenuType.EmptySpace);
      }
      @Override
      String getConfiguredType() {
        return "L";
      }
      
      
/*
      @Order(4000)
      public class BoxLabelsMenu extends AbstractMenu {
        @Override
        protected String getConfiguredText() {
          return TEXTS.get("Labels");
        }

        @Override
        protected Set<? extends IMenuType> getConfiguredMenuTypes() {
          return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection, TableMenuType.EmptySpace);
        }

        @Override
        protected void execAction() {
          List<Long> selectedIds = getTable().getIdColumn().getSelectedValues();
          IReportService service = BEANS.get(IReportService.class);
          BinaryResource br = service.buildLabelSheets(selectedIds);
          ClientSessionProvider.currentSession().getDesktop().openUri(br, OpenUriAction.DOWNLOAD);
          
        }
      }

      @Order(4500)
      public class SmallLabelsMenu extends AbstractMenu {
        @Override
        protected String getConfiguredText() {
          return TEXTS.get("SmallLabels");
        }

        @Override
        protected Set<? extends IMenuType> getConfiguredMenuTypes() {
          return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection, TableMenuType.EmptySpace);
        }

        @Override
        protected void execAction() {
          List<Long> selectedIds = getTable().getIdColumn().getSelectedValues();
          IReportService service = BEANS.get(IReportService.class);
          BinaryResource br = service.buildSmallLabelSheets(selectedIds);
          ClientSessionProvider.currentSession().getDesktop().openUri(br, OpenUriAction.DOWNLOAD);
          
        }
      }
*/
    }


    @Order(5000)
    public class MoveMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("MoveLocation");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
      }

      @Override
      protected void injectActionNodesInternal(OrderedCollection<IMenu> actionNodes) {
        super.injectActionNodesInternal(actionNodes);
        
        ILocationService service = BEANS.get(ILocationService.class);
        LocationTablePageData locationPageData = service.getLocationTableData(null);
        for (LocationTableRowData locationData :locationPageData.getRows()) {
          Long locationId = locationData.getId();
          String locationName = locationData.getLocation();
          
          IMenu locationMenu = new AbstractMenu() {
            @Override
            public String getText() {
              return locationName;
            }
            
            @Override
            protected Set<? extends IMenuType> getConfiguredMenuTypes() {
              return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
            }

            @Override
            protected void execAction() {
              // Demande confirmation puis déplacer toutes les boites à cet emplacement
              List<ITableRow> rows = getSelectedRows();
              int res = MessageBoxes.createYesNo()
                                .withHeader(TEXTS.get("TitleConfirmMove"))
                                .withBody(TEXTS.get("ConfirmMoveBoxes", String.valueOf(rows.size()), locationName))
                                .withYesButtonText(TEXTS.get("MoveBox"))
                                .show(IMessageBox.NO_OPTION);
              if (res==IMessageBox.YES_OPTION) {
                IBoxService boxService = BEANS.get(IBoxService.class);
                for (ITableRow row: rows) {
                  Long boxId = getIdColumn().getValue(row);
                  boxService.moveToLocation(locationId, boxId);
                }
                reloadPage();
              }
            }
          };
          actionNodes.addLast(locationMenu);
        }
      }
      
      @Override
      protected void execAction() {
        
      }
    }



    
    @Order(1000)
    public class IdColumn extends AbstractIdColumn {
    }

    @Order(1500)
    public class ReferentialColumn extends AbstractBooleanColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("ReferentialShort");
      }

      @Override
      protected int getConfiguredWidth() {
        return 60;
      }
      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
    }

    @Order(2000)
    public class LabelColumn extends AbstractLabelColumn {
    }

    @Order(3000)
    public class PartCountColumn extends AbstractIntegerColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("PartCount");
      }

      @Override
      protected int getConfiguredWidth() {
        return 75;
      }
      @Override
      protected boolean getConfiguredGroupingUsed() {
        return false;
      }
    }



    @Order(3500)
    public class DiffPartCountColumn extends AbstractIntegerColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("DiffPartCount");
      }

      @Override
      protected int getConfiguredWidth() {
        return 75;
      }
      @Override
      protected boolean getConfiguredGroupingUsed() {
        return false;
      }
    }
    
    

    @Order(4000)
    public class LocationColumn extends AbstractSmartColumn<Long> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Location");
      }

      @Override
      protected int getConfiguredWidth() {
        return 120;
      }
      
      @Override
      protected Class<? extends ICodeType<?, Long>> getConfiguredCodeType() {
        return LocationCodeType.class;
      }
    }

    @Order(5000)
    public class ValueColumn extends AbstractDecimalColumn<Double> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Value");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }

      @Override
      protected Double getConfiguredMinValue() {
        return 0.0;
      }

      @Override
      protected Double getConfiguredMaxValue() {
        return 99999999.99;
      }

      @Override
      protected IDecimalField<Double> createDefaultEditor() {
        return null;
      }
    }

    
    
    @Order(6000)
    public class ModelColumn extends AbstractSmartColumn<Long> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Model");
      }

      @Override
      protected int getConfiguredWidth() {
        return 210;
      }
      @Override
      protected Class<? extends ICodeType<?, Long>> getConfiguredCodeType() {
        return ModelCodeType.class;
      }
    }



    @Order(7000)
    public class IncludedInColumn extends AbstractSmartColumn<Long> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("IncludedIn");
      }

      @Override
      protected int getConfiguredWidth() {
        return 135;
      }
      @Override
      protected Class<? extends ILookupCall<Long>> getConfiguredLookupCall() {
        return BoxLookupCall.class;
      }
    }
    
    @Order(8000)
    public class LengthColumn extends AbstractLengthColumn {
      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
    }



    @Order(9000)
    public class WidthColumn extends AbstractWidthColumn {
      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
    }



    @Order(10000)
    public class HeightColumn extends AbstractHeightColumn {
      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
    }



    @Order(10500)
    public class BoxTypeColumn extends AbstractSmartColumn<Long> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Type");
      }

      @Override
      protected int getConfiguredWidth() {
        return 235;
      }
      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
      @Override
      protected Class<? extends ILookupCall<Long>> getConfiguredLookupCall() {
        return BoxTypeLookupCall.class;
      }
    }

    
    @Order(11000)
    public class DescriptionColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Description");
      }

      @Override
      protected int getConfiguredWidth() {
        return 220;
      }
      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
    }

    @Order(10000)
    public class GivenColumn extends AbstractBooleanColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Given");
      }

      @Override
      protected int getConfiguredWidth() {
        return 72;
      }
      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
    }

    
    
    
  }
  
  private class BoxFormListener implements FormListener {
    @Override
    public void formChanged(FormEvent e) {
      // reload page to reflect new/changed data after saving any changes
      if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
        reloadPage();
      }
    }
  }
}
