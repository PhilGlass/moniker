package glass.phil.monzo.model.transactions;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.Nullable;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.threeten.bp.Instant;

import glass.phil.monzo.model.transactions.Transaction.Category;
import glass.phil.monzo.model.transactions.Transaction.DeclineReason;
import glass.phil.monzo.model.transactions.Transaction.Merchant;
import glass.phil.monzo.model.transactions.Transaction.Merchant.Address;
import glass.phil.monzo.model.transactions.TransactionsApi.TransactionsResponse.TransactionData;
import glass.phil.monzo.model.transactions.TransactionsApi.TransactionsResponse.TransactionData.MerchantData;

import static glass.phil.monzo.core.Cursors.getBoolean;
import static glass.phil.monzo.core.Cursors.getDouble;
import static glass.phil.monzo.core.Cursors.getLong;
import static glass.phil.monzo.core.Cursors.getString;
import static glass.phil.monzo.core.Enums.enumConstant;
import static glass.phil.monzo.core.Enums.enumConstantOrThrow;
import static glass.phil.monzo.core.Strings.emptyToNull;

final class TransactionMapper {
  static ContentValues toContentValues(MerchantData merchant) {
    final ContentValues values = new ContentValues();
    values.put("id", merchant.id());
    values.put("name", merchant.name());
    values.put("online", merchant.online());
    values.put("logo_url", emptyToNull(merchant.logo()));
    final MerchantData.Address address = merchant.address();
    values.put("address", address.short_formatted());
    values.put("latitude", address.latitude());
    values.put("longitude", address.longitude());
    return values;
  }

  static ContentValues toContentValues(TransactionData transaction) {
    final ContentValues values = new ContentValues();
    values.put("id", transaction.id());
    values.put("category", transaction.category());
    values.put("created", transaction.created().toEpochMilli());
    values.put("currency", transaction.currency());
    values.put("amount", transaction.amount());
    values.put("local_currency", transaction.local_currency());
    values.put("local_amount", transaction.local_amount());
    values.put("decline_reason", emptyToNull(transaction.decline_reason()));
    values.put("top_up", transaction.is_load());
    values.put("include_in_spending", transaction.include_in_spending());
    values.put("hide_amount", Boolean.parseBoolean(transaction.metadata().get("hide_amount")));
    values.put("notes", emptyToNull(transaction.notes()));
    final MerchantData merchant = transaction.merchant();
    values.put("merchant", merchant == null ? null : merchant.id());
    return values;
  }

  static Transaction fromCursor(Cursor cursor) {
    return Transaction.builder()
        .id(getString(cursor, "id"))
        .category(enumConstantOrThrow(Category.class, getString(cursor, "category")))
        .created(Instant.ofEpochMilli(getLong(cursor, "created")))
        .amount(money(getString(cursor, "currency"), getLong(cursor, "amount")))
        .localAmount(money(getString(cursor, "local_currency"), getLong(cursor, "local_amount")))
        .declineReason(enumConstant(DeclineReason.class, getString(cursor, "decline_reason")))
        .topUp(getBoolean(cursor, "top_up"))
        .hideAmount(getBoolean(cursor, "hide_amount"))
        .notes(getString(cursor, "notes"))
        .merchant(merchant(cursor))
        .build();
  }

  private static Money money(String currency, long amountMinor) {
    return Money.ofMinor(CurrencyUnit.of(currency), amountMinor);
  }

  @Nullable private static Merchant merchant(Cursor cursor) {
    final String merchantId = getString(cursor, "merchant");
    if (merchantId == null) {
      return null;
    }
    return Merchant.create(
        merchantId,
        getString(cursor, "name"),
        getBoolean(cursor, "online"),
        getString(cursor, "logo_url"),
        Address.create(
            getString(cursor, "address"),
            getDouble(cursor, "latitude"),
            getDouble(cursor, "longitude")
        )
    );
  }

  private TransactionMapper() {
    throw new AssertionError("No instances");
  }
}
