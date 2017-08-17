package glass.phil.monzo.presentation.login;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import glass.phil.monzo.presentation.base.BasePresenter;

interface LoginContract {
  abstract class Presenter extends BasePresenter<LoginView, SavedState> {
    abstract void onLoginResult(String code, String state);
    abstract void retry();
  }

  interface LoginView {
    void render(ViewModel model);
  }

  @AutoValue abstract class ViewModel {
    static ViewModel just(String state) {
      return new AutoValue_LoginContract_ViewModel(state, false, false, false);
    }

    abstract String state();
    abstract boolean loading();
    abstract boolean error();
    abstract boolean loginComplete();

    abstract ViewModel withState(String state);
    abstract ViewModel withLoading(boolean loading);
    abstract ViewModel withError(boolean error);
    abstract ViewModel withLoginComplete(boolean loginComplete);
  }

  @AutoValue abstract class SavedState implements Parcelable {
    static SavedState create(String state, boolean error) {
      return new AutoValue_LoginContract_SavedState(state, error);
    }

    abstract String state();
    abstract boolean error();
  }
}
