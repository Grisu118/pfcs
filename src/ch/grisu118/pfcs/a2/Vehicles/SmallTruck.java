package ch.grisu118.pfcs.a2.Vehicles;

import ch.grisu118.pfcs.a2.ParkingCar;

import javax.media.opengl.GL4;

/**
 * Created by benjamin on 08.10.2015.
 */
public class SmallTruck extends AbstractVehicle {
    public SmallTruck(ParkingCar context, String name) {
        super(context, name);
        this.type = "Truck";
        this.width = 2.3f;
        this.length = 6.7f;
        this.backAxis = 2.06f;
        this.wheelSize = 1;
        this.wheelWidth = 0.4f;
        this.axisDistance = 3.4f;
        this.maxAlpha = 60;
        this.minAlpha = this.maxAlpha * -1;

        calcMinAlpha();
    }

    @Override
    public void draw(GL4 gl) {
        drawBody(gl);
        context.setColor(0.01f, 0.01f, 0.01f);
        drawWheels(gl, wheelSize, wheelWidth, 0, (+width / 2 - wheelWidth)); //backleft
        drawWheels(gl, wheelSize, wheelWidth, 0, +width / 2 - 2*wheelWidth-0.008);
        drawWheels(gl, wheelSize, wheelWidth, 0, -(width / 2 - wheelWidth)); //backright
        drawWheels(gl, wheelSize, wheelWidth, 0, -(width / 2 - 2*wheelWidth-0.008));
        if (debug) {
            drawDebug(gl);
        }

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
