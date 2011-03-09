package edu.gemini.aspen.gmp.commands.model;

import com.google.common.collect.ImmutableList;
import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.DefaultConfiguration;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.gmp.commands.test.CompletionListenerMock;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static org.junit.Assert.assertEquals;

/**
 * Test class for the ActionMessageBuilder
 */
public abstract class ActionMessageBuilderTestBase extends ActionMessageTestBase {

    protected Configuration config;
    protected Action action;

    protected abstract ActionMessageBuilder getActionMessageBuilder();

    @Before
    public void buildConfiguration() {
        config = DefaultConfiguration.configurationBuilder()
                .withPath(configPath("X.val1"), "x1")
                .withPath(configPath("X.val2"), "x2")
                .withPath(configPath("X.val3"), "x3")

                .withPath(configPath("X:A.val1"), "xa1")
                .withPath(configPath("X:A.val2"), "xa2")
                .withPath(configPath("X:A.val3"), "xa3")

                .withPath(configPath("X:B.val1"), "xb1")
                .withPath(configPath("X:B.val2"), "xb2")
                .withPath(configPath("X:B.val3"), "xb3")

                .withPath(configPath("X:C.val1"), "xc1")
                .withPath(configPath("X:C.val2"), "xc2")
                .withPath(configPath("X:C.val3"), "xc3").build();

        action = new Action(new Command(SequenceCommand.ABORT, Activity.START, config), new CompletionListenerMock());
    }

    /**
     * Test the action messages produced by this builder
     *
     * @param a Action to be converted into a message
     * @return an action Message produced by this builder
     */
    protected ActionMessage getActionMessage(Action a) {
        return getActionMessageBuilder().buildActionMessage(a);
    }

    /**
     * Test the message building when specifying sub configurations
     * to match
     */
    @Test
    public void testBuildMessageWithSecondLevelConfigPath() {
        List<ConfigPath> configPaths = ImmutableList.of(configPath("X:A"),
                configPath("X:B"),
                configPath("X:C"));

        testConfigPaths(action, configPaths, 3);
    }

    /**
     * Test the message building when specifying sub configurations
     * to match
     */
    @Test
    public void testBuildMessageWithTopLevelPath() {
        testConfigPaths(action, ImmutableList.of(configPath("X")), 12);
    }


    /**
     * Test the message building when specifying sub configurations
     * to match
     */
    @Test
    public void testBuildMessageWithNullConfigPath() {
        //finally, test with a null Config Path.
        ActionMessage am = getActionMessageBuilder().buildActionMessage(action, null);

        Map<String, Object> data = am.getDataElements();
        //null should be interpreted as no-filter, so all the stuff should be there
        assertEquals(12, data.keySet().size());
        assertConfigurationKeys(data);
    }

    private void assertConfigurationKeys(Map<String, Object> data) {
        for (String keys : data.keySet()) {
            ConfigPath configPath = new ConfigPath(keys);
            String value = config.getValue(configPath);
            assertEquals(value, data.get(keys));
        }
    }

    //just an auxiliary method to test config paths from a list
    private void testConfigPaths(Action a,
                                 List<ConfigPath> configPaths,
                                 int expectedMatches) {
        for (ConfigPath cp : configPaths) {
            ActionMessage am = getActionMessageBuilder().buildActionMessage(a, cp);
            Map<String, Object> data = am.getDataElements();
            assertEquals(expectedMatches, data.keySet().size());
            assertConfigurationKeys(data);
        }
    }


}
