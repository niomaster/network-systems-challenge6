package ns.tcphack;

abstract class TcpHandler {
	private TcpHackClient client;

	public TcpHandler() {
		client = new TcpHackClient(this);
	}

	public void sendData(byte[] data) {
		client.send(data);
	}

	public abstract void dataReceived(byte[] data);
}
