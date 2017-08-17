package glass.phil.monzo.model.balance;

import com.google.auto.value.AutoValue;

import org.joda.money.Money;

import glass.phil.auto.moshi.AutoMoshi;
import glass.phil.monzo.model.Refreshable;
import io.reactivex.Observable;

public interface BalanceManager extends Refreshable {
  Observable<Balance> balance();

  @AutoValue @AutoMoshi abstract class Balance {
    public static Balance create(Money balance, Money spentToday) {
      return new AutoValue_BalanceManager_Balance(balance, spentToday);
    }

    public abstract Money balance();
    public abstract Money spentToday();
  }
}
