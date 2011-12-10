/**
 * 
 */
package ro.softgress.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author valer
 *
 */
public class Main {

	private final int THREAD_COUNT = 15;
	private final int PACKAGE_SIZE = 1000;
	private long startTime;
	private long lastTime;
	
	private long count;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main();
		main.process();
	}

	private void process() {
		ClientThread[] clientThreads = new ClientThread[THREAD_COUNT];
		for (int i = 0; i < THREAD_COUNT; i++) {
			clientThreads[i] = new ClientThread();
		}
		startTime = System.currentTimeMillis();
		lastTime = startTime;
		for (int i = 0; i < THREAD_COUNT; i++) {
			clientThreads[i].start();
		}
	}

	/*
	private class ClientThread extends Thread {

		@Override
		public void run() {
			Generator gen = new Generator();
			DataRow dataRow = new DataRow();
			DataRow[] dataRows = new DataRow[PACKAGE_SIZE];
			int packagesNb = 0;
			long lastSecond = -1;
			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Data data = gen.generateData();
				//send to server
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
				//check time interval
				long currentTime = System.currentTimeMillis();
				boolean timeHasElapsed = false;
				synchronized (Boolean.TRUE) {
					if (currentTime - lastTime >= 60000) { //1 minute
						lastTime = currentTime;
						timeHasElapsed = true;
					};
				}
				if (timeHasElapsed) {
					System.out.println("client " + count + " objects in 1 minute");
					count = 0;
				}
			}
		}
		
	}
	*/
	
	private class ClientThread extends Thread {

		@Override
		public void run() {
			Generator gen = new Generator();
			Data[] datas = new Data[PACKAGE_SIZE];
			int packagesNb = 0;
			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Data data = gen.generateData();
				//send to server
				datas[packagesNb++] = data;
				if (packagesNb == PACKAGE_SIZE) {
					sendToServer(datas);
					packagesNb = 0;
				}
				count++;
				//check time interval
				long currentTime = System.currentTimeMillis();
				boolean timeHasElapsed = false;
				synchronized (Boolean.TRUE) {
					if (currentTime - lastTime >= 60000) { //1 minute
						lastTime = currentTime;
						timeHasElapsed = true;
					};
				}
				if (timeHasElapsed) {
					System.out.println("client " + count + " objects in 1 minute");
					count = 0;
				}
			}
		}
		
	}

	/*
	private void sendToServer(DataRow[] dataRows) {
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
			serverDataStream.writeObject(dataRows);
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
	*/
	
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
