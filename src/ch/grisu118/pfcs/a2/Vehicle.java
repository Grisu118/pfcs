package ch.grisu118.pfcs.a2;

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

    public double getX();
    public void setX(double x);

    public double getY();
    public void setY(double y);
}
