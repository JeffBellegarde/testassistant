package org.bellegar.assistant;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.ExpectationError;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

@RunWith(RunWithAssistants.class)
public class JMockTest {

    @Assistant
    private JUnitCoreAssistant jUnitCoreAssistant;

    @RunWith(RunWithAssistants.class)
    static public class JMockExample2 {

        @MockeryAssistant
        private Mockery mockery;

        @Mock
        private Runnable runnable;

        @Test
        @Ignore
        public void something() throws Exception {
            mockery.checking(new Expectations() {
                {
                    one(runnable).run();
                }
            });
        }
    }

    @Ignore
    @Test
    public void assertIsSatisfiedIsCalled() throws Exception {
        final Failure failure = jUnitCoreAssistant.run(JMockTest.JMockExample2.class).getFailure();
        assertThat(failure, notNullValue(Failure.class));
        failure.getException().printStackTrace();
        assertThat(failure.getException(), is(ExpectationError.class));
    }
}
