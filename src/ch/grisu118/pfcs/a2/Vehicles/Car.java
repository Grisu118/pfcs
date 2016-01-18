package ch.grisu118.pfcs.a2.Vehicles;

import ch.grisu118.pfcs.a2.ParkingCar;

import javax.media.opengl.GL3;

/**
 * Created by benjamin on 06.10.2015.
 */
public class Car extends AbstractVehicle {

    public enum Cars {
        MercedesBenzGL, VWGolf
    }

    public static Car CarFactory(ParkingCar context, Cars type) {
        Car c;
        switch (type) {
            case MercedesBenzGL:
                c = new Car(context, "Mercedes Benz GL");
                c.length = 5.12f;
                c.width = 1.934f;
                c.axisDistance = 3.075f;
                c.backAxis = 1.16f;
                break;
            case VWGolf:
                c = new Car(context, "VW Golf");
                c.length = 4.199f;
                c.width = 1.786f;
                c.axisDistance = 2.575f;
                c.backAxis = 0.745f;
                break;
            default:
                throw new IllegalArgumentException("Wrong Car Type");
        }

        return c;
    }

    private Car(ParkingCar context, String name) {
        super(context, name);
        this.type = "Car";

    }

    @Override
    public void draw(GL3 gl) {
        drawBody(gl);
        context.setColor(0.01f, 0.01f, 0.01f);
        drawWheels(gl, wheelSize, wheelWidth, 0, wheelDistance, true); //backleft
        drawWheels(gl, wheelSize, wheelWidth, 0, -wheelDistance, true); //backright
        if (debug) {
            drawDebug(gl);
        }
        drawZentriPetal(gl);
        //Dynamic
        context.pushMatrix(gl);
        context.translate(gl, axisDistance, wheelDistance, 0);
        context.rotate(gl, (float) gamma, 0, 0, 1);
        drawWheels(gl, wheelSize, wheelWidth, 0, 0, false);
        context.popMatrix(gl);
        context.pushMatrix(gl);
        context.translate(gl, axisDistance, -wheelDistance, 0);
        context.rotate(gl, (float) beta, 0, 0, 1);
        drawWheels(gl, wheelSize, wheelWidth, 0, 0, false);
        context.popMatrix(gl);
    }
}
