package com.secureops.fieldextraction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

public class ByteUtils {

	public static List<byte[]> split(byte[] pattern, byte[] input) {
		List<byte[]> l = new ArrayList<byte[]>();
		int blockStart = 0;
		for (int i = 0; i < input.length; i++) {
			if (isMatch(pattern, input, i)) {
				l.add(Arrays.copyOfRange(input, blockStart, i));
				blockStart = i + pattern.length;
				i = blockStart;
			}
		}
		l.add(Arrays.copyOfRange(input, blockStart, input.length));
		return l;
	}

	public static List<Byte[]> split(Byte[] pattern, Byte[] input) {		
		byte[] primitivePattern = ArrayUtils.toPrimitive(pattern);
		byte[] primitiveInput = ArrayUtils.toPrimitive(input);
		List<byte[]> primitiveList = ByteUtils.split(primitivePattern, primitiveInput);
		List<Byte[]> ret = new ArrayList<Byte[]>();
		for(byte[] b : primitiveList) {
			ret.add(ArrayUtils.toObject(b));
		}
		return ret;
	}

	public static boolean isMatch(byte[] pattern, byte[] input, int pos) {
		for (int i = 0; i < pattern.length; i++) {
			if (pattern[i] != input[pos + i]) {
				return false;
			}
		}
		return true;
	}

	public static byte[] concatenateByteArrays(List<byte[]> byteArray)
			throws IOException {
		ByteArrayOutputStream ret = new ByteArrayOutputStream();
		for (byte[] bytes : byteArray) {
			if (bytes != null) {
				ret.write(bytes);
			}
		}
		return ret.toByteArray();
	}

	public static byte[] longToBytes(long in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeLong(in);
		dos.close();

		return baos.toByteArray();
	}

	public static long bytesToLong(byte[] bytes) throws Exception {
		ByteArrayInputStream baos = new ByteArrayInputStream(bytes);
		DataInputStream dos = new DataInputStream(baos);
		long result;
		result = dos.readLong();
		dos.close();

		return result;
	}

	public static byte[] concatenateByteArrays(byte[] one, byte[] two) throws IOException {
		ArrayList<byte[]> byteList = new ArrayList<byte[]>();
		byteList.add(one);
		byteList.add(two);
		return concatenateByteArrays(byteList);
	}
}
