package com.tieto.qpid.jms;

import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 *
 * @author jentzmar
 */
public class Consumer {

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

      ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("localhost-ssl");
      Connection connection = connectionFactory.createConnection();
      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Destination dest = (Destination) context.lookup("test.topic");
      MessageConsumer consumer = session.createConsumer(dest);
      long start = System.currentTimeMillis();
      long count = 1;
      System.out.println("Waiting for messages...");
      while (true) {
        Message msg = consumer.receive();
        if (msg instanceof TextMessage) {
          String body = ((TextMessage) msg).getText();
          if ("QUIT".equals(body)) {
            long diff = System.currentTimeMillis() - start;
            System.out.println(String.format("Received %d in %.2f seconds", count, (1.0 * diff / 1000.0)));
            connection.close();
            System.exit(1);
          } 
          else {
            try {
              if (count != msg.getIntProperty("id")) {
                System.out.println("mismatch: " + count + "!=" + msg.getIntProperty("id"));
              }
            } 
            catch (NumberFormatException ignore) {
            }
            if (count == 1) {
              start = System.currentTimeMillis();
            } 
            else if (count % 1000 == 0) {
              System.out.println(String.format("Received %d messages.", count));
            }
            count++;
          }
        } 
        else {
          System.out.println("Unexpected message type: " + msg.getClass());
        }
      }
    } 
    catch (Throwable e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    System.out.println("Consumer");
    Consumer consumer = new Consumer();
    consumer.run();
  }
}
