package pers.mr.ft.inventory.client.forms;

import org.eclipse.scout.rt.client.testenvironment.TestEnvironmentClientSession;
import org.eclipse.scout.rt.testing.client.runner.ClientTestRunner;
import org.eclipse.scout.rt.testing.client.runner.RunWithClientSession;
import org.eclipse.scout.rt.testing.platform.mock.BeanMock;
import org.eclipse.scout.rt.testing.platform.runner.RunWithSubject;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import pers.mr.ft.inventory.shared.forms.ILocationService;
import pers.mr.ft.inventory.shared.forms.LocationFormData;

@RunWithSubject("anonymous")
@RunWith(ClientTestRunner.class)
@RunWithClientSession(TestEnvironmentClientSession.class)
public class LocationFormTest {
  @BeanMock
  private ILocationService m_mockSvc;
  // TODO [mreverbel] add test cases

  @Before
  public void setup() {
    LocationFormData answer = new LocationFormData();
    Mockito.when(m_mockSvc.prepareCreate(ArgumentMatchers.any())).thenReturn(answer);
    Mockito.when(m_mockSvc.create(ArgumentMatchers.any())).thenReturn(answer);
    Mockito.when(m_mockSvc.load(ArgumentMatchers.any())).thenReturn(answer);
    Mockito.when(m_mockSvc.store(ArgumentMatchers.any())).thenReturn(answer);
  }
}
