package com.gnarly.engine.display;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

public class Window {
	
	public static final byte
		UNPRESSED = 0,
		RELEASED  = 1,
		PRESSED   = 2,
		HELD      = 3;
	
	private long window;
	
	private GLFWVidMode vidMode;
	
	private int xOff, yOff, vWidth, vHeight;
	
	private double scrollX, scrollY;
	
	private StringBuilder input;
	
	private boolean resized;
	
	byte[] keys = new byte[GLFW_KEY_LAST];
	
	public Window(int width, int height, boolean vSync, boolean resizable, String title) {
		init(width, height, vSync, resizable, title);
	}
	
	public Window(boolean vSync) {
		init(0, 0, vSync, false, null);
	}
	
	private void init(int width, int height, boolean vSync, boolean resizable, String title) {
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		
		if(!glfwInit()) {
			System.err.println("GLFW failed to initialize!");
			System.exit(-1);
		}
		
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		
		glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);

		vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		if(width == 0 || height == 0 || width >= vidMode.width() || height >= vidMode.height()) {
			width = vidMode.width();
			height = vidMode.height();
			window = glfwCreateWindow(width, height, "", glfwGetPrimaryMonitor(), NULL);
		}
		else {
			window = glfwCreateWindow(width, height, title, NULL, NULL);
			glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
		}
		
		glfwMakeContextCurrent(window);
		createCapabilities();

		glfwSwapInterval(vSync ? 1 : 0);
		
		glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {
			public void invoke(long window, int width, int height) {
				resized = true;
				glViewport(0, 0, width, height);
			}
		});
		
		glfwSetScrollCallback(window, new GLFWScrollCallback() {
			public void invoke(long window, double x, double y) {
				scrollX = x;
				scrollY = y;
			}
		});

		input = new StringBuilder();
		glfwSetCharModsCallback(window, new GLFWCharModsCallback() {
			public void invoke(long window, int key, int mods) {
				input.append((char) key);
			}
		});
		glfwSetKeyCallback(window, new GLFWKeyCallback() {
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if(action != GLFW_RELEASE) {
					if(glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) != GLFW_PRESS && glfwGetKey(window, GLFW_KEY_RIGHT_CONTROL) != GLFW_PRESS && glfwGetKey(window, GLFW_KEY_LEFT_ALT) != GLFW_PRESS && glfwGetKey(window, GLFW_KEY_RIGHT_ALT) != GLFW_PRESS) {
						switch (key) {
							case GLFW_KEY_ENTER:
								input.append((char) 10);
								break;
							case GLFW_KEY_BACKSPACE:
								input.append((char) 8);
								break;
							case GLFW_KEY_DELETE:
								input.append((char) 127);
								break;
							case GLFW_KEY_LEFT:
								input.append((char) 1);
								break;
							case GLFW_KEY_RIGHT:
								input.append((char) 2);
								break;
							case GLFW_KEY_UP:
								input.append((char) 3);
								break;
							case GLFW_KEY_DOWN:
								input.append((char) 4);
								break;
							case GLFW_KEY_TAB:
								input.append((char) 9);
								break;
						}
					}
					if(keys[key] < PRESSED)
						keys[key] = PRESSED;
					else
						keys[key] = HELD;
				}
				else {
					if(keys[key] > RELEASED)
						keys[key] = RELEASED;
					else
						keys[key] = UNPRESSED;
				}
			}
		});
		
		float vWidth = 0, vHeight = 0;
		while(vWidth < vidMode.width() && vHeight < vidMode.height()) {
			vWidth += 1;
			vHeight += 9 /16f;
		}
		
		xOff = (int) ((vidMode.width() - vWidth) / 2);
		yOff = (int) ((vidMode.height() - vHeight) / 2);
		this.vWidth = (int) vWidth;
		this.vHeight = (int) vHeight;
		bind();
		
		glClearColor(0, 0, 0, 1);
		
		glEnable(GL_TEXTURE_2D);
		
		glEnable(GL_DEPTH_TEST);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void update() {
		for (int i = 0; i < keys.length; i++) {
			if(keys[i] == PRESSED)
				++keys[i];
			else if(keys[i] == RELEASED)
				--keys[i];
		}
		scrollX = 0;
		scrollY = 0;
		resized = false;
		input.delete(0,  input.length());
		glfwPollEvents();
	}
	
	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public void swap() {
		glfwSwapBuffers(window);
	}
	
	public void setVSync(boolean vSync) {
		glfwSwapInterval(vSync ? 1 : 0);
	}
	
	public void close() {
		glfwSetWindowShouldClose(window, true);
	}
	
	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}
	
	public String getInput() {
		return input.toString();
	}
	
	public byte getKey(int keyCode) {
		return keys[keyCode];
	}
	
	public Vector3f getMouseCoords(Camera camera) {
		double[] x = new double[1], y = new double[1];
		glfwGetCursorPos(window, x, y);
		Vector3f ret = new Vector3f((float) x[0] - xOff, (float) y[0] - yOff, 0);
		ret.mul(camera.getWidth() / vWidth, camera.getHeight() / vHeight, 1);
		return ret;
	}
	
	public boolean mousePressed(int button) {
		return glfwGetMouseButton(window, button) == GLFW_PRESS;
	}
	
	public int getWidth() {
		return vWidth;
	}
	
	public int getHeight() {
		return vHeight;
	}
	
	public boolean wasResized() {
		return resized;
	}
	
	public float getScrollX() {
		return (float) scrollX;
	}
	
	public float getScrollY() {
		return (float) scrollY;
	}
	
	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(xOff, yOff, vWidth, vHeight);
	}
}
