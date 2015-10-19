package ch.grisu118.pfcs.a1;

import javax.media.opengl.GL3;
import java.util.Random;

/**
 * Class for a Train object on the AirTrain line.
 * @author Benjamin Leber
 */
public class Train {

    /**
     * position
     **/
    private float x = 0;
    private float v = 0.25f;
    private float mass;
    private float length = 0.25f;
    private float heigth = 0.1f;
    private float[] color = new float[4];

    private AirTrain context;

    public Train(AirTrain context) {
        Random r = new Random();
        this.x = ((float) r.nextInt(400) - 200) / 100;
        this.v = ((float) r.nextInt(200) - 100) / 100;
        this.length = ((float) r.nextInt(50)) / 100;
        if (this.length < 0.05) this.length = 0.05f;
        this.heigth = ((float) r.nextInt(50)) / 100;
        if (this.heigth < 0.05) this.heigth = 0.05f;
        this.mass = length * heigth;
        this.color[0] = (float) Math.random();
        this.color[1] = (float) Math.random();
        this.color[2] = (float) Math.random();
        this.color[3] = 1;
        this.context = context;
    }

    public Train(float pos, float speed, AirTrain context) {
        this.x = pos;
        this.v = speed;
        this.mass = length * heigth;
        this.context = context;
    }

    public void setPosition(float pos) {
        this.x = pos;
    }

    public float getPosition() {
        return x;
    }

    public float getHalfLength() {
        return length / 2;
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

    public boolean checkPosition(Train t2) {
        return Math.abs(this.getPosition() - t2.getPosition()) <= this.getHalfLength() + t2.getHalfLength();
    }

    public float getMass() {
        return mass;
    }

    public void colorChange(Train t2) {
        float[] c = t2.color;
        t2.color = this.color;
        this.color = c;
    }

    public void draw(GL3 gl) {
        float[] c = context.getColor();
        context.rewindBuffer(gl);
        context.setColor(color);
        context.putVertex(x + length / 2, heigth, 0);           // Eckpunkte in VertexArray speichern
        context.putVertex(x - length / 2, heigth, 0);
        context.putVertex(x - length / 2, 0, 0);
        context.putVertex(x + length / 2, 0, 0);
        int nVertices = 4;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, nVertices);
        context.setColor(c);
    }
}
