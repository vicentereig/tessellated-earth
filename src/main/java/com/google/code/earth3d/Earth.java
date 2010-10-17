package com.google.code.earth3d;

import java.awt.image.BufferedImage;

import javax.media.opengl.GL;

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
public interface Earth {

	public abstract double getRadius();

	public abstract void setTexture(Texture t);

	public abstract void render(GL gl);

	public abstract void setHeightMap(BufferedImage dem);

}