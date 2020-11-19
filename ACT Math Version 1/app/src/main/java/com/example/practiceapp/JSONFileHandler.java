package com.example.practiceapp;

import android.content.Context;
import org.json.JSONObject;
import java.io.File;
import java.io.InputStream;

public class JSONFileHandler {
    private Context currentContext;
    private JSONObject jsonObject;
    private String JSONFileName;
    private File JSONFile;
    private String jsonString;

    public JSONFileHandler(Context currentContext) {
        this.currentContext = currentContext;
    }

    public JSONFileHandler(Context currentContext, String fileName) {
        this.currentContext = currentContext;
        this.JSONFileName = fileName;
    }

    public String loadJSONFromAsset() {
        try {
            InputStream is = this.currentContext.getAssets().open(this.JSONFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            this.jsonString = new String(buffer, "UTF-8");
            is.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return this.jsonString;
    }
}
