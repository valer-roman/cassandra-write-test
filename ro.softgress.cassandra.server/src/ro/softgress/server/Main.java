/**
 * 
 */
package ro.softgress.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.softgress.client.Data;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


/**
 * @author valer
 *
 */
public class Main {

	private Logger logger = LoggerFactory.getLogger(Main.class);

	private final int PROCESS_THREAD_COUNT = 10;
	
	private long count = 0;
	private long startTime = -1;
	private long lastTime;
	
	private ConcurrentLinkedQueue<Data[]> queue = new ConcurrentLinkedQueue<Data[]>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main();
		main.startServer();
	}

	private class ServerHttpHandler implements HttpHandler {
		
		/* (non-Javadoc)
		 * @see com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange)
		 */
		@Override
		public void handle(HttpExchange xchg) throws IOException {
			synchronized (Boolean.TRUE) {
				if (startTime == -1) {
					startTime = System.currentTimeMillis();
					lastTime = startTime;
				}
			}
			Data[] datas = null;
			ObjectInputStream ois = new ObjectInputStream(xchg.getRequestBody());
			try {
				datas = (Data[]) ois.readObject();
				queue.add(datas);
				//System.out.println(dataRow);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			xchg.sendResponseHeaders(200, "SUCCESS".length());
			OutputStream os = xchg.getResponseBody();
			os.write("SUCCESS".getBytes());
			os.flush();
			os.close();
		}
		
	}
	
	private void checkTime() {
		long currentTime = System.currentTimeMillis();
		boolean timeHasElapsed = false;
		synchronized (Boolean.TRUE) {
			if (currentTime - lastTime >= 60000) { //1 minute
				lastTime = currentTime;
				timeHasElapsed = true;
			};
		}
		if (timeHasElapsed) {
			logger.info("server " + count + " objects in 1 minute, queue contains" + queue.size());
			count = 0;
		}		
	}
	
	public class ProcessingQueue extends Thread {
		private DAO dao = new DAO();
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			/*
				long theSecond = data.getTimestamp()/1000;
				if (lastSecond == -1) {
					dataRow.setSecond(theSecond);
					lastSecond = theSecond;
				} 
				if (lastSecond == -1 || theSecond != lastSecond) {
					dataRows[packagesNb++] = dataRow;
					if (packagesNb == PACKAGE_SIZE) {
						sendToServer(dataRows);
						dataRows= new DataRow[PACKAGE_SIZE];
						packagesNb = 0;					
					}
					dataRow = new DataRow();
					dataRow.setSecond(theSecond);
					lastSecond = theSecond;
				}
				count++;
				dataRow.getColumns().add(data);
			 */
			while(true) {
				Data[] datas = queue.poll();
				//DAO dao = new DAO();
				if (datas != null) {
					for (int i = 0; i < datas.length; i++) {
						dao.insertData(datas[i]);
						count++;
						checkTime();
					}
				}
			}
		}
		
	}
	
	private void startServer() {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(5889), 0);
			server.createContext("/server", new ServerHttpHandler());
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ProcessingQueue[] processThreads = new ProcessingQueue[PROCESS_THREAD_COUNT];
		for (int i = 0; i < PROCESS_THREAD_COUNT; i++) {
			processThreads[i] = new ProcessingQueue();
		}
		for (int i = 0; i < PROCESS_THREAD_COUNT; i++) {
			processThreads[i].start();
		}
	}
}
