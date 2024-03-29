/**
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package ro.softgress.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import ro.softgress.client.Data;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Server application that creates PROCESS_THREAD_COUNT threads for writing to
 * cassandra. Communication with the client is done using
 * com.sun.net.httpserver.HttpServer.
 * 
 * @author valer
 * @author mcq
 */
public class Main {

	private static final int PROCESS_THREAD_COUNT = 3;
	private static final long PRINT_INTERVAL = 60000;

	private LinkedBlockingQueue<Data[]> queue = new LinkedBlockingQueue<Data[]>(2000);
	private final AtomicLong count = new AtomicLong(0);

	private enum DB_SYSTEM {
		NO_SQL,
		RDBMS
	}
	
	//system to test
	private DB_SYSTEM dbSystemToTest = DB_SYSTEM.RDBMS;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main();
		main.startServer();
	}

	private void startServer() {
		startHttpServer();
		startCassandraInsertThreads();
		printStats();
	}

	private void startHttpServer() {
		// start java sun http server
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(5889), 0);
			server.createContext("/server", new ServerHttpHandler());
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startCassandraInsertThreads() {
		// create and start threads to insert data into cassandra
		ProcessingQueue[] processThreads = new ProcessingQueue[PROCESS_THREAD_COUNT];
		for (int i = 0; i < PROCESS_THREAD_COUNT; i++) {
			processThreads[i] = new ProcessingQueue();
		}
		for (int i = 0; i < PROCESS_THREAD_COUNT; i++) {
			processThreads[i].start();
		}
	}

	private void printStats() {
		// print every PRINT_INTERVAL milliseconds
		while (true) {
			long startTime = System.currentTimeMillis();
			try {
				Thread.sleep(PRINT_INTERVAL);
			} catch (InterruptedException e) {
				break;
			}
			long timeDelta = (System.currentTimeMillis() - startTime) / 1000;
			long cc = count.getAndSet(0);
			long objectsPerSec = cc / timeDelta;
			System.out.println("server inserts " + objectsPerSec + " objects per second");
		}
	}

	private class ServerHttpHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange xchg) throws IOException {
			Data[] datas = null;
			ObjectInputStream ois = new ObjectInputStream(xchg.getRequestBody());
			boolean accepted = false;
			try {
				datas = (Data[]) ois.readObject();
				accepted = queue.offer(datas);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			String response = accepted ? "ACCEPTED" : "REJECTED";
			xchg.sendResponseHeaders(200, response.length());
			OutputStream os = xchg.getResponseBody();
			os.write(response.getBytes());
			os.flush();
			os.close();
		}
	}

	private class ProcessingQueue extends Thread {
		private DAO dao= null;
		
		@Override
		public void run() {
			if (dbSystemToTest == DB_SYSTEM.NO_SQL) {
				dao = new NoSqlDAO();
			}
			if (dbSystemToTest == DB_SYSTEM.RDBMS) {
				dao = new RdbmsDAO();
			}
			while (true) {
				Data[] datas;
				try {
					datas = queue.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
				if (datas != null) {
					dao.insertData(datas);
					count.getAndAdd(datas.length);
				}
			}
		}
	}
}
