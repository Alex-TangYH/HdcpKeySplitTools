package com.example.hdcpkeytools;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        readConfiguration();
        String path = new FileUtils(this).searchFileInFactoryTestPath("or_key.bin");
        if (path == null) {
            Toast.makeText(this, "找不到文件：udisk/HDCP_OR_KEY/or_key.bin", Toast.LENGTH_LONG).show();
        } else {
            String udiskPath = path.split("HDCP_OR_KEY/or_key.bin")[0];
            String outputPath = udiskPath + "HDCP_KEY/";

            //TODO 通过配置文件读取参数
            new CutHdcpKeyTestUtils()
                    .cutKeyFile(path
                            , "GX1000WHITE-07ASS-G2Y-10000"
                            , 10000
                            , 0
                            , 100
                            , 200
                            , outputPath);
            new Md5Utils().getMd5OfDirToFile(outputPath, udiskPath + "/md5.txt");
        }
    }
}
