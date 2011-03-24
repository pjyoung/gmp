package edu.gemini.aspen.giapi.util.jms;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionInformation;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.DefaultConfiguration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.StatusVisitor;
import edu.gemini.aspen.giapi.util.jms.status.StatusItemParser;
import edu.gemini.aspen.giapi.util.jms.status.StatusSerializerVisitor;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Enumeration;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.copy;

/**
 * Collection of utility methods that will help to transform from JMS messages
 * to GIAPI data structures and vice versa
 */
public final class MessageBuilder {

    private static final Logger LOG = Logger.getLogger(MessageBuilder.class.getName());

    //the following are a bunch of methods to return error messages.

    static String InvalidHandlerResponseMessage() {
        return "Invalid Message type to build HandlerResponse object";
    }

    static String InvalidResponseTypeMessage(String key) {
        return format("Invalid response type contained in the reply", key);
    }

    public static String InvalidResponseTypeMessage() {
        return InvalidResponseTypeMessage(null);
    }

    static String InvalidCompletionInformationMessage() {
        return "Invalid Message to construct Completion Information";
    }

    static String InvalidSequenceCommandMessage(String key) {
        return format("Invalid sequence command ", key);
    }

    static String InvalidActivityMessage(String key) {
        return format("Invalid Activity ", key);
    }

    static String InvalidConfigurationMessage() {
        return "Invalid configuration received";
    }

    //a formatter for the error messages
    private static String format(String message, String key) {
        StringBuilder sb = new StringBuilder(message);
        sb.append(" (").append(key).append(")");
        return sb.toString();
    }

    public static Message buildHandlerResponseMessage(Session session, HandlerResponse response) throws JMSException {
        MapMessage message = session.createMapMessage();

        //fill in the message
        message.setString(JmsKeys.GMP_HANDLER_RESPONSE_KEY, response.getResponse().name());

        if (response.hasErrorMessage()) {
            message.setString(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY, response.getMessage());
        }
        return message;
    }

    public static CompletionInformation buildCompletionInformation(Message m) throws JMSException {
        //reconstruct CompletionInfo based on the message

        if (!(m instanceof MapMessage)) {
            throw new JMSException(InvalidCompletionInformationMessage());
        }

        MapMessage msg = (MapMessage) m;

        //get the Handler Response.
        String key = msg.getStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY);
        HandlerResponse handlerResponse = null;
        if (key != null) {
            HandlerResponse.Response response;
            try {
                response = HandlerResponse.Response.valueOf(key);
            } catch (IllegalArgumentException ex) {
                throw new JMSException(InvalidResponseTypeMessage(key));
            }

            if (response == HandlerResponse.Response.ERROR) {
                String errMsg = msg.getStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY);
                handlerResponse = HandlerResponse.createError(errMsg);
            } else {
                handlerResponse = HandlerResponse.get(response);
            }
        }

        //get the sequence command
        SequenceCommand sc = null;
        key = msg.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY);
        if (key != null) {
            try {
                sc = SequenceCommand.valueOf(key);
            } catch (IllegalArgumentException ex) {
                throw new JMSException(InvalidSequenceCommandMessage(key));
            }
        }

        //get the activity
        Activity activity = null;
        key = msg.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY);
        if (key != null) {
            try {
                activity = Activity.valueOf(key);
            } catch (IllegalArgumentException ex) {
                throw new JMSException(InvalidActivityMessage(key));
            }
        }

        //get configuration
        Configuration config = DefaultConfiguration.emptyConfiguration();
        Enumeration names = msg.getMapNames();

        // FIXME Here is a potential NPE
        if (names.hasMoreElements()) {
            DefaultConfiguration.Builder builder = copy(config);
            while (names.hasMoreElements()) {
                Object o = names.nextElement();
                if (!(o instanceof String)) {
                    throw new JMSException(InvalidConfigurationMessage());
                }
                String path = (String) o;
                String value = msg.getString(path);
                builder.withPath(configPath(path), value);
            }
            config = builder.build();
        }

        return new CompletionInformation(handlerResponse, new Command(sc, activity, config));
    }

    public static Set<String> buildStatusNames(Message m) throws JMSException {
        if (!(m instanceof BytesMessage)) {
            return ImmutableSortedSet.of();
        }

        BytesMessage bm = (BytesMessage) m;

        Set<String> names = Sets.newTreeSet();
        int numNames = bm.readInt();
        for (int i = 0; i < numNames; i++) {
            names.add(bm.readUTF());
        }
        return ImmutableSortedSet.copyOf(names);
    }

    public static StatusItem buildStatusItem(Message m) throws JMSException {
        if (!(m instanceof BytesMessage)) {
            return null;
        }

        BytesMessage bm = (BytesMessage) m;
        try {
            return StatusItemParser.parse(bm);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static Message buildStatusItemMessage(Session session, StatusItem item) throws JMSException {

        BytesMessage bm = session.createBytesMessage();

        if (item == null) {
            return bm; //an empty message.
        }

        StatusVisitor serializer = new StatusSerializerVisitor(bm);

        try {
            item.accept(serializer);
        } catch (JMSException e) {
            throw e;
        } catch (Exception e) {
            //this shouldn't happen, since the serializer only throws JMS Exceptions.
            LOG.log(Level.SEVERE, "Received unexpected exception ", e);
        }

        return bm;
    }

}
