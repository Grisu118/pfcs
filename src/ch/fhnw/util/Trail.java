package ch.fhnw.util;

import ch.fhnw.pfcs.MyRenderer1;
import ch.fhnw.util.math.Vec3;

import javax.media.opengl.GL3;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by benjamin on 09.12.2015.
 */
public class Trail {

    private final Deque<Vec3> deque;
    private final boolean remove;
    private final int size;

    public Trail(int size) {
        remove = size > 1;
        this.size = size;
        deque = new LinkedList<>();
    }

    public Trail() {
        deque = new LinkedList<>();
        remove = true;
        size = 2000;
    }

    public void add(Vec3 v) {
        deque.add(v);
        if (remove && deque.size() > size) {
            deque.remove();
        }
    }

    public void draw(MyRenderer1 rd, GL3 gl) {
        rd.rewindBuffer(gl);
        deque.forEach(v ->  rd.putVertex(v.x, v.y, v.z));
        int nPunkte = deque.size();
        rd.copyBuffer(gl, nPunkte);
        gl.glDrawArrays(GL3.GL_LINE_STRIP, 0, nPunkte);
    }
}
