package site.binghai.store.tools;

/**
 * Created by IceSea on 2018/4/8.
 * GitHub: https://github.com/IceSeaOnly
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class IoUtils {
    public static String ReadCH(String name) {
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(name);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), "utf-8");
                BufferedReader reader = new BufferedReader(read);
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                read.close();
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }

    public static void WriteCH(String filename, String stringContent) {
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
            BufferedWriter writer = new BufferedWriter(write);
            writer.write(stringContent);
            writer.close();
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
