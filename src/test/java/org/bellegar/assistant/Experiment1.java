package org.bellegar.assistant;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RunWithAssistants.class)
public class Experiment1 {

    @Assistant
    private String value;

    @Assistant
    private final Integer alreadyCreated = 42;

    @Test
    public void valueIsAssigned() {
        assertThat(value, notNullValue(String.class));
        assertThat(alreadyCreated, equalTo(42));
    }
}
