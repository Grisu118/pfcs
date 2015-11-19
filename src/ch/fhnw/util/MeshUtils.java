package ch.fhnw.util;

import ch.fhnw.pfcs.MyRenderer1;

import javax.media.opengl.GL3;
import java.awt.*;

/**
 * Created by benjamin on 19.11.2015.
 */
public class MeshUtils {

    public static void createGroundPlane(GL3 gl, MyRenderer1 rd) {
        rd.setColor(Color.GREEN);
        rd.putVertex(-100, 0, 100);
        rd.putVertex(100, 0,100);
        rd.putVertex(100,0, -100);
        rd.putVertex(100,0, -100);
        rd.putVertex(-100,0,100);
        rd.putVertex(-100, 0, 100);
        rd.copyBuffer(gl, 6);
        gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, 6);
    }
}
