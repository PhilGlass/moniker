package glass.phil.monzo.model;

import org.threeten.bp.Clock;
import org.threeten.bp.ZoneId;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import glass.phil.monzo.TestData;
import glass.phil.monzo.model.Clocks.Local;
import glass.phil.monzo.model.Clocks.Utc;

@Module public abstract class TestClockModule {
  @Provides @Reusable @Utc static Clock clock() {
    return Clock.fixed(TestData.NOW, ZoneId.of("UTC"));
  }

  @Provides @Reusable @Local static Clock localClock() {
    return Clock.fixed(TestData.NOW, ZoneId.of("UTC"));
  }
}
