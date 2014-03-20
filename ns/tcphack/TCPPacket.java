package ns.tcphack;

/**
 * Created by pieter on 3/20/14.
 */
public class TCPPacket {
    private int source;
    private int destination;
    private long sequenceNumber;
    private long ackNumber;
    private boolean urg;
    private boolean ack;
    private boolean psh;
    private boolean rst;
    private boolean syn;
    private boolean fin;
    private int window;
    private byte[] payload;
    public static final int CHECKSUM_START = 128;
    public static final int CHECKSUM_END = 144;
    private IPPacket packet;

    public TCPPacket(int source, int destination, long sequenceNumber, long ackNumber, boolean urg, boolean ack, boolean psh, boolean rst, boolean syn, boolean fin, int window, byte[] data) {
        this.source = source;
        this.destination = destination;
        this.sequenceNumber = sequenceNumber;
        this.ackNumber = ackNumber;
        this.urg = urg;
        this.ack = ack;
        this.psh = psh;
        this.rst = rst;
        this.syn = syn;
        this.fin = fin;
        this.window = window;
        this.payload = data;
    }

    public TCPPacket(int source, int destination, long sequenceNumber, long ackNumber, boolean ack, boolean syn, boolean fin, int window, byte[] data) {
        this(source, destination, sequenceNumber, ackNumber, false, ack, false, false, syn, fin, window, data);
    }

    public TCPPacket(String x) {
        this(Integer.parseInt(x.substring(160, 160+16), 2),
                Integer.parseInt(x.substring(160+16, 160+32), 2),
                Long.parseLong(x.substring(160 + 32, 160 + 64), 2),
                Integer.parseInt(x.substring(160+64, 160+96), 2),
                x.substring(160+106, 160+107).equals("1"),
                x.substring(160+107, 160+108).equals("1"),
                x.substring(160+108, 160+109).equals("1"),
                x.substring(160+109, 160+110).equals("1"),
                x.substring(160+110, 160+111).equals("1"),
                x.substring(160+111, 160+112).equals("1"),
                Integer.parseInt(x.substring(160+112, 160+128), 2),
                BitUtility.bitsToBytes(x.substring(160+32*Integer.parseInt(x.substring(160+96, 160+100), 2), 8*Integer.parseInt(x.substring(16, 32), 2))));
    }

    public boolean isAck() {
        return ack;
    }

    public boolean isSyn() {
        return syn;
    }

    public byte[] getPayload() {
        return payload;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public long getAckNumber() {
        return ackNumber;
    }

    public void setPacket(IPPacket packet) {
        this.packet = packet;
    }

    public String toStringWithoutChecksum() {
        return  BitUtility.intToBits(source, 16) +
                BitUtility.intToBits(destination, 16) +
                BitUtility.longToBits(sequenceNumber, 32) +
                BitUtility.longToBits(ackNumber, 32) +
                BitUtility.intToBits(5, 4) +
                BitUtility.intToBits(BitUtility.EMPTY, 6) +
                (urg ? "1" : "0") +
                (ack ? "1" : "0") +
                (psh ? "1" : "0") +
                (rst ? "1" : "0") +
                (syn ? "1" : "0") +
                (fin ? "1" : "0") +
                BitUtility.intToBits(window, 16) +
                BitUtility.intToBits(BitUtility.EMPTY, 16) +
                BitUtility.intToBits(BitUtility.EMPTY, 16) +
                BitUtility.bytesToBits(payload);
    }

    public String toString() {
        return toStringWithoutChecksum().substring(0,CHECKSUM_START) +
                BitUtility.intToBits(getChecksum(), 16) +
                toStringWithoutChecksum().substring(CHECKSUM_END);
    }

    public int getChecksum() {
        String data = toStringWithoutChecksum();

        long sum = 0;

        for(int start = 0; start < data.length() / 16; start++) {
            int hi = Integer.parseInt(data.substring(start*16, start*16 + 8), 2);
            int lo = Integer.parseInt(data.substring(start*16 + 8, start*16 + 16), 2);

            sum += (hi << 8) + lo;
        }

        data = packet.getSource() + packet.getDestination() + BitUtility.intToBits(BitUtility.EMPTY, 8) + BitUtility.intToBits(packet.getProtocol(), 8) + BitUtility.intToBits(data.length() / 8, 16);

        System.out.println(data);

        for(int start = 0; start < data.length() / 16; start++) {
            int hi = Integer.parseInt(data.substring(start*16, start*16 + 8), 2);
            int lo = Integer.parseInt(data.substring(start*16 + 8, start*16 + 16), 2);

            if(data.substring(start*16 + 8, start*16 + 16).equals("")) {
                sum += (hi << 8);
            } else {
                sum += (hi << 8) + lo;
            }
        }

        int result = (int) ((~((sum & 0xffff) + (sum >> 16))) & 0xffff);

        return result;
    }

    public byte[] toBytes() {
        return BitUtility.bitsToBytes(toString());
    }
}
