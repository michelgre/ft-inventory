package pers.mr.ft.inventory.client;

import java.beans.PropertyChangeEvent;
import java.security.AccessController;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.desktop.AbstractDesktop;
import org.eclipse.scout.rt.client.ui.desktop.OpenUriAction;
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutlineViewButton;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.client.ui.form.ScoutInfoForm;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;

import pers.mr.ft.inventory.client.Desktop.UserProfileMenu.ThemeMenu.DarkThemeMenu;
import pers.mr.ft.inventory.client.Desktop.UserProfileMenu.ThemeMenu.DefaultThemeMenu;
import pers.mr.ft.inventory.client.forms.BoxForm;
import pers.mr.ft.inventory.client.forms.BoxTypeForm;
import pers.mr.ft.inventory.client.forms.DocumentForm;
import pers.mr.ft.inventory.client.forms.PartForm;
import pers.mr.ft.inventory.client.inventory.InventoryOutline;
import pers.mr.ft.inventory.client.referential.ReferentialOutline;
import pers.mr.ft.inventory.client.settings.SettingsOutline;
import pers.mr.ft.inventory.shared.Icons;
import pers.mr.ft.inventory.shared.forms.IPartService;
import pers.mr.ft.inventory.shared.reports.IReportService;

/**
 * @author michel
 */
public class Desktop extends AbstractDesktop {
  private Map<Long,BoxForm> boxFormsById = new HashMap<>();
  private Map<Long,PartForm> partFormsById = new HashMap<>();
  private Map<Long,DocumentForm> documentFormsById = new HashMap<>();
  private Map<Long,BoxTypeForm> boxTypeFormsById = new HashMap<>();

  private Set<Long> partsClipboard = new HashSet<>();
  
  public Desktop() {
    addPropertyChangeListener(PROP_THEME, this::onThemeChanged);
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("ApplicationTitle");
  }

  @Override
  protected String getConfiguredLogoId() {
    return Icons.AppLogo;
  }

  @Override
  protected List<Class<? extends IOutline>> getConfiguredOutlines() {
    return CollectionUtility.<Class<? extends IOutline>>arrayList(
        InventoryOutline.class, 
        //SearchOutline.class,
        ReferentialOutline.class, 
        //DocumentOutline.class,
        SettingsOutline.class);
  }

  @Override
  protected void execDefaultView() {
    selectFirstVisibleOutline();
  }

  protected void selectFirstVisibleOutline() {
    for (IOutline outline : getAvailableOutlines()) {
      if (outline.isEnabled() && outline.isVisible()) {
        setOutline(outline.getClass());
        return;
      }
    }
  }

  protected void onThemeChanged(PropertyChangeEvent evt) {
    IMenu darkMenu = getMenuByClass(DarkThemeMenu.class);
    IMenu defaultMenu = getMenuByClass(DefaultThemeMenu.class);
    String newThemeName = (String) evt.getNewValue();
    if (DarkThemeMenu.DARK_THEME.equalsIgnoreCase(newThemeName)) {
      darkMenu.setIconId(Icons.CheckedBold);
      defaultMenu.setIconId(null);
    } else {
      darkMenu.setIconId(null);
      defaultMenu.setIconId(Icons.CheckedBold);
    }
  }


  @Order(0)
  public class FileMenu extends AbstractMenu {
    @Override
    protected String getConfiguredText() {
      return TEXTS.get("File");
    }

    @Override
    protected Set<? extends IMenuType> getConfiguredMenuTypes() {
      return CollectionUtility.hashSet();
    }

    @Override
    protected void execAction() {
    }


    @Order(0)
    public class BoxReportMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("BoxReport");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet();
      }

