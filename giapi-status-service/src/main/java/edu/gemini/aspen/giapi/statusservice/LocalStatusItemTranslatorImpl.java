package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.gmp.top.Top;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;
import org.apache.felix.ipojo.annotations.*;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Class LocalStatusItemTranslatorImpl will publish status items translations directly back to the status handler aggregate
 *
 * @author Nicolas A. Barriga
 *         Date: 4/5/12
 */
@Component
@Provides
public class LocalStatusItemTranslatorImpl extends AbstractStatusItemTranslator implements JmsArtifact, StatusItemTranslator {
    private static final Logger LOG = Logger.getLogger(LocalStatusItemTranslatorImpl.class.getName());
    private final StatusHandlerAggregate aggregate;

    public LocalStatusItemTranslatorImpl(@Requires Top top,
                                         @Requires StatusHandlerAggregate aggregate,
                                         @Property(name = "xmlFileName", value = "INVALID", mandatory = true) String xmlFileName) {
        super(top, xmlFileName);
        this.aggregate = aggregate;
    }

    @Validate
    public void start() throws IOException, JAXBException, SAXException {
        LOG.finer("Start validate");
        super.start();
        initItems();
        LOG.finer("End validate");
    }

    @Invalidate
    public void stop() {
        LOG.finer("Start stop");
        super.stop();
        LOG.finer("End stop");
    }

    @Override
    public <T> void update(StatusItem<T> item) {
        LOG.fine("Status item received: " + item);
        //publish translation
        for (StatusItem<?> newItem : translate(item)) {
            LOG.fine("Publishing translated status item: " + newItem);
            aggregate.update(newItem);
        }
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        LOG.finer("Start startJms");
        getter.startJms(provider);
        jmsStarted.set(true);
        LOG.finer("End startJms");
    }

    @Override
    public void stopJms() {
        LOG.finer("Start stopJms");
        jmsStarted.set(false);
        getter.stopJms();
        LOG.finer("End stopJms");
    }
}
