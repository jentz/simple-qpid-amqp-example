package com.tieto.qpid.jms;

import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.Context;
import javax.naming.NamingException;
import org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory;

/**
 * The JNDI initial context factory provided by QPID requires reading
 * the properties from a file in the file system for some unexplained reason.
 * 
 * This class assumes that you have all the properties defined the the 
 * environment already, instead.
 * 
 *
 * @author jentzmar
 */
public class QpidInitialContextFactory extends PropertiesFileInitialContextFactory {

  public QpidInitialContextFactory() {
    super();
  }

  @Override
  public Context getInitialContext(Hashtable environment) throws NamingException {
    Map data = new ConcurrentHashMap();

    try {
      createConnectionFactories(data, environment);
    }
    catch (MalformedURLException e) {
      NamingException wrapped = new NamingException();
      wrapped.setRootCause(e);
      throw wrapped;
    }

    createDestinations(data, environment);

    createQueues(data, environment);

    createTopics(data, environment);

    return createContext(data, environment);
  }  
}
