package com.google.code.earth3d;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.code.earth3d.TessellatedSphere;


public class EarthTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBuildModel() {
		TessellatedSphere sphere = new TessellatedSphere(1);
		
		int vertices = sphere.buildModel(1);		
		assertEquals(4, vertices);
		
		sphere = new TessellatedSphere(1);
		vertices = sphere.buildModel(2);		
		assertEquals(16, vertices);
		
		sphere = new TessellatedSphere(1);
		vertices = sphere.buildModel(4);		
		assertEquals(256, vertices);
		
		sphere = new TessellatedSphere(1);
		vertices = sphere.buildModel(5);		
		assertEquals(1024, vertices);
				
		sphere = new TessellatedSphere(1);
		vertices = sphere.buildModel(6);		
		assertEquals(4096, vertices);
		
		sphere = new TessellatedSphere(1);
		vertices = sphere.buildModel(7);		
		assertEquals(4096*4, vertices);
	}

}
