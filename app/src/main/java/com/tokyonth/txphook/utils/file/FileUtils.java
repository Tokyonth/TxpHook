package com.tokyonth.txphook.utils.file;

import android.net.Uri;
import android.util.Log;

import com.tokyonth.txphook.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    private void openUriForRead(Uri uri) {
        if (uri == null)
            return;
        try {
            //获取输入流
            InputStream inputStream = App.Companion.getContext().getContentResolver().openInputStream(uri);
            byte[] readContent = new byte[1024];
            int len = 0;
            do {
                //读文件
                len = inputStream.read(readContent);
                if (len != -1) {
                    Log.d("test", "read content:" + new String(readContent).substring(0, len));
                }
            } while (len != -1);
            inputStream.close();
        } catch (Exception e) {
            Log.d("test", e.getLocalizedMessage());
        }
    }

    public static String read(File file) {
        if (!file.exists())
            return "";
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] b = new byte[(int) file.length()];
            int c = fis.read(b);
            return new String(b, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void write(String content, String dirPath, String fileName) {
        File file = createFile(dirPath, fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File createFile(String dirPath, String fileName) {
        String filePath;
        if (null == dirPath || dirPath.isEmpty()) {
            filePath = fileName;
        } else {
            if (dirPath.endsWith("/")) {
                filePath = dirPath + fileName;
            } else {
                filePath = dirPath + "/" + fileName;
            }
            File dir = new File(dirPath);
            if (!dir.exists()) {
                boolean b = dir.mkdirs();
            }
        }
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                boolean b = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
