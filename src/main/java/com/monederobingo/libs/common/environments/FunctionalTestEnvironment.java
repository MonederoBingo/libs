package com.monederobingo.libs.common.environments;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FunctionalTestEnvironment extends Environment
{

    @Value("${db_savepoint_driver}")
    private String dbDriver;

    @Value("${db_savepoint_driver_class}")
    private String dbDriverClass;

    @Value("${functional_test.db_url}")
    private String dbUrl;

    @Value("${functional_test.db_user}")
    private String dbUser;

    @Value("${functional_test.db_password}")
    private String dbPassword;

    @Value("${functional_test.images_dir}")
    private String imagesDir;

    @Value("${functional_test.client_url}")
    private String clientUrl;

    @Override
    public String getDatabasePath()
    {
        return dbDriver + dbUrl;
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
