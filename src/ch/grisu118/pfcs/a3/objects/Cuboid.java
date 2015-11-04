package ch.grisu118.pfcs.a3.objects;

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
    double rotSpeed;
    double speed;
    double x, y, z;
    FlyingCuboid rd;

    public Cuboid(FlyingCuboid rd) {
        super(rd);
        this.rd = rd;
        init();
    }

    private void init() {
        Random r = new Random();
        this.a = ((float) r.nextInt(50)) / 100;
        if (this.a < 0.05) this.a = 0.05f;
        this.b = a;
        this.c = a;

        do {
            x = ((float) r.nextInt((int) ((2 * 10) * 100))) / 100 - 10;
        } while (!rd.checkAreaX(x));
        do {
            y = ((float) r.nextInt((int) ((2 * 10) * 100))) / 100 - 10;
        } while (!rd.checkAreaY(y));
        z = -2000;
        this.rotAxis = new Vec3(r.nextFloat(), r.nextFloat(), r.nextFloat());
        this.rotAngle = r.nextDouble();
        this.rotSpeed = r.nextInt(200);
        this.speed = r.nextInt(500);
    }

    public Cuboid(double a, double b, double c, Vec3 rotAxis, FlyingCuboid rd) {
        super(a, b, c, rd);
        this.rotAxis = rotAxis;
        this.rotAngle = Math.random();
        this.rotSpeed = Math.random();
    }

    @Override
    public void draw(GL3 gl) {
        rd.pushMatrix(gl);
        rd.translate(gl, x, y, z);
        rd.rotate(gl, (float) rotAngle, rotAxis.x, rotAxis.y, rotAxis.z);
                //rd.setModelViewMatrix(gl, Mat4.multiply(Mat4.translate((float) x, (float) y, (float) z), Mat4.rotate((float) rotAngle, rotAxis)));
        super.draw(gl);
        rd.popMatrix(gl);
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
        if (!rd.checkZ(z)) {
            init();
        }
        this.z = z;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public double getRotSpeed() {
        return rotSpeed;
    }
}
