package pers.mr.ft.inventory.client;

import java.util.regex.Matcher;

import org.eclipse.scout.rt.client.deeplink.AbstractDeepLinkHandler;
import org.eclipse.scout.rt.client.deeplink.DeepLinkException;
import org.eclipse.scout.rt.client.deeplink.DeepLinkUriBuilder;
import org.eclipse.scout.rt.client.ui.desktop.BrowserHistoryEntry;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.platform.Order;

import pers.mr.ft.inventory.client.forms.BoxForm;

@Order(1000)
public class FormDeepLinkHandler extends AbstractDeepLinkHandler {

  private static final String HANDLER_NAME = "box";

  public FormDeepLinkHandler() {
    super(defaultPattern(HANDLER_NAME, "\\d+"));
  }

  @Override
  public void handleImpl(Matcher matcher) throws DeepLinkException {
    String formId = matcher.group(1);
    
    BoxForm form = getFormById(formId);
    form.startModify();
  }

  private BoxForm getFormById(String formId) throws DeepLinkException {
    try {
      Long boxId = Long.parseLong(formId);
      BoxForm boxForm = new BoxForm();
      boxForm.setBoxId(boxId);
      return boxForm;
    }
    catch (Exception e) {
      // throw a DeepLinkException when resource requested by deep-link does not exist
      throw new DeepLinkException("Form not found");
    }
  }

  public BrowserHistoryEntry createBrowserHistoryEntry(IForm form) {
    return DeepLinkUriBuilder.createRelative()
        .parameterInfo(form.getTitle())
        .parameterPath(toDeepLinkPath(getFormId(form)))
        .createBrowserHistoryEntry();
  }

  private String getFormId(IForm form) {
    return form.getFormId();
  }

  @Override
  public String getName() {
    return HANDLER_NAME;
  }

}