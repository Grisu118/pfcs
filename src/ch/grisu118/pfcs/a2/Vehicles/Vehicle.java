package ch.grisu118.pfcs.a2.Vehicles;

import ch.fhnw.util.math.Mat4;

import javax.media.opengl.GL4;

/**
 * Created by benjamin on 06.10.2015.
 */
public interface Vehicle {

    void draw(GL4 gl);

    void setSpeed(double speed);
    double getSpeed();

    void setAngle(double angle);
    double getAngle();

    double getAngleMofifier();

    double getX();
    void setX(double x);

    double getAlpha();
    void setAlpha(double alpha);
    double getYm();

    double getZentripetalForce();

    double getMaxZentripetalForce();

    Mat4 getMatrix();
    void setMatrix(Mat4 matrix);

    void setDebug(boolean debug);

    void reset();
}
