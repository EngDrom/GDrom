package org.EngDrom.GDrom.opengl.buffer;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class VAO {
	
	private int vao;
	public int getVAO() { return vao; }
	
	private ArrayList<VBO> vbos = new ArrayList<>();
	private ArrayList<VBO> indices_vbos = new ArrayList<>();
	
	public VAO (int vao) {
		this.vao = vao;
	}
	
	public static VAO createVao () {
		int vao = GL30.glGenVertexArrays();
		
		return new VAO(vao);
	}
	
	public void bind () {
		GL30.glBindVertexArray(this.vao);
	}
	
	public void enable () {
		this.bind();
		for (VBO vbo:vbos)
			vbo.enable();
	}
	public void disable () {
		for (VBO vbo:vbos)
			vbo.disable();
		GL30.glBindVertexArray(0);
	}
	
	public VBO createVBO (int attrib_location, int vector_size, int el_type) {
		this.bind();
		
		int vbo = GL30.glGenBuffers();
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
		GL30.glVertexAttribPointer(attrib_location, vector_size, el_type, false, 0, 0);
		
		VBO obj = new VBO(this, vbo, attrib_location);
		vbos.add(obj);
		return obj;
	}
	public VBO createIndicesVBO (int attrib_location) {
		int vbo = GL30.glGenBuffers();
		GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, vbo);

		VBO obj = new VBO(this, vbo, attrib_location);
		indices_vbos.add(obj);
		return obj;
	}
	public void delete () {
		for (VBO vbo:vbos) 
			vbo.delete();
		for (VBO ind_vbo: indices_vbos)
			ind_vbo.delete();
		
		GL30.glDeleteVertexArrays(vao);
	}

}
