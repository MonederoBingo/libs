package com.monederobingo.libs.common.context;

import com.monederobingo.libs.common.environments.Environment;

public class ThreadContext {
    private Environment _environment;

    public Environment getEnvironment() {
        return _environment;
    }

    public void setEnvironment(Environment environment) {
        _environment = environment;
    }
}
