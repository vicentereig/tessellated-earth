package com.google.code.earth3d;

import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import com.sun.opengl.util.texture.Texture;
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
public class TessellatedSphere implements Earth {
	
	private List<Triangle> facets;	
	private double radius;
	private Texture texture;
	private boolean renderNormals = false;
	private boolean renderMesh = false;
	private boolean applyTexture = true;
	private FloatBuffer specular,ambient,diffuse;
	private float shininess;
	
	private BufferedImage heightMap;
	
	public void setHeightMap(BufferedImage hm) {
		heightMap = hm;
	}
	
	public TessellatedSphere(double radius, BufferedImage hm) {
		this.radius = radius;
		
		facets = new LinkedList<Triangle>();
		heightMap = hm;
		long before = System.nanoTime();		
		int v = buildModel(8);
		System.out.format("[build] %d vertices ···· %.2f ms\n", v, (System.nanoTime()-before)/1000000d );
		float spec[] = {1f,1f,1f,1f};
		float amb[] = {0.7f,0.7f,0.7f,1};
		float diff[] = {0.1f,0.1f,0.1f,1};
		shininess = 0.12f;
		ambient = FloatBuffer.wrap(amb);
		specular = FloatBuffer.wrap(spec);
		diffuse = FloatBuffer.wrap(diff);
	}
	
	public TessellatedSphere(double radius) {
		this(radius,null);
	}
	
	public double getRadius() { return radius; }
	
	public void setTexture(Texture t) {
		texture = t;
	}
	
	public int buildModel(int iterations) {	
		int n,nstart;
		Triangle current;
				
		Vector3d p1 = new Vector3d(1,1,1);
		Vector3d p2 = new Vector3d(-1,-1,1);
		Vector3d p3 = new Vector3d(1,-1,-1);
		Vector3d p4 = new Vector3d(-1,1,-1);	

		
		Triangle t0 = new Triangle(p1,p2,p3);
		facets.add(t0);
		
		Triangle t1 = new Triangle(p2,p1,p4);
		facets.add(t1);
		
		Triangle t2 = new Triangle(p2,p4,p3);
		facets.add(t2);
		
		Triangle t3 = new Triangle(p1,p3,p4);
		facets.add(t3);
		
		n = 4;
			
		for( int i=1; i<iterations; i++) {
			nstart = n;
			for(int j=0; j<nstart; j++) {
				/* Crear copias iniciales */
				current = facets.get(j);
				t0 = new Triangle(current);
				t1 = new Triangle(current);
				t2 = new Triangle(current);
				facets.add(t0);
				facets.add(t1);
				facets.add(t2);
				
				/* calcular los puntos medios */
				p1 = new Vector3d(current.p1);
				p1.add(current.p2);
				p1.scale(0.5d);
				
				p2 = new Vector3d(current.p2);
				p2.add(current.p3);
				p2.scale(0.5d);
				
				p3 = new Vector3d(current.p3);
				p3.add(current.p1);
				p3.scale(0.5d);

				//subdivisiones
				current.p2 = p1;
				current.p3 = p3;
				
				t0.p1 = p1; t0.p3 = p2;
				t1.p1 = p3; t1.p2 = p2;
				t2.p1 = p1; t2.p2 = p2; t2.p3 = p3;
				
				n += 3;				
			}			
		}
		
		for(Triangle t : facets) {			
			t.p1.normalize();
			t.p2.normalize();
			t.p3.normalize();		
//			t.calculateNormal();
			t.calculateCentroid();			
			t.uv1 = new Vector2d();			 			
			calcTextureCoords(t.uv1, t.p1);			
			t.uv2 = new Vector2d();			 			
			calcTextureCoords(t.uv2, t.p2);
			
			t.uv3 = new Vector2d();			 			
			calcTextureCoords(t.uv3, t.p3);
//			System.out.println(t.normal);
			if ( null != heightMap ) { 
				calcTriangleHeights(t);
			}
		}
				
		return n;
	}
	
