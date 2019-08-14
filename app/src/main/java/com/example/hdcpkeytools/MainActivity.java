package com.example.hdcpkeytools;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv_centerText = findViewById(R.id.tv_centerText);
        tv_centerText.setText("准备生成HDCP KEY生产资料");
        ConfigurationBean configurationBean = readConfiguration();

        if (configurationBean == null ){
            Toast.makeText(this, "找不到配置文件：udisk/HDCP_OR_KEY/config.ini", Toast.LENGTH_LONG).show();
            tv_centerText.setText("找不到配置文件：udisk/HDCP_OR_KEY/config.ini");
            return;
        }

        String path = new FileUtils(this).searchFileInHDCPPath("or_key.bin");
        if (path == null) {
            Toast.makeText(this, "找不到文件：udisk/HDCP_OR_KEY/or_key.bin", Toast.LENGTH_LONG).show();
            tv_centerText.setText("找不到文件：udisk/HDCP_OR_KEY/or_key.bin");
        } else {
            String udiskPath = path.split("HDCP_OR_KEY/or_key.bin")[0];
            String outputPath = udiskPath + Objects.requireNonNull(configurationBean).getOutputPath();

            new CutHdcpKeyTestUtils()
                    .cutKeyFile(path
                            , configurationBean.getOrderName()
                            , configurationBean.getCargoKeyNumber()
                            , configurationBean.getShippingSampleNumber()
                            , configurationBean.getSparePartsNumber()
                            , configurationBean.getSpareKeysNumber()
                            , outputPath);
            new Md5Utils().getMd5OfDirToFile(outputPath, udiskPath + "MD5.txt");
            //移动MD5到输出的目录中
            new File(udiskPath + "MD5.txt").renameTo(new File(outputPath + "MD5.txt"));
            Toast.makeText(this, "HDCP KEY资料已生成，存放路径为："+outputPath, Toast.LENGTH_LONG).show();
            tv_centerText.setText("HDCP KEY资料已生成，存放路径为："+outputPath);
        }
    }

    private ConfigurationBean readConfiguration() {
        //查找取配置文件
        String configurationPath = new FileUtils(this).searchFileInHDCPPath("config.ini");
        if (configurationPath == null) {
            return null;
        }
        return new ConfigurationBean(configurationPath);
    }
}
