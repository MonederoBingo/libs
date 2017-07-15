package com.monederobingo.libs.common.environments;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UnitTestEnvironment extends Environment
{
    @Value("${db_driver}")
    private String dbDriver;

    @Value("${db_driver_class}")
    private String dbDriverClass;

    @Value("${unit_test.db_url}")
    private String dbUrl;

    @Value("${unit_test.db_user}")
    private String dbUser;

    @Value("${unit_test.db_password}")
    private String dbPassword;

    @Value("${unit_test.images_dir}")
    private String imagesDir;

    @Value("${unit_test.client_url}")
    private String clientUrl;

    @Value("${db_test_schema}")
    private String schema;

    public UnitTestEnvironment()
    {
    }

    public UnitTestEnvironment(String dbDriver, String dbDriverClass, String dbUrl, String dbUser, String dbPassword)
    {
        this.dbDriver = dbDriver;
        this.dbDriverClass = dbDriverClass;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    @Override
    public String getDatabasePath()
    {
        return dbDriver + dbUrl;
    }

    public String getDbUrl()
    {
        return dbUrl;
    }

    public String getDatabaseDriverClass()
    {
        return dbDriverClass;
    }

    public String getDatabaseUsername()
    {
        return dbUser;
    }

    public String getDatabasePassword()
    {
        return dbPassword;
    }

    public String getImageDir()
    {
        return imagesDir;
    }

    @Override public String getSchema()
    {
        return schema;
    }

    @Override public String getURIPrefix()
    {
        return "";
    }

    public String getClientUrl()
    {
        return clientUrl;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof DevEnvironment))
        {
            return false;
        }
        DevEnvironment that = (DevEnvironment) obj;
        return getDatabasePath().equals(that.getDatabasePath());
    }

    @Override
    public int hashCode()
    {
        return getDatabasePath().hashCode();
    }
}
