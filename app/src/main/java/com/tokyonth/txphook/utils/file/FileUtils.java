package com.tokyonth.txphook.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileUtils {

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
            writer.close();
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
