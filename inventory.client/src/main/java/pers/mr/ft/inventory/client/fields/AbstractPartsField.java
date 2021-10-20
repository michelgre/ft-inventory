package pers.mr.ft.inventory.client.fields;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.action.keystroke.AbstractKeyStroke;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.action.keystroke.KeyStroke;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.cell.Cell;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractDecimalColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractIconColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractIntegerColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.basic.table.controls.ITableControl;
import org.eclipse.scout.rt.client.ui.desktop.OpenUriAction;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.form.fields.IValidateContentDescriptor;
import org.eclipse.scout.rt.client.ui.form.fields.decimalfield.IDecimalField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.IStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.client.ui.messagebox.IMessageBox;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxes;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractFormFieldData;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import pers.mr.ft.inventory.client.ClientSession;
import pers.mr.ft.inventory.client.Desktop;
import pers.mr.ft.inventory.client.PartNumberComparator;
import pers.mr.ft.inventory.client.fields.AbstractPartsField.Table.BinColumn;
import pers.mr.ft.inventory.client.fields.AbstractPartsField.Table.CountColumn;
import pers.mr.ft.inventory.client.fields.AbstractPartsField.Table.IdColumn;
import pers.mr.ft.inventory.client.fields.AbstractPartsField.Table.KitCountColumn;
import pers.mr.ft.inventory.client.forms.BoxForm;
import pers.mr.ft.inventory.client.forms.PartForm;
import pers.mr.ft.inventory.shared.codetype.ColorCodeType;
import pers.mr.ft.inventory.shared.forms.IPartService;
import pers.mr.ft.inventory.shared.forms.PartFormData;
import pers.mr.ft.inventory.shared.lookup.BoxLookupCall;

public abstract class AbstractPartsField extends AbstractTableField<AbstractPartsField.Table> {
  abstract protected void setTotalValue(Double value);
  abstract protected void setPartsCount(Integer partsCount);
  
  private boolean onParHidden = false;
  
  protected boolean hasKit() {
    return false;
  }
  
  protected boolean getConfiguredEditable() {
    return false;
  }
  
  protected ITableControl createAggregateTableControl() {
    return null;
  }
  
  private boolean getInternalConfiguredEditable() {
    return getConfiguredEditable();
  }
  
  @Override
  protected byte getConfiguredLabelPosition() {
    return LABEL_POSITION_TOP;
  }
  @Override
  protected int getConfiguredGridW() {
    return 2;
  }
  
  @Override
  protected void execInitField() {
    super.execInitField();
    ITableControl control = createAggregateTableControl();
    if (control != null) {
      getTable().addTableControl(control);
    }
  }
  
  protected void execMoveParts() {
    
  }

  public class Table extends AbstractTable {
    @Override
    protected String getConfiguredCssClass() {
      return "parts-table";
    }
    @Override
    protected Class<? extends IMenu> getConfiguredDefaultMenu() {
      return EditPartMenu.class;
    }
    
    @Order(1000)
    public class AddRowMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("AddRow");
      }

