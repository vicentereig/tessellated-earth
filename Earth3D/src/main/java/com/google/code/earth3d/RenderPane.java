package com.google.code.earth3d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
/**
 * http://code.google.com/p/tessellated-earth-3d/
 * 
 * The MIT License
 *
 * Copyright (c) 2010 Vicente Reig Rinc�n de Arellanos
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
 * @author Vicente Reig Rinc�n de Arellano <vicente.reig@gmail.com>
 *
 */
public class RenderPane extends GLCanvas implements GLEventListener {

	private GLU glu = new GLU();
	private GL gl;
	private Earth earth;
	private FPSAnimator animator;

	//hud stats panel stuff
	private BufferedImage hud;
	private Graphics2D g2d;

	private int MAX_SIZE = 64;
	private int fpsHistory[] = new int[MAX_SIZE];
	private int position=0;
	private int fpssum=0;
	private long init;
	private double time;
	private int fps;
	
	private final Color blue1 = new Color(95,144,253);
	private final Color blue2 = new Color(169,193,217);	
	private double maxValue = 1d;
	
	private GraphicsConfiguration gconf;
	
	public RenderPane(GLCapabilities caps, GraphicsConfiguration gconf) {
		super(caps);
		this.gconf = gconf;
		addGLEventListener(this);
			
		hud = gconf.createCompatibleImage(MAX_SIZE+10,66, Transparency.OPAQUE);
			//new BufferedImage(MAX_SIZE+10,66, BufferedImage.TYPE_3BYTE_BGR);
		g2d = hud.createGraphics();

		try {
			BufferedImage dem = ImageIO.read( new File("textures/dem_2160x1080.jpg"));
			earth = new TessellatedSphere(60, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
//		earth = new Sphere(60);
	}

	@Override
	public void init(GLAutoDrawable canvas) {
		gl = canvas.getGL();
		gl.glClearColor(0, 0, 0, 1);

		Texture earthTex = TextureLoader
		.loadTexture("src/main/resources/world.topo.bathy.200407.3x2160x1080.jpg");
		earth.setTexture(earthTex);
		
		// Global settings.
		float[] lpos = { (float) (earth.getRadius()+5) , 0,0, 1};
		float[] wh = {0.2f,0.2f,0.2f,1f};
		float[] diffuse = {0.1f,0.1f,0.1f,1f};
		float[] specular = {0.7f,0.7f,0.7f,1f };
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, FloatBuffer.wrap(lpos));
		
		gl.glLightfv(GL.GL_LIGHT0,GL.GL_AMBIENT, FloatBuffer.wrap(wh));
		gl.glLightfv(GL.GL_LIGHT0,GL.GL_DIFFUSE, FloatBuffer.wrap(diffuse));
		gl.glLightfv(GL.GL_LIGHT0,GL.GL_SPECULAR, FloatBuffer.wrap(specular));
//		gl.glLightModeli(GL.GL_LIGHT_MODEL_COLOR_CONTROL,GL.GL_SEPARATE_SPECULAR_COLOR);
		gl.glEnable(GL.GL_LIGHT0);
		gl.glDisable(GL.GL_NORMALIZE);
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glHint(GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);

		animator = new FPSAnimator(this, 60);
		animator.start();		
	}
	
//	private final ExecutorService pool = Executors.newFixedThreadPool(5);
	private static final class SaveFrameTask implements Runnable {

		private String path = "frame/frame_%d.png";
		private BufferedImage image;
		
		public SaveFrameTask(Long task, BufferedImage img) {
			image = img;
			path = String.format(path, task);
		}
		
		@Override
		public void run() {
			try {
				ImageIO.write(image, "png", new File(path));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private long frame=0;
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		//create task for saving frameoffline
//		frame++;
//		SaveFrameTask t = new SaveFrameTask(frame,g.create());
	}

	private double angleRate = 0;
	private double speed = 0.03d;
	private double zoomLevel = 0d;
	private double zoomStep = 0.01d;
	
	@Override
	public void display(GLAutoDrawable canvas) {
		init = System.nanoTime();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glViewport(0, 0, getWidth(), getHeight());		
		gl = canvas.getGL();
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		float ratio = getWidth() / (1f * getHeight());
		glu.gluPerspective(60, ratio, 0.1, 5000);
	
		

		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(100, 50, 100, 0, 0, 0, 0, 1, 0);
//		gl.glTranslated(0, 0, zoomLevel);
		gl.glPushMatrix();

		gl.glRotated(angleRate, 0, 1, 0);
		gl.glRotated(90, 1, 0, 0);
		earth.render(gl);
		gl.glPopMatrix();
		gl.glFlush();
		
		if (time > 0d) {
			angleRate += time * speed;
			zoomLevel += time * zoomStep;
		}
		if ( zoomLevel > 100d ) {
			zoomStep = -0.01d;
		} else if ( zoomLevel < 0d ) {
			zoomStep = 0.01d;
		}
		
		if (angleRate >= 360d)
			angleRate -= 360d;		
		
		
		renderHud(time, fps);		
		drawHud(gl);
		
		time = (System.nanoTime() - init) / 1000000d;
		fps = (int) (1000 / time);
		System.out.format("[render] %.2f ms %d fps\n", time, fps);
	}
	
	private void drawHud(GL gl2) {
		gl.glViewport(10, 10, hud.getWidth(), hud.getHeight());
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(0, hud.getWidth(), hud.getHeight(),0);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc (GL.GL_DST_ALPHA, GL.GL_ONE_MINUS_DST_ALPHA);
		
		Texture t = TextureIO.newTexture(hud, false);		
		t.bind();
		t.enable();
		t.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NICEST);
		t.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NICEST);
		t.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
		t.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);		
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glColor4f(1, 1, 1, 0.8f);
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2i(0,0);
		gl.glVertex2i(0,0);
		gl.glTexCoord2i(1,0);
		gl.glVertex2i(hud.getWidth(),0);
		gl.glTexCoord2i(1,1);
		gl.glVertex2i(hud.getWidth(),hud.getHeight());
		gl.glTexCoord2i(0,1);
		gl.glVertex2i(0,hud.getHeight());
		gl.glEnd();
		t.disable();
		t.dispose();
		gl.glDisable(GL.GL_BLEND);
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glDisable(GL.GL_DEPTH_TEST);
	}

