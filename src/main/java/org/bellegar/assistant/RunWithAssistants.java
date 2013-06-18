package org.bellegar.assistant;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class RunWithAssistants extends BlockJUnit4ClassRunner {

    private final Map<Object, Map<Class<?>, Object>> assistants = new HashMap<Object, Map<Class<?>, Object>>();

    private final List<AssistantWrapper> assistantWrappers;

    public RunWithAssistants(final Class<?> klass) throws InitializationError, InstantiationException,
            IllegalAccessException {
        super(klass);
        assistantWrappers = createAssistantWrappers(klass);
    }

    private List<AssistantWrapper> createAssistantWrappers(final Class<?> klass) {
        final List<AssistantWrapper> assistantWrappers = klass.getSuperclass() == null ? new LinkedList<AssistantWrapper>()
                : createAssistantWrappers(klass.getSuperclass());
        for (final Field field : klass.getDeclaredFields()) {
            final Assistant assistedFieldAnnotation = getAssistedFieldAnnotation(field);
            if (assistedFieldAnnotation != null) {
                field.setAccessible(true);
                assistantWrappers.add(new AssistantWrapper(field, assistedFieldAnnotation));
            }
        }
        return assistantWrappers;
    }

    private class AssistantWrapper {
        private final Field field;
        private final Assistant assistant;

        public AssistantWrapper(final Field field, final Assistant assistant) {
            this.field = field;
            this.assistant = assistant;
        }

        public void afterMethod(final Object target) throws Exception {
            createAssistant(target, assistant, assistant.afterMethodClass()).afterMethod(target);
        }

        public void create(final Object target) throws Exception {
            if (field.get(target) == null) {
                field.set(target, getInitialValue(target));
            }
        }

        private <T> T createAssistant(final Object target, final Assistant assistedFieldAnnotation, final Class<T> klass)
                throws InstantiationException, IllegalAccessException {

            T assistant = getStoredAssistant(target, klass);
            if (assistant == null) {
                assistant = klass.newInstance();
                storeAssistant(target, klass, assistant);
            }
            return assistant;
        }

        @SuppressWarnings("unchecked")
        private <T> T getStoredAssistant(final Object target, final Class<T> instanceClass) {
            return (T) getAssistants(target).get(instanceClass);
        }

        private void storeAssistant(final Object target, final Class<?> instanceClass, final Object assistant) {
            getAssistants(target).put(instanceClass, assistant);
        }

        private Map<Class<?>, Object> getAssistants(final Object target) {
            Map<Class<?>, Object> targetAssistants = assistants.get(target);
            if (targetAssistants == null) {
                targetAssistants = new HashMap<Class<?>, Object>();
                assistants.put(target, targetAssistants);
            }
            return targetAssistants;
        }

        private Object getInitialValue(final Object target) throws Exception {
            return createAssistant(target, assistant, assistant.instantiatingClass()).createDelegate(target, field);
        }

        public void deinitialize(final Object target) throws IllegalArgumentException, IllegalAccessException {
            field.set(target, null);
        }

        public void initialize(final Object target) throws Exception {
            createAssistant(target, assistant, assistant.initialisingClass()).initialise(target, field);

        }
    }

    private Assistant getAssistedFieldAnnotation(final Field field) {
        if (field.getAnnotation(Assistant.class) != null) {
            return field.getAnnotation(Assistant.class);
        }
        for (final Annotation annotation : field.getAnnotations()) {
            if (annotation.annotationType().getAnnotation(Assistant.class) != null) {
                return annotation.annotationType().getAnnotation(Assistant.class);
            }
        }
        return null;
    }

    static abstract private class FieldBasedStatement extends Statement {
        private final Statement next;
        private final List<AssistantWrapper> assistantWrapper;
        protected final Object target;
        private final Statement previous;

        public FieldBasedStatement(final Statement previous, final Statement next,
                final List<AssistantWrapper> assistantFields, final Object target) {
            this.previous = previous;
            this.next = next;
            this.assistantWrapper = assistantFields;
            this.target = target;

        }

        @Override
        public void evaluate() throws Throwable {
            if (previous != null) {
                previous.evaluate();
            }
            for (final AssistantWrapper wrapper : assistantWrapper) {
                processWrapper(wrapper);
            }
            if (next != null) {
                next.evaluate();
            }
        }

        abstract protected void processWrapper(AssistantWrapper wrapper) throws Throwable;

    }

    @Override
    protected Statement withBefores(final FrameworkMethod method, final Object target, final Statement statement) {
        return new InitializeAssistanteStatement(new CreateAssistantStatement(assistantWrappers, target), super
                .withBefores(method, target, statement), assistantWrappers, target);
    }

    static private class CreateAssistantStatement extends FieldBasedStatement {

        CreateAssistantStatement(final List<AssistantWrapper> assistantFields, final Object target) {
            super(null, null, assistantFields, target);
        }

        @Override
        protected void processWrapper(final AssistantWrapper wrapper) throws Exception {
            wrapper.create(target);
        }

    }

    static private class InitializeAssistanteStatement extends FieldBasedStatement {

        public InitializeAssistanteStatement(final Statement previous, final Statement next,
                final List<AssistantWrapper> assistantFields, final Object target) {
            super(previous, next, assistantFields, target);
        }

        @Override
        protected void processWrapper(final AssistantWrapper wrapper) throws Throwable {
            wrapper.initialize(target);
        }

    }

    @Override
    protected Statement methodInvoker(final FrameworkMethod method, final Object test) {
        return new AfterMethodAssistantStatement(super.methodInvoker(method, test), assistantWrappers, test);
    }

    static private class AfterMethodAssistantStatement extends FieldBasedStatement {

        public AfterMethodAssistantStatement(final Statement previous, final List<AssistantWrapper> assistantFields,
                final Object target) {
            super(previous, null, assistantFields, target);
        }

        @Override
        protected void processWrapper(final AssistantWrapper wrapper) throws Throwable {
            wrapper.afterMethod(target);
        }

    }

    @Override
    protected Statement withAfters(final FrameworkMethod method, final Object target, final Statement statement) {
        return new DeinitializeAssistant(super.withAfters(method, target, statement), new ClearAsssitantsStatement(
                target), assistantWrappers, target);
    }

    static private class DeinitializeAssistant extends FieldBasedStatement {
        DeinitializeAssistant(final Statement previous, final Statement next,
                final List<AssistantWrapper> assistantFields, final Object target) {
            super(previous, next, assistantFields, target);
        }

        @Override
        protected void processWrapper(final AssistantWrapper field) throws IllegalArgumentException,
                IllegalAccessException, InstantiationException {
            field.deinitialize(target);
        }

        @Override
        public void evaluate() throws Throwable {
            super.evaluate();
        }
    }

    private class ClearAsssitantsStatement extends Statement {

        private final Object target;

        public ClearAsssitantsStatement(final Object target) {
            this.target = target;
        }

        @Override
        public void evaluate() throws Throwable {
            assistants.remove(target);

        }

    }
}
