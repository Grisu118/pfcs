package ch.grisu118.pfcs.a3;

import ch.fhnw.pfcs.GLBase1;
import ch.grisu118.pfcs.a1.Train;
import ch.grisu118.pfcs.a3.objects.Cuboid;
import ch.grisu118.pfcs.a3.objects.RotObjects;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 *
 * @author Benjamin Leber
 */
public class FlyingCuboid extends GLBase1 {
    //  ---------  globale Daten  ---------------------------

    float left = -0.03f, right = 0.03f;             // ViewingVolume
    float bottom = left, top = right;
    float near = 0.4f, far = 2000;
    float dCam = 40;

    volatile long time = 0;
    volatile boolean run = true;

    //  ---------  Methoden  ----------------------------------

    private Thread t;
    private List<RotObjects> objects = new ArrayList<>();


    public FlyingCuboid() {
        super();
        ImageIcon icon = new ImageIcon("res/icon.png");
        if (icon.getIconHeight() < 0) {
            icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
        }

        jFrame.setIconImage(icon.getImage());
        jFrame.setExtendedState(jFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH );

        objects.add(new Cuboid(this));
        objects.add(new Cuboid(this));

        runSimulation();
    }

    private void runSimulation() {

        run = false;
        //noinspection StatementWithEmptyBody
        while (t != null && t.isAlive()) {
            //wait
        }
        run = true;
        t = new Thread(() -> {
            while (run) {
                if (time == 0) {
                    update(0);
                } else {
                    float dt = (System.currentTimeMillis() - time) * 0.001f;
                    update(dt);
                }
                time = System.currentTimeMillis();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        t.start();
    }

    void update(float dt) {
        objects.forEach(o -> {
            o.setZ(o.getZ() + dt);
        });
    }

    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        GL3 gl = drawable.getGL().getGL3();
        gl.glClearColor(0, 0, 1, 1);                         // Hintergrundfarbe (RGBA)
        gl.glDisable(GL3.GL_DEPTH_TEST);                  // ohne Sichtbarkeitstest
        setCameraSystem(gl, dCam, 0, 0);

        FPSAnimator fpsAnimator = new FPSAnimator(drawable, 60, true);
        fpsAnimator.start();
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
        loadIdentity(gl);
        setColor(Color.WHITE);
        drawLine(gl, left, 0, -near, right, 0, -near);
        drawLine(gl, 0, top, -near, 0, bottom, -near);
        setColor(Color.RED);
        objects.forEach(o -> {
            o.draw(gl);
        });
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
        //setOrthogonalProjection(gl, left, right, bottom, top, near, far);
        setPerspectiveProjection(gl, left, right, bottom, top, near, far);
    }


    public boolean checkAreaX(double x) {
        return x >= left && x <= right;
    }
    public boolean checkAreaY(double y) {
        return y >= bottom && y <= top;
    }
    public boolean checkZ(double z) {
        return z >= near && z <= far;
    }

    public float getLeft() {
        return left;
    }

    public float getRight() {
        return right;
    }

    public float getBottom() {
        return bottom;
    }

    public float getTop() {
        return top;
    }

    public float getNear() {
        return near;
    }

    public float getFar() {
        return far;
    }

    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args) {
        new FlyingCuboid();
    }
}
