package com.example.hdcpkeytools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Objects;


public class Md5Utils {
    private ArrayList<String> md5Array = new ArrayList<>();
    private String mRootPath;

    /**
     * 将所有在rootPath目录下的文件的MD5计算出，并连同路径输出到outputFilePath文件中
     */
    public void getMd5OfDirToFile(String rootPath,String outputFilePath) {
        mRootPath= rootPath;
        getDirMD5(new File(mRootPath));
        printArrayListToFile(md5Array, outputFilePath);
    }

    //遍历文件夹
    private void getDirMD5(File rootFile) {
        if (!rootFile.exists()) {
            return;
        }
        if (rootFile.isDirectory()) {
            File[] childFiles = rootFile.listFiles();
            for (File childFile :
                    Objects.requireNonNull(childFiles)) {
                getDirMD5(childFile);
            }
        } else {
            String md5 = getFileMD5(rootFile);
            md5Array.add(rootFile.getAbsolutePath().replace(mRootPath,"") + "        " + md5);
        }
    }

    // 计算指定文件的MD5
    private static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        byte [] buffer= new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }

    //输出ArrayList到文本
    private void printArrayListToFile(ArrayList<String> stringArray, String outputFilePath) {
        //封装目的地
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath));
            //遍历集合
            for (String s : stringArray) {
                //写数据
                bw.write(s);
                bw.newLine();
                bw.flush();
            }
            //释放资源
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
