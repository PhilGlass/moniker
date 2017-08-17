package glass.phil.monzo.model.transactions;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.util.concurrent.atomic.AtomicLong;

import glass.phil.monzo.TestData;
import glass.phil.monzo.model.transactions.Transaction.Category;
import glass.phil.monzo.model.transactions.Transaction.Merchant;

@SuppressWarnings("WeakerAccess")
public final class TestTransactions {
  public static final Merchant AMAZON = merchant("m_amazon", "Amazon", true);
  public static final Merchant ASDA = merchant("m_asda", "Asda", false);
  public static final Merchant STARBUCKS = merchant("m_starbucks", "Starbucks", false);

  private static final AtomicLong ID_GENERATOR = new AtomicLong();

  public static TestTransaction topUp() {
    return TestTransaction.builder()
        .id(String.valueOf(ID_GENERATOR.getAndIncrement()))
        .category(Category.MONDO)
        .created(TestData.NOW)
        .amount(Money.zero(CurrencyUnit.GBP))
        .localAmount(Money.zero(CurrencyUnit.GBP))
        .topUp(true)
        .hideAmount(false)
        .includeInSpending(true)
        .build();
  }

  public static TestTransaction atMerchant(Merchant merchant) {
    return TestTransaction.builder()
        .id(String.valueOf(ID_GENERATOR.getAndIncrement()))
        .category(Category.GENERAL)
        .created(TestData.NOW)
        .amount(Money.zero(CurrencyUnit.GBP))
        .localAmount(Money.zero(CurrencyUnit.GBP))
        .topUp(false)
        .hideAmount(false)
        .includeInSpending(true)
        .merchant(merchant)
        .build();
  }

  private static Merchant merchant(String id, String name, boolean online) {
    return Merchant.create(id, name, online, null, Merchant.Address.create("Somewhere", 0, 0));
  }

  private TestTransactions() {
    throw new AssertionError("No instances");
  }
}