	private void calcVertexHeight(Vector3d p, Vector2d uv) {
		int x = (int) Math.abs(uv.x * (heightMap.getWidth()-1));
		int y = (int) Math.abs(uv.y * (heightMap.getHeight()-1));
//		System.out.format("(%d,%d)\n", x,y);
		int[] color = new int[1];
		heightMap.getData().getPixel(x, y, color);
		Vector3d h = new Vector3d(p);		
		h.scale(color[0]/255d);
		h.normalize();
//		System.out.format("%s %d\n", h, color[0]);
		p.add(h);
	}
	
	private void calcTriangleHeights(Triangle t) {		
		calcVertexHeight(t.p1, t.uv1);
		calcVertexHeight(t.p2, t.uv2);
		calcVertexHeight(t.p3, t.uv3);
	}
	
	private void calcTextureCoords(Vector2d uv, Vector3d vertex) {	
		uv.x = Math.atan2(vertex.y, vertex.x)/(2*Math.PI);		
		uv.y = Math.acos(vertex.z/vertex.length())/(Math.PI);		
	}
	
	public void render(GL gl) {
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, ambient);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, diffuse);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, specular);
		gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, shininess);
		gl.glEnable(GL.GL_LIGHTING);
		
//		gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
		gl.glEnable(GL.GL_LIGHTING);		
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glEnable(GL.GL_DEPTH_TEST);
	    gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL.GL_BLEND);
		gl.glAlphaFunc(GL.GL_ONE_MINUS_SRC_ALPHA, GL.GL_SRC_ALPHA);
//		gl.glAlphaFunc(GL.GL_NOTEQUAL, 0.0f);
		if ( applyTexture ) {
			gl.glEnable(GL.GL_TEXTURE_2D);		
			texture.bind();
			texture.enable();
			gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
		}
	    gl.glRotatef(180f,1f,0f,0f);
		gl.glScaled(radius,radius,radius);
		gl.glPushMatrix();
		


		gl.glColor3f(1,1,1);
		for(Triangle t : facets) 
			t.render(gl);			
		gl.glPopMatrix();
		if ( applyTexture ) {
			gl.glDisable(GL.GL_TEXTURE_2D);
			texture.disable();
		}
		gl.glDisable(GL.GL_LIGHTING);				
		
		if (renderMesh) {
			gl.glLineWidth(1);
			gl.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);
			gl.glColor4f(1, 1, 1, 0.25f);
			for (Triangle t : facets)
				t.render(gl);
			gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
		}
		if (renderNormals) {
			gl.glLineWidth(1);
			gl.glColor4f(1, 0, 0,0.25f);
			
			gl.glBegin(GL.GL_LINES);
			for (Triangle t : facets) {	
				t.normal.scale(1 / 30d);
				gl.glVertex3d(t.centroid.x, t.centroid.y, t.centroid.z);
				gl.glVertex3d(t.centroid.x + t.normal.x, t.centroid.y + t.normal.y,
						t.centroid.z + t.normal.z);
				
				gl.glVertex3d(t.p1.x, t.p1.y, t.p1.z);
				gl.glVertex3d(t.p1.x + t.normal.x, t.p1.y + t.normal.y,
						t.p1.z + t.normal.z);
				
				gl.glVertex3d(t.p2.x, t.p2.y, t.p2.z);
				gl.glVertex3d(t.p2.x + t.normal.x, t.p2.y + t.normal.y,
						t.p2.z + t.normal.z);
				
				gl.glVertex3d(t.p3.x, t.p3.y, t.p3.z);
				gl.glVertex3d(t.p3.x + t.normal.x, t.p3.y + t.normal.y,
						t.p3.z + t.normal.z);
				t.normal.scale(30d);				
			}
			gl.glEnd();
			
		}
		gl.glDisable(GL.GL_BLEND);
		gl.glDisable(GL.GL_CULL_FACE);
	}

	public int getFacetsCount() {
		return facets.size();
	}
}
