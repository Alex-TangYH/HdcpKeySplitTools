package com.example.hdcpkeytools;

import java.util.ArrayList;

public class ConfigurationBean {
    private static String CONFIG_SPLIT_REGEX = "=";

    public ConfigurationBean(String configurationFilePath) {
        //读取配置文件
        ArrayList<String> arrayList = FileUtils.readFileByLine(configurationFilePath);

        //解析配置文件信息到Bean
        initBean(arrayList);
    }

    public void initBean(ArrayList<String> arrayList) {
        for (String configLine :
                arrayList) {
            String[] configItem = configLine.split(CONFIG_SPLIT_REGEX);
            String var = "-1";
            String value = "-1";
            if (configItem.length > 1) {
                var = configItem[0].trim();
                value = configItem[1].trim();
            }
            switch (var) {
                case "orderName":
                    setOrderName(value);
                    break;
                case "outputPath":
                    setOutputPath(value);
                    break;
                case "cargoKeyNumber":
                    setCargoKeyNumber(Integer.valueOf(value));
                    break;
                case "shippingSampleNumber":
                    setShippingSampleNumber(Integer.valueOf(value));
                    break;
                case "sparePartsNumber":
                    setSparePartsNumber(Integer.valueOf(value));
                    break;
                case "spareKeysNumber":
                    setSpareKeysNumber(Integer.valueOf(value));
                    break;
            }
        }
    }

    private String orderName;
    private String outputPath;
    private int cargoKeyNumber;
    private int shippingSampleNumber;
    private int sparePartsNumber;
    private int spareKeysNumber;

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public int getCargoKeyNumber() {
        return cargoKeyNumber;
    }

    public void setCargoKeyNumber(int cargoKeyNumber) {
        this.cargoKeyNumber = cargoKeyNumber;
    }

    public int getShippingSampleNumber() {
        return shippingSampleNumber;
    }

    public void setShippingSampleNumber(int shippingSampleNumber) {
        this.shippingSampleNumber = shippingSampleNumber;
    }

    public int getSparePartsNumber() {
        return sparePartsNumber;
    }

    public void setSparePartsNumber(int sparePartsNumber) {
        this.sparePartsNumber = sparePartsNumber;
    }

    public int getSpareKeysNumber() {
        return spareKeysNumber;
    }

    public void setSpareKeysNumber(int spareKeysNumber) {
        this.spareKeysNumber = spareKeysNumber;
    }
}

