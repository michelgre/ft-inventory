package pers.mr.ft.inventory.shared.lookup;

import org.eclipse.scout.rt.shared.services.lookup.ILookupService;
import org.eclipse.scout.rt.shared.services.lookup.LookupCall;

public class PartTypeLookupCall extends LookupCall<Long> {
  private static final long serialVersionUID = 1L;

  @Override
  protected Class<? extends ILookupService<Long>> getConfiguredService() {
    return IPartTypeLookupService.class;
  }
}
