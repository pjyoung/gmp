//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.12.09 at 04:04:46 PM CLST 
//


package edu.gemini.aspen.gmp.status.simulator.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the edu.gemini.aspen.gmp.status.simulator.generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Simulatedstatus_QNAME = new QName("", "simulatedstatus");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: edu.gemini.aspen.gmp.status.simulator.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SimulatedstatusType }
     * 
     */
    public SimulatedstatusType createSimulatedstatusType() {
        return new SimulatedstatusType();
    }

    /**
     * Create an instance of {@link StatusType }
     * 
     */
    public StatusType createStatusType() {
        return new StatusType();
    }

    /**
     * Create an instance of {@link ParametersType }
     * 
     */
    public ParametersType createParametersType() {
        return new ParametersType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimulatedstatusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "simulatedstatus")
    public JAXBElement<SimulatedstatusType> createSimulatedstatus(SimulatedstatusType value) {
        return new JAXBElement<SimulatedstatusType>(_Simulatedstatus_QNAME, SimulatedstatusType.class, null, value);
    }

}
