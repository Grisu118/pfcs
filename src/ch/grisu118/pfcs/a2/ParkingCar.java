package ch.grisu118.pfcs.a2;

import ch.fhnw.pfcs.GLBase1;
import ch.grisu118.pfcs.a2.Vehicles.*;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

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
    private boolean isDebug = false;

    public ParkingCar() {



        vehicles.add(new Car(this, "Auto 1"));
        vehicles.add(new Car(this, "Auto 2"));
        vehicles.add(new Trike(this, "Dreirad"));
        vehicles.add(new SmallTruck(this, "SuLa 10"));
        vehicles.add(new BigTruck(this, "5 Achs Sattelzugmaschine"));
        vehicles.add(new Kiro(this, "Kirovets K700A"));



        headerPanel.setLayout(new BorderLayout());
        JPanel leftPanel = new JPanel(new GridLayout(2,1));

        JList<Vehicle> vList = new JList<>(vehicles.toArray(new Vehicle[1]));
        vList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        vList.setLayoutOrientation(JList.VERTICAL);
        vList.setVisibleRowCount(-1);
        JScrollPane listScroller = new JScrollPane(vList);
        listScroller.setPreferredSize(new Dimension(250, 80));
        leftPanel.add(listScroller);
        JButton select = new JButton("Select Vehicle");
        select.addActionListener(e -> {
            if (activeVehicle != null) {
                activeVehicle.setAngle(0);
                activeVehicle.setSpeed(0);
            }
            activeVehicle=vList.getSelectedValue();
        });
        leftPanel.add(select);
        headerPanel.add(leftPanel, BorderLayout.WEST);
        JCheckBox debugBos = new JCheckBox("Debug");
        debugBos.addActionListener(e -> isDebug = !isDebug);
        headerPanel.add(debugBos, BorderLayout.NORTH);

        jFrame.setSize(1000, 800);
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
        if (activeVehicle == null) {
            return;
        }
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
            activeVehicle.setAlpha(activeVehicle.getAlpha() + activeVehicle.getAngleMofifier()*dt);
        }
        if (keys[KEY_D]) {
            activeVehicle.setAlpha(activeVehicle.getAlpha() - activeVehicle.getAngleMofifier()*dt);
        }
        if (keys[KEY_E]) {
            activeVehicle.setAlpha(0);
        }

        if (keys[KEY_SPACE]) {
            if (activeVehicle.getSpeed() > 0) {
                activeVehicle.setSpeed(activeVehicle.getSpeed() <= 500 * dt ? 0 : activeVehicle.getSpeed() - 500 * dt);
            } else {
                activeVehicle.setSpeed(activeVehicle.getSpeed() >= 500 * dt ? 0 : activeVehicle.getSpeed() + 500 * dt);
            }
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
        if (activeVehicle != null) {
            if (activeVehicle.getMatrix() != null) {
                setModelViewMatrix(gl, activeVehicle.getMatrix());
            }
            if (activeVehicle.getAlpha() != 0) {
                translate(gl, 0, activeVehicle.getYm(), 0);
                rotate(gl, (float) activeVehicle.getAngle(), 0, 0, 1);
                translate(gl, 0, -activeVehicle.getYm(), 0);
            } else {
                translate(gl, activeVehicle.getX(), 0, 0);
            }
            activeVehicle.setMatrix(getModelViewMatrix(gl));
            activeVehicle.draw(gl);
            if (isDebug) {
                activeVehicle.drawDebug(gl);
            }
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
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                keys[KEY_W] = true;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                keys[KEY_S] = true;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                keys[KEY_A] = true;
                break;
            case KeyEvent.VK_RIGHT:
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
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                keys[KEY_W] = false;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                keys[KEY_S] = false;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                keys[KEY_A] = false;
                break;
            case KeyEvent.VK_RIGHT:
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
