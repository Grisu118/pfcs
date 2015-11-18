package ch.grisu118.pfcs.a3;

import ch.fhnw.pfcs.GLBase1;
import ch.fhnw.pfcs.objects.Cuboid;
import ch.grisu118.pfcs.a3.objects.FlyingCuboid;
import ch.grisu118.pfcs.util.Animatable;
import ch.grisu118.pfcs.util.Simulator;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

/**
 * Main Class for Storm Exercise.
 *
 * @author Benjamin Leber
 */
public class StormMain extends GLBase1 {

    //  ---------  globale Daten  ---------------------------

    float left = -0.06f, right = 0.06f;
    float bottom, top;
    float near = 0.4f, far = 2000;

    float dCam = 0;                 // Abstand vom absoluten Nullpunkt
    float elevation = 0;            // Orientierung
    float azimut = 0;

    private Animatable[] objects;

    private volatile float speedMultiplication = 1;

    Cuboid cub = new Cuboid(0.02f, 0.02f, 0.02f, this);
    Color cubColor = Color.blue;
    private boolean fullscreen = false;
    private FPSAnimator fpsAnimator;
    private Simulator sim;

    private int prevHeight;
    private int prevWidth;
    private int prevY;
    private int prevX;


    //  ---------  Methoden  ----------------------------------

    public StormMain() {
        super();
        vShader = "vShader_fog.glsl";

        createUI();


        ImageIcon icon = new ImageIcon("res/icon.png");
        if (icon.getIconHeight() < 0) {
            //noinspection ConstantConditions
            icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
        }

        jFrame.setIconImage(icon.getImage());
        jFrame.setExtendedState(jFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);

        regenerateObjects(4000);
        sim = new Simulator(objects);
        new Thread(sim).start();
    }

    public void regenerateObjects(int n) {
        objects = new Animatable[n];
        for (int i = 0; i < objects.length; i++) {
            objects[i] = generateObject();
        }

        for (int i = 0; i < 100; i++) {
            for (Animatable object : objects) {
                object.update(0.1);
            }
        }
    }

    public FlyingCuboid generateObject() {

        return new FlyingCuboid(cub,
                (float) Math.random() * 10 + 1,
                ((float) Math.random() - 0.5f) * 4, ((float) Math.random() - 0.5f) * 4, -100,
                (float) Math.random(), (float) Math.random(), (float) Math.random(), (float) Math.random() * 10, this);
    }

    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        GL3 gl = drawable.getGL().getGL3();
        setShadingLevel(gl, 1);
        setLightPosition(gl, 5, 5, 0);
        gl.glClearColor(0, 0, 0, 1);
        fpsAnimator = new FPSAnimator(drawable, 60, true);
        fpsAnimator.start();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        fpsAnimator.remove(drawable);
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        // ------  Kamera-System  -------

        setCameraSystem(gl, dCam, elevation, azimut);
        setColor(0.8f, 0.8f, 0f, 1);
        setLightPosition(gl, 0, 6, 10);
        drawAxis(gl, 8, 8, 8);             //  Koordinatenachsen
        setColor(cubColor);
        if (objects != null) {
            for (Animatable o : objects) {
                o.draw(gl);
            }
        }
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);

        // -----  Projektionsmatrix festlegen  -----
        float aspect = (float) height / width;
        bottom = aspect * left;
        top = aspect * right;
        setPerspectiveProjection(gl, left, right, bottom, top, near, far);
    }


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args) {
        new StormMain();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_D:
                azimut++;
                break;
            case KeyEvent.VK_W:
                elevation++;
                break;
            case KeyEvent.VK_S:
                elevation--;
                break;
            case KeyEvent.VK_A:
                azimut--;
                break;
            case KeyEvent.VK_SPACE:
                sim.setPause(!sim.isPause());
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_F11:
                if (!fullscreen) {
                    sim.setPause(true);
                    headerPanel.setVisible(false);
                    prevX = jFrame.getX();
                    prevY = jFrame.getY();
                    prevWidth = jFrame.getWidth();
                    prevHeight = jFrame.getHeight();
                    jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    jFrame.dispose();
                    jFrame.setUndecorated(true);
                    jFrame.setVisible(true);
                    canvas.requestFocus();
                    fullscreen = true;
                    sim.setPause(false);
                } else {
                    sim.setPause(true);
                    headerPanel.setVisible(true);
                    jFrame.setExtendedState(JFrame.NORMAL);
                    jFrame.setBounds(prevX, prevY, prevWidth, prevHeight);
                    jFrame.dispose();
                    jFrame.setUndecorated(false);
                    jFrame.setVisible(true);
                    canvas.requestFocus();
                    fullscreen = false;
                    sim.setPause(false);
                }
        }
        canvas.display();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        if (fullscreen) {
            if (headerPanel.isVisible()) {
                if (e.getY() > 10) {
                    headerPanel.setVisible(false);
                }
            } else {
                if (e.getY() < 5) {
                    headerPanel.setVisible(true);
                }
            }
        }
    }

    private void createUI() {
        headerPanel.setLayout(new GridBagLayout());
        JLabel textLabel = new JLabel("Set Speed");
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        headerPanel.add(textLabel, constraints);

        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 100);
        speedSlider.addChangeListener(e -> {
            float val = ((JSlider) e.getSource()).getValue();
            val /= 100;
            speedMultiplication = val;
        });
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0, new JLabel("0x"));
        labelTable.put(100, new JLabel("1x"));
        labelTable.put(500, new JLabel("5x"));
        labelTable.put(1000, new JLabel("10x"));
        speedSlider.setMajorTickSpacing(100);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setLabelTable(labelTable);
        constraints.gridy++;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(speedSlider, constraints);

        JButton color = new JButton("Color");
        color.addActionListener(this::colorChooser);
        headerPanel.add(color, constraints);
    }

    private void colorChooser(ActionEvent e) {
        Color c = JColorChooser.showDialog(
                ((Component) e.getSource()).getParent(),
                "Choose Color", Color.blue);
        if (c != null) {
            cubColor = c;
        }
    }

    public float getSpeedMultiplication() {
        return speedMultiplication;
    }

}
