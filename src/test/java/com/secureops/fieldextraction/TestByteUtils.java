package com.secureops.fieldextraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

import com.secureops.fieldextraction.ByteUtils;



public class TestByteUtils {

	@Test
	public void testLongToBytesConversion() throws Exception {
		long ts = 1408507949000L;
		byte[] tsBytes = ByteUtils.longToBytes(ts);		
		long out = ByteUtils.bytesToLong(tsBytes);
		
		assertEquals(ts,out);
	}
	
	@Test
	public void testConcatenateByteArraysPair() throws IOException {
		String control = "test1test2";
		String test1 = "test1";
		String test2 = "test2";
		
		byte[] output = ByteUtils.concatenateByteArrays(test1.getBytes(), test2.getBytes());
		assertTrue(Arrays.equals(control.getBytes(), output));
	}

	@Test
	public void testSplitByteArray() {
		String control = "test1:test2";
		String test1 = "test1";
		String test2 = "test2";
		
		List<byte[]> output = ByteUtils.split(":".getBytes(), control.getBytes());

		assertTrue(output.size() == 2);
		assertTrue(Arrays.equals(output.get(0), test1.getBytes()));
		assertTrue(Arrays.equals(output.get(1), test2.getBytes()));
		
	}
	
	@Test
	public void testConcatenateByteArraysList() throws IOException {
		String control = "test1test2test3";
		String test1 = "test1";
		String test2 = "test2";
		String test3 = "test3";
		List<byte[]> input = new ArrayList<byte[]>();
		input.add(test1.getBytes());
		input.add(test2.getBytes());
		input.add(test3.getBytes());
		byte[] output = ByteUtils.concatenateByteArrays(input);
		assertTrue(Arrays.equals(control.getBytes(), output));
	}



	

	


}
