package ch.fhnw.pfcs;//  -------------   JOGL 2D-Programm  -------------------

import ch.fhnw.pfcs.objects.RotKoerper;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author Benjamin Leber
 *         Lšngeneinheit E = 1000km = 10^6m
 */
public class Sat extends GLBase1 {

    //  ---------  globale Daten  ---------------------------

    float left = -55, right = 55;             // ViewingVolume
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

    final static double GM = g * rE * rE;

    /**
     * Beschleunigungen
     */
    double ax = 0, ay = -g;

    /**
     * Zeitschritt
     */
    double dt = 1;

    double r1 = 42.05; //Bahnradius
    double v1 = Math.sqrt(GM / r1); //Bahngeschwindigkeit
    Satellit sat1 = new Satellit(r1, 0, 0, v1, 0.25 );

    RotKoerper rotKoerper = new RotKoerper(this);

    private FPSAnimator fpsAnimator;
    private float elevation = 0;
    private float azimut = 0;

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
            //setColor(1, 1, 0, 1);
            putVertex(xm, ym, 0);
        }
        //setColor(1, 0, 0, 1);
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

    void zeichneErde(GL3 gl) {
        rotKoerper.zeichneKugel(gl, (float) rE, 10, 10);
    }

    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        GL3 gl = drawable.getGL().getGL3();
        gl.glClearColor(1, 1, 1, 1);                         // Hintergrundfarbe (RGBA)
        fpsAnimator = new FPSAnimator(drawable, 60, true);
        fpsAnimator.start();
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        // ------  Kamera-System  -------
        float dCam = 10;                 // Abstand vom absoluten Nullpunkt
        setCameraSystem(gl, dCam, elevation, azimut);
        setColor(0, 0, 0);
        loadIdentity(gl);
        drawAxis(gl, 50, 50, 50);
        setColor(Color.BLUE);
        zeichneErde(gl);
        setColor(Color.RED);
        sat1.draw(gl);
        for (int i = 0; i<60; i++) {
            sat1.move(dt);
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
        new Sat();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP : elevation++;
                break;
            case KeyEvent.VK_DOWN: elevation--;
                break;
            case KeyEvent.VK_LEFT: azimut--;
                break;
            case KeyEvent.VK_RIGHT: azimut++;
                break;

        }
        canvas.repaint();
    }

    private class Satellit {
        /**
         * Position
         */
        double x;
        double y;
        double z;
        /**
         * Geschwindigkeit
         */
        double vx, vy;

        /**
         * Kugelradius
         */
        double r;

        public Satellit(double x, double y, double vx, double vy, double r) {
            this.x = x;
            this.y = y;
            this.z = 0;
            this.vx = vx;
            this.vy = vy;
            this.r = r;
        }

        public void move(double dt) {
            double lr = Math.sqrt(x*x + y*y);
            double r3 = lr*lr*lr;
            double ax = -(GM/r3)*x;
            double ay = -(GM/r3)*y;
            x += vx * dt;
            y += vy * dt;
            vx += ax * dt;
            vy += ay * dt;
        }

        public void draw(GL3 gl) {
            translate(gl, x, y, z);
            rotKoerper.zeichneKugel(gl, (float) r, 10,10);
        }
    }

}