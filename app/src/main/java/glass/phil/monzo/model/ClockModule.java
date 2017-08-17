package glass.phil.monzo.model;

import org.threeten.bp.Clock;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import glass.phil.monzo.model.Clocks.Local;
import glass.phil.monzo.model.Clocks.Utc;

@Module public abstract class ClockModule {
  @Provides @Reusable @Utc static Clock clock() {
    return Clock.systemUTC();
  }

  @Provides @Reusable @Local static Clock localClock() {
    return Clock.systemDefaultZone();
  }
}
