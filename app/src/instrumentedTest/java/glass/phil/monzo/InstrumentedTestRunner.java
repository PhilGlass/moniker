package glass.phil.monzo;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

public final class InstrumentedTestRunner extends AndroidJUnitRunner {
  @Override public Application newApplication(ClassLoader cl, String className, Context context) throws
      InstantiationException, IllegalAccessException, ClassNotFoundException {
    return Instrumentation.newApplication(InstrumentedTestApp.class, context);
  }
}
