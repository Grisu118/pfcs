package ch.grisu118.pfcs.a1;

import ch.fhnw.pfcs.GLBase1;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Main Class for Luftkissenbahn �bung
 *
 * @author Benjamin Leber
 */
public class AirTrain extends GLBase1 {
    //  ---------  globale Daten  ---------------------------

    float left = -3, right = 3;             // ViewingVolume
    float bottom, top;
    float near = -10, far = 1000;

    float leftEnd = -2;
    float rightEnd = 2;

    float k = 1f;

    volatile long time = 0;
    volatile boolean run = true;
    volatile boolean requestAdd = false;

    volatile boolean colorExChange = true;

    //  ---------  Methoden  ----------------------------------

    private List<Train> trainList = new LinkedList<>();
    private Thread t;
    private final JButton add;

    public AirTrain() {
        TextField kt = new TextField("1", 5);
        JButton b = new JButton("ReStart");
        JLabel l = new JLabel("k: ");
        JButton clear = new JButton("Clear");
        JCheckBox box = new JCheckBox("Color Exchange");
        box.setSelected(colorExChange);
        add = new JButton("Add");
        headerPanel.add(l);
        headerPanel.add(kt);
        headerPanel.add(b);
        headerPanel.add(add);
        headerPanel.add(clear);
        headerPanel.add(box);

        ImageIcon icon = new ImageIcon("res/icon.png");
        if (icon.getIconHeight() < 0) {
            icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
        }

        jFrame.setIconImage(icon.getImage());
        jFrame.setExtendedState(jFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        box.addActionListener(e -> colorExChange = box.isSelected());

        kt.addTextListener(e -> {
            String t1 = kt.getText();
            float k1 = 0;
            try {
                k1 = Float.parseFloat(t1);
            } catch (NumberFormatException ex) {
                kt.setText("1");
            }
            if (k1 < 0) {
                kt.setText("1");
            }
            k = Float.parseFloat(kt.getText());
        });

        b.addActionListener(e -> AirTrain.this.runSimulation());

        clear.addActionListener(e -> {
            trainList.clear();
            add.setEnabled(true);
        });

        add.addActionListener(e -> requestAddTrain());


        runSimulation();
    }

    private void runSimulation() {
        add.setEnabled(true);
        trainList.clear();
        addTrain(new Train(this));
        addTrain(new Train(this));
        addTrain(new Train(this));
        run = false;
        //noinspection StatementWithEmptyBody
        while (t != null && t.isAlive()) {
            //wait
        }
        run = true;
        t = new Thread(() -> {
            while (run) {
                if (requestAdd) {
                    add.setEnabled(addTrain(new Train(this)));
                    requestAdd = false;
                }
                if (time == 0) {
                    update(0);
                } else {
                    float dt = (System.currentTimeMillis() - time) * 0.001f;
                    update2(dt);
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

    private void requestAddTrain() {
        requestAdd = true;
    }

    private boolean addTrain(Train t) {
        if (trainList.size() == 0) {
            trainList.add(t);
            return true;
        } else {
            int tries = 5;
            boolean solved;
            Random r = new Random();
            do {
                solved = true;
                for (Train t2 : trainList) {
                    if (t.checkPosition(t2)) {
                        t.setPosition(((float) r.nextInt(400) - 200) / 100);
                        solved = false;
                    }
                }
                if (tries-- < 0) {
                    break;
                }
            } while (!solved);
            if (solved) {
                trainList.add(t);
                return true;
            }
        }
        return false;
    }

    void update2(float dt) {
        float step = 0.0001f;
        while (dt > 0) {
            update(step);
            dt -= step;
        }
    }

    void update(float dt) {
        for (int i = 0; i < trainList.size(); i++) {
            Train t = trainList.get(i);
            if (t.getPosition() + t.getHalfLength() >= 2) {
                t.setPosition(2 - t.getHalfLength());
                t.invertSpeed();
            } else if (t.getPosition() - t.getHalfLength() <= -2) {
                t.setPosition(-2 + t.getHalfLength());
                t.invertSpeed();
            }

            for (int j = i + 1; j < trainList.size(); j++) {
                Train t2 = trainList.get(j);
                if (t.checkCollision(t2)) {
                    calcNewSpeed(t, t2);
                    if (colorExChange) {
                        t.colorChange(t2);
                    }
                }
            }


            t.setPosition(t.getPosition() + t.getSpeed() * dt);

        }
    }

    void calcNewSpeed(Train t1, Train t2) {
        float v1 = (t1.getMass() * t1.getSpeed() + t2.getMass() * t2.getSpeed() - (t1.getSpeed() - t2.getSpeed()) * t2.getMass() * k) / (t1.getMass() + t2.getMass());
        float v2 = (t2.getMass() * t2.getSpeed() + t1.getMass() * t1.getSpeed() - (t2.getSpeed() - t1.getSpeed()) * t1.getMass() * k) / (t1.getMass() + t2.getMass());
        t1.setSpeed(v1);
        t2.setSpeed(v2);
    }

    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        GL3 gl = drawable.getGL().getGL3();
        gl.glClearColor(0, 0, 1, 1);                         // Hintergrundfarbe (RGBA)
        gl.glDisable(GL3.GL_DEPTH_TEST);                  // ohne Sichtbarkeitstest

        FPSAnimator fpsAnimator = new FPSAnimator(drawable, 60, true);
        fpsAnimator.start();
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
        setColor(1, 1, 1);
        drawBorders(gl);
        setColor(1, 0, 0);
        for (Train t : trainList) {
            t.draw(gl);
        }
    }

    void drawRectangle(GL3 gl, float x, float y, float z, float length, float width, float height) {
        rewindBuffer(gl);
        putVertex(x, y, z);
        putVertex(x + length, y, z);
        putVertex(x + length, y + width, z);
        putVertex(x, y + width, z);
    }

    void drawBorders(GL3 gl) {
        {
            rewindBuffer(gl);
            putVertex(leftEnd, 10, 0);           // Eckpunkte in VertexArray speichern
            putVertex(leftEnd, -10, 0);
            putVertex(rightEnd, 10, 0);
            putVertex(rightEnd, -10, 0);
            putVertex(leftEnd, 0, 0);
            putVertex(rightEnd, 0, 0);
            int nVertices = 6;
            copyBuffer(gl, nVertices);
            gl.glDrawArrays(GL3.GL_LINES, 0, nVertices);
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
        new AirTrain();
    }
}
