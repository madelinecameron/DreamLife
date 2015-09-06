package madelinecameron.dreamlife;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DreamLife.db";
    public static final String SQL_CREATE_CHAR_TBL = "CREATE CharacterItems (" +
            "ID INTEGER, " +
            "Qty INTEGER);";
    public static final String SQL_CREATE_ITEM_TBL = "CREATE Items (" +
            "ID INTEGER UNIQUE PRIMARY_KEY, Name STRING, Type STRING, Cost INTEGER, Boost BLOB, CashGT INTEGER, SciGT INTEGER, " +
            "ReadGT INTEGER, SpeakGT INTEGER, OlderThan INTEGER, Home INTEGER, Edu INTEGER)";

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CHAR_TBL);
        db.execSQL(SQL_CREATE_ITEM_TBL);

        //Load items
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
