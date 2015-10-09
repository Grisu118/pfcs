package ch.grisu118.pfcs.a2.Vehicles;

import ch.fhnw.util.math.Mat4;
import ch.grisu118.pfcs.a2.ParkingCar;

import javax.media.opengl.GL4;
import java.awt.*;

/**
 * Created by benjamin on 07.10.2015.
 */
public abstract class AbstractVehicle implements Vehicle {
    protected ParkingCar context;
    protected Mat4 matrix;
    protected double x = 0;
    protected volatile double speed = 0;
    protected double angle = 0;
    protected double angleModifier = 200;
    protected volatile double ym = 0;
    protected volatile double alpha = 0;
    protected double beta = 0;
    protected float width = 1.8f;
    protected float axisDistance = 2.9f;
    protected float wheelSize = 0.7f;
    protected float wheelWidth = 0.3f;
    protected double maxAlpha = 50;
    protected double minAlpha;
    protected String name;
    protected String type;
    protected float length = 4.8f;
    protected float backAxis = 1;

    protected float[] debugColor = {1, 1, 0, 1};

    public AbstractVehicle(ParkingCar context, String name) {
        this.context = context;
        this.name = name;

        calcMinAlpha();
    }

    protected void calcMinAlpha() {
        double b = width / 2;
        double ym = b + axisDistance / Math.tan(Math.toRadians(maxAlpha));
        this.minAlpha = -1 * Math.toDegrees(Math.atan(axisDistance / (ym + b)));
    }

    @Override
    public void setAlpha(double alpha) {
        if (alpha <= maxAlpha && alpha >= minAlpha) {
            if (this.alpha < 0 && alpha > 0 || this.alpha > 0 && alpha < 0) {
                alpha = 0;
            }
            this.alpha = alpha;
            double b = width / 2;
            this.ym = b + axisDistance / Math.tan(Math.toRadians(alpha));
            this.beta = Math.toDegrees(Math.atan(axisDistance / (ym + b)));
        }
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
    public double getAngleMofifier() {
        return angleModifier;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public void setX(double x) {
        this.x = x;
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

    @Override
    public String toString() {
        return this.type + ": " + this.name;
    }

    @Override
    public void drawDebug(GL4 gl) {
        float[] color = context.getColor();
        context.setColor(debugColor);
        drawCenter(gl);
        drawCircle(gl, 0, ym, ym, false, 100);
        context.drawAxis(gl, length, (float) ym, 1);
        drawLine(gl, axisDistance, width / 2 - wheelWidth, 0, ym);
        drawLine(gl, axisDistance, -(width / 2 - wheelWidth), 0, ym);
        context.setColor(color);
    }

    protected void drawLine(GL4 gl, double x0, double y0, double x1, double y1) {
        context.rewindBuffer(gl);
        context.putVertex(x0, y0, 0);
        context.putVertex(x1, y1, 0);
        context.copyBuffer(gl, 2);
        gl.glDrawArrays(GL4.GL_LINES, 0, 2);
        context.rewindBuffer(gl);
    }

    protected void drawCenter(GL4 gl) {
        float len = 0.2f;
        context.rewindBuffer(gl);
        context.putVertex(0, ym - len, 0);
        context.putVertex(0, ym + len, 0);
        context.putVertex(-len, ym, 0);
        context.putVertex(len, ym, 0);
        int nVertices = 4;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL4.GL_LINES, 0, nVertices);
        context.rewindBuffer(gl);

    }

    protected void drawCircle(GL4 gl, double xm, double ym, double r, boolean fill, int nPunkte) {
        context.rewindBuffer(gl);
        if (fill) {
            context.putVertex(xm, ym, 0);
        }
        double phi = 2 * Math.PI / nPunkte;
        for (int i = 0; i < nPunkte + 1; i++) {
            context.putVertex((float) (r * Math.cos(phi * i)) + xm, (float) (r * Math.sin(phi * i)) + ym, 0);
            //System.out.println((r * Math.cos(phi) * i) + " : " + (r * Math.sin(phi) * i));
        }
        if (fill) {
            context.putVertex((float) (r * Math.cos(0)) + xm, (float) (r * Math.sin(0)) + ym, 0);
            context.copyBuffer(gl, nPunkte + 2);   // VertexArray in OpenGL-Buffer kopieren
            gl.glDrawArrays(GL4.GL_TRIANGLE_FAN, 0, nPunkte + 2);  // Kreis zeichnen
        } else {
            context.copyBuffer(gl, nPunkte);   // VertexArray in OpenGL-Buffer kopieren
            gl.glDrawArrays(GL4.GL_LINE_LOOP, 0, nPunkte);  // Kreis zeichnen
        }
        context.rewindBuffer(gl);
    }

    protected void drawBody(GL4 gl) {
        context.rewindBuffer(gl);
        context.putVertex(length - backAxis, +width / 2, 0);
        context.putVertex(-backAxis, width / 2, 0);
        context.putVertex(-backAxis, -width / 2, 0);
        context.putVertex(length - backAxis, -width / 2, 0);
        int nVertices = 4;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL4.GL_LINE_LOOP, 0, nVertices);
        context.rewindBuffer(gl);
        context.putVertex(length - backAxis, 0, 0);
        context.putVertex(length * (2.0f / 3) - backAxis, -width / 2, 0);
        context.putVertex(length * (2.0f / 3) - backAxis, width / 2, 0);
        nVertices = 3;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL4.GL_LINE_LOOP, 0, nVertices);
        context.rewindBuffer(gl);
    }

    protected void drawWheels(GL4 gl, double length, double width, double x, double y) {
        float[] color = context.getColor();
        context.setColor(0.01f, 0.01f, 0.01f);
        context.rewindBuffer(gl);
        context.putVertex(x + length / 2, y + width / 2, 0);
        context.putVertex(x - length / 2, y + width / 2, 0);
        context.putVertex(x - length / 2, y - width / 2, 0);
        context.putVertex(x + length / 2, y - width / 2, 0);
        int nVertices = 4;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL4.GL_TRIANGLE_FAN, 0, nVertices);
        context.setColor(color);
    }
}