      @Override
      protected void execAction() {
        IReportService service = BEANS.get(IReportService.class);
        BinaryResource br = service.buildReport(null);
        openUri(br, OpenUriAction.DOWNLOAD);
      }
    }
    
    
    @Order(1000)
    public class SynchronizeMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("SyncFromDatenbank");
      }

      @Order(1000)
      public class SyncPartsMenu extends AbstractMenu {
        @Override
        protected String getConfiguredText() {
          return TEXTS.get("SyncParts");
        }

        @Override
        protected Set<? extends IMenuType> getConfiguredMenuTypes() {
          return CollectionUtility.hashSet();
        }

        @Override
        protected void execAction() {
          IPartService service = BEANS.get(IPartService.class);
          service.syncCategoryFromDatenbank(773); // Parts (Einzelteile)
        }
      }



      @Order(1500)
      public class SyncBoxesMenu extends AbstractMenu {
        @Override
        protected String getConfiguredText() {
          return TEXTS.get("SyncBoxes");
        }

        @Override
        protected Set<? extends IMenuType> getConfiguredMenuTypes() {
          return CollectionUtility.hashSet();
        }

        @Override
        protected void execAction() {
          IPartService service = BEANS.get(IPartService.class);
          service.syncCategoryFromDatenbank(653); // Kits (Baukästen)
        }
      }



      @Order(2000)
      public class SyncImagesMenu extends AbstractMenu {
        @Override
        protected String getConfiguredText() {
          return TEXTS.get("SyncImages");
        }

        @Override
        protected Set<? extends IMenuType> getConfiguredMenuTypes() {
          return CollectionUtility.hashSet();
        }

        @Override
        protected void execAction() {
          IPartService service = BEANS.get(IPartService.class);
          service.syncImagesFromDatenbank();
        }
      }
      
      
      
      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet();
      }

    }
  }

  
  @Order(1000)
  public class UserProfileMenu extends AbstractMenu {

    @Override
    protected String getConfiguredKeyStroke() {
      return IKeyStroke.F10;
    }

    @Override
    protected String getConfiguredIconId() {
      return Icons.PersonSolid;
    }

    @Override
    protected String getConfiguredText() {
      Subject subject = Subject.getSubject(AccessController.getContext());
      Principal firstPrincipal = CollectionUtility.firstElement(subject.getPrincipals());
      return StringUtility.uppercaseFirst(firstPrincipal.getName());
    }

    @Order(1000)
    public class AboutMenu extends AbstractMenu {

      @Override
      protected String getConfiguredText() {
        return TEXTS.get("About");
      }

      @Override
      protected void execAction() {
        ScoutInfoForm form = new ScoutInfoForm();
        form.startModify();
      }
    }

    @Order(2000)
    public class ThemeMenu extends AbstractMenu {

      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Theme");
      }

      @Order(1000)
      public class DefaultThemeMenu extends AbstractMenu {

        private static final String DEFAULT_THEME = "Default";

        @Override
        protected String getConfiguredText() {
          return DEFAULT_THEME;
        }

        @Override
        protected void execAction() {
          setTheme(DEFAULT_THEME.toLowerCase());
        }
      }

      @Order(2000)
      public class DarkThemeMenu extends AbstractMenu {

        private static final String DARK_THEME = "Dark";

        @Override
        protected String getConfiguredText() {
          return DARK_THEME;
        }

        @Override
        protected void execAction() {
          setTheme(DARK_THEME.toLowerCase());
        }
      }
      @Order(3000)
      public class FTThemeMenu extends AbstractMenu {

        private static final String FT_THEME = "ft";

        @Override
        protected String getConfiguredText() {
          return FT_THEME;
        }

        @Override
        protected void execAction() {
          setTheme(FT_THEME.toLowerCase());
        }
      }

    }

    @Order(3000)
    public class LogoutMenu extends AbstractMenu {

      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Logout");
      }

      @Override
      protected void execAction() {
        ClientSessionProvider.currentSession().stop();
      }
    }
  }

  @Order(1000)
  public class InventoryOutlineViewButton extends AbstractOutlineViewButton {

    public InventoryOutlineViewButton() {
      this(InventoryOutline.class);
    }

    protected InventoryOutlineViewButton(Class<? extends InventoryOutline> outlineClass) {
      super(Desktop.this, outlineClass);
    }

    @Override
    protected String getConfiguredKeyStroke() {
      return IKeyStroke.F2;
    }
  }

  /*
  @Order(2000)
  public class SearchOutlineViewButton extends AbstractOutlineViewButton {

    public SearchOutlineViewButton() {
      this(SearchOutline.class);
    }

    protected SearchOutlineViewButton(Class<? extends SearchOutline> outlineClass) {
      super(Desktop.this, outlineClass);
    }

    @Override
    protected DisplayStyle getConfiguredDisplayStyle() {
      return DisplayStyle.TAB;
    }

    @Override
    protected String getConfiguredKeyStroke() {
      return IKeyStroke.F3;
    }
    
  }
  */
 
  @Order(2500)
  public class ReferentialOutlineViewButton extends AbstractOutlineViewButton {

    public ReferentialOutlineViewButton() {
      this(ReferentialOutline.class);
    }

    protected ReferentialOutlineViewButton(Class<? extends ReferentialOutline> outlineClass) {
      super(Desktop.this, outlineClass);
    }

    @Override
    protected DisplayStyle getConfiguredDisplayStyle() {
      return DisplayStyle.TAB;
    }

    @Override
    protected String getConfiguredKeyStroke() {
      return IKeyStroke.F4;
    }
  }
  @Order(3000)
  public class SettingsOutlineViewButton extends AbstractOutlineViewButton {

    public SettingsOutlineViewButton() {
      this(SettingsOutline.class);
    }

    protected SettingsOutlineViewButton(Class<? extends SettingsOutline> outlineClass) {
      super(Desktop.this, outlineClass);
    }

    @Override
    protected DisplayStyle getConfiguredDisplayStyle() {
      return DisplayStyle.TAB;
    }

    @Override
    protected String getConfiguredKeyStroke() {
      return IKeyStroke.F10;
    }
  }
  
  public BoxForm findBoxForm(Long boxId, FormListener listener) {
    BoxForm form = boxFormsById.get(boxId);
    
    if (form!=null) {
      if (form.isDisposeDone()) {
        partFormsById.remove(boxId);
        form = null;
      }
    }    
    if (form==null) {
      form = new BoxForm();
      form.setBoxId(boxId);
      boxFormsById.put(boxId, form);
      form.addFormListener(listener);
      form.addFormListener(new FormListener() {
        @Override
        public void formChanged(FormEvent e) {
          if (e.getType()==FormEvent.TYPE_CLOSED) {
            boxFormsById.remove(boxId);
          }
        }
        
      });
      form.startModify();
    }
    else {
      form.addFormListener(listener);
      form.activate();
    }

    return form;
  }
  
  
  public PartForm findPartForm(Long partId, FormListener listener) {
    PartForm form = partFormsById.get(partId);
    if (form!=null) {
      if (form.isDisposeDone()) {
        partFormsById.remove(partId);
        form = null;
      }
    }    
    if (form==null) {
      form = new PartForm();
      form.setPartId(partId);
      partFormsById.put(partId, form);
      form.addFormListener(listener);
      form.addFormListener(new FormListener() {
        @Override
        public void formChanged(FormEvent e) {
          if (e.getType()==FormEvent.TYPE_DISCARDED) {
            partFormsById.remove(partId);
          }
        }
        
      });
      form.startModify();
    }
    else {
      form.addFormListener(listener);
      form.activate();
    }

    return form;
  }

  public DocumentForm findDocumentForm(Long docId, FormListener listener) {
    DocumentForm form = documentFormsById.get(docId);
    if (form!=null) {
      if (form.isDisposeDone()) {
        partFormsById.remove(docId);
        form = null;
      }
    }    
    if (form==null) {
      form = new DocumentForm();
      form.setDocId(docId);
      documentFormsById.put(docId, form);
      form.addFormListener(listener);
      form.addFormListener(new FormListener() {
        @Override
        public void formChanged(FormEvent e) {
          if (e.getType()==FormEvent.TYPE_DISCARDED) {
            documentFormsById.remove(docId);
          }
        }
        
      });
      form.startModify();
    }
    else {
      form.addFormListener(listener);
      form.activate();
    }

    return form;
  }

  public BoxTypeForm findBoxTypeForm(Long objectId, FormListener listener) {
    BoxTypeForm form = boxTypeFormsById.get(objectId);
    if (form!=null) {
      if (form.isDisposeDone()) {
        boxTypeFormsById.remove(objectId);
        form = null;
      }
    }    
    if (form==null) {
      form = new BoxTypeForm();
      form.setBoxTypeId(objectId);
      boxTypeFormsById.put(objectId, form);
      form.addFormListener(listener);
      form.addFormListener(new FormListener() {
        @Override
        public void formChanged(FormEvent e) {
          if (e.getType()==FormEvent.TYPE_DISCARDED) {
            boxTypeFormsById.remove(objectId);
          }
        }
        
      });
      form.startModify();
    }
    else {
      form.addFormListener(listener);
      form.activate();
    }

    return form;
  }

  public void copyPartsClipboard(List<Long> partIds, boolean clearFirst) {
    if (clearFirst) {
      partsClipboard.clear();
    }
    for (Long id: partIds) {
      partsClipboard.add(id);
    }
  }
  
  public List<Long> getPartsClipboard() {
    List<Long> partList = new LinkedList<>();
    for (Long id: partsClipboard) {
      partList.add(id);
    }
    return partList;
  }
}
