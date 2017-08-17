package glass.phil.monzo.test.server;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import glass.phil.monzo.BuildConfig;
import glass.phil.monzo.TestData;
import glass.phil.monzo.core.Strings;
import glass.phil.monzo.model.balance.BalanceManager.Balance;
import glass.phil.monzo.model.transactions.TestTransaction;
import glass.phil.monzo.model.transactions.Transaction.DeclineReason;
import glass.phil.monzo.model.transactions.Transaction.Merchant;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public final class MonzoServer {
  private static final MonzoServer INSTANCE = new MonzoServer();

  public static MonzoServer getInstance() {
    return INSTANCE;
  }

  private final MockWebServer server = new MockWebServer();

  private boolean tokenError;
  private Balance balanceData;
  private boolean transactionsError;
  private List<TestTransaction> transactionsData;

  private MonzoServer() {
    server.setDispatcher(new Dispatcher() {
      @Override public MockResponse dispatch(RecordedRequest request) {
        return handleRequest(request);
      }
    });
  }

  public void start() {
    try {
      reset();
      server.start();
    } catch (IOException e) {
      throw new RuntimeException("Unable to start MonzoServer", e);
    }
  }

  public void stop() {
    try {
      server.shutdown();
    } catch (IOException ignore) {}
  }

  public HttpUrl url(String path) {
    return server.url(path);
  }

  public synchronized void onTokenRequestReturnError() {
    tokenError = true;
  }

  public synchronized void onBalanceRequestReturn(Balance balance) {
    balanceData = balance;
  }

  public synchronized void onTransactionsRequestReturnError() {
    transactionsError = true;
  }

  public synchronized void onTransactionsRequestReturn(TestTransaction transaction) {
    transactionsData = Collections.singletonList(transaction);
  }

  public synchronized void onTransactionsRequestReturn(List<TestTransaction> transactions) {
    transactionsData = transactions;
  }

  synchronized void reset() {
    tokenError = false;
    balanceData = TestData.balance(100_00, 50_00);
    transactionsError = false;
    transactionsData = Collections.emptyList();
  }

  private synchronized MockResponse handleRequest(RecordedRequest request) {
    final String path = request.getRequestUrl().encodedPath();
    if ("POST".equals(request.getMethod()) && "/oauth2/token".equals(path)) {
      if (tokenError) {
        return new MockResponse().setResponseCode(500);
      }
      return tokenResponse(request);
    } else if ("GET".equals(request.getMethod())) {
      switch (path) {
        case "/accounts":
          return checkToken(request, accountsResponse());
        case "/balance":
          return checkToken(request, checkAccountId(request, balanceResponse()));
        case "/transactions":
          if (transactionsError) {
            return new MockResponse().setResponseCode(500);
          }
          return checkToken(request, checkAccountId(request, transactionsResponse()));
      }
    }
    return new MockResponse().setResponseCode(404);
  }

  private static MockResponse checkToken(RecordedRequest request, MockResponse response) {
    final String expected = "Bearer " + TestData.ACCESS_TOKEN;
    if (!expected.equals(request.getHeader("Authorization"))) {
      return new MockResponse().setResponseCode(401);
    }
    return response;
  }

  private static MockResponse checkAccountId(RecordedRequest request, MockResponse response) {
    final String accountId = request.getRequestUrl().queryParameter("account_id");
    if (!TestData.ACCOUNT_ID.equals(accountId)) {
      return new MockResponse().setResponseCode(403);
    }
    return response;
  }

  // Matches the observed validation order of the Monzo backend.
  private static MockResponse tokenResponse(RecordedRequest request) {
    final Map<String, String> fields = parseFormUrlEncodedBody(request);

    if (!"authorization_code".equals(fields.get("grant_type"))) {
      return new MockResponse().setResponseCode(400);
    }

    final boolean clientIdMatch = BuildConfig.CLIENT_ID.equals(fields.get("client_id"));
    final boolean clientSecretMatch = BuildConfig.CLIENT_SECRET.equals(fields.get("client_secret"));
    if (!clientIdMatch || !clientSecretMatch) {
      return new MockResponse().setResponseCode(401);
    }

    final boolean redirectUriMatch = BuildConfig.REDIRECT_URL.equals(fields.get("redirect_uri"));
    final boolean codeMatch = TestData.CODE.equals(fields.get("code"));
    if (!redirectUriMatch || !codeMatch) {
      return new MockResponse().setResponseCode(400);
    }

    final String responseBody = jsonObject(
        "access_token", TestData.ACCESS_TOKEN,
        "client_id", BuildConfig.CLIENT_ID,
        "expires_in", 3600,
        "token_type", "Bearer",
        "user_id", TestData.ACCOUNT_ID
    ).toString();
    return new MockResponse().setResponseCode(200).setBody(responseBody);
  }

  private static MockResponse accountsResponse() {
    final String responseBody = jsonObject(
        "accounts", jsonArray(
            jsonObject(
                "id", TestData.ACCOUNT_ID,
                "created", "2015-07-15T12:00:00Z",
                "description", "John Smith",
                "type", "uk_prepaid"
            )
        )
    ).toString();
    return new MockResponse().setResponseCode(200).setBody(responseBody);
  }

  private MockResponse balanceResponse() {
    final String responseBody = jsonObject(
        "balance", balanceData.balance().getAmountMinorLong(),
        "currency", balanceData.balance().getCurrencyUnit().getCurrencyCode(),
        "spend_today", balanceData.spentToday().getAmountMinorLong()
    ).toString();
    return new MockResponse().setResponseCode(200).setBody(responseBody);
  }

  private MockResponse transactionsResponse() {
    final List<JSONObject> transactions = new ArrayList<>(transactionsData.size());
    for (TestTransaction transaction : transactionsData) {
      transactions.add(convertTransaction(transaction));
    }
    final String responseBody = jsonObject("transactions", jsonArray(transactions)).toString();
    return new MockResponse().setResponseCode(200).setBody(responseBody);
  }

  private static JSONObject convertTransaction(TestTransaction transaction) {
    final JSONObject json = jsonObject(
        "id", transaction.id(),
        "category", transaction.category().name().toLowerCase(Locale.US),
        "created", DateTimeFormatter.ISO_INSTANT.format(transaction.created()),
        "currency", transaction.amount().getCurrencyUnit().getCurrencyCode(),
        "amount", transaction.amount().getAmountMinorLong(),
        "local_currency", transaction.localAmount().getCurrencyUnit().getCurrencyCode(),
        "local_amount", transaction.localAmount().getAmountMinorLong(),
        "include_in_spending", transaction.includeInSpending(),
        "is_load", transaction.topUp(),
        "notes", Strings.nullToEmpty(transaction.notes()),
        "metadata", convertMetadata(transaction),
        "merchant", convertMerchant(transaction.merchant())
    );
    final DeclineReason declineReason = transaction.declineReason();
    if (declineReason != null) {
      put(json, "decline_reason", declineReason.name().toLowerCase(Locale.US));
    }
    return json;
  }

  private static JSONObject convertMetadata(TestTransaction transaction) {
    // All of the values in the metadata block are strings, even the boolean flags.
    return transaction.hideAmount() ? jsonObject("hide_amount", "true") : jsonObject();
  }

  @Nullable private static JSONObject convertMerchant(@Nullable Merchant merchant) {
    if (merchant == null) {
      return null;
    }
    return jsonObject(
        "id", merchant.id(),
        "name", merchant.name(),
        "online", merchant.online(),
        "logo", Strings.nullToEmpty(merchant.logoUrl()),
        "address", jsonObject(
            "latitude", merchant.address().latitude(),
            "longitude", merchant.address().longitude(),
            "short_formatted", merchant.address().formattedAddress()
        )
    );
  }

  private static Map<String, String> parseFormUrlEncodedBody(RecordedRequest request) {
    try {
      final String body = URLDecoder.decode(request.getBody().readString(Charset.forName("UTF-8")), "UTF-8");
      final Map<String, String> fields = new HashMap<>();
      for (String field : body.split("&")) {
        final String[] split = field.split("=");
        fields.put(split[0], split[1]);
      }
      return fields;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static JSONObject jsonObject(Object... keysAndValues) {
    try {
      final JSONObject body = new JSONObject();
      for (int i = 0; i < keysAndValues.length; i += 2) {
        body.put(keysAndValues[i].toString(), keysAndValues[i + 1]);
      }
      return body;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void put(JSONObject json, String key, Object value) {
    try {
      json.put(key, value);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  private static JSONArray jsonArray(Object... entries) {
    try {
      return new JSONArray(entries);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static JSONArray jsonArray(Collection<?> entries) {
    try {
      return new JSONArray(entries);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
