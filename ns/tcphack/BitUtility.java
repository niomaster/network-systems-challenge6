package ns.tcphack;

public class BitUtility {
    static final int EMPTY = 0;

    public static String intToBits(int value, int bits) {
		String result = "";
		
		for(int i = 0; i < bits; i++) {
			result = (value & 1) + result;
			value >>>= 1;
		}
		
		return result;
	}
	
	public static String ipToBits(String ip) {
		String[] byteStrings = ip.split("\\.");
		
		if(byteStrings.length != 4) {
			throw new IllegalArgumentException("Malformed IP address.");
		}
		
		String result = "";
		
		for(String byteString : byteStrings) {
			result += intToBits(Integer.parseInt(byteString), 8);
		}
		
		return result;
	}
	
	public static byte[] bitsToBytes(String bitString) {
		if(bitString.length() % 8 != 0) {
			throw new IllegalArgumentException("Bitstring is not properly aligned.");
		}
		
		byte[] result = new byte[bitString.length() / 8];
		
		for(int i = 0; i < result.length; i++) {
			result[i] = (byte)(Integer.parseInt(bitString.substring(i * 8, i * 8 + 8), 2));
		}
		
		return result;
	}

    public static String bytesToBits(byte[] data) {
        String result = "";

        for(byte b : data) {
            result += intToBits((b + 256) % 256, 8);
        }

        return result;
    }

    public static String longToBits(long value, int bits) {
        String result = "";

        for(int i = 0; i < bits; i++) {
            result = (value & 1) + result;
            value >>>= 1;
        }

        return result;
    }
}
