package org.bellegar.assistant.impl;

import java.lang.reflect.Field;

import org.bellegar.assistant.Mock;
import org.bellegar.assistant.MockeryAssistant;
import org.bellegar.assistant.core.AfterMethodAssistant;
import org.bellegar.assistant.core.DelegatingAssistant;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

public class MockeryAssistantImpl implements AfterMethodAssistant, DelegatingAssistant {

    private Mockery mockery;

    public void afterMethod(final Object target) throws IllegalArgumentException, IllegalAccessException {
        mockery.assertIsSatisfied();
    }

    public Object createDelegate(final Object target, final Field field) {
        if (field.getAnnotation(MockeryAssistant.class) != null) {
            return createMockery();
        } else if (field.getAnnotation(Mock.class) != null) {
            return createMockery().mock(field.getType(), field.getName());
        } else {
            return null;
        }
    }

    private Mockery createMockery() {
        if (mockery == null) {
            mockery = new Mockery();
            mockery.setImposteriser(ClassImposteriser.INSTANCE);
        }
        return mockery;
    }
}
