package ru.chertkov.logparser.util;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtils {

    private static final Logger logger = Logger.getLogger(PropertiesUtils.class);

    public static void readProperties() throws IOException {
        logger.info("start read config");

        FileInputStream fis = new FileInputStream("config.properties");
        Properties property = new Properties();
        property.load(fis);

        System.setProperty("inputDirectory", property.getProperty("inputDirectory"));
        System.setProperty("outputDirectory", property.getProperty("outputDirectory"));

        logger.info("inputDirectory ->" + System.getProperty("inputDirectory"));
        logger.info("outputDirectory ->" + System.getProperty("outputDirectory"));

        logger.info("finish read config");
    }
}
