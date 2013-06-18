package org.bellegar.assistant.core;

import java.lang.reflect.Field;

public interface InitialisingAssistant {
    void initialise(Object target, Field field) throws Exception;
}