	private int getAvgFps(int fps) {
		fpssum -= fpsHistory[position];
		fpssum += fps;
		fpsHistory[position] = fps;
		if ( ++position == MAX_SIZE )
			position = 0;
		return fpssum/MAX_SIZE;
	}
	
	private void renderHud(double t, int fps) {		
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, hud.getWidth(), hud.getHeight());
		g2d.setColor(Color.black);
		g2d.drawString(String.format("FPS: %d", getAvgFps(fps)), 5, 15);
		g2d.setColor(Color.white);
		//sparkline a pelaco sin anestesia		
		g2d.fillRect(5, 25, MAX_SIZE, 32);		
		double y;
		for(int i=0; i<MAX_SIZE; i++) {
			if ( fpsHistory[i] > maxValue )
				maxValue = fpsHistory[i];
			y = 25+32*(1-(fpsHistory[i]/maxValue));
			g2d.setColor(blue2);
			g2d.drawLine(5+i, (int)y, 5+i, 25+32);
			g2d.setColor(blue1);			
			g2d.drawLine(5+i, (int)y, 5+i, (int)y+1);
		}
		
	}

	public void drawAxis(GL gl) {
		// gl.glPushMatrix();

		gl.glBegin(GL.GL_LINES);
		gl.glColor3f(1, 0, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(100, 0, 0);

		gl.glColor3f(0, 1, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, 100, 0);

		gl.glColor3f(0, 0, 1);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, 0, 100);

		gl.glEnd();
		// gl.glPopMatrix();
	}

	@Override
	public void displayChanged(GLAutoDrawable canvas, boolean modeChanged,
			boolean deviceChanged) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reshape(GLAutoDrawable canvas, int left, int top, int width,
			int height) {
		gl.glViewport(left, top, width, height);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		float ratio = getWidth() / getHeight();
		glu.gluPerspective(60, ratio, 0.1, 1000);

//		repaint();
	}

}
