package edu.gemini.aspen.heartbeatdistributor;

/**
 * Interface IHeartbeatConsumer, to be implemented by components willing to receive heartbeat notifications
 *
 * @author Nicolas A. Barriga
 *         Date: 3/10/11
 */
public interface IHeartbeatConsumer {
    /**
     * Receive a heartbeat
     *
     * @param beatNumber the last heartbeat number received
     */
    void beat(long beatNumber);
}
