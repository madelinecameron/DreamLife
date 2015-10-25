package madelinecameron.dreamlife.Misc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.common.io.CharStreams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

public class DBManager extends SQLiteOpenHelper {
    private static Context context;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DreamLife.db";

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;

        if(!context.getDatabasePath("DreamLife.db").exists()) {
            createDB();
        }
        else {
            openDB();
        }
    }
    public void onCreate(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void createDB() {
        this.getReadableDatabase();

        try {
            importDB();
        }
        catch(Exception e) {
            Log.d("DreamLife", "Err creating DB");
        }
    }
    public SQLiteDatabase openDB() {
        return SQLiteDatabase.openDatabase(context.getDatabasePath(DATABASE_NAME).getAbsolutePath(), null, 0);
    }
    private void importDB() {
        try {
            //Open your local db as the input stream
            InputStream myInput = context.getAssets().open("db/dreamlife.db");
            Log.d("DreamLife", String.valueOf(myInput.available()));

            //Open the empty db as the output stream
            Log.d("DreamLife", context.getDatabasePath(DATABASE_NAME).getName());
            Log.d("DreamLife", context.getDatabasePath(DATABASE_NAME).getAbsolutePath());
            OutputStream myOutput = new FileOutputStream(context.getDatabasePath(DATABASE_NAME));

            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int len;
            while ((len = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, len);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();

            Log.d("DreamLife", "Loaded");
        }
        catch(Exception e) {
            Log.e("DreamLife", e.toString());
        }
    }
}
