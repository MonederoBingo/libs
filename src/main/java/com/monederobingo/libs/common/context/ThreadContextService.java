package com.monederobingo.libs.common.context;

import com.monederobingo.libs.common.environments.Environment;

public interface ThreadContextService {

    void initializeContext(Environment env);

    ThreadContext getThreadContext();

    Environment getEnvironment();

    void setThreadContextOnThread(ThreadContext threadContext);
}
