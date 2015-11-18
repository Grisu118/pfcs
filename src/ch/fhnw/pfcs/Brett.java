package ch.fhnw.pfcs;//  -------------   JOGL 2D-Programm  -------------------

import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import java.awt.event.KeyEvent;

public class Brett extends GLBase1 {

    //  ---------  globale Daten  ---------------------------

    float left = -2, right = 2;             // ViewingVolume
    float bottom, top;
    float near = -10, far = 1000;

    /**
     * Erdbeschleunigung
     */
    final static double g = 9.81;

    /**
     * Zeitschritt
     */
    double dt = 0.001;

    /*Brett*/
    double a = 1.1, b = 0.01, m = 1;
    double phi0 = Math.toRadians(40), omega0 = 0;
    double phi = phi0, omega = omega0;
    double I = m * (a * a + b * b) / 12 + m * a * a / 4;

    /**
     * Kugel
     * Position
     */

    float x0 = (float) (a*Math.cos(phi0));
    float x = x0;
    float y0 = (float) (a*Math.sin(phi0));
    float y = y0;
    /**
     * Geschwindigkeit
     */
    double vx0 = 0, vy0 = 0;
    double vx = vx0, vy = vy0;
    /**
     * Beschleunigungen
     */
    double ax = 0, ay = -g;

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

    public void zeichneRechteck(GL3 gl, float w, float h) {
        rewindBuffer(gl);
        putVertex(0, -h / 2, 0);
        putVertex(w, -h / 2, 0);
        putVertex(w, h / 2, 0);
        putVertex(0, h / 2, 0);

        int nVertices = 4;
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
        setColor(1, 0, 0);
        zeichneKreis(gl, x, y, 0.02f, true, 20);

        rotate(gl, (float) Math.toDegrees(phi), 0, 0, 1);
        zeichneRechteck(gl, (float) a, (float) b);

        if (phi > 0) {
            phi += omega*dt;
            double alpha = -(a / 2 * m * g * Math.cos(phi)) / I;
            omega += alpha * dt;
            x += vx * dt;
            y += vy * dt;
            vx += ax * dt;
            vy += ay * dt;
        }

    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);
        float aspect = (float) height / width;
        bottom = aspect * left;
        top = aspect * right;
        // -----  Projektionsmatrix festlegen  -----
        setOrthogonalProjection(gl, left, right, bottom, top, near, far);
    }


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args) {
        new Brett();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        phi = phi0;
        omega = omega0;
        vx = vx0;
        vy = vy0;
        x = x0;
        y = y0;
    }
}