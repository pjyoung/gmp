package edu.gemini.aspen.gmp.logging;

/**
 * A test log processor that simply registers that it was called
 */
public class TestLogProcessor implements LogProcessor {

    private boolean _wasInvoked;

    public void processLogMessage(LogMessage msg) {

        _wasInvoked = true;

    }

    public boolean wasInvoked() {
        return _wasInvoked;
    }

    public void reset() {
        _wasInvoked = false;
    }

}
