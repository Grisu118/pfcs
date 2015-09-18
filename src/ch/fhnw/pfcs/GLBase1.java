package ch.fhnw.pfcs;//  -------------   JOGL Basis-Programm (fuer Erweiterungenh mittels 'extends')  -------------------
//
//                                                            E.Gutknecht, Juli 2015
//  adaptiert von:
//  http://www.lighthouse3d.com/cg-topics/code-samples/opengl-3-3-glsl-1-5-sample/
//

import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import java.nio.*;
import java.util.*;

import com.jogamp.common.nio.*;
import ch.fhnw.util.math.*;

public class GLBase1
        implements WindowListener, GLEventListener, KeyListener, MouseListener, MyRenderer1 {

    //  --------------  Default-Werte ----------------------------------------

    String windowTitle = "JOGL-Application";
    int windowWidth = 800;
    int windowHeight = 600;
    String vShader = "shaders/vShader1.glsl";               // Filename Vertex-Shader
    String fShader = "shaders/fShader1.glsl";               // Filename Fragment-Shader
    int maxVerts = 2048;                            // max. Anzahl Vertices im Vertex-Array


    //  --------------  globale Daten  -------------------------------------

    float[] clearColor = {0, 0, 1, 1};                 // Fensterhintergrund (Blau)
    GLCanvas canvas;                                // OpenGL Window


    //  --------  Vertex-Array (fuer die Attribute Position, Color, Normal, Texture-Coord)  ------------

    FloatBuffer vertexBuf;                              // Vertex-Array
    final int vPositionSize = 4 * Float.SIZE / 8;           // Anz. Bytes der x,y,z,w (homogene Koordinaten)
    final int vColorSize = 4 * Float.SIZE / 8;              // Anz. Bytes der rgba Werte
    final int vertexSize = vPositionSize + vColorSize;  // Anz. Bytes eines Vertex
    int bufSize;                                        // Anzahl Bytes des VertexArrays = maxVerts * vertexSize

    float[] currentColor = {1, 1, 1, 1};                  // aktuelle Farbe fuer Vertices


    //  --------  ModelView-Transformation  ---------

    Mat4 viewMatrix = Mat4.ID;                          // ModelView-Matrix
    Stack<Mat4> matrixStack = new Stack<Mat4>();


    // ------ Identifiers fuer OpenGL-Objekte und Shader-Variablen  ------

    int program;                                        // Program-Object
    int vaoId;                                          // Identifier fuer OpenGL VertexArray Object
    int vertexBufId;                                    // Identifier fuer OpenGL Vertex Buffer

    int projMatrixLoc, viewMatrixLoc;                   // Uniform Shader Variables

    int vPositionLocation, vColorLocation;              // Vertex Attribute Shader Variables
    protected Panel headerPanel;


    //  ------------- Methoden  ---------------------------

    public GLBase1()                                   // Konstruktor ohne Parameter
    {
        createFrame();
    }


    public GLBase1(String windowTitle,                 // Konstruktor
                   int windowWidth, int windowHeight,
                   String vShader, String fShader,      // Filenamen Vertex-/Fragment-Shader
                   int maxVerts)                        // max. Anzahl Vertices im Vertex-Array
    {
        this.windowTitle = windowTitle;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.vShader = vShader;
        this.fShader = fShader;
        this.maxVerts = maxVerts;
        createFrame();
    }

    ;


    private void createFrame()                         // Fenster erzeugen
    {
        Frame f = new Frame(windowTitle);
        f.setLayout(new BorderLayout());
        f.setSize(windowWidth, windowHeight);
        f.addWindowListener(this);
        GLProfile glp = GLProfile.get(GLProfile.GL4);
        GLCapabilities glCaps = new GLCapabilities(glp);
        canvas = new GLCanvas(glCaps);
        canvas.addGLEventListener(this);
        f.add(canvas, BorderLayout.CENTER);
        headerPanel = new Panel();
        f.add(headerPanel, BorderLayout.NORTH);
        f.setVisible(true);
        canvas.addKeyListener(this);
        canvas.addMouseListener(this);
    }

    ;


    private void setupVertexBuffer(int pgm, GL4 gl, int maxVerts) {
        bufSize = maxVerts * vertexSize;
        vertexBuf = Buffers.newDirectFloatBuffer(bufSize);
        // ------  OpenGl-Objekte -----------
        int[] tmp = new int[1];
        gl.glGenVertexArrays(1, tmp, 0);                 // VertexArrayObject
        vaoId = tmp[0];
        gl.glBindVertexArray(vaoId);
        gl.glGenBuffers(1, tmp, 0);                      // VertexBuffer
        vertexBufId = tmp[0];
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vertexBufId);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, bufSize,            // Speicher allozieren
                null, GL4.GL_STATIC_DRAW);

        // ----- get shader variable identifiers  -------------
        vPositionLocation = gl.glGetAttribLocation(pgm, "vertexPosition");
        vColorLocation = gl.glGetAttribLocation(pgm, "vertexColor");


        //  ------  enable vertex attributes ---------------
        gl.glEnableVertexAttribArray(vPositionLocation);
        gl.glEnableVertexAttribArray(vColorLocation);
        gl.glVertexAttribPointer(vPositionLocation, 4, GL4.GL_FLOAT, false, vertexSize, 0);
        gl.glVertexAttribPointer(vColorLocation, 4, GL4.GL_FLOAT, false, vertexSize, vPositionSize);

    }

    ;


    private void setupMatrices(int pgm, GL4 gl) {
        // ----- get shader variable identifiers  -------------
        projMatrixLoc = gl.glGetUniformLocation(pgm, "projMatrix");
        viewMatrixLoc = gl.glGetUniformLocation(pgm, "viewMatrix");

        // -----  set uniform variables  -----------------------
        float[] identityMatrix = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, identityMatrix, 0);
        gl.glUniformMatrix4fv(projMatrixLoc, 1, false, identityMatrix, 0);
    }

    ;


    //  ----------  oeffentliche Methoden (fuer Verwendung in Erweiterungsklassen)  -------------

    public GLCanvas getGLCanvas()                              // OpenGL Window-Handle
    {
        return canvas;
    }


    // --------  Vertex-Methoden  --------

    public void setColor(float r, float g, float b)             // aktuelle Vertexfarbe setzen
    {
        currentColor[0] = r;
        currentColor[1] = g;
        currentColor[2] = b;
        currentColor[3] = 1;
    }

    public void setColor(float r, float g, float b, float a)             // aktuelle Vertexfarbe setzen
    {
        currentColor[0] = r;
        currentColor[1] = g;
        currentColor[2] = b;
        currentColor[3] = a;
    }


    public void putVertex(float x, float y, float z)            // Vertex-Daten in Buffer speichern
    {
        vertexBuf.put(x);
        vertexBuf.put(y);
        vertexBuf.put(z);
        vertexBuf.put(1);
        vertexBuf.put(currentColor[0]);                          // Farbe
        vertexBuf.put(currentColor[1]);
        vertexBuf.put(currentColor[2]);
        vertexBuf.put(currentColor[3]);
    }


    public void copyBuffer(GL gl, int nVertices)                // Vertex-Array in OpenGL-Buffer kopieren
    {
        vertexBuf.rewind();
        if (nVertices > maxVerts)
            throw new IndexOutOfBoundsException();
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vertexBufId);
        gl.glBufferSubData(GL4.GL_ARRAY_BUFFER, 0, nVertices * vertexSize, vertexBuf);
    }


    public void rewindBuffer(GL gl)                            // Bufferposition zuruecksetzen
    {
        vertexBuf.rewind();
    }


    //  --------  Methoden fuer ModelView-Transformation  -----------


    // ---------  Kamera-System festlegen  ----------

    public void setCameraSystem(GL4 gl, float r,                // Abstand der Kamera von O
                                float elevation,                // Elevationswinkel in Grad
                                float azimut)                   // Azimutwinkel in Grad
    {
        float toRad = (float) (Math.PI / 180);
        float c = (float) Math.cos(toRad * elevation);
        float s = (float) Math.sin(toRad * elevation);
        float cc = (float) Math.cos(toRad * azimut);
        float ss = (float) Math.sin(toRad * azimut);
        viewMatrix = new Mat4(cc, -s * ss, c * ss, 0, 0, c, s, 0, -ss, -s * cc, c * cc, 0, 0, 0, -r, 1);
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }


    public void setCameraSystem(GL4 gl, Vec3 A, Vec3 B, Vec3 up)       // LookAt-Psotionierung
    {
        viewMatrix = Mat4.lookAt(A, B, up);
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }


    //  ---------  Operationen fuer ModelView-Matrix  --------------------

    public void rotate(GL4 gl, float phi,                                           // Objekt-System drehen, phi in Grad
                       float x, float y, float z)                                   // Drehachse
    {
        viewMatrix = viewMatrix.postMultiply(Mat4.rotate(phi, x, y, z));
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }

    public void translate(GL4 gl,                                                   // Objekt-System verschieben
                          float x, float y, float z) {
        viewMatrix = viewMatrix.postMultiply(Mat4.translate(x, y, z));
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }


    public void scale(GL4 gl, float scale)                                          // Skalierung Objekt-System
    //  nur ein xyz-Faktor wegen Normalentransformation
    {
        viewMatrix = viewMatrix.postMultiply(Mat4.scale(scale, scale, scale));
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }


    public void rotateCam(GL4 gl, float phi,                                        // Kamera-System drehen, phi in Grad
                          float x, float y, float z) {
        viewMatrix = viewMatrix.preMultiply(Mat4.rotate(-phi, x, y, z));
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }

    public void translateCam(GL4 gl,                                                 // Kamera-System verschieben
                             float x, float y, float z) {
        viewMatrix = viewMatrix.preMultiply(Mat4.translate(-x, -y, -z));
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }


    public void pushMatrix(GL4 gl)                                                   // ModelView-Matrix speichern
    {
        matrixStack.push(viewMatrix);
    }

    public void popMatrix(GL4 gl)                                                    // ModelView-Matrix vom Stack holen
    {
        viewMatrix = matrixStack.pop();
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }

    public Mat4 getModelViewMatrix(GL4 gl) {
        return viewMatrix;
    }

    public void setModelViewMatrix(GL4 gl, Mat4 M) {
        viewMatrix = M;
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }

    public void loadIdentity(GL4 gl)                                                 // Rueckstellung auf Einheitsmatrix
    {
        viewMatrix = Mat4.ID;
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.toArray(), 0);
    }


    //  ---------  Projektion auf Bildebene -------------------

    public void setOrthogonalProjection(GL4 gl, float left, float right,      // Orthogonal-Projektion auf Bildebene
                                        float bottom, float top,
                                        float near, float far) {
        float m00 = 2.0f / (right - left);
        ;
        float m11 = 2.0f / (top - bottom);
        float m22 = -2.0f / (far - near);
        float m03 = -(right + left) / (right - left);
        float m13 = -(top + bottom) / (top - bottom);
        float m23 = -(far + near) / (far - near);
        float m33 = 1;
        float[] projMatrix = {m00, 0, 0, 0, 0, m11, 0, 0, 0, 0, m22, 0, m03, m13, m23, m33};
        gl.glUniformMatrix4fv(projMatrixLoc, 1, false, projMatrix, 0);
    }


    public void setPerspectiveProjection(GL4 gl, float left, float right,     // Zentralprojektion auf Bildebene
                                         float bottom, float top,
                                         float near, float far) {
        Mat4 projMatrix = Mat4.perspective(left, right, bottom, top, near, far);
        gl.glUniformMatrix4fv(projMatrixLoc, 1, false, projMatrix.toArray(), 0);
    }


    //  ---------  Zeichenmethoden  ------------------------------

    public void drawAxis(GL4 gl, float a, float b, float c)                   // Koordinatenachsen zeichnen
    {
        rewindBuffer(gl);
        putVertex(0, 0, 0);           // Eckpunkte in VertexArray speichern
        putVertex(a, 0, 0);
        putVertex(0, 0, 0);
        putVertex(0, b, 0);
        putVertex(0, 0, 0);
        putVertex(0, 0, c);
        int nVertices = 6;
        copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL4.GL_LINES, 0, nVertices);
    }


    //  --------  OpenGL Event-Methoden fuer Ueberschreibung in Erweiterungsklassen   ----------------

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
        System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
        program = GLShaders.loadShaders(gl, vShader, fShader);
        setupVertexBuffer(program, gl, maxVerts);
        setupMatrices(program, gl);
        gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
        gl.glEnable(GL4.GL_DEPTH_TEST);
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
        setColor(0.8f, 0.8f, 0.8f);
        drawAxis(gl, 8, 8, 8);             //  Koordinatenachsen
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height) {
        GL4 gl = drawable.getGL().getGL4();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);

        // -----  Projektionsmatrix festlegen  -----
        float left = -1, right = 1;                       // ViewingVolume im Kamera-System
        float bottom = -1, top = 1;
        float near = -10, far = 1000;
        setOrthogonalProjection(gl, left, right, bottom, top, near, far);
    }


    @Override
    public void dispose(GLAutoDrawable drawable) {
    }


    //  ---------  Window-Events  --------------------

    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }


    //  ---------  Keuboard-Events ---------------------
    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }


    //  ---------  Mouse-Events ---------------------
    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args) {
        new GLBase1();
    }

}