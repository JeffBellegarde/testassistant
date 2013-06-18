package org.bellegar.assistant.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.bellegar.assistant.core.InitialisingAssistant;

public class NoOpInitializer implements InitialisingAssistant {

    public void initialise(final Object target, final Field field) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
    }

}
