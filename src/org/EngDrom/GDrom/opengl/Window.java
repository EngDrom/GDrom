package org.EngDrom.GDrom.opengl;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;

public class Window {

	private long window;
	private boolean working = true;
	
	public Window (long window) {
		this.window = window;
	}
	
	public long getWindow() {
		return window;
	}
	
	public boolean isWorking () {
		return this.working;
	}
	public void delete () {
		Callbacks.glfwFreeCallbacks(this.window);
		GLFW.glfwDestroyWindow(this.window);
		this.working = false;
	}
	
	public void show () { GLFW.glfwShowWindow(window); }
	public void hide () { GLFW.glfwHideWindow(window); }
	
	public void setContext() {
		GLFW.glfwMakeContextCurrent(this.window);
	}
}
