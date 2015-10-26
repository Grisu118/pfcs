package ch.grisu118.pfcs.a3.objects;

import ch.fhnw.pfcs.MyRenderer1;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import ch.grisu118.pfcs.a3.FlyingCuboid;

import javax.media.opengl.GL3;
import java.util.Random;

/**
 * Created by benjamin on 26.10.2015.
 */
public class Cuboid extends ch.fhnw.pfcs.objects.Cuboid implements RotObjects {

    Vec3 rotAxis;
    double rotAngle;
    double rotStep;
    double speed;
    double x, y, z;
    Mat4 matrix = Mat4.ID;

    public Cuboid(FlyingCuboid rd) {
        super(rd);
        Random r = new Random();
        this.a = ((float) r.nextInt(50)) / 100;
        if (this.a < 0.05) this.a = 0.05f;
        this.b = ((float) r.nextInt(50)) / 100;
        if (this.b < 0.05) this.b = 0.05f;
        this.c = ((float) r.nextInt(50)) / 100;
        if (this.c < 0.05) this.c = 0.05f;

        do {
            x = ((float) r.nextInt((int) ((2 * rd.getRight()) * 100))) / 100 - rd.getRight();
        } while (!rd.checkAreaX(x));
        do {
            y = ((float) r.nextInt((int) ((2 * rd.getTop()) * 100))) / 100 - rd.getTop();
        } while (!rd.checkAreaY(y));
        z = -rd.getFar();
        matrix = Mat4.multiply(matrix, Mat4.translate((float) x, (float) y, (float) z));
        this.rotAxis = new Vec3(Math.random(), Math.random(), Math.random());
        this.rotAngle = r.nextDouble();
        this.rotStep = ((float) r.nextInt(100)) / 100;
        this.speed = r.nextDouble();
    }

    public Cuboid(double a, double b, double c, Vec3 rotAxis, FlyingCuboid rd) {
        super(a, b, c, rd);
        this.rotAxis = rotAxis;
        this.rotAngle = Math.random();
        this.rotStep = Math.random(); //TODO 1-100/100
    }

    @Override
    public void draw(GL3 gl) {
        rd.setModelViewMatrix(gl, matrix);
        super.draw(gl);
    }

    @Override
    public void setRotAngle(double angle) {
        this.rotAngle = angle;
    }

    @Override
    public double getRotAngle() {
        return rotAngle;
    }

    @Override
    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public double getZ() {
        return z;
    }
}
