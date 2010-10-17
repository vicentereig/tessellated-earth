package com.google.code.earth3d;

import javax.media.opengl.GL;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
/**
 * http://code.google.com/p/tessellated-earth-3d/
 * 
 * The MIT License
 *
 * Copyright (c) 2010 Vicente Reig Rincón de Arellanos
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 * @author Vicente Reig Rincón de Arellano <vicente.reig@gmail.com>
 *
 */
public class Triangle {
	
	Vector3d p1,p2,p3;
	
	Vector2d uv1,uv2,uv3;
	Vector3d normal,centroid;
	
	public Triangle(Vector3d p1, Vector3d p2, Vector3d p3) {
		this.p1 = new Vector3d(p1);		
		this.p2 = new Vector3d(p2);
		this.p3 = new Vector3d(p3);
		
	}

	public Triangle() {
		p1 = null; p2 = null; p3 = null;
	}

	public Triangle(Triangle triangle) {
		this(triangle.p1, triangle.p2, triangle.p3);
	}
	
	public void calculateNormal() {
		Vector3d pn1 = new Vector3d(p2);
		pn1.sub(p1);
		
		Vector3d pn2 = new Vector3d(p3);
		pn2.sub(p1);
		
		normal = new Vector3d();		
		normal.cross(pn1, pn2);	
		normal.normalize();		
	}
	
	public void calculateCentroid() {
		if ( null == centroid ) {
			centroid = new Vector3d();
			centroid.add(p1,p2);	
			centroid.add(p3);
			centroid.scale(1/3d);
		}		
	}

	public void render(GL gl) {	
		gl.glBegin(GL.GL_TRIANGLES);			
		
		gl.glNormal3d(p1.x, p1.y, p1.z);				
		gl.glTexCoord2d(uv1.x,uv1.y);		
		gl.glVertex3d(p1.x, p1.y, p1.z);
		
		gl.glNormal3d(p2.x, p2.y, p2.z);
		gl.glTexCoord2d(uv2.x,uv2.y);		
		gl.glVertex3d(p2.x, p2.y, p2.z);
		gl.glNormal3d(p3.x, p3.y, p3.z);
		gl.glTexCoord2d(uv3.x,uv3.y);
		gl.glVertex3d(p3.x, p3.y, p3.z);
					
		gl.glEnd();
		
	}
}
