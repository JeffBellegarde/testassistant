package org.bellegar.assistant;

import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class JUnitCoreAssistant {
    public static class RunListenerExtension extends RunListener {
        private Failure failure;

        public Failure getFailure() {
            return failure;
        }

        @Override
        public void testFailure(final Failure failure) throws Exception {
            this.failure = failure;
            super.testFailure(failure);
        }
    }

    public RunListenerExtension run(final Class<?>... classes) {
        final JUnitCore unitCore = new JUnitCore();
        final RunListenerExtension listener = new RunListenerExtension();
        unitCore.addListener(listener);
        unitCore.run(classes);
        return listener;
    }
}
