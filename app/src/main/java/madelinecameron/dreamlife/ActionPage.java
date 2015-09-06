package madelinecameron.dreamlife;

import android.content.Context;
import android.util.Xml;
import android.view.View;
import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by madel on 8/30/2015.
 */
public class ActionPage {
    private JSONObject actionList;

    public ActionPage(Context context, PageType type) {
        String fileBasename = "home";

        switch(type) {
            case PERSONAL:
                fileBasename = "personal";
                break;
            case EDUCATION:
                fileBasename = "education";
                break;
            case WORK:
                fileBasename = "work";
                break;
            case SHOP:
                fileBasename = "shop";
                break;
        }

        try {
            InputStream inputStream = context.getAssets().open(fileBasename + ".json");
            byte[] buffer = new byte[inputStream.available()];
            if(inputStream.read(buffer) > 0) {
                actionList = new JSONObject(new String(buffer, "UTF-8"));
            }
            inputStream.close();


            return;
        }
        catch(IOException ioE) {
            ioE.printStackTrace();
            return;
        }
        catch(JSONException jsonE) {
            jsonE.printStackTrace();
            return;
        }
    }

    public ArrayAdapter<String> updateOrCreateList() {
        return null;
    }
}
