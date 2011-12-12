Load test for Cassandra
=========================

Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php 


DESCRIPTION
-----------

This project is a tool used to test Cassandra performance.

The tool is designed as a client server application. 
The client application creates THREAD_COUNT number of threads and sends random generated data to the server using java serialization and http for transport.
The server application creates PROCESS_THREAD_COUNT threads for writing to Cassandra. Communication with the client is done using com.sun.net.httpserver.HttpServer.

USAGE
-----

Before starting, Cassandra must be already set up.
Start the server, then the client. Every minute the client will print to the console the rate of data objects that were generated and sent to the server. Same as the client, the server will print to the console the rate of data objects that were inserted into cassandra.

To increase the number of generated data objects increase client.Main.THREAD_COUNT.
To increase the number of inserted data objects increase server.Main.PROCESS_THREAD_COUNT.


