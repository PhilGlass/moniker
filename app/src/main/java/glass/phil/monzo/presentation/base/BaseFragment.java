package glass.phil.monzo.presentation.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import glass.phil.monzo.core.Bundles;
import glass.phil.monzo.core.Preconditions;

public abstract class BaseFragment<V, P extends BasePresenter<V, ?>> extends DaggerFragment {
  private static final String KEY_FRAGMENT_ID = "fragment_id";
  private static final String KEY_PRESENTER_STATE = "presenter_state";

  @Inject Provider<P> presenterProvider;

  // We first attach to our presenter in onActivityCreated(), as we want the ability to set the
  // initial state of the fragment *after* its view hierarchy has been constructed but *before* its
  // view state has been restored. This ensures that any transient UI state (such as text entered
  // into an EditText or the checked state of a CheckBox) is correctly restored. If the fragment
  // moves back into a started state without its view hierarchy having been destroyed and recreated,
  // we reattach to our presenter in onStart().
  // In order to ensure no fragment transactions are committed after our parent's instance state has
  // been saved, we must detach from our presenter before onStop() returns. In the case where the
  // fragment has gone through onActivityCreated() but has not been started, we detach from our
  // presenter in onDestroyView().
  protected P presenter;

  private String id;
  private Unbinder unbinder;
  private boolean attached;

  @Override @CallSuper public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState == null) {
      id = generateId();
    } else {
      id = savedInstanceState.getString(KEY_FRAGMENT_ID);
      presenter = PresenterStore.get(getActivity(), id);
    }
    if (presenter == null) {
      presenter = presenterProvider.get();
      presenter.performCreate(Bundles.getParcelable(savedInstanceState, KEY_PRESENTER_STATE));
      PresenterStore.put(getActivity(), id, presenter);
    }
  }

  private static String generateId() {
    return UUID.randomUUID().toString();
  }

  @Nullable @Override public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(layout(), container, false);
  }

  @LayoutRes protected abstract int layout();

  @Override @CallSuper public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    unbinder = ButterKnife.bind(this, view);
  }

  @Override @CallSuper public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    attachToPresenter();
  }

  private void attachToPresenter() {
    presenter.performAttach(view());
    attached = true;
  }

  protected abstract V view();

  @Override @CallSuper public void onStart() {
    super.onStart();
    if (!attached) {
      attachToPresenter();
    }
  }

  @Override @CallSuper public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(KEY_FRAGMENT_ID, id);
    outState.putParcelable(KEY_PRESENTER_STATE, presenter.saveState());
  }

  @Override @CallSuper public void onStop() {
    super.onStop();
    detachFromPresenter();
  }

  private void detachFromPresenter() {
    presenter.performDetach();
    attached = false;
  }

  @Override @CallSuper public void onDestroyView() {
    super.onDestroyView();
    if (attached) {
      detachFromPresenter();
    }
    unbinder.unbind();
  }

  @Override @CallSuper public void onDestroy() {
    super.onDestroy();
    final FragmentActivity activity = getActivity();
    if (!activity.isChangingConfigurations()) {
      presenter.performDestroy();
      PresenterStore.remove(activity, id);
    }
  }

  protected final View requireView() {
    return Preconditions.checkNotNull(getView());
  }
}
