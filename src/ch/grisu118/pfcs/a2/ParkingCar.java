package ch.grisu118.pfcs.a2;

import ch.fhnw.pfcs.GLBase1;
import ch.grisu118.pfcs.a2.Vehicles.*;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;


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
    private final JLabel speedLabel;

    public ParkingCar() {



        vehicles.add(Car.CarFactory(this, Car.Cars.MercedesBenzGL));
        vehicles.add(Car.CarFactory(this, Car.Cars.VWGolf));
        vehicles.add(new Trike(this, "Dreirad"));
        vehicles.add(new SmallTruck(this, "SuLa 10"));
        vehicles.add(new BigTruck(this, "5 Achs Sattelzugmaschine"));
        vehicles.add(new Kiro(this, "Kirovets K700A [BETA]"));

        ImageIcon icon = new ImageIcon("res/icon.png");
        if (icon.getIconHeight() < 0) {
            icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
        }

        jFrame.setIconImage(icon.getImage());

        headerPanel.setLayout(new BorderLayout());
        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;

        JList<Vehicle> vList = new JList<>(vehicles.toArray(new Vehicle[1]));
        vList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        vList.setLayoutOrientation(JList.VERTICAL);
        vList.setVisibleRowCount(-1);
        JScrollPane listScroller = new JScrollPane(vList);
        listScroller.setPreferredSize(new Dimension(250, 60));
        leftPanel.add(listScroller, c);
        JButton select = new JButton("Fahrzeug wählen");
        select.addActionListener(e -> {
            if (activeVehicle != null) {
                activeVehicle.setAngle(0);
                activeVehicle.setSpeed(0);
            }
            activeVehicle = vList.getSelectedValue();
        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
        leftPanel.add(select, c);
        headerPanel.add(leftPanel, BorderLayout.WEST);
        JCheckBox debugBox = new JCheckBox("Debug");
        debugBox.addActionListener(e -> vehicles.forEach(v -> v.setDebug(debugBox.isSelected())));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 1;
        leftPanel.add(debugBox, c);

        JTextArea center = new JTextArea(
                "left/right, A/D: Lenkeinschlag ändern \n" +
                "up/down, W/S: Beschleunigen / Bremsen \n" +
                "SPACE: Stark Bremsen \n" +
                "SHIFT: Schneller Beschleunigen/Bremsen \n" +
                "R: Reset \n" +
                "E: Reset Lenkeinschlag");
        center.setFocusable(false);
        center.setEditable(false);
        center.setBackground(headerPanel.getBackground());
        center.setMargin(new Insets(0, 10, 0, 0));

        headerPanel.add(center, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        headerPanel.add(rightPanel, BorderLayout.EAST);
        speedLabel = new JLabel("0 km/h");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        rightPanel.add(speedLabel, c);

        jFrame.setExtendedState(jFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
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
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
        activeVehicle.setAngle(Math.toDegrees(omega * dt));
        activeVehicle.setX(activeVehicle.getSpeed() * dt);
        this.setSpeedLabel(activeVehicle.getSpeed());
    }

    private void setSpeedLabel(double speed) {
        speedLabel.setText(String.format("%.2f km/h", speed * 3.6));
    }

    private void handleInput(float dt) {
        double mult = 0.8;
        if (keys[KEY_SHIFT]) {
            mult = 8;
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
                activeVehicle.setSpeed(activeVehicle.getSpeed() <= 15 * dt ? 0 : activeVehicle.getSpeed() - 15 * dt);
            } else if (activeVehicle.getSpeed() < 0) {
                activeVehicle.setSpeed(activeVehicle.getSpeed() >= 15 * dt ? 0 : activeVehicle.getSpeed() + 15 * dt);
            }
        }
    }

    private void drawParcArea(GL3 gl) {
        drawParcField(gl, -3, -10);
        drawParcField(gl, -3+2.35f, -10);
        drawParcField(gl, -3+2*2.35f, -10);
        drawParcField(gl, -3+3*2.35f, -10);
        drawParcField(gl, -3+4*2.35f, -10);
    }

    private void drawParcField(GL3 gl, float x, float y) {
        float width = 2.35f;
        float length = 5;
        float[] c = getColor();
        setColor(1,1,1);
        drawLine(gl, x-width*0.5f, y-length*0.5f, x+width*0.5f, y-length*0.5f);
        drawLine(gl, x+width*0.5f, y-length*0.5f, x+width*0.5f, y+length*0.5f);
        drawLine(gl, x+width*0.5f, y+length*0.5f, x-width*0.5f, y+length*0.5f);
        drawLine(gl, x-width*0.5f, y+length*0.5f, x-width*0.5f, y-length*0.5f);
        setColor(c);
    }


    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        GL3 gl = drawable.getGL().getGL3();
        gl.glClearColor(160.0f/255, 160.0f/255, 160.0f/255, 1);                         // Hintergrundfarbe (RGBA)
        gl.glDisable(GL3.GL_DEPTH_TEST);                  // ohne Sichtbarkeitstest

        FPSAnimator fpsAnimator = new FPSAnimator(drawable, 60, true);
        fpsAnimator.start();
    }


    @Override
    public void display(GLAutoDrawable drawable) {

        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
        loadIdentity(gl);
        setColor(1, 1, 1);
        drawAxis(gl, 20, 20, 20);
        drawParcArea(gl);
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
            case KeyEvent.VK_R:
                activeVehicle.reset();
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

    public void drawLine(GL3 gl, double x0, double y0, double x1, double y1) {
        rewindBuffer(gl);
        putVertex(x0, y0, 0);
        putVertex(x1, y1, 0);
        copyBuffer(gl, 2);
        gl.glDrawArrays(GL3.GL_LINES, 0, 2);
        rewindBuffer(gl);
    }
}
