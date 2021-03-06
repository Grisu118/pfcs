package ch.grisu118.pfcs.a2.Vehicles;

import ch.grisu118.pfcs.a2.ParkingCar;

import javax.media.opengl.GL3;

/**
 * Created by benjamin on 08.10.2015.
 */
public class Trike extends AbstractVehicle {

    public Trike(ParkingCar context, String name) {
        super(context, name);
        this.type = "Trike";
        this.width = 1.81f;
        this.length = 3.4f;
        this.backAxis = 0.8f;
        this.wheelSize = 0.6f;
        this.wheelWidth = 0.35f;
        this.axisDistance = this.length - this.backAxis - this.wheelSize / 2 - 0.01f;
        this.maxAlpha = 60;
        this.minAlpha = this.maxAlpha * -1;
        this.wheelDistance = 0;

    }


    @Override
    public void draw(GL3 gl) {
        drawBody(gl);
        context.setColor(0.01f, 0.01f, 0.01f);
        drawWheels(gl, wheelSize, wheelWidth, 0, (+width / 2 - wheelWidth), true); //backleft
        drawWheels(gl, wheelSize, wheelWidth, 0, -(width / 2 - wheelWidth), true); //backright
        if (debug) {
            drawDebug(gl);
        }
        drawZentriPetal(gl);
        //Dynamic
        context.pushMatrix(gl);
        context.translate(gl, axisDistance, 0, 0);
        context.rotate(gl, (float) alpha, 0, 0, 1);
        drawWheels(gl, wheelSize, wheelWidth, 0, 0, false);
        context.popMatrix(gl);
    }

    protected void drawDebug(GL3 gl) {
        float[] color = context.getColor();
        context.setColor(debugColor);
        drawCenter(gl);
        drawCircle(gl, 0, ym, ym, false, 100);
        context.drawAxis(gl, length, (float) ym, 1);
        context.setColor(color);
    }

    @Override
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
}
