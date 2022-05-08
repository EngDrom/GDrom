package org.EngDrom.GDrom.opengl.buffer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL30;

public class VBO {

	private VAO vao;
	private int vbo;
	private int attrib_pointer;
	private int buffer_size;
	public int getVBO () { return this.vbo; }
	
	public VBO (VAO vao, int vbo, int attrib_pointer) {
		this.vao = vao;
		this.vbo = vbo;
		this.attrib_pointer = attrib_pointer;
	}
	
	public void bind () {
		this.vao.bind();

		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
	}
	public void delete () {
		GL30.glDeleteBuffers(vbo);
	}
	
	public void enable () {
		GL30.glEnableVertexAttribArray(this.attrib_pointer);
	}
	public void disable () {
		GL30.glDisableVertexAttribArray(this.attrib_pointer);
	}
	
	public void bindData(FloatBuffer data) {
		this.bind();
		buffer_size = data.remaining(); // Assume buffer hasn't been modified
		GL30.glBufferData(GL30.GL_ARRAY_BUFFER, data, GL30.GL_STATIC_DRAW);
	}
	public void bindData(IntBuffer data) {
		this.bind();
		buffer_size = data.remaining(); // Assume buffer hasn't been modified
		GL30.glBufferData(GL30.GL_ARRAY_BUFFER, data, GL30.GL_STATIC_DRAW);
	}

	public void bindData(float[] data) {
		this.bind();
		buffer_size = data.length;
		GL30.glBufferData(GL30.GL_ARRAY_BUFFER, data, GL30.GL_STATIC_DRAW);
	}
	public void bindData(int[] data) {
		this.bind();
		buffer_size = data.length;
		GL30.glBufferData(GL30.GL_ARRAY_BUFFER, data, GL30.GL_STATIC_DRAW);
	}

	public long getAttributePointer() {
		return this.attrib_pointer;
	}
	public int getLength() {
		return this.buffer_size;
	}
	
}
