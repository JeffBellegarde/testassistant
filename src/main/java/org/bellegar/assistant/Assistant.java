package org.bellegar.assistant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.bellegar.assistant.core.AfterMethodAssistant;
import org.bellegar.assistant.core.DelegatingAssistant;
import org.bellegar.assistant.core.InitialisingAssistant;
import org.bellegar.assistant.impl.FieldTypeInstantiator;
import org.bellegar.assistant.impl.NoOpAfterMethod;
import org.bellegar.assistant.impl.NoOpInitializer;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.ANNOTATION_TYPE })
public @interface Assistant {

    // Class<?> instanceClass() default Object.class;

    Class<? extends DelegatingAssistant> instantiatingClass() default FieldTypeInstantiator.class;

    Class<? extends InitialisingAssistant> initialisingClass() default NoOpInitializer.class;

    Class<? extends AfterMethodAssistant> afterMethodClass() default NoOpAfterMethod.class;

}
