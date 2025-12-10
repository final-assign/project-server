package org.example.db;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PooledDataSource {
    private static BasicDataSource basicDS;
    static {
        try {
            basicDS = new BasicDataSource();
            Properties properties = new Properties();
            InputStream inputStream = new FileInputStream("src/main/resources/config/db.yml");
            properties.load(inputStream);
            basicDS.setDriverClassName(properties.getProperty("DRIVER_CLASS")); //loads the jdbc driver
            basicDS.setUrl(properties.getProperty("DB_CONNECTION_URL"));
            basicDS.setUsername(properties.getProperty("DB_USER"));
            basicDS.setPassword(properties.getProperty("DB_PWD"));
            // Parameters for connection pooling
            basicDS.setInitialSize(15);
            basicDS.setMaxTotal(15);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static DataSource getDataSource() {
        return basicDS;
    }
}
