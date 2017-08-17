package glass.phil.monzo.presentation.base;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BasePresenter<V, S extends Parcelable> {
  private final CompositeDisposable createdDisposable = new CompositeDisposable();
  private final CompositeDisposable attachedDisposable = new CompositeDisposable();

  final void performCreate(@Nullable S state) {
    create(state);
  }

  /** Called when the presenter is being created, possibly with restored state. */
  protected void create(@Nullable S savedState) {}

  final void performAttach(V view) {
    attach(view);
  }

  /** Called when the presenter's view is available. */
  protected void attach(V view) {}

  /** Called before {@link #detach()} if the presenter's state should be saved. */
  @Nullable protected S saveState() {
    return null;
  }

  final void performDetach() {
    attachedDisposable.clear();
    detach();
  }

  /** Called when the presenter should release references to the previously attached view. */
  @SuppressWarnings("WeakerAccess")
  protected void detach() {}

  final void performDestroy() {
    createdDisposable.clear();
    destroy();
  }

  /** Called when the presenter is being destroyed and will not be re-used. */
  @SuppressWarnings("WeakerAccess")
  protected void destroy() {}

  protected final void disposeOnDestroy(Disposable disposable) {
    createdDisposable.add(disposable);
  }

  protected final void disposeOnDetach(Disposable disposable) {
    attachedDisposable.add(disposable);
  }
}
