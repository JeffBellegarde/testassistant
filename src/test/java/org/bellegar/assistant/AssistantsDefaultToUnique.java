package org.bellegar.assistant;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RunWithAssistants.class)
public class AssistantsDefaultToUnique {

    @Assistant
    private String string1;

    @Assistant
    private String string2;

    @Test
    public void areUnique() {
        assertThat(string1, not(sameInstance(string2)));
    }
}
