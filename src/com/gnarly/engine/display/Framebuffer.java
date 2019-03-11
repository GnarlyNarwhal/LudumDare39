package com.gnarly.engine.display;
 
import java.nio.ByteBuffer;
 
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;
 
public class Framebuffer {
 
	private int fbo;
	private int texture;
	private int depth;
	
	private int width, height;
 
	public Framebuffer(int width, int height) {
		this.width = width;
		this.height = height;
		fbo = createFrameBuffer();
		texture = createTextureAttachment();
		depth = createDepthBufferAttachment();
	}
 
	public void cleanUp() {
		glDeleteFramebuffers(fbo);
		glDeleteTextures(texture);
		glDeleteRenderbuffers(depth);
	}
 
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glViewport(0, 0, width, height);
	}
 
	public int getTexture() {
		return texture;
	}
 
	private int createFrameBuffer() {
		int frameBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		glDrawBuffer(GL_COLOR_ATTACHMENT0);
		return frameBuffer;
	}
 
	private int createTextureAttachment() {
		int texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, texture, 0);
		return texture;
	}
 
	private int createDepthBufferAttachment() {
		int depthBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);
		return depthBuffer;
	}
}