package org.bellegar.assistant;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RunWithAssistants.class)
public class InjectableTest {
    public static class Service {

    }

    public static class NeedsInjection {
        Service service;

        Service service2;

        Service service3;

        public void setSomething(final Service service) {
            this.service = service;

        }

        public void setSomethingElse(final Service service2) {
            this.service2 = service2;
        }

        public void setNoSuchField(final Service service3) {
            this.service3 = service3;

        }
    }

    @Assistant
    private Service something;

    @Injectable
    private NeedsInjection injectable;

    @Injectable
    private final NeedsInjection alreadyCreated = new NeedsInjection();

    @Assistant
    private Service somethingElse;

    @Test
    public void injectableIsCreated() {
        assertThat(injectable, notNullValue());
        assertThat(injectable.service, sameInstance(something));
        assertThat(injectable.service2, sameInstance(somethingElse));
        assertThat(injectable.service3, nullValue());
        assertThat(alreadyCreated, notNullValue());
        assertThat(alreadyCreated.service, sameInstance(something));
        assertThat(alreadyCreated.service2, sameInstance(somethingElse));
        assertThat(alreadyCreated.service3, nullValue());
        assertThat(somethingElse, notNullValue());
    }
}
