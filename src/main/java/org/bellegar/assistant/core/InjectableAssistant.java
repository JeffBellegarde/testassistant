package org.bellegar.assistant.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class InjectableAssistant implements InitialisingAssistant {

    public void initialise(final Object testObject, final Field field) throws Exception {
        for (final Method method : field.getType().getMethods()) {
            final Field testField = getTargetObjectFieldIfPassableToSetterMethod(testObject, method);
            if (testField != null) {
                testField.setAccessible(true);
                method.invoke(field.get(testObject), testField.get(testObject));
                return;
            }
        }
        for (final Field targetField : field.getType().getDeclaredFields()) {
            targetField.setAccessible(true);
            if (!Modifier.isStatic(targetField.getModifiers()) && targetField.get(field.get(testObject)) == null) {
                final Field testField = getTargetObjectFieldIfAssisgnableToField(testObject, targetField);
                if (testField != null) {
                    testField.setAccessible(true);
                    targetField.set(field.get(testObject), testField.get(testObject));
                    return;
                }
            }
        }
    }

    private Field getTargetObjectFieldIfAssisgnableToField(final Object testObject, final Field targetField) {
        final Field testField = getField(testObject.getClass(), targetField.getName());
        if (testField != null) {
            return targetField.getType().isAssignableFrom(testField.getType()) ? testField : null;
        }
        return null;
    }

    private Field getTargetObjectFieldIfPassableToSetterMethod(final Object testObject, final Method method) {
        final String name = method.getName();
        if (name.startsWith("set") && method.getParameterTypes().length == 1) {
            final Field testField = getField(testObject.getClass(), name.substring(3, 4).toLowerCase()
                    + name.substring(4));
            if (testField != null) {
                return isMethodSettableFromField(method, testField) ? testField : null;
            }
        }
        return null;
    }

    private boolean isMethodSettableFromField(final Method method, final Field testField) {
        return method.getParameterTypes()[0].isAssignableFrom(testField.getType());
    }

    private Field getField(final Class<? extends Object> testClass, final String name) {
        try {
            return testClass.getDeclaredField(name);
        } catch (final NoSuchFieldException ex) {
            return null;
        }

    }
}
