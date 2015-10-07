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
    private Vehicle activeVehicle;
    private volatile boolean[] keys = new boolean[7];

    private final static int KEY_W = 0;
    private final static int KEY_A = 1;
    private final static int KEY_S = 2;
    private final static int KEY_D = 3;
    private final static int KEY_SHIFT = 4;
    private final static int KEY_SPACE = 5;
    private final static int KEY_E = 6;

    public ParkingCar() {

        jFrame.setSize(1000, 800);
        Vehicle v = new Car(this);
        v.setAlpha(0);
        v.setSpeed(0);
        vehicles.add(v);
        activeVehicle = v;
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
        handleInput(dt);

        double omega = activeVehicle.getSpeed() / activeVehicle.getYm();
        activeVehicle.setAngle((omega * dt));
        activeVehicle.setX(activeVehicle.getSpeed() / 50.0 * dt);
    }

    private void handleInput(float dt) {
        double mult = 30;
        if (keys[KEY_SHIFT]) {
            mult = 300;
        }
        if (keys[KEY_W]) {
            activeVehicle.setSpeed(activeVehicle.getSpeed() + mult*dt);
        }
        if (keys[KEY_S]) {
            activeVehicle.setSpeed(activeVehicle.getSpeed() - mult*dt);
        }

        if (keys[KEY_A]) {
            activeVehicle.setAlpha(activeVehicle.getAlpha() + 100*dt);
        }
        if (keys[KEY_D]) {
            activeVehicle.setAlpha(activeVehicle.getAlpha() - 100*dt);
        }
        if (keys[KEY_E]) {
            activeVehicle.setAlpha(0);
        }

        if (keys[KEY_SPACE]) {
            activeVehicle.setSpeed(activeVehicle.getSpeed() <= 500*dt ? 0 : activeVehicle.getSpeed() - 500*dt);
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
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                keys[KEY_W] = true;
                break;
            case KeyEvent.VK_S:
                keys[KEY_S] = true;
                break;
            case KeyEvent.VK_A:
                keys[KEY_A] = true;
                break;
            case KeyEvent.VK_D:
                keys[KEY_D] = true;
                break;
            case KeyEvent.VK_E:
                keys[KEY_E] = true;
                break;
            case KeyEvent.VK_SPACE:
                keys[KEY_SPACE] = true;
                break;
            case KeyEvent.VK_SHIFT:
                keys[KEY_SHIFT] = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                keys[KEY_W] = false;
                break;
            case KeyEvent.VK_S:
                keys[KEY_S] = false;
                break;
            case KeyEvent.VK_A:
                keys[KEY_A] = false;
                break;
            case KeyEvent.VK_D:
                keys[KEY_D] = false;
                break;
            case KeyEvent.VK_E:
                keys[KEY_E] = false;
                break;
            case KeyEvent.VK_SPACE:
                keys[KEY_SPACE] = false;
                break;
            case KeyEvent.VK_SHIFT:
                keys[KEY_SHIFT] = false;
                break;
            default:
                break;
        }
    }
}
