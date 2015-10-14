package ch.grisu118.pfcs.a2.Vehicles;

import ch.grisu118.pfcs.a2.ParkingCar;

import javax.media.opengl.GL4;

/**
 * Created by benjamin on 08.10.2015.
 */
public class BigTruck extends AbstractVehicle{

    private double alpha2;
    private double alpha5;
    private double beta2;
    private double beta5;

    private double axisDistance2 = 2.35;
    private double axisDistance5 = -2.25;

    public BigTruck(ParkingCar context, String name) {
        super(context, name);
        this.type = "Truck";
        this.width = 2.55f;
        this.length = 9.137f;
        this.backAxis = 2.887f;
        this.wheelSize = 1;
        this.wheelWidth = 0.4f;
        this.axisDistance = 4.75f;
        this.maxAlpha = 45;

        calcMinAlpha();
    }

    @Override
    public void draw(GL4 gl) {
        drawBody(gl);
        context.setColor(0.01f, 0.01f, 0.01f);
        drawWheels(gl, wheelSize, wheelWidth, 0.7, (+width / 2 - wheelWidth), true); //backleft
        drawWheels(gl, wheelSize, wheelWidth, 0.7, +width / 2 - 2*wheelWidth-0.008, true);
        drawWheels(gl, wheelSize, wheelWidth, 0.7, -(width / 2 - wheelWidth), true); //backright
        drawWheels(gl, wheelSize, wheelWidth, 0.7, -(width / 2 - 2*wheelWidth-0.008), true);
        drawWheels(gl, wheelSize, wheelWidth, -0.7, (+width / 2 - wheelWidth), true); //backleft
        drawWheels(gl, wheelSize, wheelWidth, -0.7, +width / 2 - 2*wheelWidth-0.008, true);
        drawWheels(gl, wheelSize, wheelWidth, -0.7, -(width / 2 - wheelWidth), true); //backright
        drawWheels(gl, wheelSize, wheelWidth, -0.7, -(width / 2 - 2*wheelWidth-0.008), true);
        if (debug) {
            drawDebug(gl);
        }

        //Dynamic
        context.pushMatrix(gl);
        context.translate(gl, axisDistance, width / 2 - wheelWidth, 0);
        context.rotate(gl, (float) alpha, 0, 0, 1);
        drawWheels(gl, wheelSize, wheelWidth, 0, 0, false);
        context.popMatrix(gl);
        context.pushMatrix(gl);
        context.translate(gl, axisDistance, -width / 2 + wheelWidth, 0);
        context.rotate(gl, (float) beta, 0, 0, 1);
        drawWheels(gl, wheelSize, wheelWidth, 0, 0, false);
        context.popMatrix(gl);

        context.pushMatrix(gl);
        context.translate(gl, axisDistance2, width / 2 - wheelWidth, 0);
        context.rotate(gl, (float) alpha2, 0, 0, 1);
        drawWheels(gl, wheelSize, wheelWidth, 0, 0, false);
        context.popMatrix(gl);
        context.pushMatrix(gl);
        context.translate(gl, axisDistance2, -width / 2 + wheelWidth, 0);
        context.rotate(gl, (float) beta2, 0, 0, 1);
        drawWheels(gl, wheelSize, wheelWidth, 0, 0, false);
        context.popMatrix(gl);

        context.pushMatrix(gl);
        context.translate(gl, axisDistance5, width / 2 - wheelWidth, 0);
        context.rotate(gl, (float) alpha5, 0, 0, 1);
        drawWheels(gl, wheelSize, wheelWidth, 0, 0, false);
        context.popMatrix(gl);
        context.pushMatrix(gl);
        context.translate(gl, axisDistance5, -width / 2 + wheelWidth, 0);
        context.rotate(gl, (float) beta5, 0, 0, 1);
        drawWheels(gl, wheelSize, wheelWidth, 0, 0, false);
        context.popMatrix(gl);
    }

    @Override
    public void setAlpha(double alpha) {
        super.setAlpha(alpha);
        if (alpha <= maxAlpha && alpha >= minAlpha) {
            double b = width / 2;
            this.alpha2 = Math.toDegrees(Math.atan(axisDistance2 / (ym - b)));
            this.beta2 = Math.toDegrees(Math.atan(axisDistance2 / (ym + b)));

            this.alpha5 = Math.toDegrees(Math.atan(axisDistance5 / (ym - b)));
            this.beta5 = Math.toDegrees(Math.atan(axisDistance5 / (ym + b)));
        }
    }
}
