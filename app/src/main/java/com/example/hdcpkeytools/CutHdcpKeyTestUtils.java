
package com.example.hdcpkeytools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class CutHdcpKeyTestUtils {
    private final static int DEFAULT_CARGO_KEY_NUMBER_PER_FILE = 500;
    private final static int DEFAULT_SHIPPING_SAMPLE_KEY_NUMBER_PER_FILE = 10;
    private final static int DEFAULT_SPARE_PARTS_KEY_NUMBER_PER_FILE = 500;
    private final static int DEFAULT_SPARE_KEYS_KEY_NUMBER_PER_FILE = 300;
    private final static int GIEC_HDCP_KEY_LENGTH = 308;
    private final static String DEFAULT_OUTPUT_ROOT_PATH = "/sdcard/HDCP_KEY/";
    private final static String AMLOGIC_HDCP_KEY_FILE_NAME = "HDCP_LIENCE";
    private int mIndexOfKey = 0;
    private String mOrderName;

    public void cutKeyFile(String originalFilePath, String orderName
            , int cargoKeyNumber, int shippingSampleNumber
            , int sparePartsNumber, int spareKeysNumber) {
        cutKeyFile(originalFilePath, orderName, cargoKeyNumber, shippingSampleNumber, sparePartsNumber,spareKeysNumber, DEFAULT_OUTPUT_ROOT_PATH);
    }

    public void cutKeyFile(String originalFilePath, String orderName
            , int cargoKeyNumber, int shippingSampleNumber
            , int sparePartsNumber, int spareKeysNumber, String outputPath) {
        mOrderName = orderName;

        if (!new File(outputPath).exists()) {
            new File(outputPath).mkdir();
        }

        // 按以下顺序截取KEY数据输出到文件
        // 大货KEY -- 船样KEY -- 备件KEY -- 备用KEY
        int dir_sn = 1;

        // 截取出大货KEY的数据
        String cargoKeyDir = outputPath + dir_sn++ + "_大货KEY_" + cargoKeyNumber + "个" + "/";
        getOutputKeyFiles(originalFilePath, cargoKeyNumber, DEFAULT_CARGO_KEY_NUMBER_PER_FILE, cargoKeyDir);

        // 截取出船样KEY的数据
        if (shippingSampleNumber > 0) {
            String shippingSampleKeyDir = outputPath + dir_sn++ + "_船样KEY_" + shippingSampleNumber + "个" + "/";
            getOutputKeyFiles(originalFilePath, cargoKeyNumber + shippingSampleNumber
                    , DEFAULT_SHIPPING_SAMPLE_KEY_NUMBER_PER_FILE, shippingSampleKeyDir);
        }

        // 截取出备件KEY的数据
        if (sparePartsNumber > 0) {
            String sparePartsKeyDir = outputPath + dir_sn++ + "_备品KEY_" + sparePartsNumber + "个" + "/";
            getOutputKeyFiles(originalFilePath, cargoKeyNumber + shippingSampleNumber + sparePartsNumber
                    , DEFAULT_SPARE_PARTS_KEY_NUMBER_PER_FILE, sparePartsKeyDir);
        }

        // 截取出备用KEY的数据
        if (spareKeysNumber > 0) {
            String spareKeysDir = outputPath + dir_sn + "_备用KEY_" + spareKeysNumber + "个" + "/";
            getOutputKeyFiles(originalFilePath, cargoKeyNumber + shippingSampleNumber + sparePartsNumber + spareKeysNumber
                    , DEFAULT_SPARE_KEYS_KEY_NUMBER_PER_FILE, spareKeysDir);
        }

        //计算MD5并输出
    }

    /**
     * 输入原始KEY路径，起始KEY位置、结束KEY位置、每个KEY文件的最大KEY数量、KEY文件的存放路径
     * 生成文件
     */
    private void getOutputKeyFiles(String originalFilePath, int endIndexOfKey, int keyNumberPerFile, String savePath) {
        try {
            // 打开原始Key文件
            RandomAccessFile rafKeyFile = new RandomAccessFile(new File(originalFilePath), "r");
            int keyNumberInTempFile;
            while (mIndexOfKey < endIndexOfKey) {
                //一个一个文件读取
                //计算当前KEY文件中需要包含的KEY数量
                if (mIndexOfKey + keyNumberPerFile <= endIndexOfKey) {
                    keyNumberInTempFile = keyNumberPerFile;
                } else {
                    keyNumberInTempFile = endIndexOfKey - mIndexOfKey;
                }
                outputSingelCargoKeyFile(keyNumberInTempFile, rafKeyFile, savePath);
                //将Key数量校准
                mIndexOfKey = mIndexOfKey + keyNumberInTempFile;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 输入起始坐标，结束坐标，KEY数量，文件名参数，生成文件
     **/
    private void outputSingelCargoKeyFile(int keyNumber, RandomAccessFile rafKeyFile, String savePath) {
        int beginKeyIndex = mIndexOfKey + 1;
        int endKeyIndex = mIndexOfKey + keyNumber;
        int preCountOfHdcpKeySize = 4;  // Amlogic HDCP Key 文件前4位为当前KEY文件包含的KEY的数量
        int sizeOfAllHdcpKey = keyNumber * GIEC_HDCP_KEY_LENGTH;
        int outputFileSize = preCountOfHdcpKeySize + sizeOfAllHdcpKey;
        byte[] bytes = new byte[outputFileSize];

        //获取当前的坐标位置
        int mIndexInRafKeyFile = mIndexOfKey * GIEC_HDCP_KEY_LENGTH;
        try {
            //把当前数量写入bytes数组中的前缀
            for (int i = 0; i < preCountOfHdcpKeySize; i++) {
                bytes[i] = (byte) (keyNumber >> (preCountOfHdcpKeySize - i) * 8);
            }

            //当前坐标+KEY长度*KEY数量
            rafKeyFile.seek(mIndexInRafKeyFile);
            rafKeyFile.read(bytes, preCountOfHdcpKeySize, sizeOfAllHdcpKey);

            if (!new File(savePath).exists()) {
                new File(savePath).mkdir();
            }

            //输出到文件
            RandomAccessFile rafOutputCargoKey = new RandomAccessFile(
                    savePath + AMLOGIC_HDCP_KEY_FILE_NAME + "_"
                            + mOrderName + "_"
                            + beginKeyIndex + "-" + endKeyIndex + "_" + keyNumber + ".bin"
                    , "rws");
            rafOutputCargoKey.write(bytes, 0, outputFileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

