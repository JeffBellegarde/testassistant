package org.bellegar.assistant.core;

import java.lang.reflect.Field;

public interface DelegatingAssistant {
    public Object createDelegate(Object target, Field field) throws Exception;
}
