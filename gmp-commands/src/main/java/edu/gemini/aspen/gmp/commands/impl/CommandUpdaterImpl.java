package edu.gemini.aspen.gmp.commands.impl;

import edu.gemini.aspen.giapi.commands.CommandUpdater;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.gmp.commands.model.ActionManager;

/**
 * Implementation of the {@link edu.gemini.aspen.giapi.commands.CommandUpdater}
 * interface. It notifies {@link edu.gemini.aspen.gmp.commands.model.ActionManager}
 * whenever new updates are available to process. 
 */
public class CommandUpdaterImpl implements CommandUpdater {

    private final ActionManager _manager;

    public CommandUpdaterImpl(ActionManager manager) {
        _manager = manager;
    }

    @Override
    public void updateOcs(int actionId, HandlerResponse response) {
        //make the completion information available for the Action Manager
        //to notify the clients.
        _manager.registerCompletionInformation(actionId, response);
    }
}
