package org.bellegar.assistant.impl;

import java.lang.reflect.Field;

import org.bellegar.assistant.core.DelegatingAssistant;

public class FieldTypeInstantiator implements DelegatingAssistant {

    public Object createDelegate(final Object target, final Field field) throws InstantiationException,
            IllegalAccessException {
        return field.getType().newInstance();
    }

}
