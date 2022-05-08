package org.EngDrom.GDrom;


import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.EngDrom.GDrom.opengl.Window;
import org.EngDrom.GDrom.opengl.buffer.VAO;
import org.EngDrom.GDrom.opengl.buffer.VBO;
import org.EngDrom.GDrom.opengl.shader.Shader;
import org.EngDrom.GDrom.opengl.texture.Texture;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class EngineManager {

	private static boolean glfwInit = false;
	public static void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		
		if ( !GLFW.glfwInit() )
			throw new IllegalStateException("[GDrom] Unable to enable GLFW");
		glfwInit = true;
		
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
	}
	
	private static ArrayList<Window> windows = new ArrayList<>();
	public static void delete () {
		for (Window window:windows)
			if (window.isWorking())
				window.delete();
		
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}
	
	public static Window createWindow (int width, int height, String title) {
		if (!glfwInit) EngineManager.init();
		
		long wnd = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
		
		try ( MemoryStack stack = MemoryStack.stackPush() ) {
			IntBuffer pWidth  = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			GLFW.glfwGetWindowSize(wnd, pWidth, pHeight);

			GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
			GLFW.glfwSetWindowPos(
				wnd,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		}
		
		GLFW.glfwMakeContextCurrent(wnd);
		GLFW.glfwSwapInterval(1);
		GLFW.glfwShowWindow(wnd);
		
		GL.createCapabilities();
		
		return new Window(wnd);
	}
	
	public static void main(String[] args) {
		float[] diamond = new float []{
			    0.0f,  1.0f  , /* Top point */
			      1.0f,  0.0f  , /* Right point */
			      0.0f, -1.0f  , /* Bottom point */
			     -1.0f,  0.0f   }; /* Left point */

		float[] colors = new float [] {
			      0.0f,  0.0f,  0.0f  , /* Red */
			      1.0f,  0.0f,  0.0f  , /* Green */
			      1.0f,  1.0f,  1.0f  , /* Blue */
			      0.0f,  1.0f,  1.0f   }; /* White */
		
		int[] indices = new int[] {
				0, 1, 2,
				0, 2, 3
		};
		
		EngineManager.init();
		Window wnd = EngineManager.createWindow(400, 400, "Default Window");
		Window wnd2 = EngineManager.createWindow(400, 400, "Default Window");
		
		wnd.setContext();
		GL11.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
		
		VAO vao = VAO.createVao();
		VBO mesh = vao.createVBO(0, 2, GL11.GL_FLOAT);
		VBO color_mesh = vao.createVBO(1, 3, GL11.GL_FLOAT);
		VBO ind_mesh = vao.createIndicesVBO(0);
		
		mesh.bindData(diamond);
		color_mesh.bindData(colors);
		ind_mesh.bindData(indices);
		
		HashMap<String, Integer> bindmap = new HashMap<>();
		bindmap.put("in_Position", 0);
		bindmap.put("in_Color", 1);
		
		Shader shader = new Shader (
				"#version 150\n" + "// in_Position was bound to attribute index 0 and in_Color was bound to attribute index 1\n" + "in  vec2 in_Position;\n" + "in  vec3 in_Color;\n" + "\n" + "// We output the ex_Color variable to the next shader in the chain\n" + "out vec3 ex_Color;\n" + "void main(void) {\n" + "    // Since we are using flat lines, our input only had two points: x and y.\n" + "    // Set the Z coordinate to 0 and W coordinate to 1\n" + "\n" + "    gl_Position = vec4(in_Position.x, in_Position.y, 0.0, 1.0);\n" + "\n" + "    // GLSL allows shorthand use of vectors too, the following is also valid:\n" + "    // gl_Position = vec4(in_Position, 0.0, 1.0);\n" + "\n" + "    // We\'re simply passing the color through unmodified\n" + "    ex_Color = in_Color;\n" + "}",
				"#version 150\n" + "// It was expressed that some drivers required this next line to function properly\n" + "precision highp float;\n" + "\n" + "in  vec3 ex_Color;\n" + "out vec4 gl_FragColor;\n" + "\n" + "uniform sampler2D u_Texture;" + "\n" + "void main(void) {\n" + "    // Pass through our original color with full opacity.\n" + "    gl_FragColor = texture(u_Texture, ex_Color.xy);\n" + "}",
				bindmap
				);
		shader.build();
		
		// Texture path here
		Texture texture = Texture.createTexture("");
		shader.setUniform1i("u_Texture", 0);
		
		wnd2.setContext();
		GL11.glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
		
		while (!GLFW.glfwWindowShouldClose(wnd.getWindow()) && !GLFW.glfwWindowShouldClose(wnd2.getWindow())) {
			wnd.setContext();
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			shader.use();
			vao.enable();
			texture.bind(0);
			GL11.glDrawElements(GL11.GL_TRIANGLES, ind_mesh.getLength(), GL11.GL_UNSIGNED_INT, ind_mesh.getAttributePointer());
			texture.unbind();
			vao.disable();
			
			GLFW.glfwSwapBuffers(wnd.getWindow());
			
			GLFW.glfwPollEvents();

			wnd2.setContext();
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			GLFW.glfwSwapBuffers(wnd2.getWindow());
			
			GLFW.glfwPollEvents();
		}
		
		texture.delete();
		EngineManager.delete();
	}

}
