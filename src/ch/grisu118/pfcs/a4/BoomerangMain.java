package ch.grisu118.pfcs.a4;

import ch.fhnw.pfcs.GLBase1;
import ch.grisu118.pfcs.util.Animatable;
import ch.grisu118.pfcs.util.Simulator;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Created by benjamin on 16.11.2015.
 */
public class BoomerangMain extends GLBase1 {

    float left = -35, right = 35;
    float bottom, top;
    float near = -20, far = 100;

    float dCam = 20;                 // Abstand vom absoluten Nullpunkt
    float elevation = 15;            // Orientierung
    float azimut = 20;

    private Simulator sim = new Simulator();
    private final Animatable bomerang;
    private final StormBoomerang bomerang2;

    public BoomerangMain() {
        super();

        ImageIcon icon = new ImageIcon("res/icon.png");
        if (icon.getIconHeight() < 0) {
            //noinspection ConstantConditions
            icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
        }

        jFrame.setIconImage(icon.getImage());

        bomerang = new StormBoomerang(this, 25, 0);
        bomerang2 = new StormBoomerang(this, 30, 20);
        sim.addAnimatable(bomerang);
        sim.addAnimatable(bomerang2);

    }

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        GL3 gl = drawable.getGL().getGL3();

        setLightPosition(gl, 5, 5, 0);
        new Thread(sim).start();
        FPSAnimator fpsAnimator = new FPSAnimator(drawable, 60, true);
        fpsAnimator.start();
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        // ------  Kamera-System  -------
        setShadingLevel(gl, 0);
        drawAxis(gl, 8, 8, 8);
        setShadingLevel(gl, 1);
        setCameraSystem(gl, dCam, elevation, azimut);
        setLightPosition(gl, 0, 6, 10);
                 //  Koordinatenachsen
        bomerang.draw(gl);
        bomerang2.draw(gl);
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
        setOrthogonalProjection(gl, left, right, bottom, top, near, far);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                azimut++;
                break;
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                elevation++;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                elevation--;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                azimut--;
                break;
            case KeyEvent.VK_SPACE:
                sim.setPause(!sim.isPause());
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
        }
    }

    public static void main(String[] args) {
        new BoomerangMain();
    }
}
