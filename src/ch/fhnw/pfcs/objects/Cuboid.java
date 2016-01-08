package ch.fhnw.pfcs.objects;

import ch.fhnw.pfcs.MyRenderer1;
import ch.fhnw.util.math.Vec3;

import javax.media.opengl.GL3;

/**
 * Created by benjamin on 14.10.2015.
 */
public class Cuboid {
    protected double a;
    protected double b;
    protected double c;
    protected MyRenderer1 rd;

    public Cuboid(MyRenderer1 rd) {
        this.rd = rd;
        this.a = 1;
        this.b = 1;
        this.c = 1;
    }

    public Cuboid(double a, double b, double c, MyRenderer1 rd) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.rd = rd;
    }

    public void draw(GL3 gl) {
        double a2 =a*0.5, b2=b*0.5, c2=c*0.5f;
        Vec3 A = new Vec3(a2, -b2, c2);
        Vec3 B = new Vec3(a2, -b2, -c2);
        Vec3 C = new Vec3(-a2, -b2, -c2);
        Vec3 D = new Vec3(-a2, -b2, c2);

        Vec3 E = new Vec3(a2, b2, c2);
        Vec3 F = new Vec3(a2, b2, -c2);
        Vec3 G = new Vec3(-a2, b2, -c2);
        Vec3 H = new Vec3(-a2, b2, c2);

        rd.setNormal(0,-1,0);
        putRectangle(D, C, B, A); //Boden
        rd.setNormal(0,1,0);
        putRectangle(E, F, G, H); //Decke
        rd.setNormal(1,0,0);
        putRectangle(A, B, F, E);
        rd.setNormal(0,0,1);
        putRectangle(A, E, H, D);
        rd.setNormal(-1,0,0);
        putRectangle(D, H, G, C);
        rd.setNormal(0,0,-1);
        putRectangle(B, C, G, F);
        rd.copyBuffer(gl, 36);
        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, 36);

    }

    private void putRectangle(Vec3 A, Vec3 B, Vec3 C, Vec3 D) {
        rd.putVertex(A.x, A.y, A.z);
        rd.putVertex(B.x, B.y, B.z);
        rd.putVertex(C.x, C.y, C.z);

        rd.putVertex(C.x, C.y, C.z);
        rd.putVertex(D.x, D.y, D.z);
        rd.putVertex(A.x, A.y, A.z);
    }

    public MyRenderer1 getRd() {
        return rd;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }
}
