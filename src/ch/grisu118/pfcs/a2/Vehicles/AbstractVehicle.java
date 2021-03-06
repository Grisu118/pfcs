package ch.grisu118.pfcs.a2.Vehicles;

import ch.fhnw.util.math.Mat4;
import ch.grisu118.pfcs.a2.ParkingCar;

import javax.media.opengl.GL3;

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
    protected volatile double beta = 0;
    protected volatile double gamma = 0;
    protected float width = 1.8f;
    protected float wheelDistance;
    protected float axisDistance = 2.9f;
    protected float wheelSize = 0.7f;
    protected float wheelWidth = 0.3f;
    protected double maxAlpha = 50;
    protected double minAlpha;
    protected String name;
    protected String type;
    protected float length = 4.8f;
    protected float backAxis = 1;
    protected double zentripetalForce;
    protected double maxZentripetalForce;

    protected boolean debug = false;

    protected float[] debugColor = {1, 1, 0, 1};

    public AbstractVehicle(ParkingCar context, String name) {
        this.context = context;
        this.name = name;
        this.wheelDistance = width / 2 - wheelWidth;

        this.maxZentripetalForce = 1.2 * 9.81;
        calcMinAlpha();
    }

    protected void calcMinAlpha() {
        this.minAlpha = -maxAlpha;
    }

    @Override
    public void setAlpha(double alpha) {
        double a = this.alpha;
        double ym2 = this.ym;
        double be = this.beta;
        if (alpha <= maxAlpha && alpha >= minAlpha) {
            if (this.alpha < 0 && alpha > 0 || this.alpha > 0 && alpha < 0) {
                alpha = 0;
            }
            this.alpha = alpha;
            double b = wheelDistance / 2;
            this.ym = axisDistance / Math.tan(Math.toRadians(alpha));
            this.beta = Math.toDegrees(Math.atan(axisDistance / (ym + b)));
            this.gamma = Math.toDegrees(Math.atan(axisDistance / (ym - b)));
            if (!calcZentripetalForce()) {
                this.alpha = a;
                this.ym = ym2;
                this.beta = be;
                this.gamma = Math.toDegrees(Math.atan(axisDistance / (ym - b)));
                calcZentripetalForce();
            }
        }
    }

    protected boolean calcZentripetalForce() {
        if (this.ym == 0) {
            return true;
        }
        this.zentripetalForce = Math.pow(this.speed, 2) / ym;
        return Math.abs(this.zentripetalForce) <= Math.abs(maxZentripetalForce);
    }

    @Override
    public void setSpeed(double speed) {
        double s = this.speed;
        this.speed = speed;

        if (!calcZentripetalForce() && speed > s) {
            this.speed = s;
            calcZentripetalForce();
        }
    }

    @Override
    public void reset() {
        this.matrix = Mat4.ID;
        this.setSpeed(0);
        this.setAlpha(0);
        this.setAngle(0);
        this.setX(0);
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
    public double getZentripetalForce() {
        return zentripetalForce;
    }

    @Override
    public double getMaxZentripetalForce() {
        return maxZentripetalForce;
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
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    protected void drawDebug(GL3 gl) {
        float[] color = context.getColor();
        context.setColor(debugColor);
        drawCenter(gl);
        drawCircle(gl, 0, ym, ym, false, 100);
        context.drawAxis(gl, length, (float) ym, 1);
        context.setColor(color);
    }

    protected void drawZentriPetal(GL3 gl) {
        /*
        float[] c = context.getColor();

        //Max
        context.setColor(0, 0, 255);
        double m = maxZentripetalForce * (ym / Math.abs(ym));
        context.drawLine(gl, 0, 0, 0, m);
        context.drawLine(gl, -0.2, m, 0.2, m);

        context.setColor(0, 255, 0);
        context.drawLine(gl, 0, 0, 0, zentripetalForce);
        context.drawLine(gl, -0.2, zentripetalForce, 0.2, zentripetalForce);


        context.setColor(c);
        */
    }

    protected void drawCenter(GL3 gl) {
        float len = 0.2f;
        context.rewindBuffer(gl);
        context.putVertex(0, ym - len, 0);
        context.putVertex(0, ym + len, 0);
        context.putVertex(-len, ym, 0);
        context.putVertex(len, ym, 0);
        int nVertices = 4;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_LINES, 0, nVertices);
        context.rewindBuffer(gl);

    }

    protected void drawCircle(GL3 gl, double xm, double ym, double r, boolean fill, int nPunkte) {
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
            gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, nPunkte + 2);  // Kreis zeichnen
        } else {
            context.copyBuffer(gl, nPunkte);   // VertexArray in OpenGL-Buffer kopieren
            gl.glDrawArrays(GL3.GL_LINE_LOOP, 0, nPunkte);  // Kreis zeichnen
        }
        context.rewindBuffer(gl);
    }

    protected void drawBody(GL3 gl) {
        context.rewindBuffer(gl);
        context.putVertex(length - backAxis, +width / 2, 0);
        context.putVertex(-backAxis, width / 2, 0);
        context.putVertex(-backAxis, -width / 2, 0);
        context.putVertex(length - backAxis, -width / 2, 0);
        int nVertices = 4;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_LINE_LOOP, 0, nVertices);
        context.rewindBuffer(gl);
        context.putVertex(length - backAxis, 0, 0);
        context.putVertex(length * (2.0f / 3) - backAxis, -width / 2, 0);
        context.putVertex(length * (2.0f / 3) - backAxis, width / 2, 0);
        nVertices = 3;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_LINE_LOOP, 0, nVertices);
        context.rewindBuffer(gl);
    }

    protected void drawWheels(GL3 gl, double length, double width, double x, double y, boolean isBackAxis) {
        float[] color = context.getColor();
        context.setColor(0.01f, 0.01f, 0.01f);
        context.rewindBuffer(gl);
        context.putVertex(x + length / 2, y + width / 2, 0);
        context.putVertex(x - length / 2, y + width / 2, 0);
        context.putVertex(x - length / 2, y - width / 2, 0);
        context.putVertex(x + length / 2, y - width / 2, 0);
        int nVertices = 4;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, nVertices);
        if (debug && !isBackAxis) {
            context.setColor(debugColor);
            context.rewindBuffer(gl);
            context.putVertex(x, y, 0);
            double l = Math.sqrt(Math.pow(axisDistance, 2) + Math.pow(ym + y, 2)) * (ym / Math.abs(ym));
            context.putVertex(x, l, 0);
            nVertices = 2;
            context.copyBuffer(gl, nVertices);
            gl.glDrawArrays(GL3.GL_LINES, 0, nVertices);
        }
        context.rewindBuffer(gl);
        context.setColor(color);
    }
}
