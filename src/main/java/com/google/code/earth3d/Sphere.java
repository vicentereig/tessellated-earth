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
public class Sphere implements Earth {

	private List<Vector3d> vertices;
	private List<Vector3d> normals;
	private List<Vector2d> mapping;
		
	private int resolution = 9;
	
	private Texture texture;
	
	private FloatBuffer diffuse, ambient, specular;
	private float shininess;
	
	private double radius;
	

	private BufferedImage heightMap;
	
	public void setHeightMap(BufferedImage hm) {
		heightMap = hm;
	}
	
	public Sphere(double radius) {
		this.radius = radius;
		vertices = new LinkedList<Vector3d>();
		normals = new LinkedList<Vector3d>();
		mapping = new LinkedList<Vector2d>();
		float spec[] = {0.5f,0.5f,0.5f,1f};
		float amb[] = {0.4f,0.4f,0.4f,1};
		float diff[] = {0.1f,0.1f,0.1f,1};
		shininess = 1f;
		ambient = FloatBuffer.wrap(amb);
		specular = FloatBuffer.wrap(spec);
		diffuse = FloatBuffer.wrap(diff);
		
		Vector3d v1, v2, v3, v4;
		Vector2d uv;
		Vector3d normal;
		for (double lat = 0; lat <= 90-resolution ; lat += resolution) {
			for (double lon = 0; lon <= 360-resolution; lon += resolution) {
				v1 = new Vector3d();
				v1.x = radius * Math.sin(lon / 180d * Math.PI) * Math.sin(lat / 180d * Math.PI);
				v1.y = radius * Math.cos(lon / 180d * Math.PI) * Math.sin(lat / 180d * Math.PI);
				v1.z = radius * Math.cos(lat / 180d * Math.PI);
				vertices.add(v1);

				uv = new Vector2d();
				uv.x = lon / 360d;
				uv.y = (2 * lat) / 360d;
				mapping.add(uv);

				v2 = new Vector3d();
				v2.x = radius * Math.sin(lon / 180d * Math.PI) * Math.sin((lat + resolution) / 180 * Math.PI);
				v2.y = radius * Math.cos(lon / 180d * Math.PI) * Math.sin((lat + resolution) / 180d * Math.PI);
				v2.z = radius * Math.cos((lat + resolution) / 180d * Math.PI);
				vertices.add(v2);

				uv = new Vector2d();
				uv.x = lon / 360d;
				uv.y = (2 * (lat + resolution)) / 360d;
				mapping.add(uv);

				v3 = new Vector3d();
				v3.x = radius * Math.sin((lon + resolution) / 180d * Math.PI)	* Math.sin(lat / 180d * Math.PI);
				v3.y = radius * Math.cos((lon + resolution) / 180d * Math.PI)	* Math.sin(lat / 180d * Math.PI);
				v3.z = radius * Math.cos(lat / 180d * Math.PI);
				vertices.add(v3);

				uv = new Vector2d();
				uv.x = (lon + resolution) / 360d;
				uv.y = (2 * lat) / 360d;
				mapping.add(uv);

				v4 = new Vector3d();
				v4.x = radius * Math.sin((lon + resolution) / 180d * Math.PI) * Math.sin((lat + resolution) / 180d * Math.PI);
				v4.y = radius * Math.cos((lon + resolution) / 180d * Math.PI)	* Math.sin((lat + resolution) / 180d * Math.PI);
				v4.z = radius * Math.cos((lat + resolution) / 180d * Math.PI);
				vertices.add(v4);

				uv = new Vector2d();
				uv.x = (lon + resolution) / 360d;
				uv.y = (2 * (lat + resolution)) / 360d;
				mapping.add(uv);
				Vector3d n1,n2,n3,n4;
				n1 = new Vector3d(v1);
				n2 = new Vector3d(v2);
				n3 = new Vector3d(v3);
				n4 = new Vector3d(v4);
//				Vector3d pn1 = new Vector3d(v2);
//				pn1.sub(v1);
//
//				Vector3d pn2 = new Vector3d(v3);
//				pn2.sub(v1);
//
//				normal = new Vector3d();
//				normal.cross(pn2, pn1);
//				normal.normalize();
				
	
				//apañete
				normals.add(n1);
				normals.add(n2);
				normals.add(n3);
				normals.add(n4);			
			}
			
			
			
			
		}

	}

	/* (non-Javadoc)
	 * @see es.uv.ii.aig.earth3d.Earth#getRadius()
	 */
	public double getRadius() { return radius; }
	
	/* (non-Javadoc)
	 * @see es.uv.ii.aig.earth3d.Earth#setTexture(com.sun.opengl.util.texture.Texture)
	 */
	public void setTexture(Texture t) {
		texture = t;
	}
	
	private void draw(GL gl) {
		Vector2d uv;
		Vector3d normal, vertex;
//		debo tener algún problema con el orden de los vertices?
//		gl.glEnable(GL.GL_CULL_FACE);
//		gl.glCullFace(GL.GL_FRONT);
		gl.glBegin(GL.GL_TRIANGLE_STRIP);
		for(int i=0; i<vertices.size(); i++) {
			vertex = vertices.get(i);
			normal = normals.get(i);
			uv = mapping.get(i);
			gl.glNormal3d(normal.x, normal.y, normal.z);
			gl.glTexCoord2d(uv.x, -uv.y);
			gl.glVertex3d(vertex.x, vertex.y,vertex.z);
		}
//		gl.glCullFace(GL.GL_BACK);
//		gl.glEnd();
//		gl.glBegin(GL.GL_TRIANGLE_STRIP);
		for(int i=0; i<vertices.size(); i++) {
			vertex = vertices.get(i);
			normal = normals.get(i);
			uv = mapping.get(i);
			gl.glNormal3d(normal.x, normal.y, normal.z);
			gl.glTexCoord2d(uv.x, uv.y);
			gl.glVertex3d(vertex.x, vertex.y, -vertex.z);
		}

		gl.glEnd();
		gl.glDisable(GL.GL_CULL_FACE);
	}

	/* (non-Javadoc)
	 * @see es.uv.ii.aig.earth3d.Earth#render(javax.media.opengl.GL)
	 */
	public void render(GL gl) {
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, ambient);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, diffuse);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, specular);
		gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, shininess);
		gl.glEnable(GL.GL_LIGHTING);

		gl.glEnable(GL.GL_DEPTH_TEST);
	    gl.glDepthFunc(GL.GL_LEQUAL);

		gl.glEnable(GL.GL_BLEND);
//		gl.glAlphaFunc(GL.GL_ONE_MINUS_SRC_ALPHA, GL.GL_SRC_ALPHA);
//		gl.glAlphaFunc(GL.GL_NOTEQUAL, 0.0f);
		gl.glEnable(GL.GL_TEXTURE_2D);
		texture.bind();
		texture.enable();
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
		draw(gl);		
		gl.glDisable(GL.GL_TEXTURE_2D);
		texture.disable();

//		gl.glColor4f(1, 1, 1, 1f);
//		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
//		draw(gl);		
		//meridianos/paralelos		

		
		gl.glDisable(GL.GL_BLEND);	
		gl.glDisable(GL.GL_LIGHTING);
		
		
	}
}
