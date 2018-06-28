package com.secureops.fieldextraction;

import static org.junit.Assert.*;

import java.security.MessageDigest;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

public class TestUUIDGenerator {

	@Test
	public void testEventHash() throws Exception {
		byte[] body = new String("test").getBytes();
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] control = md.digest(body);		
		byte[] out = UUIDGenerator.generateUUID(body, UUIDType.EVENTHASH, null, "SHA-1");
		assertTrue(ArrayUtils.isEquals(control, out));
		
		// Now tack on a timestamp onto the "out"
		out = ByteUtils.concatenateByteArrays(out, ByteUtils.longToBytes(System.currentTimeMillis()));
		
		// Take it out and see if the hashes are still equal
		byte[] clipped = ArrayUtils.subarray(out, 0, UUIDGenerator.getDigestLength("SHA-1"));
		assertTrue(ArrayUtils.isEquals(control, clipped));
	}
}
