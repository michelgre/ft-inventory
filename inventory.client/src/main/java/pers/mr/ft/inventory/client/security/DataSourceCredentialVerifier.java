package pers.mr.ft.inventory.client.security;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.security.auth.Subject;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.context.RunContext;
import org.eclipse.scout.rt.platform.context.RunContexts;
import org.eclipse.scout.rt.platform.security.ICredentialVerifier;
import org.eclipse.scout.rt.platform.security.SimplePrincipal;

import pers.mr.ft.inventory.shared.security.IAuthorizationService;

public class DataSourceCredentialVerifier implements ICredentialVerifier {


  @Override
  public int verify(String username, char[] password) throws IOException {
    Subject subject = new Subject();
    subject.getPrincipals().add(new SimplePrincipal("system"));
    subject.setReadOnly();
    RunContext runContext = RunContexts.copyCurrent(true).withSubject(subject);
    
    return runContext.call(new Callable<Integer>() {
         @Override
         public Integer call() throws Exception {
           IAuthorizationService service = BEANS.get(IAuthorizationService.class);
           return service.verify(username, password);
         }
    });
  }
}

