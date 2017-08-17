package glass.phil.monzo.model.transactions;

import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import glass.phil.monzo.core.Strings;
import glass.phil.monzo.model.transactions.Transaction.Merchant;
import glass.phil.monzo.model.transactions.TransactionsApi.TransactionsResponse.TransactionData;
import glass.phil.monzo.model.transactions.TransactionsApi.TransactionsResponse.TransactionData.MerchantData;

final class TestTransactionMapper {
  static Transaction toTransaction(TestTransaction testTransaction) {
    return Transaction.builder()
        .id(testTransaction.id())
        .category(testTransaction.category())
        .created(testTransaction.created())
        .amount(testTransaction.amount())
        .localAmount(testTransaction.localAmount())
        .declineReason(testTransaction.declineReason())
        .topUp(testTransaction.topUp())
        .hideAmount(testTransaction.hideAmount())
        .notes(Strings.emptyToNull(testTransaction.notes()))
        .merchant(testTransaction.merchant())
        .build();
  }

  static TransactionData toTransactionData(TestTransaction testTransaction) {
    final Transaction.DeclineReason declineReason = testTransaction.declineReason();
    return new AutoValue_TransactionsApi_TransactionsResponse_TransactionData(
        testTransaction.id(),
        testTransaction.category().name().toLowerCase(Locale.US),
        testTransaction.created(),
        testTransaction.amount().getCurrencyUnit().getCurrencyCode(),
        testTransaction.amount().getAmountMinorLong(),
        testTransaction.localAmount().getCurrencyUnit().getCurrencyCode(),
        testTransaction.localAmount().getAmountMinorLong(),
        declineReason == null ? null : declineReason.name().toLowerCase(Locale.US),
        testTransaction.topUp(),
        testTransaction.includeInSpending(),
        Strings.nullToEmpty(testTransaction.notes()),
        toMetadata(testTransaction),
        toMerchantData(testTransaction.merchant())
    );
  }

  @Nullable private static Map<String, String> toMetadata(TestTransaction testTransaction) {
    final Map<String, String> metadata = new HashMap<>();
    if (testTransaction.hideAmount()) {
      metadata.put("hide_amount", "true");
    }
    return metadata;
  }

  @Nullable private static MerchantData toMerchantData(@Nullable Merchant merchant) {
    if (merchant == null) {
      return null;
    }
    return new AutoValue_TransactionsApi_TransactionsResponse_TransactionData_MerchantData(
        merchant.id(),
        merchant.name(),
        merchant.online(),
        Strings.nullToEmpty(merchant.logoUrl()),
        new AutoValue_TransactionsApi_TransactionsResponse_TransactionData_MerchantData_Address(
            merchant.address().latitude(),
            merchant.address().longitude(),
            merchant.address().formattedAddress()
        )
    );
  }

  private TestTransactionMapper() {
    throw new AssertionError("No instances");
  }
}
