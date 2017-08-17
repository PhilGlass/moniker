package glass.phil.monzo.model.transactions;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import javax.inject.Inject;

final class TransactionsDb extends SQLiteOpenHelper {
  private static final int VERSION = 1;

  @Inject TransactionsDb(Context context, @DbName @Nullable String dbName) {
    super(context, dbName, null, VERSION);
  }

  @Override public void onConfigure(SQLiteDatabase db) {
    db.setForeignKeyConstraintsEnabled(true);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE transactions (" +
        "id TEXT NOT NULL PRIMARY KEY," +
        "category TEXT NOT NULL," +
        "created  INTEGER NOT NULL," +
        "currency TEXT NOT NULL," +
        "amount INTEGER NOT NULL," +
        "local_currency TEXT NOT NULL," +
        "local_amount INTEGER NOT NULL," +
        "decline_reason TEXT," +
        "include_in_spending INTEGER NOT NULL," +
        "top_up INTEGER NOT NULL," +
        "hide_amount INTEGER NOT NULL," +
        "notes TEXT," +
        "merchant INTEGER REFERENCES merchants(id))");

    db.execSQL("CREATE TABLE merchants (" +
        "id TEXT NOT NULL PRIMARY KEY," +
        "name TEXT NOT NULL," +
        "online INTEGER NOT NULL," +
        "logo_url TEXT," +
        "address TEXT NOT NULL," +
        "latitude FLOAT NOT NULL," +
        "longitude FLOAT NOT NULL)");

    db.execSQL("CREATE INDEX transactions_created ON transactions(created)");
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
