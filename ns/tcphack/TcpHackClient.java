package ns.tcphack;

import java.io.*;
import java.net.*;

class TcpHackClient {
	public static final String CON_IP = "127.0.0.1";
	public static final int CON_PORT = 1235;

	private DataInputStream in;
	private DataOutputStream out;
	private TcpHandler handler;

	public TcpHackClient(TcpHandler handler) {
		this.handler = handler;

		try {
			new Thread(new Communicator()).start();
			Thread.sleep(100);
		} catch (InterruptedException e) { }
	}

	public void send(byte[] data) {
		if (out != null) {
			try {
				out.writeInt(data.length);
				out.write(data);
				out.flush();
			} catch (IOException e) {
				System.err.println("Couldn't write socket: " + e.getMessage());
			}
		} else {
			System.err.println("Didn't write socket: not connected");
		}
	}

	class Communicator implements Runnable {
		public void run() {
			Socket clientSocket = null;
			try {
				clientSocket = new Socket(CON_IP, CON_PORT);
				out = new DataOutputStream(clientSocket.getOutputStream());
				in = new DataInputStream(clientSocket.getInputStream());

				while (true) {
					int size = in.readInt();
					byte[] data = new byte[size];
					in.read(data, 0, size);
					handler.dataReceived(data);
				}
			} catch (IOException e) {
				System.err.println("Couldn't read socket: " + e.getMessage());
			} finally {
				try {
					if (clientSocket != null)
						clientSocket.close();
				} catch (IOException e) { }
			}

			System.err.println("Communicator stopped!");
		}
	}
}
