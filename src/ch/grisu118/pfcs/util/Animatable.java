package ch.grisu118.pfcs.util;

import javax.media.opengl.GL3;

public interface Animatable {
    /**
     * Updates the Animatable object.
     * @param dt Time since last update in Seconds.
     */
    void update(double dt);

    void draw(GL3 gl);

}
