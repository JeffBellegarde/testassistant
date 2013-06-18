package org.bellegar.assistant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.bellegar.assistant.core.InjectableAssistant;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Assistant(initialisingClass = InjectableAssistant.class)
public @interface Injectable {

}
