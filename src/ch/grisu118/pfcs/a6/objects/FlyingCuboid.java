package ch.grisu118.pfcs.a6.objects;

import ch.fhnw.pfcs.objects.Cuboid;
import ch.fhnw.util.math.Dynamics;
import ch.fhnw.util.math.Mat4;
import ch.grisu118.pfcs.a6.Storm2Main;
import ch.grisu118.pfcs.util.Animatable;

import javax.media.opengl.GL3;
import java.util.Random;

/**
 * Created by benjamin on 04.11.2015.
 */
public class FlyingCuboid extends Dynamics implements Animatable{

    float px, py, pz;
    float rx, ry, rz;
    float phi;
    float speed = (float) (Math.random()*10);
    Mat4 R = Mat4.ID;
    Cuboid cub;
    Storm2Main renderer;

    float I1;
    float I2;
    float I3;

    public FlyingCuboid(Cuboid cub,
                        float v, float px, float py, float pz,
                        float rx, float ry, float rz, float rv, Storm2Main rd) {
        this.px = px;
        this.py = py;
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.pz = pz;
        this.cub = cub;
        renderer = rd;

        phi = 0;
    }

    public FlyingCuboid(Cuboid cub, Storm2Main rd) {
        Random r = new Random();
        this.px = ((float) Math.random() - 0.5f) * 4;
        this.py = ((float) Math.random() - 0.5f) * 4;
        this.rx = r.nextInt(100);
        this.ry = r.nextInt(100);
        this.rz = r.nextInt(100);
        this.pz = -100;
        this.cub = cub;
        renderer = rd;

        double a = cub.getA()*cub.getA(), b = cub.getB()*cub.getB(), c = cub.getC()*cub.getC();
        double m = cub.getA()*cub.getB()*cub.getC();

        I1 = (float)(1/12*m*(b+c));
        I2 = (float)(1/12*m*(a + c));
        I3 = (float)(1/12*m*(a + b));

        phi = 0;
    }

    public FlyingCuboid(Storm2Main rd) {
        this(new Cuboid((float) Math.random()/10, (float) Math.random()/10, (float) Math.random()/10, rd), rd);
    }

    @Override
    public void draw(GL3 gl) {
        renderer.pushMatrix(gl);
        renderer.translate(gl, px, py, pz);
        renderer.setModelViewMatrix(gl, renderer.getModelViewMatrix(gl).postMultiply(R));
        //renderer.rotate(gl, phi, rx, ry, rz);
        cub.draw(gl);
        renderer.popMatrix(gl);
    }

    @Override
    public void update(double dt) {
        setZ(getZ() + getSpeed() * dt * renderer.getSpeedMultiplication());
        double[] a = runge(new double[]{rx, ry, rz}, dt);
        rx = (float) a[0];
        ry = (float) a[1];
        rz = (float) a[2];
        setRotAngle(Math.sqrt(rx*rx + ry*ry + rz*rz) * dt);
        R = R.postMultiply(Mat4.rotate(phi, rx, ry, rz));
    }


    @Override
    public double[] f(double[] x) {
        return new double[] {
                (I2-I3) * x[1]*x[2],
                (I3-I1) * x[2]*x[0],
                (I1-I2) * x[0]*x[1]
        };
    }

    public void setRotAngle(double angle) {
        this.phi = (float)angle%360;
    }

    public double getRotAngle() {
        return this.phi;
    }

    public void setZ(double z) {
        this.pz = (float) z;
        if(pz > 1.0) pz = -100;
    }

    public double getZ() {
        return this.pz;
    }

    public double getSpeed() {
        return this.speed;
    }

}
