package org.quickstart.mq.kafka.sample.redefine;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

public class Interceptor {
    @RuntimeType
    public void intercept(@This Object obj,
        @AllArguments Object[] allArguments) {
        System.out.println("after constructor!");
    }
}