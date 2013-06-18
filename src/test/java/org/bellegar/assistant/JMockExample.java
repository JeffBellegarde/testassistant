package org.bellegar.assistant;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.Callable;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RunWithAssistants.class)
public class JMockExample {

    @MockeryAssistant
    private Mockery mockery;

    @Mock
    private Callable<String> callable;

    @Mock
    private Callable<String> callable2;

    @Test
    public void mockAnnotationisRecognized() {
        assertThat(callable, notNullValue(Callable.class));
        assertThat(callable2, notNullValue(Callable.class));
    }

    @Test
    public void mockeryAnnotationIsRecognized() throws Exception {
        assertThat(mockery, notNullValue(Mockery.class));
        mockery.checking(new Expectations() {
            {
                one(callable).call();
            }
        });
        callable.call();

    }

}
