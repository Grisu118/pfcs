package ch.grisu118.pfcs.a3.objects;

import javax.media.opengl.GL3;

/**
 * Created by benjamin on 26.10.2015.
 */
public interface RotObjects {

    void draw(GL3 gl);

    void setRotAngle(double angle);
    double getRotAngle();

    void setZ(double z);
    double getZ();

    double getSpeed();
    double getRotSpeed();
}
