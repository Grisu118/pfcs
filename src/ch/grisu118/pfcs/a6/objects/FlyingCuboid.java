package ch.grisu118.pfcs.a6.objects;

import ch.fhnw.pfcs.objects.Cuboid;
import ch.fhnw.util.math.Dynamics;
import ch.fhnw.util.math.Mat4;
import ch.grisu118.pfcs.a6.Storm2Main;
import ch.grisu118.pfcs.util.Animatable;

import javax.media.opengl.GL3;

/**
 * Created by benjamin on 04.11.2015.
 */
public class FlyingCuboid extends Dynamics implements Animatable{

    float px, py, pz;
    float rx, ry, rz;
    float phi;
    float speed = (float) (Math.random()*10);
    float rotSpeed = (float) (Math.random()*100);
    Mat4 R = Mat4.ID;
    Cuboid cub;
    Storm2Main renderer;

    float I1 = 1.4f;
    float I2 = 1.5f;
    float I3 = 1.2f;

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
        this.px = ((float) Math.random() - 0.5f) * 4;
        this.py = ((float) Math.random() - 0.5f) * 4;
        this.rx = (float) Math.random();
        this.ry = (float) Math.random();
        this.rz = (float) Math.random();
        this.pz = -100;
        this.cub = cub;
        renderer = rd;

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
        double[] a = euler(new double[]{rx, ry, rz}, dt);
        rx = (float) a[0];
        ry = (float) a[1];
        rz = (float) a[2];
        setRotAngle(Math.sqrt(rx*rx + ry*ry + rz*rz) * dt);
        R = R.postMultiply(Mat4.rotate(phi, rx, ry, rz));
        System.out.println(R);
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

    public double getRotSpeed() {
        return this.rotSpeed;
    }

}
