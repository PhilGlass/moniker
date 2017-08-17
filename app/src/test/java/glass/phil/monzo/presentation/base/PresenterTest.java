package glass.phil.monzo.presentation.base;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import org.junit.ClassRule;

import glass.phil.monzo.RxSchedulerOverrideRule;
import io.reactivex.android.schedulers.AndroidSchedulers;

public abstract class PresenterTest {
  @ClassRule public static final RxSchedulerOverrideRule RX_RULE = new RxSchedulerOverrideRule();

  protected final <S extends Parcelable> void performCreate(BasePresenter<?, S> presenter) {
    performCreate(presenter, null);
  }

  protected final <S extends Parcelable> void performCreate(BasePresenter<?, S> presenter, @Nullable S state) {
    presenter.performCreate(state);
  }

  protected final <V> void performAttach(BasePresenter<V, ?> presenter, V view) {
    presenter.performAttach(view);
  }

  protected final void performDetach(BasePresenter<?, ?> presenter) {
    presenter.performDetach();
  }

  protected final void performDestroy(BasePresenter<?, ?> presenter) {
    presenter.performDestroy();
  }

  /**
   * In our presenter tests, {@link AndroidSchedulers#mainThread()} is overridden such that no submitted tasks will be
   * executed until this method is called.
   *
   * This is useful when we want to check that an emission is synchronous. For example, when a view is attached to its
   * presenter, we usually want to synchronously emit a view model representing its initial state. If this emission
   * were instead scheduled to run on the main thread, the view would be in an indeterminate state when it renders its
   * first frame. This can result in noticeable UI flicker when the view renders its second frame, which correctly
   * represents the emitted initial state.
   */
  protected final void executePendingMainThreadActions() {
    RX_RULE.executePendingMainThreadActions();
  }
}
