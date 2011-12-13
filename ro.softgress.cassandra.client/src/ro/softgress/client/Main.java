/**
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package ro.softgress.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Client application that creates THREAD_COUNT number of threads and sends
 * random generated data to the server using java serialization and http for
 * transport.
 * 
 * @author valer
 * @author mcq
 */
public class Main {

	private static final int THREAD_COUNT = 7;
	private static final int PACKAGE_SIZE = 100;
	private static final long PRINT_INTERVAL = 60000;

	private final AtomicLong count = new AtomicLong(0);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main();
		main.process();
	}

	private void process() {
		ClientThread[] clientThreads = new ClientThread[THREAD_COUNT];
		// create clients
		for (int i = 0; i < THREAD_COUNT; i++) {
			clientThreads[i] = new ClientThread();
		}
		// start clients
		for (int i = 0; i < THREAD_COUNT; i++) {
			clientThreads[i].start();
		}
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
			System.out.println("client gen&sent " + objectsPerSec + " objects per second");
		}
	}

	private class ClientThread extends Thread {

		@Override
		public void run() {
			Generator gen = new Generator();
			Data[] datas = new Data[PACKAGE_SIZE];
			int packagesNb = 0;
			while (true) {
				// try {
				// Thread.sleep(1);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				Data data = gen.generateData();
				//send to server
				datas[packagesNb++] = data;
				if (packagesNb == PACKAGE_SIZE) {
					sendToServer(datas);
					packagesNb = 0;
					count.getAndAdd(PACKAGE_SIZE);
				}
			}
		}

	}

	private void sendToServer(Data[] datas) {
		ObjectOutputStream serverDataStream = null;
		BufferedReader serverResponseStream = null;
		try {
			URL agentURL = new URL("http://localhost:5889/server");
			URLConnection serverConnection = agentURL.openConnection();
			serverConnection.setConnectTimeout(1000);
			serverConnection.setReadTimeout(3000);
			serverConnection.setDoInput(true);
			serverConnection.setDoOutput(true);
			serverConnection.setUseCaches(false);
			serverDataStream = new ObjectOutputStream(serverConnection.getOutputStream());
			serverDataStream.writeObject(datas);
			serverDataStream.flush();
			serverDataStream.close();
			serverDataStream = null;
			serverResponseStream = new BufferedReader(new InputStreamReader(
					serverConnection.getInputStream()));
			String agentResponse = serverResponseStream.readLine();
			serverResponseStream.close();
			serverResponseStream = null;
			if (!"SUCCESS".equalsIgnoreCase(agentResponse)) {
				System.out.println("server communication failure1: " + agentResponse);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (serverDataStream != null) {
				try {
					serverDataStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (serverResponseStream != null) {
				try {
					serverDataStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
