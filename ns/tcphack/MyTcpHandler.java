package ns.tcphack;

import javax.print.DocFlavor;
import java.io.UnsupportedEncodingException;

class MyTcpHandler extends TcpHandler {
    private static final String REQUEST = "GET /?nr=1481304 HTTP/1.1\r\n" +
            "Host: telnetw.ewi.utwente.nl\r\n" +
            "User-Agent: Superunit van Pieter en Sophie\r\n" +
            "\r\n";

    public static void main(String[] args) {
        new MyTcpHandler();
	}

    private long ack;

	public MyTcpHandler() {
		super();

        TCPPacket tcp = new TCPPacket(44100, 7709, 1, 0, false, true, false, 1024, new byte[0]);
        IPPacket ip = new IPPacket(0, false, 0, "130.89.129.176", "130.89.144.74");

        tcp.setPacket(ip);
        ip.setPacket(tcp);

        sendData(ip.toBytes());
	}

	/**
	 * Called when a packet is received from the server.
	 * byte[] data contains the raw IP/TCP/(optionally HTTP) 
	 * headers and data.
	 */
	public void dataReceived(byte[] data) {
		IPPacket ip = new IPPacket(BitUtility.bytesToBits(data));
        System.out.println(BitUtility.bytesToBits(data));
        TCPPacket tcp = new TCPPacket(BitUtility.bytesToBits(data));

        if(tcp.isAck() && tcp.isSyn()) {
            ack = (tcp.getSequenceNumber() + 1) % ((long)1 << (long)32);
            tcp = new TCPPacket(44100, 7709, tcp.getAckNumber(), ack, true, false, false, 1024, REQUEST.getBytes());
            ip = new IPPacket(1, false, 0, "130.89.129.176", "130.89.144.74");

            tcp.setPacket(ip);
            ip.setPacket(tcp);

            sendData(ip.toBytes());
        }
	}
}
