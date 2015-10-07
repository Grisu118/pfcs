package ch.grisu118.pfcs.a2;

import ch.fhnw.pfcs.GLBase1;
import ch.grisu118.pfcs.a1.Train;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by benjamin on 17.09.2015.
 */
public class ParkingCar extends GLBase1 {
    //  ---------  globale Daten  ---------------------------

    float left = -30, right = 30;             // ViewingVolume
    float bottom, top;
    float near = -10, far = 1000;

    float leftEnd = -2;
    float rightEnd = 2;

    float k = 1f;

    volatile long time = 0;
    volatile boolean run = true;

    //  ---------  Methoden  ----------------------------------

    private Thread t;
    private List<Vehicle> vehicles = new ArrayList<>();
    private int keyModifier = 1;

    public ParkingCar() {

        jFrame.setSize(1000, 800);
        Vehicle v = new Car(this);
        v.setAlpha(0);
        v.setSpeed(0);
        vehicles.add(v);
        runSimulation();
    }

    private void runSimulation() {

        run = false;
        while (t != null && t.isAlive()) {
            //wait
        }
        run = true;
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (run) {
                    if (time == 0) {
                        update(0);
                    } else {
                        float dt = (System.currentTimeMillis() - time) * 0.001f;
                        update(dt);
                    }

                    time = System.currentTimeMillis();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }


    void update(float dt) {
        for (Vehicle v : vehicles) {
            double omega = v.getSpeed() / v.getYm();
            v.setAngle((omega*dt));
            v.setX(v.getSpeed()/50.0*dt);
        }
    }


    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        GL4 gl = drawable.getGL().getGL4();
        gl.glClearColor(0, 0, 1, 1);                         // Hintergrundfarbe (RGBA)
        gl.glDisable(GL4.GL_DEPTH_TEST);                  // ohne Sichtbarkeitstest

        FPSAnimator fpsAnimator = new FPSAnimator(drawable, 60, true);
        fpsAnimator.start();
    }


    @Override
    public void display(GLAutoDrawable drawable) {

        GL4 gl = drawable.getGL().getGL4();
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT);
        loadIdentity(gl);
        setColor(1, 1, 1);
        drawAxis(gl, 20, 20, 20);
        setColor(1, 0, 0);
        for (Vehicle v : vehicles) {
            if (v.getMatrix() != null) {
                setModelViewMatrix(gl, v.getMatrix());
            }
            if (v.getAlpha() != 0) {
                translate(gl, 0, v.getYm(), 0);
                rotate(gl, (float) v.getAngle(), 0, 0, 1);
                translate(gl, 0, -v.getYm(), 0);
            } else {
                translate(gl, v.getX(), 0, 0);
            }
            v.setMatrix(getModelViewMatrix(gl));
            v.draw(gl);
        }
    }



    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height) {
        GL4 gl = drawable.getGL().getGL4();
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
        new ParkingCar();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);
        System.out.println(e.getKeyChar());
        switch (e.getKeyChar()) {
            case 'w' :
            {
                for (Vehicle v : vehicles) {
                    v.setSpeed(v.getSpeed() + 1);
                }
                break;
            }
            case 'W' : {
                for (Vehicle v : vehicles) {
                    v.setSpeed(v.getSpeed() + 50);
                }
                break;
            }
            case 'S' :
            case 's' : {
                for (Vehicle v : vehicles) {
                    v.setSpeed(v.getSpeed() - 1);
                }
                break;
            }
            case 'D' :
            case 'd' : {
                for (Vehicle v : vehicles) {
                    v.setAlpha(v.getAlpha() - 1);
                }
                break;
            }
            case 'A' :
            case 'a' : {
                for (Vehicle v : vehicles) {
                    v.setAlpha(v.getAlpha() + 1);
                }
                break;
            }
            case KeyEvent.VK_SPACE : {
                for (Vehicle v : vehicles) {
                    v.setSpeed(0);
                }
                break;
            }
        }
    }

}
