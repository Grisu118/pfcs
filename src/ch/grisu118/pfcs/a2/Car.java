package ch.grisu118.pfcs.a2;

import ch.fhnw.util.math.Mat4;

import javax.media.opengl.GL4;

/**
 * Created by benjamin on 06.10.2015.
 */
public class Car implements Vehicle {

    private ParkingCar context;
    private Mat4 matrix;

    private double x = 0;
    private double y = 0;
    private volatile double speed = 0;
    private double angle = 0;
    private volatile double ym = 0;

    private volatile double alpha = 0;
    private double beta = 0;

    private float width = 1.8f;
    private float length = 4.8f;
    private float backAxis = 1;
    private float axisDistance = 2.9f;
    private float wheelSize = 0.7f;
    private float wheelWidth = 0.3f;



    public Car(ParkingCar context) {
        this.context = context;
    }

    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public void setAngle(double angle) {
        this.angle = angle;
    }

    @Override
    public double getAngle() {
        return angle;
    }

    @Override
    public void draw(GL4 gl) {
        drawBody(gl);
        context.setColor(0.01f,0.01f,0.01f);
        drawWheels(gl, wheelSize, wheelWidth, 0, (+width / 2 - wheelWidth)); //backleft
        drawWheels(gl, wheelSize, wheelWidth, 0, -(width/2-wheelWidth)); //backright

        //Dynamic
        context.pushMatrix(gl);
        context.translate(gl, axisDistance, width/2-wheelWidth, 0);
        context.rotate(gl, (float) alpha, 0, 0, 1);
        drawWheels(gl, wheelSize, wheelWidth, 0, 0);
        context.popMatrix(gl);
        context.pushMatrix(gl);
        context.translate(gl, axisDistance, -width/2+wheelWidth, 0);
        context.rotate(gl, (float) beta, 0, 0, 1);
        drawWheels(gl, wheelSize, wheelWidth, 0, 0);
        context.popMatrix(gl);
    }



    private void drawBody(GL4 gl) {
        context.rewindBuffer(gl);
        context.putVertex(length-backAxis, +width/2, 0);
        context.putVertex(-backAxis, width/2, 0);
        context.putVertex(-backAxis, -width/2, 0);
        context.putVertex(length-backAxis, -width/2, 0);
        int nVertices = 4;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL4.GL_LINE_LOOP, 0, nVertices);
        context.rewindBuffer(gl);
        context.putVertex(length-backAxis, 0, 0);
        context.putVertex(length*(2.0f/3)-backAxis, -width/2, 0);
        context.putVertex(length*(2.0f/3)-backAxis, width/2, 0);
        nVertices = 3;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL4.GL_LINE_LOOP, 0, nVertices);
        context.rewindBuffer(gl);
    }

    private void drawWheels(GL4 gl, double length, double width, double x, double y) {
        context.rewindBuffer(gl);
        context.putVertex(x+length/2, y+width/2, 0);
        context.putVertex(x-length/2, y+width/2, 0);
        context.putVertex(x-length/2, y-width/2, 0);
        context.putVertex(x+length/2, y-width/2, 0);
        int nVertices = 4;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL4.GL_TRIANGLE_FAN, 0, nVertices);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public void setAlpha(double alpha) {
        this.alpha = alpha;
        double b = width/2;
        this.ym = b + axisDistance / Math.tan(Math.toRadians(alpha));
        this.beta = Math.toDegrees(Math.atan(axisDistance / (ym + b)));
    }

    @Override
    public double getAlpha() {
        return alpha;
    }

    @Override
    public double getYm() {
        return ym;
    }

    @Override
    public void setMatrix(Mat4 matrix) {
        this.matrix = matrix;
    }

    @Override
    public Mat4 getMatrix() {
        return matrix;
    }
}
