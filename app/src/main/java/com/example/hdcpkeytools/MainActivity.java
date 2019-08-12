package com.example.hdcpkeytools;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new CutHdcpKeyTestUtils()
                .cutKeyFile("/sdcard/HDCP_KEY/or_key.bin"
                        , "GX1000WHITE-07ASS-G2Y-10000"
                        , 10000
                        , 0
                        , 100
                        , 200);
    }
}
