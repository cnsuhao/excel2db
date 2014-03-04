package com.sltalk.cfg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

/**
 * Created by h_hu on 14-3-4.
 */
public class JsonLoader {

    static public JSONObject loadFromPath(String filePath) {
        File file = new File(filePath);
        if(!file.exists())
            return null;

        try {
            String source = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.trim();
                if(line.startsWith("//"))
                    continue;

                source += line;
                source += "\n";
            }

            JSONObject obj = new JSONObject(source);
            return obj;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
