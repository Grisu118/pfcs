package ch.grisu118.pfcs.a2.Vehicles;

import ch.grisu118.pfcs.a2.ParkingCar;

import javax.media.opengl.GL3;

/**
 * Created by benjamin on 08.10.2015.
 */
public class Kiro extends AbstractVehicle {

    public Kiro(ParkingCar context, String name) {
        super(context, name);
        this.type = "Tractor";
        this.width = 2.85f;
        this.length = 7.4f;
        this.backAxis = 1.88f;
        this.wheelSize = 1.6f;
        this.wheelWidth = 0.735f;
        this.axisDistance = 3.2f;
        this.maxAlpha = 40;
        this.minAlpha = this.maxAlpha * -1;
        this.angleModifier = 50;
    }

    public void setAlpha(double alpha) {
        if (alpha <= maxAlpha && alpha >= minAlpha) {
            if (this.alpha < 0 && alpha > 0 || this.alpha > 0 && alpha < 0) {
                alpha = 0;
            }
            double a = this.alpha;
            this.alpha = alpha;
            this.ym = axisDistance / Math.tan(Math.toRadians(alpha));
            if (!calcZentripetalForce()) {
                this.alpha = a;
                this.ym = axisDistance / Math.tan(Math.toRadians(alpha));
                calcZentripetalForce();
            }
        }
    }

    @Override
    public void draw(GL3 gl) {
        drawBody(gl);
    }

    @Override
    protected void drawBody(GL3 gl) {

        double space = 0.5;
        //Front
        context.pushMatrix(gl);
        context.translate(gl, axisDistance/2, 0, 0);
        context.pushMatrix(gl);
        context.rotate(gl, (float) (alpha), 0,0,1);
        context.rewindBuffer(gl);
        context.putVertex(space, +width / 2, 0.01);
        context.putVertex(length - axisDistance / 2 - backAxis, width / 2, 0.01);
        context.putVertex(length - axisDistance / 2 - backAxis, -width / 2, 0.01);
        context.putVertex(space, -width / 2, 0.01);
        int nVertices = 4;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_LINE_LOOP, 0, nVertices);
        context.rewindBuffer(gl);
        context.putVertex(backAxis + axisDistance / 2, 0, 0.01);
        context.putVertex(space, -width / 2, 0.01);
        context.putVertex(0, 0, 0.01);
        context.putVertex(space, width / 2, 0.01);
        nVertices = 4;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_LINE_LOOP, 0, nVertices);
        context.rewindBuffer(gl);
        drawWheels(gl, wheelSize, wheelWidth, axisDistance/2, width / 2 - wheelWidth/2, false);
        drawWheels(gl, wheelSize, wheelWidth, axisDistance/2, -width / 2 + wheelWidth/2, false);
        context.popMatrix(gl);
        //Back
        if (debug) {
            drawDebug(gl);
        }
        drawZentriPetal(gl);
        context.putVertex(-space, +width / 2, 0.01);
        context.putVertex(-space, -width / 2, 0.01);
        context.putVertex(-(axisDistance / 2 + backAxis), -width / 2, 0.01);
        context.putVertex(-(axisDistance / 2 + backAxis), width / 2, 0.01);
        nVertices = 4;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_LINE_LOOP, 0, nVertices);
        context.rewindBuffer(gl);
        context.putVertex(-space, +width / 2, 0.01);
        context.putVertex(0, 0, 0.01);
        context.putVertex(-space, -width / 2, 0.01);
        nVertices = 3;
        context.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_LINE_STRIP, 0, nVertices);
        context.rewindBuffer(gl);
        drawWheels(gl, wheelSize, wheelWidth, -axisDistance/2, width / 2 - wheelWidth/2, true);
        drawWheels(gl, wheelSize, wheelWidth, -axisDistance/2, -width / 2 + wheelWidth/2, true);
        context.popMatrix(gl);
    }

    @Override
    public void drawDebug(GL3 gl) {
        float[] color = context.getColor();
        context.setColor(debugColor);
        context.pushMatrix(gl);
        context.translate(gl, -axisDistance/2, 0, 0);
        drawCenter(gl);
        drawCircle(gl, 0, ym, ym, false, 100);
        context.drawAxis(gl, length, (float) ym, 1);
        context.setColor(color);
        context.popMatrix(gl);
    }
}
