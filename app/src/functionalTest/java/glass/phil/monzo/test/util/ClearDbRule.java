package glass.phil.monzo.test.util;

import android.support.test.InstrumentationRegistry;

import org.junit.rules.ExternalResource;

import glass.phil.monzo.FunctionalTestApp;

public final class ClearDbRule extends ExternalResource {
  @Override protected void before() throws Throwable {
    // Any data in our in-memory database will be discarded when it is closed.
    FunctionalTestApp.getComponent(InstrumentationRegistry.getTargetContext()).transactionsDb().close();
  }
}
