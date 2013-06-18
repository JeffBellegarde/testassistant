package org.bellegar.assistant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.bellegar.assistant.impl.MockeryAssistantImpl;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
@Assistant(instantiatingClass = MockeryAssistantImpl.class)
public @interface Mock {

}
