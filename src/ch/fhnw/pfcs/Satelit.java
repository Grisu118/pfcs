package ch.fhnw.pfcs;//  -------------   JOGL 2D-Programm  -------------------

import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;

/**
 * @author Benjamin Leber
 * Längeneinheit E = 1000km = 10^6m
 */
public class Satelit extends GLBase1 {

    //  ---------  globale Daten  ---------------------------

    float left = -45, right = 45;             // ViewingVolume
    float bottom, top;
    float near = -10, far = 1000;

    /**
     * Erdbeschleunigung
     */
    final static double g = 9.81e-6;
    /**
     * Erdradius 6371km
     */
    final static double rE = 6.371;

    /**
     * Position
     */
    float x = left;
    float x0 = left;
    float y = 0;
    float y0 = 0;
    /**
     * Geschwindigkeit
     */
    double vx0=20, vy0=20;
    double vx=vx0, vy=vy0;
    /**
     * Beschleunigungen
     */
    double ax=0, ay = -g;

    /**
     * Zeitschritt
     */
    double dt = 0.01;

    private FPSAnimator fpsAnimator;

    //  ---------  Methoden  ----------------------------------

    public void zeichneDreieck(GL3 gl,
                               float x1, float y1, float z1,
                               float x2, float y2, float z2,
                               float x3, float y3, float z3) {
        rewindBuffer(gl);
        putVertex(x1, y1, z1);           // Eckpunkte in VertexArray speichern
        putVertex(x2, y2, z2);
        putVertex(x3, y3, z3);
        int nVertices = 3;
        copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, nVertices);
    }

    public void zeichneKreis(GL3 gl, float xm, float ym, float r, boolean fill, int nPunkte) {
        vertexBuf.rewind();
        if (fill) {
            setColor(1, 1, 0, 1);
            putVertex(xm, ym, 0);
        }
        setColor(1, 0, 0, 1);
        double phi = 2 * Math.PI / nPunkte;
        for (int i = 0; i < nPunkte + 1; i++) {
            putVertex((float) (r * Math.cos(phi * i)) + xm, (float) (r * Math.sin(phi * i)) + ym, 0);
            //System.out.println((r * Math.cos(phi) * i) + " : " + (r * Math.sin(phi) * i));
        }
        if (fill) {
            putVertex((float) (r * Math.cos(0)) + xm, (float) (r * Math.sin(0)) + ym, 0);
            copyBuffer(gl, nPunkte + 2);   // VertexArray in OpenGL-Buffer kopieren
            gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, nPunkte + 2);  // Kreis zeichnen
        } else {
            copyBuffer(gl, nPunkte);   // VertexArray in OpenGL-Buffer kopieren
            gl.glDrawArrays(GL3.GL_LINE_LOOP, 0, nPunkte);  // Kreis zeichnen
        }

    }

    public void zeichneBahn(GL3 gl, double x0, double y0, double vx0, double vy0, double dt, int nPunkte) {
        double x1, y1, t;
        for (int i = 0; i < nPunkte; i++) {
            t = i*dt;
            x1 = vx0 * t + x0;
            y1 = -0.5 * g * t * t + vy0 * t + y0;
            putVertex((float)x1, (float)y1, 0);
        }
        copyBuffer(gl, nPunkte);
        gl.glDrawArrays(GL3.GL_LINE_STRIP, 0, nPunkte);
    }

    public void zeichneSpeer(GL3 gl, float w, float h) {
        w *= 0.5f;
        h *= 0.5f;
        float w2 = 0.8f*w;
        rewindBuffer(gl);
        putVertex(-w, 0, 0);
        putVertex(-w2, -h, 0);
        putVertex(w2, -h, 0);
        putVertex(w, 0, 0);
        putVertex(w2, h, 0);
        putVertex(-w2, h, 0);

        int nVertices = 6;
        copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, nVertices);
    }

    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        GL3 gl = drawable.getGL().getGL3();
        gl.glClearColor(1, 1, 1, 1);                         // Hintergrundfarbe (RGBA)
        gl.glDisable(GL3.GL_DEPTH_TEST);                  // ohne Sichtbarkeitstest
        fpsAnimator = new FPSAnimator(drawable, 60, true);
        fpsAnimator.start();
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
        setColor(0, 0, 0);
        loadIdentity(gl);
        drawAxis(gl, 50, 50, 50);
        zeichneBahn(gl, left, 0, vx0, vy0, 0.1, 100);
        setColor(1, 0, 0);
        translate(gl, x, y, 0);
        double alpha = Math.toDegrees(Math.atan(vy/vx));
        rotate(gl, (float)alpha, 0, 0, 1);
        zeichneSpeer(gl, 10, 0.5f);
        x += vx*dt;
        y += vy*dt;
        vx += ax*dt;
        vy += ay*dt;
        if (x > right || y < bottom) {
            x = x0;
            y = y0;
            vx = vx0;
            vy = vy0;
        }
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);
        float aspect = (float)height / width;
        bottom = aspect * left;
        top = aspect * right;
        // -----  Projektionsmatrix festlegen  -----
        setOrthogonalProjection(gl, left, right, bottom, top, near, far);
    }


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args) {
        new Satelit();
    }

}