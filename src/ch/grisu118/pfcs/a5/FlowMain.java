package ch.grisu118.pfcs.a5;

import ch.fhnw.pfcs.GLBase1;
import ch.fhnw.util.math.Dynamics;
import ch.grisu118.pfcs.util.Animatable;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.ConcurrentLinkedDeque;


public class FlowMain extends GLBase1 {
    //  ---------  globale Daten  ---------------------------

    float left = -1, right = 1;             // ViewingVolume
    float bottom, top;
    float near = -10, far = 1000;

    volatile long time = 0;
    volatile boolean run = true;

    final float R = 0.3f;
    final FlowLines line;
    boolean isIdeal = true;
    boolean isCirc = false;
    private boolean random = false;

    //  ---------  Methoden  ----------------------------------

    private Thread t;


    public FlowMain() {

        ImageIcon icon = new ImageIcon("res/icon.png");
        if (icon.getIconHeight() < 0) {
            icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
        }
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setIconImage(icon.getImage());

        headerPanel.setLayout(new BorderLayout());

        jFrame.setExtendedState(jFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);

        line = new FlowLines();

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
        line.update(dt);
    }


    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        GL3 gl = drawable.getGL().getGL3();
        gl.glClearColor(160.0f / 255, 160.0f / 255, 160.0f / 255, 1);                         // Hintergrundfarbe (RGBA)
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
        zeichneKreis(gl, 0, 0, R, false, 20);
        line.draw(gl);
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

    private void zeichneKreis(GL3 gl, float xm, float ym, float r, boolean fill, int nPunkte) {
        rewindBuffer(gl);
        if (fill) {
            putVertex(xm, ym, 0);
        }
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


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args) {
        new FlowMain();
    }

    class FlowLines implements Animatable {

        ConcurrentLinkedDeque<FlowLine> list;




        FlowLines() {
            list = new ConcurrentLinkedDeque<>();

            addLines();
        }

        void addLines() {
            for(float f = -1.01f; f < 1.02; f += 0.05f) {
                list.add(new FlowLine(f));
            }
        }

        void addPoints() {
            for (FlowLine flowLine : list) {
                flowLine.add();
            }
        }

        @Override
        public void update(double dt) {
            if (isIdeal) {
                addPoints();
            }
            list.forEach(flowLine -> flowLine.update(dt));
        }

        @Override
        public void draw(GL3 gl) {
            list.forEach(flowLine -> flowLine.draw(gl));
        }
    }

    class FlowLine implements Animatable {

        ConcurrentLinkedDeque<Point> list;
        FlowIdeal ideal;
        FlowCirc circ;
        float f;
        boolean isBlack = false;
        int c = 0;
        private Color color;

        FlowLine(float _f) {
            list = new ConcurrentLinkedDeque<>();
            ideal = new FlowIdeal();
            circ = new FlowCirc();
            f = _f;
            color = color.WHITE;
        }

        void add() {
            if (random) {
                list.add(new Point(new double[]{-1, Math.random()*2-1}, color.YELLOW));
            } else {
                if (c++ > (20)) {
                    changeColor();
                    c = 0;
                }
                list.add(new Point(new double[]{-1, f}, color));
            }
        }

        @Override
        public void update(double dt) {
            for (Point p : list) {
                if (isIdeal)  {
                    double[] a = ideal.runge(p.pos, dt);
                    p.pos[0] = a[0];
                    p.pos[1] = a[1];
                }
                if (isCirc) {
                    double[] a = circ.runge(p.pos, dt);
                    p.pos[0] = a[0];
                    p.pos[1] = a[1];
                }
            }
        }

        @Override
        public void draw(GL3 gl) {
            list.removeIf(p -> p.pos[0] > 1);
            rewindBuffer(gl);
            gl.glPointSize(1.5f);
            for (Point p : list) {
                setColor(p.c);
                putVertex((float)p.pos[0], (float)p.pos[1], 0);
            }
            copyBuffer(gl, list.size());
            gl.glDrawArrays(GL3.GL_POINTS, 0, list.size());
        }

        void changeColor() {
            if (isBlack) {
                color = Color.WHITE;
                isBlack = false;
            } else {
                color = Color.DARK_GRAY;
                isBlack = true;
            }
        }
    }

    class Point {
        double[] pos;
        Color c;

        Point(double[] pos, Color c) {
            this.pos = pos;
            this.c = c;
        }
    }

    class FlowIdeal extends Dynamics {

        @Override
        public double[] f(double[] x) {
            double xy = x[0] * x[0] + x[1] * x[1];
            return new double[]{
                    1 + (R * R) / xy - (2 * R * R * x[0] * x[0]) / (xy * xy),
                    -(2 * R * R * x[0] * x[1]) / (xy * xy)
            };
        }
    }

    class FlowCirc extends Dynamics {

        double w = 0.1;

        @Override
        public double[] f(double[] x) {
            double xy = x[0] * x[0] + x[1] * x[1];
            return new double[]{
                    w / xy * x[1],
                    -w / xy * x[0]
            };
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        switch (e.getKeyCode()) {
            case KeyEvent.VK_1:
                isCirc = false;
                isIdeal = true;
                break;
            case KeyEvent.VK_2:
                isCirc = true;
                isIdeal = false;
                break;
            case KeyEvent.VK_3:
                isCirc = true;
                isIdeal = true;
                break;
            case KeyEvent.VK_4:
                random = !random;
                break;
        }
    }
}