      @Override
      protected boolean getConfiguredEnabled() {
        return getInternalConfiguredEditable();
      }
      @Override
      protected boolean getConfiguredVisible() {
        return getInternalConfiguredEditable();
      }
      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.EmptySpace);
      }

      @Override
      protected void execAction() {
        appendRowAndEdit();
      }
    }


    @Order(2000)
    public class EditPartMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("EditPart");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        Long partId = getIdColumn().getSelectedValue();
        if (partId!=null && partId>0) {
          @SuppressWarnings("unused")
          PartForm form = ((Desktop) ClientSession.get().getDesktop()).findPartForm(partId, new RowChangedListener());
        }
      }
    }


    @Order(3000)
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
        getTable().deleteRows(getTable().getSelectedRows());
      }
      @Override
      protected boolean getConfiguredEnabled() {
        return getInternalConfiguredEditable();
      }
      @Override
      protected boolean getConfiguredVisible() {
        return getInternalConfiguredEditable();
      }
    }


    @Order(3500)
    public class OpenBinMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("OpenBin");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        Long binId = getBinColumn().getSelectedValue();
        if (binId != null && binId > 0) {
          @SuppressWarnings("unused")
          BoxForm binForm = ((Desktop) ClientSession.get().getDesktop()).findBoxForm(binId, new RowChangedListener());
        }
      }
    }


    @Order(3750)
    public class MovePartsMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("MoveParts");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
      }

      @Override
      protected void execAction() {
        execMoveParts();
      }
    }

    
    @Order(4000)
    public class DatenbankMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("GotoDatenbank");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        ClientSessionProvider.currentSession().getDesktop().openUri("https://ft-datenbank.de/ft-article/"+getIdColumn().getSelectedValue(), OpenUriAction.NEW_WINDOW);
      }
    }


    @Order(5000)
    public class ClearCountsMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("ClearCounts");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
      }

      @Override
      protected void execAction() {
        List<ITableRow> rows = getSelectedRows();
        int res = MessageBoxes.createYesNo()
                          .withHeader(TEXTS.get("TitleConfirmClearCounts"))
                          .withBody(TEXTS.get("ConfirmClearCounts", String.valueOf(rows.size())))
                          .withYesButtonText(TEXTS.get("Clear"))
                          .show(IMessageBox.NO_OPTION);
        if (res==IMessageBox.YES_OPTION) {
          for (ITableRow row: rows) {
            getCountColumn().setValue(row, null);
          }
        }
      }
    }
    
    
    @Order(1000)
    public class CopyKeyStroke extends AbstractKeyStroke {
      @Override
      protected String getConfiguredKeyStroke() {
        return KeyStroke.combineKeyStrokes(IKeyStroke.CONTROL, "C");
      }

      @Override
      protected void execAction() {
        List<Long> parts = getIdColumn().getSelectedValues();
        ((Desktop) ClientSession.get().getDesktop()).copyPartsClipboard(parts, true);
      }
    }
 
    


    @Order(2000)
    public class PasteKeyStroke extends AbstractKeyStroke {
      @Override
      protected String getConfiguredKeyStroke() {
        return KeyStroke.combineKeyStrokes(IKeyStroke.CONTROL, "V");
      }

      @Override
      protected void execAction() {
        addParts(((Desktop) ClientSession.get().getDesktop()).getPartsClipboard());
      }
    }


    @Order(3000)
    public class PlusKeyStroke extends AbstractKeyStroke {
      @Override
      protected String getConfiguredKeyStroke() {
        return "ADD";
      }

      @Override
      protected void execAction() {
        if (getTable().getSelectedRowCount()==1) {
          CountColumn col = getCountColumn();
          ITableRow row = getSelectedRow();
          Integer value = col.getValue(row);
          if (value==null) {
            value = 0;
          }
          col.setValue(row, value+1);
          updateCalculatedValues(row);
          showKitStatus(row);
        }
      }
    }


    @Order(4000)
    public class MinusKeyStroke extends AbstractKeyStroke {
      @Override
      protected String getConfiguredKeyStroke() {
        return "SUBTRACT";
      }

      @Override
      protected void execAction() {
        if (getTable().getSelectedRowCount()==1) {
          CountColumn col = getCountColumn();
          ITableRow row = getSelectedRow();
          Integer value = col.getValue(row);
          if (value!=null && value > 0) {
            col.setValue(row, value-1);
            updateCalculatedValues(row);
            showKitStatus(row);
          }
        }
      }
    }


    @Order(5000)
    public class ShowHideOnParKeyStroke extends AbstractKeyStroke {
      @Override
      protected String getConfiguredKeyStroke() {
        return "P";
      }

      @Override
      protected void execAction() {
        CountColumn countColumn = getTable().getCountColumn();
        KitCountColumn kitCountColumn = getTable().getKitCountColumn();
        
        onParHidden = !onParHidden;
        for (ITableRow row: getTable().getRows()) {
          Integer count = countColumn.getValue(row);
          Integer kitCount = kitCountColumn.getValue(row);
          // ???
        }
      }
    }
    
    public CountColumn getCountColumn() {
      return getColumnSet().getColumnByClass(CountColumn.class);
    }

    public MaxCountColumn getMaxCountColumn() {
      return getColumnSet().getColumnByClass(MaxCountColumn.class);
    }

    public IconColumn getIconColumn() {
      return getColumnSet().getColumnByClass(IconColumn.class);
    }

    public ValueColumn getValueColumn() {
      return getColumnSet().getColumnByClass(ValueColumn.class);
    }

    public PartNumberColumn getPartNumberColumn() {
      return getColumnSet().getColumnByClass(PartNumberColumn.class);
    }

    public PartValueColumn getPartValueColumn() {
      return getColumnSet().getColumnByClass(PartValueColumn.class);
    }

    public ColorColumn getColorColumn() {
      return getColumnSet().getColumnByClass(ColorColumn.class);
    }

    public KitCountColumn getKitCountColumn() {
      return getColumnSet().getColumnByClass(KitCountColumn.class);
    }

    public BinColumn getBinColumn() {
      return getColumnSet().getColumnByClass(BinColumn.class);
    }
    public DeltaCountColumn getDeltaCountColumn() {
      return getColumnSet().getColumnByClass(DeltaCountColumn.class);
    }
    public FTDBCountColumn getFTDBCountColumn() {
      return getColumnSet().getColumnByClass(FTDBCountColumn.class);
    }
    public OldIdColumn getOldIdColumn() {
      return getColumnSet().getColumnByClass(OldIdColumn.class);
    }

    public PartLabelColumn getPartLabelColumn() {
      return getColumnSet().getColumnByClass(PartLabelColumn.class);
    }

    public IdColumn getIdColumn() {
      return getColumnSet().getColumnByClass(IdColumn.class);
    }


    
    @Order(1000)
    public class IdColumn extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Identifier");
      }

      @Override
      protected int getConfiguredWidth() {
        return 80;
      }
      
      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
    }


    @Order(2000)
    public class PartNumberColumn extends AbstractStringColumn {
      protected String getConfiguredHeaderText() {
        return TEXTS.get("PartNumber");
      }
      
      protected int getConfiguredWidth() {
        return 100;
      }
      
      protected boolean getConfiguredEditable() {
        return getInternalConfiguredEditable();
      }
      
      protected void execCompleteEdit(ITableRow row, IFormField editingField) {
        super.execCompleteEdit(row, editingField);
        String partNumber = ((IStringField)editingField).getValue();
        if (partNumber != null) {
          partNumber = partNumber.trim();
        } else {
          partNumber = "";
        } 
        if (partNumber.length() > 0) {
          IPartService partService = BEANS.get(IPartService.class);
          PartFormData partData = partService.loadByNumber(partNumber);
          String partLabel = partData.getTitle().getValue();
          Double partValue = partData.getValue().getValue();
          getIdColumn().setValue(row, partData.getId().getValue());
          getPartLabelColumn().setValue(row, partLabel);
          getPartValueColumn().setValue(row, partValue);
          getColorColumn().setValue(row, partData.getColor().getValue());
          Long iconId = partData.getImageId();
          if (iconId != null && iconId.longValue() > 0L)
            getIconColumn().setValue(row, "icons/?image=" + iconId); 
        } else {
          getIdColumn().setValue(row, null);
          getPartLabelColumn().setValue(row, "");
          getIconColumn().setValue(row, "");
          getPartValueColumn().setValue(row, null);
          getValueColumn().setValue(row, Double.valueOf(0));
          getColorColumn().setValue(row, null);
        } 
        updateCalculatedValues(row);
      }
      
      protected String execValidateValue(ITableRow row, String rawValue) {
        String newPartNumber = super.execValidateValue(row, rawValue);
        if (newPartNumber != null)
          newPartNumber = newPartNumber.trim(); 
        return newPartNumber;
      }
      
      protected IFormField execPrepareEdit(ITableRow row) {
        return super.execPrepareEdit(row);
      }
      
      public int compareTableRows(ITableRow r1, ITableRow r2) {
        String s1 = getValue(r1);
        String s2 = getValue(r2);
        return (new PartNumberComparator()).compare(s1, s2);
      }
    }
    
    
    @Order(2500)
    public class CountColumn extends AbstractIntegerColumn {
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Quantity");
      }
      
      protected int getConfiguredWidth() {
        return 75;
      }
      
      protected String getConfiguredCssClass() {
        return "count-col";
      }
      
      protected boolean getConfiguredEditable() {
        return getInternalConfiguredEditable();
      }
      
      protected void execCompleteEdit(ITableRow row, IFormField editingField) {
        super.execCompleteEdit(row, editingField);
        updateCalculatedValues(row);
        showKitStatus(row);
        if (((AbstractPartsField.Table.AddRowMenu)getMenuByClass(AbstractPartsField.Table.AddRowMenu.class)).isEnabled()) {
          int nbRows = getTable().getRowCount();
          if (row.getRowIndex() == nbRows - 1)
            appendRowAndEdit(); 
        } 
      }
      
      protected Integer execValidateValue(ITableRow row, Integer rawValue) {
        Integer value = super.execValidateValue(row, rawValue);
        Integer maxValue = getMaxCountColumn().getValue(row);
        if (maxValue != null && 
          value.intValue() > maxValue.intValue())
          value = maxValue; 
        return value;
      }
    }


    @Order(2550)
    public class FTDBCountColumn extends AbstractIntegerColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("FTDBCount");
      }

      @Override
      protected int getConfiguredWidth() {
        return 75;
      }
      
      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
      
      @Override
      protected boolean getConfiguredEditable() {
        return false;
      }
    }
    
    
    @Order(2600)
    public class MaxCountColumn extends AbstractIntegerColumn {
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Available");
      }
      
      protected int getConfiguredWidth() {
        return 75;
      }
      
      protected boolean getConfiguredEditable() {
        return false;
      }
      
      protected boolean getConfiguredDisplayable() {
        return false;
      }
    }
    
    @Order(2750)
    public class KitCountColumn extends AbstractIntegerColumn {
      protected String getConfiguredHeaderText() {
        return TEXTS.get("KitCount");
      }
      
      protected int getConfiguredWidth() {
        return 60;
      }
      
      protected boolean getConfiguredVisible() {
        return false;
      }
    }


    @Order(2875)
    public class DeltaCountColumn extends AbstractIntegerColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("DeltaCount");
      }

      @Override
      protected int getConfiguredWidth() {
        return 60;
      }
      
      protected boolean getConfiguredVisible() {
        return false;
      }
    }
    
    
    @Order(3000)
    public class PartLabelColumn extends AbstractStringColumn {
      protected String getConfiguredHeaderText() {
        return TEXTS.get("PartName");
      }
      
      protected int getConfiguredWidth() {
        return 300;
      }
      
      protected boolean getConfiguredEditable() {
        return false;
      }
      
      protected String execValidateValue(ITableRow row, String rawValue) {
        return super.execValidateValue(row, rawValue);
      }
    }
    
    @Order(3500)
    public class ColorColumn extends AbstractSmartColumn<Long> {
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Color");
      }
      
      protected int getConfiguredWidth() {
        return 80;
      }
      
      protected Class<? extends ICodeType<?, Long>> getConfiguredCodeType() {
        return ColorCodeType.class;
      }
    }
    
    @Order(4000)
    public class IconColumn extends AbstractIconColumn {
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Icon");
      }
      
      protected int getConfiguredWidth() {
        return 109;
      }
      
      protected String getConfiguredCssClass() {
        return "part-icon";
      }
    }
    
    @Order(5000)
    public class PartValueColumn extends AbstractDecimalColumn<Double> {
      protected String getConfiguredHeaderText() {
        return TEXTS.get("PartValue");
      }
      
      protected int getConfiguredWidth() {
        return 60;
      }
      
      protected IDecimalField<Double> createDefaultEditor() {
        return null;
      }
      
      protected Double getConfiguredMinValue() {
        return Double.valueOf(0);
      }
      
      protected Double getConfiguredMaxValue() {
        return Double.valueOf(9999999.99D);
      }
      
      protected String getConfiguredAggregationFunction() {
        return "none";
      }
    }
    
    @Order(5500)
    public class BinColumn extends AbstractSmartColumn<Long> {
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Bin");
      }
      
      protected int getConfiguredWidth() {
        return 160;
      }
      
      protected Class<? extends ILookupCall<Long>> getConfiguredLookupCall() {
        return BoxLookupCall.class;
      }
    }
    
    @Order(6000)
    public class ValueColumn extends AbstractDecimalColumn<Double> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("CurrencySign");
      }

      @Override
      protected int getConfiguredWidth() {
        return 85;
      }

      @Override
      protected IDecimalField<Double> createDefaultEditor() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      protected Double getConfiguredMinValue() {
        return 0.0;
      }

      @Override
      protected Double getConfiguredMaxValue() {
        return 9999999.99;
      }
    }

    @Order(7000)
    public class OldIdColumn extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("OldIdentifier");
      }

      @Override
      protected int getConfiguredWidth() {
        return 80;
      }
      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
    }


    @Override
    protected void execRowsSelected(List<? extends ITableRow> rows) {
      super.execRowsSelected(rows);
      
      IMenu binMenu = getMenuByClass(OpenBinMenu.class);
      if (rows.size()==1) {
        Long selBin = getBinColumn().getValue(rows.get(0));
        if (selBin!=null && selBin > 0) {
          binMenu.setVisible(true);
        }
        else {
          binMenu.setVisible(false);
        }
      }
      else {
        binMenu.setVisible(false);
      }
    }
    
    private void appendRowAndEdit() {
      ITableRow newRow = getTable().addRow(true);
      getTable().selectRow(newRow);
      getTable().requestFocusInCell(getPartNumberColumn(), newRow);
    }
    
    public void addParts(List<Long> partIds) {
      IPartService partService = BEANS.get(IPartService.class);

      IdColumn idColumn = getIdColumn();
      PartNumberColumn pnColumn = getPartNumberColumn();
      PartLabelColumn partLabelColumn = getPartLabelColumn();
      IconColumn iconColumn = getIconColumn();
      ColorColumn colorColumn = getColorColumn();
      PartValueColumn partValueColumn = getPartValueColumn();
      ITableRow firstAddedRow = null;
      
      for (Long partId: partIds) {
        PartFormData partData = new PartFormData();
        partData.setPartId(partId);
        partData = partService.load(partData);
        ITableRow row = getTable().addRow();
        if (firstAddedRow==null) {
          firstAddedRow = row;
        }
        idColumn.setValue(row, partId);
        
        // Part Number: on prend le premier
        String partNumbers = partData.getPartNumbers().getValue();
        String partNumber = partNumbers;
        int posComma = partNumbers.indexOf(',');
        if (posComma>0) {
          partNumber = partNumbers.substring(0, posComma);
        }
        pnColumn.setValue(row, partNumber);
        partLabelColumn.setValue(row, partData.getTitle().getValue());
        
        // Icone: on récupère l'id
        Long iconId = partData.getImageId();
        if (iconId != null && iconId>0) {
          iconColumn.setValue(row, "icons/?image=" + iconId);
        }
        
        // Couleur
        Long colorId = partData.getColor().getValue();
        colorColumn.setValue(row, colorId);
        
        // Valeur
        partValueColumn.setValue(row, partData.getValue().getValue());
        updateCalculatedValues(row);
        getTable().selectRow(row);
      }
      getTable().scrollToSelection();
    }
    
    private class RowChangedListener implements FormListener {
      @Override
      public void formChanged(FormEvent e) {
        // reload page to reflect new/changed data after saving any changes
        if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
          Object source = e.getSource();
          if (source instanceof PartForm) {
            // Les modifications qui nous intéressent sont le coût de la pièce et son nom
            PartForm form = (PartForm) source;
            String partTitle = form.getTitleField().getValue();
            Double partValue = form.getValueField().getValue();
            
            // Ligne en cours d'édition
            ITableRow row = getTable().getSelectedRow();
            
            getPartLabelColumn().setValue(row, partTitle);
            getPartValueColumn().setValue(row, partValue);
            
            Double totalValue = computeTotalValue(row);
            setTotalValue(totalValue);
          }
          reloadTableData();
        }
      }
    }
    
  }

  @Override
  protected String getConfiguredLabel() {
    return TEXTS.get("PartsList");
  }

  @Override
  protected double getConfiguredGridWeightY() {
    return 1.0;
  }
  
  protected void mergeIdenticalParts() {
    Map<Long,ITableRow> rowsByPartId = new HashMap<>();
    IdColumn idColumn = getTable().getIdColumn();
    BinColumn binColumn = getTable().getBinColumn();
    CountColumn countColumn = getTable().getCountColumn();
    for (ITableRow row: getTable().getRows()) {
      Long partId = idColumn.getValue(row);
      Long binId = binColumn.getValue(row);
      if(binId == null) {
        binId = 0L;
      }
      Integer count = countColumn.getValue(row);
      if (count==null) {
        count = 0;
      }
      if (partId != null && partId != 0) {
        // La pièce est déjà dans la table *pour le même compartiment* ?
        Long partKey = binId * 1000000000 + partId;
        ITableRow prevRow = rowsByPartId.get(partKey);
        if (prevRow == null) {
          // Non
          rowsByPartId.put(partKey, row);
        }
        else {
          // Supprimer cette ligne après avoir ajouté les pièces à l'autre
          Integer prevCount = countColumn.getValue(prevRow);
          if (prevCount==null) {
            prevCount = 0;
          }
          Integer totalCount = prevCount + count;
          countColumn.setValue(prevRow, totalCount);
          
          // NB: la ligne ne doit pas être marquée supprimée car sinon à l'enregistrement elle
          // risque d'effacer le couple (boite, pièce) enregistré avec la nouvelle valeur.
          // Elle doit juste être enlevée de la liste.
          getTable().discardRow(row);
          
        }
      }
    }
  }
  
  @Override
  public IValidateContentDescriptor validateContent() {
    IValidateContentDescriptor vcd =  super.validateContent();
    if (vcd != null) {
      return vcd;
    }
    
    if (isSaveNeeded()) {
      // Il faut peut être joindre des lignes si c'est pour le même pièce
      mergeIdenticalParts();
    }
    return null;
  }
  
  public void updateCalculatedValues(ITableRow row) {
    updateTotalValues(row);
    updateDeltaCount(row);
  }
  
  public void updateTotalValues(ITableRow row) {
    Double totalValue = computeTotalValue(row);
    setTotalValue(totalValue);
  }
  
  public Double computeTotalValue(ITableRow changedRow) {
    // Mise à jour de la ligne modifiée éventuelle
    if (changedRow != null) {
      Double partValue = getTable().getPartValueColumn().getValue(changedRow);
      Integer count = getTable().getCountColumn().getValue(changedRow);
      if (partValue!=null && count != null) {
        Double rowValue = count * partValue;
        getTable().getValueColumn().setValue(changedRow, rowValue);
      }
    }
    
    // Recalcul global
    // Calcul valeur
    double totalValue = 0.0;
    for (ITableRow row: getTable().getRows()) {
      Double rowValue = getTable().getValueColumn().getValue(row);
      if (rowValue!=null) {
        totalValue += rowValue;
      }
    }
    return totalValue;
    
  }
  
  public void updateDeltaCount(ITableRow changedRow) {
    if (changedRow != null) {
      Integer count = getTable().getCountColumn().getValue(changedRow);
      if (count==null) {
        count = 0;
      }
      Integer kitCount = getTable().getKitCountColumn().getValue(changedRow);
      if (kitCount==null) {
        kitCount = 0;
      }
      if (count!=null && kitCount!=null) {
        getTable().getDeltaCountColumn().setValue(changedRow, count-kitCount);
      }
    }
    else {
      for (ITableRow row: getTable().getRows()) {
        updateDeltaCount(row);
      }      
    }
  }
  
  public void showKitStatus(ITableRow changedRow) {
    if (!hasKit()) {
      return;
    }
    
    CountColumn countColumn = getTable().getCountColumn();
    KitCountColumn kitCountColumn = getTable().getKitCountColumn();
    
    for (ITableRow row: getTable().getRows()) {
      if (changedRow==null || changedRow==row) {
        Integer boxCount = countColumn.getValue(row);
        Integer kitCount = kitCountColumn.getValue(row);
        if (boxCount==null) boxCount = 0;
        if (kitCount==null) kitCount = 0;
        Cell cell = (Cell) row.getCell(countColumn);
        if (boxCount < kitCount) {
          cell.setCssClass("count-col missing-parts");
        }
        else if (boxCount > kitCount) {
          cell.setCssClass("count-col too-many-parts");
        }
        else {
          cell.setCssClass("count-col");
        }
      }
    }
  }
  
  @Override
  public void importFormFieldData(AbstractFormFieldData source, boolean valueChangeTriggersEnabled) {
    super.importFormFieldData(source, valueChangeTriggersEnabled);
    
    boolean hasBin = false;
    BinColumn binColumn = getTable().getBinColumn();
    for (ITableRow row: getTable().getRows()) {
      Long binId = binColumn.getValue(row);
      if (binId!=null && binId>0) {
        hasBin = true;
        break;
      }
    }
    binColumn.setVisible(hasBin);
  }
}

