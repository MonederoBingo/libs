package com.monederobingo.libs.common.environments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentFactory
{

    private final Environment devEnvironment;
    private final Environment unitTestEnvironment;
    private final Environment functionalTestEnvironment;
    private final Environment uatEnvironment;
    private final Environment prodEnvironment;

    @Autowired
    public EnvironmentFactory(
            DevEnvironment devEnvironment,
            UnitTestEnvironment unitTestEnvironment,
            FunctionalTestEnvironment functionalTestEnvironment,
            UATEnvironment uatEnvironment,
            ProdEnvironment prodEnvironment)
    {
        this.devEnvironment = devEnvironment;
        this.unitTestEnvironment = unitTestEnvironment;
        this.functionalTestEnvironment = functionalTestEnvironment;
        this.uatEnvironment = uatEnvironment;
        this.prodEnvironment = prodEnvironment;
    }

    public Environment getDevEnvironment()
    {
        return devEnvironment;
    }

    public Environment getUnitTestEnvironment()
    {
        return unitTestEnvironment;
    }

    public Environment getFunctionalTestEnvironment()
    {
        return functionalTestEnvironment;
    }

    public Environment getUatEnvironment()
    {
        return uatEnvironment;
    }

    public Environment getProdEnvironment()
    {
        return prodEnvironment;
    }
}
