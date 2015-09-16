//  -------------   JOGL 2D-Programm  -------------------

import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

public class FPendel extends GLBase1 {

    //  ---------  globale Daten  ---------------------------

    float left = -4, right = 5;             // ViewingVolume
    float bottom, top;
    float near = -10, far = 1000;

    float R = 2;
    float phi = 0;
    private FPSAnimator fpsAnimator;

    //  ---------  Methoden  ----------------------------------

    public void zeichneDreieck(GL4 gl,
                               float x1, float y1, float z1,
                               float x2, float y2, float z2,
                               float x3, float y3, float z3) {
        rewindBuffer(gl);
        putVertex(x1, y1, z1);           // Eckpunkte in VertexArray speichern
        putVertex(x2, y2, z2);
        putVertex(x3, y3, z3);
        int nVertices = 3;
        copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL4.GL_TRIANGLES, 0, nVertices);
    }

    public void zeichneKreis(GL4 gl, float xm, float ym, float r, boolean fill, int nPunkte) {
        vertexBuf.rewind();
        if (fill) {
            setColor(1, 1, 0, 1);
            putVertex(xm, ym, 0);
        }
        setColor(1, 0, 0, 1);
        double phi = 2 * Math.PI / nPunkte;
        for (int i = 0; i < nPunkte + 1; i++) {
            putVertex((float) (r * Math.cos(phi * i)) + xm, (float) (r * Math.sin(phi * i)) + ym, 0);
            //System.out.println((r * Math.cos(phi) * i) + " : " + (r * Math.sin(phi) * i));
        }
        if (fill) {
            putVertex((float) (r * Math.cos(0)) + xm, (float) (r * Math.sin(0)) + ym, 0);
            copyBuffer(gl, nPunkte + 2);   // VertexArray in OpenGL-Buffer kopieren
            gl.glDrawArrays(GL4.GL_TRIANGLE_FAN, 0, nPunkte + 2);  // Kreis zeichnen
        } else {
            copyBuffer(gl, nPunkte);   // VertexArray in OpenGL-Buffer kopieren
            gl.glDrawArrays(GL4.GL_LINE_LOOP, 0, nPunkte);  // Kreis zeichnen
        }

    }

    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        GL4 gl = drawable.getGL().getGL4();
        gl.glClearColor(0, 0, 1, 1);                         // Hintergrundfarbe (RGBA)
        gl.glDisable(GL4.GL_DEPTH_TEST);                  // ohne Sichtbarkeitstest
        fpsAnimator = new FPSAnimator(drawable, 40, true);
        fpsAnimator.start();
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT);
        setColor(1, 1, 1);
        drawAxis(gl, 8, 8, 8);             //  Koordinatenachsen
        setColor(1, 0, 0);
        zeichneKreis(gl, 0, 0, R, false, 40);

        zeichneKreis(gl,(float) (R*Math.cos(phi)), (float)(R*Math.sin(phi)), 0.25f, true, 30);
        zeichneKreis(gl,0, (float)(R*Math.sin(phi)), 0.25f, true, 30);
        phi += 0.01;
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height) {
        GL4 gl = drawable.getGL().getGL4();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);
        float aspect = (float)height / width;
        bottom = aspect * left;
        top = aspect * right;
        // -----  Projektionsmatrix festlegen  -----
        setOrthogonalProjection(gl, left, right, bottom, top, near, far);
    }


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args) {
        new FPendel();
    }

}