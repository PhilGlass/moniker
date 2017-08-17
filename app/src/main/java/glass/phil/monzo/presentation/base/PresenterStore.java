package glass.phil.monzo.presentation.base;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.HashMap;
import java.util.Map;

public final class PresenterStore extends Fragment {
  private static final String TAG_PRESENTER_STORE = "presenter_store";

  private final Map<String, BasePresenter<?, ?>> presenters = new HashMap<>();

  public static void install(FragmentActivity activity) {
    if (store(activity) == null) {
      final PresenterStore store = new PresenterStore();
      store.setRetainInstance(true);
      activity.getSupportFragmentManager().beginTransaction().add(store, TAG_PRESENTER_STORE).commitNow();
    }
  }

  @SuppressWarnings("unchecked")
  public static @Nullable <T extends BasePresenter<?, ?>> T get(FragmentActivity activity, String id) {
    return (T) store(activity).presenters.get(id);
  }

  public static void put(FragmentActivity activity, String id, BasePresenter<?, ?> presenter) {
    store(activity).presenters.put(id, presenter);
  }

  public static void remove(FragmentActivity activity, String id) {
    // If the activity is being finished, the store may have already been destroyed & removed from the fragment manager
    final PresenterStore store = store(activity);
    if (store != null) {
      store.presenters.remove(id);
    }
  }

  private static PresenterStore store(FragmentActivity activity) {
    return (PresenterStore) activity.getSupportFragmentManager().findFragmentByTag(TAG_PRESENTER_STORE);
  }
}
