package madelinecameron.dreamlife;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.common.io.CharStreams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class DBManager extends SQLiteOpenHelper {
    private static Context context;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DreamLife.db";
    public static final String SQL_CREATE_CHAR_TBL = "CREATE TABLE CharacterItems (" +
            "ID INTEGER UNIQUE PRIMARY KEY, " +
            "Qty INTEGER);";
    public static final String SQL_CREATE_ITEM_TBL = "CREATE TABLE Items (" +
            "ID INTEGER UNIQUE PRIMARY KEY, Name STRING, Display INTEGER, Saleable INTEGER, Effects STRING, Needed STRING);";

    public static final String SQL_CREATE_ACTION_TBL = "CREATE TABLE Actions (ID INTEGER UNIQUE PRIMARY KEY, " +
            "Name STRING, Description STRING, Page STRING, Result STRING, Needed STRING);";

    public static final String SQL_INSERT_ITEM = "INSERT INTO Items VALUES (%d, %s, %s, %s);";

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CHAR_TBL);
        db.execSQL(SQL_CREATE_ITEM_TBL);

        try {
            //Load items
            for(String f : context.getAssets().list("items")) {
                InputStreamReader is = new InputStreamReader(context.getAssets().open("items/" + f));
                JSONArray items = (JSONArray)(new JSONObject(CharStreams.toString(is)).get("items"));

                for(int i = 0; i < items.length(); i++) {
                    Log.d("Dreamlife", "a");
                    JSONObject selectedObj = items.getJSONObject(i);

                    Integer id = selectedObj.getInt("ID");
                    String name = selectedObj.getString("Name");
                    Boolean display = selectedObj.getBoolean("Display");
                    Boolean saleable = selectedObj.getBoolean("Saleable");
                    String effects = selectedObj.getJSONObject("Effects")
                            .toString()
                            .replace("\"", "\\\'");


                    db.execSQL(String.format(SQL_INSERT_ITEM, id,
                            "\"" + name + "\"",
                            display,
                            saleable,
                            "\"" + effects + "\""));
                }

            }

            //Load actions
            for(String f : context.getAssets().list("action")) {
                Log.d("DreamLife", f);
            }
        }
        catch(Exception e) {
            Log.d("DreamLife", e.toString());
        }

        db.execSQL(SQL_CREATE_ACTION_TBL);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
