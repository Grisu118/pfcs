package ch.grisu118.pfcs.a2.Vehicles;

import ch.grisu118.pfcs.a2.ParkingCar;

import javax.media.opengl.GL4;

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
        this.axisDistance = this.length-this.backAxis-this.wheelSize/2-0.01f;
        this.maxAlpha = 60;
        this.minAlpha = this.maxAlpha * -1;

    }


    @Override
    public void draw(GL4 gl) {
        drawBody(gl);
        context.setColor(0.01f, 0.01f, 0.01f);
        drawWheels(gl, wheelSize, wheelWidth, 0, (+width / 2 - wheelWidth)); //backleft
        drawWheels(gl, wheelSize, wheelWidth, 0, -(width / 2 - wheelWidth)); //backright

        //Dynamic
        context.pushMatrix(gl);
        context.translate(gl, axisDistance, 0, 0);
        context.rotate(gl, (float) alpha, 0, 0, 1);
        drawWheels(gl, wheelSize, wheelWidth, 0, 0);
        context.popMatrix(gl);
    }

    @Override
    public void setAlpha(double alpha) {
        if (alpha <= maxAlpha && alpha >= minAlpha) {
            this.alpha = alpha;
            this.ym = axisDistance / Math.tan(Math.toRadians(alpha));
        }
    }
}
