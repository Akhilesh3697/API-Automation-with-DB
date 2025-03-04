package com.utilities;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.tree.OverrideCombiner;

public final class PropRepo {

    private static CombinedConfiguration propAggregator = new CombinedConfiguration(new OverrideCombiner());

    public static void appendProperties(String propertiesFile) throws Exception {

        PropertiesConfiguration properties = null;
        try {
            LogManager.getLogger(PropRepo.class).info("Loading property file : " + propertiesFile);
            properties = new PropertiesConfiguration(propertiesFile);
            properties.setDelimiterParsingDisabled(true);
        } catch (ConfigurationException ce) {
            throw new Exception("Unable to load properties", ce);
        }
        if (properties != null) {
            propAggregator.addConfiguration(properties);
        }
    }

    public static void loadAllProperties() throws Exception {
        Properties propFilesList = new Properties();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            URL url = loader.getResource(GlobalProperties.PROPS_LIST);
            propFilesList.load(new FileReader(url.getPath()));
        } catch (IOException e) {
            throw new Exception("Unable to load props-files.txt", e);
        }
        String filesList = propFilesList.getProperty("propFiles");
        LogManager.getLogger(PropRepo.class).info("List of files to load : " + filesList);
        StringTokenizer tokens = new StringTokenizer(filesList, ",");
        while (tokens.hasMoreElements()) {
            String fileName = tokens.nextToken();
            appendProperties(fileName);
        }
    }

    public static String getString(String key) {
        return propAggregator.getString(key);
    }

    public static int getInt(String key) {
        return propAggregator.getInt(key);
    }

    public static boolean getBoolean(String key) {
        return propAggregator.getBoolean(key);
    }

    public static long getLong(String key) {
        return propAggregator.getLong(key);
    }
}
