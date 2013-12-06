package com.tieto.qpid.jms;

import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 *
 * @author jentzmar
 */
public class Producer {

  public void run() {
    try {
      
      /*
       * These are only needed if the server requires a client cert for
       * mutual authentication...I will do a seperate example for that.
       */
//      System.setProperty("javax.net.ssl.keyStore", "conf/client.ks");
//      System.setProperty("javax.net.ssl.keyStorePassword", "password");
      
      // Only the trusted keystore is needed for establishing SSL toward the
      // broker. The password to the keystore is not required, either
      System.setProperty("javax.net.ssl.trustStore", "conf/client.ts");
//      System.setProperty("javax.net.ssl.trustStorePassword", "password");

      Properties properties = new Properties();
      properties.load(this.getClass().getResourceAsStream("/jms-amqp.properties"));
      Context context = new InitialContext(properties);

      ConnectionFactory connectionFactory = (ConnectionFactory)context.lookup("localhost-ssl");
      Connection connection = connectionFactory.createConnection();
      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      
      Destination dest = (Destination) context.lookup("test.topic");
      
      MessageProducer producer = session.createProducer(dest);
      producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

      for( int i=1; i <= 10000; i ++) {
        TextMessage msg = session.createTextMessage("#:" + i);
        msg.setIntProperty("id", i);
        producer.send(msg);
        if( (i % 1000) == 0) {
          System.out.println(String.format("Sent %d messages", i));
        }
      }

      producer.send(session.createTextMessage("QUIT"));
      Thread.sleep(1000*3);
      connection.close();
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
    
  }
  
  public static void main(String[] args) {
    System.out.println("Producer");
    Producer producer = new Producer();
    producer.run();
  }
}
