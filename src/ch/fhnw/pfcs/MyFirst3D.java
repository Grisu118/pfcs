package ch.fhnw.pfcs;//  -------------   JOGL 3D-Programm  -------------------

import ch.fhnw.pfcs.objects.Cuboid;
import ch.fhnw.util.math.Vec3;

import javax.media.opengl.*;
import java.awt.event.KeyEvent;

public class MyFirst3D extends GLBase1 {

    //  ---------  globale Daten  ---------------------------

    float left = -5, right = 5;
    float bottom, top;
    float near = -10, far = 1000;
    private float azimut;
    private float elevation;


    //  ---------  Methoden  ----------------------------------
    public void zeichneDreieck(GL3 gl,
                               float x1, float y1, float z1,
                               float x2, float y2, float z2,
                               float x3, float y3, float z3) {
        rewindBuffer(gl);

        Vec3 u = new Vec3(x2-x1, y2-y1, z2-z1);
        Vec3 v = new Vec3(x3-x1, y3-y1, z3-z1);
        Vec3 n = u.cross(v);
        setNormal(n.x, n.y, n.z);
        putVertex(x1, y1, z1);           // Eckpunkte in VertexArray speichern
        putVertex(x2, y2, z2);
        putVertex(x3, y3, z3);
        int nVertices = 3;
        copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, nVertices);
    }


    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        GL3 gl = drawable.getGL().getGL3();
        setShadingLevel(gl, 1);
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        // ------  Kamera-System  -------
        float dCam = 10;                 // Abstand vom absoluten Nullpunkt
        setCameraSystem(gl, dCam, elevation, azimut);
        setColor(0.8f, 0.8f, 0.8f);
        drawAxis(gl, 8, 8, 8);             //  Koordinatenachsen
        setColor(1, 0, 0);
        zeichneDreieck(gl, 3, 2, 4, 5, 1.8f, 8, 5, 2, 3);
        setColor(0.2f, 0.2f, 0.2f);
        zeichneDreieck(gl, 3, 0, 4, 5, 0, 8, 5, 0, 3);
        setColor(0.8f, 0.4f,0);
        setLightPosition(gl, 0,6,10);
        Cuboid c = new Cuboid(2, 1, 3, this);
        c.draw(gl);
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


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args) {
        new MyFirst3D();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP : elevation++;
                break;
            case KeyEvent.VK_DOWN: elevation--;
                break;
            case KeyEvent.VK_LEFT: azimut--;
                break;
            case KeyEvent.VK_RIGHT: azimut++;
                break;

        }
        canvas.repaint();
    }
}