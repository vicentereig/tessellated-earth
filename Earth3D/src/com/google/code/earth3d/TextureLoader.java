package com.google.code.earth3d;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
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
public class TextureLoader {
	public static Texture loadTexture(String path) {
		Texture t =  null;
		try {
			t = TextureIO.newTexture(new File(path), true);
			t.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NICEST);
			t.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NICEST);
			t.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
			t.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
		} catch (GLException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		return t;
		
	}
}
