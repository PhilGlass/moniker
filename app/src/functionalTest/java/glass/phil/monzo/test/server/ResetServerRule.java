package glass.phil.monzo.test.server;

import org.junit.rules.ExternalResource;

public final class ResetServerRule extends ExternalResource {
  @Override protected void before() {
    MonzoServer.getInstance().reset();
  }
}
