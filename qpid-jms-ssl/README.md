qpid-jms-ssl
============

## Overview

Simple example to illustrate connecting to an AMQP 1.0 endpoint using SSL 
with the QPID JMS Client API.

This example does not use 2-way authentication, that will be shown in a seperate
example. All we are doing here is establishing connections using SSL and for that the client
must trust the server (broker) certificate.

## Setup

The configuration file under conf/activemq.xml should be copied 
to ${activemq.home}/conf/activemq.xml, you can make a backup of the 
original file first.

Start the broker using the ${activemq.home}/bin/activemq command. On Windows 
need to type:

	cd bin
	activemq.bat

On Linux you can use:

	./bin/activemq console

The `conf/client.ks` comes from the ActiveMQ distribution and contains a 
self-signed certificate for clients to use when connecting to the broker via 
SSL if the transport.needClientAuth option is set to true.

The `conf/client.ts` also comes from the ActiveMQ distribution and has the broker's
certificate which will be needed for the client to trust the broker certificate.

The password for both of these is 'password'. Obviously all of these certificates
need to be generated or ordered from a third-party for a production environment
but the code examples will remain the same.

## Running the examples

	java -cp target/qpid-jms-ssl-1.0-SNAPSHOT.jar com.tieto.qpid.jms.Consumer

	java -cp target/qpid-jms-ssl-1.0-SNAPSHOT.jar com.tieto.qpid.jms.Producer
