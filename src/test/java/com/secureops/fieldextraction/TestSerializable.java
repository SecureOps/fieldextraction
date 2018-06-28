package com.secureops.fieldextraction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Assert;
import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class TestSerializable {

	@Test
	public void test() throws Exception {
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("fieldextraction.properties");
		FieldExtractor serializeExtractor = (FieldExtractor) SerializationUtils.deserialize(SerializationUtils.serialize(extractor));
		ExtractorResult er = extractor.extract("987654321");
		Assert.assertNotNull(er);
		
		ExtractorResult erS = serializeExtractor.extract("987654321");
		Assert.assertNotNull(erS);
	}
	
	@Test
	public void testKryoSerializable() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("fieldextraction.properties");
		Assert.assertNotNull(extractor);
		
		//Serialize/Deserialize using Kryo
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Output out = new Output(bos);
		Kryo serializer = new Kryo();
		serializer.writeClassAndObject(out, extractor);
		out.close();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		Input in = new Input(bis);
		
		FieldExtractor serializedExtractor = (FieldExtractor) serializer.readClassAndObject(in);
		in.close();
		
		ExtractorResult er = extractor.extract("987654321");
		Assert.assertNotNull(er);
		
		ExtractorResult erS = serializedExtractor.extract("987654321");
		Assert.assertNotNull(erS);
	}

}
