package com.google.code.earth3d;

import static org.junit.Assert.*;

import javax.vecmath.Vector3d;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VectorCalculus {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMidpoint() {
		Vector3d p1 = new Vector3d(1,1,1);
		Vector3d p2 = new Vector3d(0,0,0);
		Vector3d exp = new Vector3d(0.5,0.5,0.5);
		
		Vector3d midpoint = new Vector3d(p1);
		midpoint.add(p2);
		midpoint.scale(0.5);
		assertEquals(exp, midpoint);
	}

}
