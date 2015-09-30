package ch.grisu118.pfcs.a1;

import ch.fhnw.pfcs.GLBase1;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL;
import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by benjamin on 17.09.2015.
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

    //  ---------  Methoden  ----------------------------------

    private List<Train> trainList = new LinkedList<>();
    private Thread t;

    public AirTrain() {
        TextField kt = new TextField("1", 5);
        JButton b = new JButton("ReStart");
        JLabel l = new JLabel("k: ");
        JButton clear = new JButton("Clear");
        JButton add = new JButton("Add");
        headerPanel.add(l);
        headerPanel.add(kt);
        headerPanel.add(b);
        headerPanel.add(add);
        headerPanel.add(clear);
        jFrame.setSize(1000, 800);

        kt.addTextListener(e -> {
            String t1 = kt.getText();
            float k1 = 0;
            try {
                k1 = Float.parseFloat(t1);
            } catch (NumberFormatException ex) {
                kt.setText("1");
            }
            if (k1 < 0 || k1 > 1) {
                kt.setText("1");
            }
            k = Float.parseFloat(kt.getText());
        });

        b.addActionListener(e -> {
            runSimulation();
        });

        clear.addActionListener(e -> trainList.clear());

        add.addActionListener(e -> addTrain(new Train(this)));


        runSimulation();
    }

    private void runSimulation() {
        trainList.clear();
        addTrain(new Train(this));
        addTrain(new Train(this));
        addTrain(new Train(this));
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

    private void addTrain(Train t) {
        if (trainList.size() == 0) {
            trainList.add(t);
        } else {
            boolean solved;
            Random r = new Random();
            do {
                solved = true;
                for (Train t2 : trainList) {
                    if (t.checkPosition(t2)) {
                        t.setPosition(((float)r.nextInt(400)-200) / 100);
                        solved = false;
                    }
                }
            } while (!solved);
            trainList.add(t);
        }
    }

    void update(float dt) {
        for (Train t : trainList) {
            if (t.getPosition() + t.getHalfLength() >= 2) {
                t.setPosition(2 - t.getHalfLength());
                t.invertSpeed();
            } else if (t.getPosition() - t.getHalfLength() <= -2) {
                t.setPosition(-2 + t.getHalfLength());
                t.invertSpeed();
            }
            for (Train t2 : trainList) {
                if (t.equals(t2)) {
                    break;
                }
                if (t.checkCollision(t2)) {
                    calcNewSpeed(t, t2);
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
        setColor(1, 1, 1);
        drawBorders(gl);
        setColor(1, 0, 0);
        for (Train t : trainList) {
            t.draw(gl);
        }
    }

    void drawRectangle(GL4 gl, float x, float y, float z, float length, float width, float height) {
        rewindBuffer(gl);
        putVertex(x, y, z);
        putVertex(x + length, y, z);
        putVertex(x + length, y + width, z);
        putVertex(x, y + width, z);
    }

    void drawBorders(GL4 gl) {
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
            gl.glDrawArrays(GL4.GL_LINES, 0, nVertices);
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
        new AirTrain();
    }
}
