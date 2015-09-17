package ch.grisu118.pfcs.a1;

import javax.media.opengl.GL4;

/**
 * Created by benjamin on 17.09.2015.
 */
public class Train {

    /** position **/
    private float pos = 0;
    private float v = 0.25f;
    private float mass = 1;
    private final float length = 0.25f;
    private final float heigth = 0.1f;

    private AirTrain context;

    public Train(AirTrain context) {
       this(0, 0.25f, context);
    }

    public Train(float pos, float speed, AirTrain context) {
        this.pos = pos;
        this.v = speed;
        this.context = context;
    }

    public void setPosition(float pos) {
        this.pos = pos;
    }

    public float getPosition() {
        return pos;
    }

    public float getHalfLength() {
        return length/2;
    }

    public float getSpeed() {
        return v;
    }

    public void setSpeed(float v) {
        this.v = v;
    }

    public void invertSpeed() {
        v *= -1;
    }

    public boolean checkCollision(Train t2) {
        return Math.abs(this.getPosition() - t2.getPosition()) <= this.getHalfLength() + t2.getHalfLength();
    }

    public float getMass() {
        return mass;
    }

    public void draw(GL4 gl) {
        context.rewindBuffer(gl);
        context.putVertex(pos + length / 2, heigth/2, 0);           // Eckpunkte in VertexArray speichern
        context.putVertex(pos - length/2, heigth/2, 0);
        context.putVertex(pos - length/2, -heigth/2, 0);
        context.putVertex(pos + length/2, -heigth/2, 0);
        int nVertices = 4;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL4.GL_TRIANGLE_FAN, 0, nVertices);
    }
}
