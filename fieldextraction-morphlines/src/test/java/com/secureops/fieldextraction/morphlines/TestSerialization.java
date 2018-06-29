package com.secureops.fieldextraction.morphlines;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Assert;
import org.junit.Test;
import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.secureops.fieldextraction.ExtractorResult;
import com.secureops.fieldextraction.FieldExtractor;
import com.secureops.fieldextraction.FieldExtractorConfigLoader;

public class TestSerialization {


	@Test
	public void testSerializable() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/singlemorphlinebob.properties");
		Assert.assertNotNull(extractor);
		FieldExtractor serializedExtractor = (FieldExtractor) SerializationUtils.deserialize(SerializationUtils.serialize(extractor));
		
		ExtractorResult er = extractor.extract("bob");
		Assert.assertNotNull(er);
		Assert.assertEquals(er.getMatches().get("test"), "bob");
		
		ExtractorResult erS = serializedExtractor.extract("bob");
		Assert.assertNotNull(erS);
		Assert.assertEquals(erS.getMatches().get("test"), "bob");
	}
	
	@Test
	public void testKryoSerializable() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/singlemorphlinebob.properties");
		Assert.assertNotNull(extractor);
		
		//Serialize/Deserialize using Kryo
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Output out = new Output(bos);
		Kryo serializer = new Kryo();
		serializer.register(java.util.regex.Pattern.class, new com.twitter.chill.java.RegexSerializer());
		serializer.setInstantiatorStrategy(new StdInstantiatorStrategy());
		serializer.writeClassAndObject(out, extractor);
		out.close();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		Input in = new Input(bis);
		
		FieldExtractor serializedExtractor = (FieldExtractor) serializer.readClassAndObject(in);
		in.close();
		
		ExtractorResult er = extractor.extract("bob");
		Assert.assertNotNull(er);
		
		ExtractorResult erS = serializedExtractor.extract("bob");
		Assert.assertNotNull(erS);
	}

}
