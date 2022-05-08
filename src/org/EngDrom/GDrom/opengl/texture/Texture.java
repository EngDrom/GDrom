package org.EngDrom.GDrom.opengl.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class Texture {

	private int texture_id;
	
	public Texture(int texture_id) {
		this.texture_id = texture_id;
	}
	
	public void bind () { this.bind(0); }
	public void bind(int slot) {
		GL30.glActiveTexture(GL30.GL_TEXTURE0 + slot);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture_id);
	}
	
	public void unbind () {
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public void delete() {
		GL11.glDeleteTextures(texture_id);
	}
	
	public static Texture createTexture( String path ) {
		int tid = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tid);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		
		BufferedImage image = null;
		
		try {
			File file = new File(path);
			image = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int[] pixels = new int[image.getWidth() * image.getHeight()];
	    image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
	    ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);

	    for(int h = 0; h < image.getHeight(); h++) {
	        for(int w = 0; w < image.getWidth(); w++) {
	            int pixel = pixels[h * image.getWidth() + w];

	            buffer.put((byte) ((pixel >> 16) & 0xFF));
	            buffer.put((byte) ((pixel >> 8) & 0xFF));
	            buffer.put((byte) (pixel & 0xFF));
	            buffer.put((byte) ((pixel >> 24) & 0xFF));
	        }
	    }

	    buffer.flip();
	    
	    GL11.glTexImage2D(
	    		GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 
	    		image.getWidth(), image.getHeight(), 
	    		0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 
	    		pixels
	    	);
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		return new Texture(tid);
	}
	
}
