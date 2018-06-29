package com.secureops.fieldextraction;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestUUIDType {

	@Test
	public void testStringToUUIDType() {
		UUIDType control = UUIDType.UNSUPPORTED;		
		UUIDType out = UUIDType.stringToUUIDType("joe");
		assertEquals(control, out);
		out = UUIDType.stringToUUIDType("eventhash");
		assertNotEquals(control, out);
	}
}
