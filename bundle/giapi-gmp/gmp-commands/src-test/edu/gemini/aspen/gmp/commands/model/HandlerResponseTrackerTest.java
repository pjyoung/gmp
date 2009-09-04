package edu.gemini.aspen.gmp.commands.model;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import edu.gemini.aspen.gmp.commands.api.*;
import edu.gemini.aspen.gmp.util.commands.HandlerResponseImpl;

/**
 * Test suite for the HandlerResponseTracker
 *
 */
public class HandlerResponseTrackerTest {

    private HandlerResponseTracker handlerResponseTracker;

    private Action a, b;

    
    @Before
    public void setUp() {

        handlerResponseTracker = new HandlerResponseTracker();
        a = new Action(SequenceCommand.ABORT,
                       Activity.PRESET_START,
                       new DefaultConfiguration(),
                       null);

        b = new Action(SequenceCommand.ABORT,
                       Activity.PRESET_START,
                       new DefaultConfiguration(),
                       null);

    }

    @After
    public void tearDown() {
        handlerResponseTracker = null;
        a = null;
        b = null;
    }


    /**
     * Default initialization test
     */
    @Test
    public void testDefaultInitialization() {

        //the tracker should be "completed" when it is initialized
        assertTrue(handlerResponseTracker.isComplete(a));

        //The answer for an uninitialized tracker for any action is null
        assertNull(handlerResponseTracker.getResponse(a));

    }

    /**
     * Test the tracking coordination between required/received responses
     */
    @Test
    public void testResponseTracking() {
        //let's increase the required responses for action a
        handlerResponseTracker.increaseRequiredResponses(a);
        handlerResponseTracker.increaseRequiredResponses(a);

        assertFalse(handlerResponseTracker.isComplete(a));

        handlerResponseTracker.storeResponse(a,
                                             HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));

        assertFalse(handlerResponseTracker.isComplete(a));

        handlerResponseTracker.storeResponse(a,
                                             HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));

        assertTrue(handlerResponseTracker.isComplete(a));
    }

    /**
     * This test verifies we get a NULL response in case we request
     * an answer for an action we are now tracking anymore
     */
    @Test
    public void testStopTrackingAction() {

        handlerResponseTracker.increaseRequiredResponses(a);

        handlerResponseTracker.removeTrackedAction(a);

        assertNull(handlerResponseTracker.getResponse(a));

    }

    /**
     * This test verifies we get an error reply if we request
     * to get an answer from an incomplete action
     */
    @Test
    public void testGetAnswerBeforeCompleted() {

        handlerResponseTracker.increaseRequiredResponses(a);
        assertEquals(HandlerResponse.Response.ERROR,
                     handlerResponseTracker.getResponse(a).getResponse());
        assertEquals(String.format(HandlerResponseTracker.ERROR_MSG, 1, ""),
                     handlerResponseTracker.getResponse(a).getMessage());

        //now there are two required responses
        handlerResponseTracker.increaseRequiredResponses(a);
        //still we should get error
        assertEquals(HandlerResponse.Response.ERROR,
                     handlerResponseTracker.getResponse(a).getResponse());
        //but the error message differs.
        assertEquals(String.format(HandlerResponseTracker.ERROR_MSG, 2, "s"),
                     handlerResponseTracker.getResponse(a).getMessage());
    }


    /**
     * Test that verifies getting the right response for a single handler action
     */
    @Test
    public void testGetAnswer() {

        handlerResponseTracker.increaseRequiredResponses(a);

        handlerResponseTracker.storeResponse(a,
                                             HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));

        assertEquals(HandlerResponse.Response.COMPLETED,
                     handlerResponseTracker.getResponse(a).getResponse());

    }

    /**
     * Test that verifies getting the right response for a multiple handler action
     */
    @Test
    public void testGetAnswerMultiple() {
        handlerResponseTracker.increaseRequiredResponses(a);
        handlerResponseTracker.increaseRequiredResponses(a);
        handlerResponseTracker.increaseRequiredResponses(a);

        handlerResponseTracker.storeResponse(a,
                                             HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));

        handlerResponseTracker.storeResponse(a,
                                                     HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));

        handlerResponseTracker.storeResponse(a,
                                                     HandlerResponseImpl.createError("Error message"));

        assertEquals(HandlerResponse.Response.ERROR,
                     handlerResponseTracker.getResponse(a).getResponse());
        assertEquals("Error message",
                     handlerResponseTracker.getResponse(a).getMessage());

    }


    /**
     * Test to verify the handling of different actions correctly
     */
    @Test
    public void trackMultipleActions() {

        handlerResponseTracker.increaseRequiredResponses(a);

        handlerResponseTracker.increaseRequiredResponses(b);

        assertFalse(handlerResponseTracker.isComplete(a));
        assertFalse(handlerResponseTracker.isComplete(b));

        handlerResponseTracker.storeResponse(a,
                                             HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));

        assertTrue(handlerResponseTracker.isComplete(a));
        assertFalse(handlerResponseTracker.isComplete(b));

        handlerResponseTracker.storeResponse(b,
                                             HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));

        assertTrue(handlerResponseTracker.isComplete(a));
        assertTrue(handlerResponseTracker.isComplete(b));

    }

}
