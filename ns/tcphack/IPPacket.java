package ns.tcphack;

public class IPPacket {

    public static final int IP_VERSION = 4;

    public static final int INTERNET_HEADER_LENGTH = 20;

    public static final int CHECKSUM_OFFSET = 80;

    private static final int PROTOCOL_TCP = 6;

	public static final String TOS_DELAY_NORMAL = "0";
	public static final String TOS_DELAY_LOW = "1";
	
	public static final String TOS_THROUGHPUT_NORMAL = "0";
	public static final String TOS_THROUGHPUT_HIGH = "1";
	
	public static final String TOS_RELIABILITY_NORMAL = "0";
	public static final String TOS_RELIABILITY_HIGH = "1";
	
	public static final String TOS_PRECEDENCE_ROUTINE = "000";
	public static final String TOS_PRECEDENCE_PRIORITY = "001";
	public static final String TOS_PRECEDENCE_IMMEDIATE = "010";
	public static final String TOS_PRECEDENCE_FLASH = "011";
	public static final String TOS_PRECEDENCE_FLASH_OVERRIDE = "100";
	public static final String TOS_PRECEDENCE_CRITIC = "101";
	public static final String TOS_PRECEDENCE_INTERNETWORK_CONTROL = "110";
	public static final String TOS_PRECEDENCE_NETWORK_CONTROL = "111";
	
	public static final String FLAGS_FRAGMENTATION_ALLOW = "0";
	public static final String FLAGS_FRAGMENTATION_DISALLOW = "1";
	
	public static final String FLAGS_FRAGMENT_LAST = "0";
	public static final String FLAGS_FRAGMENT_NOT_LAST = "1";


    // TODO: implement options.

//    public static final String OPTION_CLASS_DEBUGGING = "10";
//    public static final String OPTION_CLASS_CONTROL = "00";
//
//    public static final String OPTION_NOT_COPIED = "1";
//    public static final String OPTION_COPIED = "0";

    private TCPPacket packet;
    private String tosDelay;
    private String tosTroughput;
    private String tosReliability;
    private String tosPrecedence;
    private int id;
    private String flagFragmentationDisallow;
    private String flagFragmentNotLast;
    private int fragmentOffset;
    private int ttl;
    private int protocol;
    private String source;
    private String destination;

    public IPPacket(String tosDelay, String tosTroughput, String tosReliability, String tosPrecedence, int id, String flagFragmentationDisallow, String flagFragmentNotLast, int fragmentOffset, int ttl, int protocol, String source, String destination) {
        this.tosDelay = tosDelay;
        this.tosTroughput = tosTroughput;
        this.tosReliability = tosReliability;
        this.tosPrecedence = tosPrecedence;
        this.id = id;
        this.flagFragmentationDisallow = flagFragmentationDisallow;
        this.flagFragmentNotLast = flagFragmentNotLast;
        this.fragmentOffset = fragmentOffset;
        this.ttl = ttl;
        this.protocol = protocol;
        this.source = source;
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setPacket(TCPPacket packet) {
        this.packet = packet;
    }

    public IPPacket(int id, boolean fragmentNotLast, int offset, String source, String destination) {
        this(TOS_DELAY_NORMAL, TOS_THROUGHPUT_NORMAL, TOS_RELIABILITY_NORMAL, TOS_PRECEDENCE_ROUTINE, id, FLAGS_FRAGMENTATION_ALLOW,
                fragmentNotLast ? FLAGS_FRAGMENT_NOT_LAST : FLAGS_FRAGMENT_LAST, offset, 64, PROTOCOL_TCP,
                BitUtility.ipToBits(source), BitUtility.ipToBits(destination));
    }

    public IPPacket(String x) {
        this(x.substring(11, 12), x.substring(12, 13), x.substring(13, 14), x.substring(8, 11), Integer.parseInt(x.substring(32, 48), 2), x.substring(49, 50), x.substring(50, 51), Integer.parseInt(x.substring(51, 64), 2), Integer.parseInt(x.substring(64, 72), 2), Integer.parseInt(x.substring(72, 80), 2), x.substring(96, 128), x.substring(128, 160));
    }

    public int getTotalLength() {
        return packet.toBytes().length + INTERNET_HEADER_LENGTH;
    }

    private int getChecksum() {
        String data = toStringWithoutChecksum();

        long sum = 0;

        for(int start = 0; start < 10; start++) {
            int hi = Integer.parseInt(data.substring(start*16, start*16 + 8), 2);
            int lo = Integer.parseInt(data.substring(start*16 + 8, start*16 + 16), 2);

            sum += (hi << 8) + lo;
        }

        int result = (int) ((~((sum & 0xffff) + (sum >> 16))) & 0xffff);

        return result;
    }

    public String toStringWithoutChecksum() {
        return BitUtility.intToBits(IP_VERSION, 4) +
                BitUtility.intToBits(INTERNET_HEADER_LENGTH / 4, 4) +
                tosPrecedence +
                tosDelay +
                tosTroughput +
                tosReliability +
                BitUtility.intToBits(BitUtility.EMPTY, 2) +
                BitUtility.intToBits(getTotalLength(), 16) +
                BitUtility.intToBits(id, 16) +
                BitUtility.intToBits(BitUtility.EMPTY, 1) +
                flagFragmentationDisallow +
                flagFragmentNotLast +
                BitUtility.intToBits(fragmentOffset, 13) +
                BitUtility.intToBits(ttl, 8) +
                BitUtility.intToBits(protocol, 8) +
                BitUtility.intToBits(BitUtility.EMPTY, 16) + // Checksum
                source +
                destination +
                packet.toString();
    }

    public String toString() {
        String data = toStringWithoutChecksum();
        data = data.substring(0, CHECKSUM_OFFSET) + BitUtility.intToBits(getChecksum(), 16) + data.substring(CHECKSUM_OFFSET + 16);
        return data;
    }

    public byte[] toBytes() {
        return BitUtility.bitsToBytes(toString());
    }
}

