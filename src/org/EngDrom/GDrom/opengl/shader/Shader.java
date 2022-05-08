package org.EngDrom.GDrom.opengl.shader;

import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL30;

public class Shader {

	private boolean built = false;
	private int vertex_shader;
	private int fragment_shader;
	
	private HashMap<String, Integer> attrib_locations;
	
	private int program;
	
	private final String vertex_src;
	private final String fragment_src;
	
	public Shader (String vertex_src, String fragment_src, HashMap<String, Integer> attrib_locations) {
		this.vertex_src = vertex_src;
		this.fragment_src = fragment_src;
		this.attrib_locations = attrib_locations;
	}
	
	public static Shader createShader (String vertex_file, String fragment_file, HashMap<String, Integer> attrib_locations) {
		return null; // TODO
	}
	
	public void use () {
		GL30.glUseProgram(program);
	}
	
	public void build () {
		if (built) return ;
		built = true;
		
		this.vertex_shader = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);
		GL30.glShaderSource(vertex_shader, vertex_src);
		GL30.glCompileShader(vertex_shader);

		this.fragment_shader = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);
		GL30.glShaderSource(fragment_shader, fragment_src);
		GL30.glCompileShader(fragment_shader);
		
		this.program = GL30.glCreateProgram();
		GL30.glAttachShader(program, vertex_shader);
		GL30.glAttachShader(program, fragment_shader);
		
		for (Entry<String, Integer> key : this.attrib_locations.entrySet()) {
			GL30.glBindAttribLocation(program, key.getValue(), key.getKey());
		}
		
		GL30.glLinkProgram(program);
	}
	public void delete () {
		GL30.glDetachShader(program, vertex_shader);
		GL30.glDetachShader(program, fragment_shader);
		GL30.glDeleteShader(vertex_shader);
		GL30.glDeleteShader(fragment_shader);
		GL30.glDeleteProgram(program);
	}
	
	public int GetUniformLocation (String name) {
		return GL30.glGetUniformLocation(program, name);
	}
	
	public void setUniform1i (String name, int value) {
		GL30.glUniform1i(GetUniformLocation(name), value);
	}
	public void setUniform1f (String name, float value) {
		GL30.glUniform1f(GetUniformLocation(name), value);
	}
	
}
