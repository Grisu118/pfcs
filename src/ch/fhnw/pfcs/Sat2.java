package ch.fhnw.pfcs;//  -------------   JOGL 2D-Programm  -------------------

import ch.fhnw.pfcs.objects.RotKoerper;
import ch.fhnw.util.math.Dynamics;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author Benjamin Leber
 *         L�ngeneinheit E = 1000km = 10^6m
 */
public class Sat2 extends GLBase1 {

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
    Satellit sat1 = new Satellit(r1, 0, 0, v1, 0.25); //Stationär
    Satellit sat4 = new Satellit(r1, 0, 0, v1, 0.25); //Stationär
    double r2 = 26.56;
    double v2 = Math.sqrt(GM / r2);
    Satellit sat2 = new Satellit(r2, 0, 0, v2, 0.25); //GPS
    Satellit sat3 = new Satellit(r2, 0, 0, v2, 0.25); //GPS

    RotKoerper rotKoerper = new RotKoerper(this);

    private FPSAnimator fpsAnimator;
    float dCam = 60;
    private float elevation = 10;
    private float azimut = 40;

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
        rotKoerper.zeichneKugel(gl, (float) rE, 15, 15);
    }

    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        GL3 gl = drawable.getGL().getGL3();
        gl.glClearColor(1, 1, 1, 1);                         // Hintergrundfarbe (RGBA)

        fpsAnimator = new FPSAnimator(drawable, 60, true);
        fpsAnimator.start();

        System.out.println(String.format("Geschwindigkeit Satelit 1: %.2f km/s", v1 * 1000));
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        // ------  Kamera-System  -------
        loadIdentity(gl);
        setColor(0, 0, 0);
        setShadingLevel(gl, 0);
        setCameraSystem(gl, dCam, elevation, azimut);
        drawAxis(gl, 50, 50, 50);
        setColor(Color.BLUE);
        setShadingLevel(gl, 1);
        setLightPosition(gl, -20, 50, 80);
        zeichneErde(gl);
        setColor(Color.RED);
        pushMatrix(gl);
        rotate(gl, -90, 1, 0, 0);
        sat1.draw(gl);
        sat4.draw(gl);
        popMatrix(gl);
        pushMatrix(gl);
        rotate(gl, 55, 0, 0, 1);
        rotate(gl, -90, 1, 0, 0);
        sat2.draw(gl);
        sat3.draw(gl);
        popMatrix(gl);
        for (int i = 0; i < 60; i++) {
            sat1.move(dt);
            sat2.move(dt);
            sat3.move2(dt);
            sat4.move2(dt);
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
        new Sat2();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                elevation++;
                break;
            case KeyEvent.VK_DOWN:
                elevation--;
                break;
            case KeyEvent.VK_LEFT:
                azimut--;
                break;
            case KeyEvent.VK_RIGHT:
                azimut++;
                break;

        }
        canvas.repaint();
    }

    private class Satellit extends Dynamics {
        /**
         * Position und Speed
         */
        double[] X;

        /**
         * Kugelradius
         */
        double r;

        public Satellit(double x, double y, double vx, double vy, double r) {
            this.X = new double[4];
            this.X[0] = x;
            this.X[1] = y;
            this.X[2] = vx;
            this.X[3] = vy;
            this.r = r;
        }

        public void move(double dt) {
            double lr = Math.sqrt(X[0] * X[0] + X[1] * X[1]);
            double r3 = lr * lr * lr;
            double ax = -(GM / r3) * X[0];
            double ay = -(GM / r3) * X[1];
            X[0] += X[2] * dt;
            X[1] += X[3] * dt;
            X[2] += ax * dt;
            X[3] += ay * dt;
        }

        public void move2(double dt) {
            X = runge(X, dt);
        }

        public void draw(GL3 gl) {
            pushMatrix(gl);
            translate(gl, X[0], X[1], 0);
            rotKoerper.zeichneKugel(gl, (float) r, 10, 10);
            popMatrix(gl);
        }

        @Override
        public double[] f(double[] x) {
            double lr = Math.sqrt(X[0] * X[0] + X[1] * X[1]);
            double r3 = lr * lr * lr;
            return new double[]{
                    x[2], //vx
                    x[3], //vy
                    -(GM / r3) * X[0],
                    -(GM / r3) * X[1]
            };
        }
    }

}