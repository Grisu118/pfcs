package ch.fhnw.pfcs;//  -------------   JOGL 2D-Programm  -------------------

import ch.fhnw.pfcs.objects.RotKoerper;
import ch.fhnw.util.Trail;
import ch.fhnw.util.math.Dynamics;
import ch.fhnw.util.math.Vec3;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * @author Benjamin Leber
 *         L�ngeneinheit E = 1000km = 10^6m
 */
public class Lorenz extends GLBase1 {

    //  ---------  globale Daten  ---------------------------

    float left = -60, right = 60;             // ViewingVolume
    float bottom, top;
    float near = -10, far = 1000;

    /**
     * Zeitschritt
     */
    double dt = 0.01;
    double[] x0 = {10, 10, 10};
    Satellit sat1 = new Satellit(x0, 1); //Stationär
    Trail t = new Trail(2000);


    RotKoerper rotKoerper = new RotKoerper(this);

    private FPSAnimator fpsAnimator;
    float dCam = 60;
    private float elevation = 10;
    private float azimut = 40;

    //  ---------  Methoden  ----------------------------------

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
        loadIdentity(gl);
        setColor(0, 0, 0);
        setShadingLevel(gl, 0);
        setCameraSystem(gl, dCam, elevation, azimut);
        drawAxis(gl, 50, 50, 50);
        setColor(Color.RED);
        t.add(new Vec3(sat1.x[0], sat1.x[1], sat1.x[2]));
        sat1.draw(gl);
        setColor(Color.BLUE);
        t.draw(this, gl);
        sat1.move(dt);


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
        new Lorenz();
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
         * Position
         */
        double[] x;

        /**
         * Kugelradius
         */
        double r;

        public Satellit(double[] x, double r) {
            this.x = Arrays.copyOf(x, x.length);
            this.r = r;
        }

        public void move(double dt) {
            x = runge(x, dt);
        }

        public void draw(GL3 gl) {
            pushMatrix(gl);
            translate(gl, x[0], x[1], x[2]);
            rotKoerper.zeichneKugel(gl, (float) r, 10, 10);
            popMatrix(gl);
        }

        @Override
        public double[] f(double[] x) {
            return new double[]{
                    10 * x[1] - 10 * x[0],
                    28 * x[0] - x[1] - x[0] * x[2],
                    x[0] * x[1] - 8.0 / 3 * x[2]};
        }
    }

}