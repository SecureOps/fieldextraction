package com.secureops.fieldextraction;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UUIDGenerator {

	@SuppressWarnings("unused")
	private static final Log LOG = LogFactory.getLog(UUIDGenerator.class);
	public static final String SHORTHASHALGO = "MD5";
	public static final String LONGHASHALGO = "SHA-256";

	public static final String UUIDTYPE_EVENTHASH = "eventhash";
	public static final String UUIDTYPE_TIMESTAMP = "timestamp";
	public static final String UUIDTYPE_NANO = "nano";
	public static final String UUIDTYPE_UNIX = "unix";
	public static final String UUIDTYPE_INVERTEDMILLIS = "invertedmillis";
	public static final String UUIDTYPE_TIMEHASH = "timehash";
	public static final String UUIDTYPE_RANDOM = "random";
	public static final String UUIDTYPE_UUID = "uuid";

	public static byte[] getUUIDKey() throws IOException {
		UUID uuid = UUID.randomUUID();
		long lowerOrder = uuid.getLeastSignificantBits();
		long upperOrder = uuid.getMostSignificantBits();
		return ByteUtils.concatenateByteArrays(ByteUtils.longToBytes(upperOrder), ByteUtils.longToBytes(lowerOrder));
	}

	public static byte[] getRandomKey() throws IOException {
		return ByteUtils.longToBytes((new Random().nextLong()));
	}

	public static byte[] getTimestampKey() throws IOException {
		return ByteUtils.longToBytes(System.currentTimeMillis());
	}

	public static byte[] getNanoTimestampKey() throws IOException {
		return ByteUtils.longToBytes(System.nanoTime());
	}

	public static byte[] getUnixTimeStampKey() throws IOException {
		return ByteUtils.longToBytes(System.currentTimeMillis());
	}

	public static byte[] getTimeHashKey(String hashAlgo) throws IOException, NoSuchAlgorithmException {
		String time = Long.toString(System.currentTimeMillis());
		MessageDigest md = null;
		md = MessageDigest.getInstance(hashAlgo);

		DateFormat dateFormat = new SimpleDateFormat("ss");
		Date date = new Date();

		byte[] hash = md.digest((time + dateFormat.format(date)).getBytes());

		return hash;
	}

	public static byte[] getEventHashKey(String hashAlgo, byte[] input,
			byte[] salt) throws IOException, NoSuchAlgorithmException {
		byte[] hash = null;

		MessageDigest md = MessageDigest.getInstance(hashAlgo);
		if (salt != null) {
			hash = md.digest(ByteUtils.concatenateByteArrays(input, salt));			
		} else {
			hash = md.digest(input);
		}
		return hash;
	}

	public static byte[] getInvertedTimeMillis() throws IOException {
		byte[] ts = UUIDGenerator.getUnixTimeStampKey();
		ArrayUtils.reverse(ts);
		return ts;
	}

	public static byte[] generateUUID(byte[] input, UUIDType uuidType)
			throws NoSuchAlgorithmException, IOException {
		return generateUUID(input, uuidType, null, LONGHASHALGO);
	}

	public static byte[] generateUUID(byte[] input, UUIDType uuidType,
			byte[] salt, String hashAlgo) throws IOException, NoSuchAlgorithmException {
		byte[] byteUuid = null;
		switch (uuidType) {
		case TS:
			byteUuid = getTimestampKey();
			break;
		 case TSNANO:
			 byteUuid = getNanoTimestampKey();
			 break;
		case RANDOM:
			byteUuid = getRandomKey();
			break;
		case TSUNIXDOTMILLIS:
			byteUuid = UUIDGenerator.getUnixTimeStampKey();
			break;
		case INVERTEDTSDOTMILLISSUFFIX:
			byteUuid = UUIDGenerator.getInvertedTimeMillis();
			break;
		case TIMEHASH:
			byteUuid = UUIDGenerator.getTimeHashKey(hashAlgo);
			break;
		case EVENTHASH:
			byteUuid = UUIDGenerator.getEventHashKey(hashAlgo, input, salt);
			break;
		default:
			byteUuid = UUIDGenerator.getUUIDKey();
			break;
		}
		return byteUuid;
	}
	
	public static int getDigestLength(String hashAlgo) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(hashAlgo);
		return md.getDigestLength();
	}
}
