package ch.grisu118.pfcs.a2.Vehicles;

import ch.grisu118.pfcs.a2.ParkingCar;

import javax.media.opengl.GL4;

/**
 * Created by benjamin on 06.10.2015.
 */
public class Car extends AbstractVehicle {

    public Car (ParkingCar context, String name) {
        super(context, name);
        this.type = "Car";
    }

    @Override
    public void draw(GL4 gl) {
        drawBody(gl);
        context.setColor(0.01f, 0.01f, 0.01f);
        drawWheels(gl, wheelSize, wheelWidth, 0, (+width / 2 - wheelWidth)); //backleft
        drawWheels(gl, wheelSize, wheelWidth, 0, -(width / 2 - wheelWidth)); //backright

        //Dynamic
        context.pushMatrix(gl);
        context.translate(gl, axisDistance, width / 2 - wheelWidth, 0);
        context.rotate(gl, (float) alpha, 0, 0, 1);
        drawWheels(gl, wheelSize, wheelWidth, 0, 0);
        context.popMatrix(gl);
        context.pushMatrix(gl);
        context.translate(gl, axisDistance, -width / 2 + wheelWidth, 0);
        context.rotate(gl, (float) beta, 0, 0, 1);
        drawWheels(gl, wheelSize, wheelWidth, 0, 0);
        context.popMatrix(gl);
    }
}
