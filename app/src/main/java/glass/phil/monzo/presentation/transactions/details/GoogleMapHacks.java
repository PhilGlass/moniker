package glass.phil.monzo.presentation.transactions.details;

import android.content.Context;
import android.support.annotation.MainThread;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

public final class GoogleMapHacks {
  /**
   * When a process uses a map for the first time, an expensive initialisation step is triggered. This happens on the
   * main thread and takes ~350ms on a Nexus 5X, resulting in a delayed & janky animation the first time we show a
   * details screen with a map.
   *
   * Google do not provide an API that allows us to do this work ahead of time ({@link MapsInitializer} does not perform
   * the expensive initialisation we care about), so we use a horrible hack that creates and immediately throws away a
   * {@link MapView}. Unfortunately we cannot offload this to a background thread, as the initialisation routine
   * explicitly checks that it is called from the main thread.
   */
  @MainThread public static void initialiseMaps(Context context) {
    final MapView mapView = new MapView(context);
    mapView.onCreate(null);
    mapView.onDestroy();
  }

  private GoogleMapHacks() {
    throw new AssertionError("No instances");
  }
}
