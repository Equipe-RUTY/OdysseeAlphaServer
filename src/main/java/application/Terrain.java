package application;

import java.util.ArrayList;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import business.Blob;
import pathfinding.Vector2;

public abstract class Terrain implements GLEventListener {
	
	private int program;
//	private ArrayList<Blob> blobs;
	private float radius;
	private float blobbiness;
	private GLJPanel glpanel;
	private float spaceWidth;
	private float spaceHeight;

	public Terrain(GLJPanel glpanel, float spaceWidth, float spaceHeight) {
		super();
		this.spaceWidth = spaceWidth;
		this.spaceHeight = spaceHeight;
		this.glpanel = glpanel;
		this.glpanel.addGLEventListener(this);
	}
	
	public abstract ArrayList<Blob> getBlobs();

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		
		// il faut aussi modifier le shader (simple.frag)
		final int maxBlobs = 100;

		gl.glUseProgram(program);
		
		ArrayList<Blob> blobs = getBlobs();

		for (int i = 0; i < Math.min(blobs.size(), maxBlobs); i++) {
			String base = "blobs[" + Integer.toString(i) + "]";
			int cloc = gl.glGetUniformLocation(program, base + ".c");
			int rloc = gl.glGetUniformLocation(program, base + ".r");
			int bloc = gl.glGetUniformLocation(program, base + ".b");
			int colorloc = gl.glGetUniformLocation(program, base + ".color");
			Vector2 newcoords = new Vector2(0.f, 0.f);
			newcoords.x = (float)(blobs.get(i).getCoordonnee()[0] * drawable.getSurfaceWidth() / spaceWidth);
			newcoords.y = (float)(blobs.get(i).getCoordonnee()[1] * drawable.getSurfaceHeight() / spaceHeight);
			gl.glUniform2f(cloc, newcoords.x, drawable.getSurfaceHeight() - newcoords.y);
			gl.glUniform1f(rloc, radius); // default: 500
			gl.glUniform1f(bloc, -blobbiness); // default: -0.1
			gl.glUniform3f(colorloc, (float)blobs.get(i).getMaSuperCouleurPreferee().getRed(), (float)blobs.get(i).getMaSuperCouleurPreferee().getGreen(), (float)blobs.get(i).getMaSuperCouleurPreferee().getBlue());
		}
		gl.glUniform1i(gl.glGetUniformLocation(program, "blobCount"), Math.min(blobs.size(), maxBlobs));
		gl.glBegin(GL2.GL_QUADS);// static field
		gl.glVertex3f(-1.0f, -1.0f, 0.0f); // The bottom left corner  
	    gl.glVertex3f(-1.0f, 1.0f, 0.0f); // The top left corner  
	    gl.glVertex3f(1.0f, 1.0f, 0.0f); // The top right corner  
	    gl.glVertex3f(1.0f, -1.0f, 0.0f); // The bottom right corner
		gl.glEnd();
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		ShaderCode vert = ShaderCode.create(gl, GL2.GL_VERTEX_SHADER, this.getClass(), "", null, "src/main/java/simple", "vert", null, true);
		ShaderCode frag = ShaderCode.create(gl, GL2.GL_FRAGMENT_SHADER, this.getClass(), "", null, "src/main/java/simple", "frag", null, true);

		ShaderProgram sprogram = new ShaderProgram();
		sprogram.add(vert);
		sprogram.add(frag);
		sprogram.link(gl, System.out);
		program = sprogram.program();
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
	}
	
	public void setTaille(float r) {
		this.radius = r;
	}
	
	public void setBlobitude(float b) {
		this.blobbiness = b;
	}
	
	public GLJPanel getPanel() {
		return glpanel;
	}
	
	public float getSpaceWidth() {
		return spaceWidth;
	}
	
	public float getSpaceHeight() {
		return spaceHeight;
	}
}
