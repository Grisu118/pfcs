package ch.grisu118.pfcs.a3b;

import ch.fhnw.pfcs.MyRenderer1;
import ch.fhnw.pfcs.objects.Cuboid;
import ch.grisu118.pfcs.a3.objects.RotObjects;

import javax.media.opengl.GL3;

/**
 * Created by benjamin on 04.11.2015.
 */
public class FlyingCuboid implements RotObjects{

    float v;
    float px, py, pz;
    float rx, ry, rz, rv;
    float phi;
    float speed = (float) (Math.random()*10);
    float rotSpeed = (float) (Math.random()*10);
    public static float SPEED = 1;
    Cuboid cub;
    MyRenderer1 renderer;

    public FlyingCuboid(Cuboid cub,
                        float v, float px, float py, float pz,
                        float rx, float ry, float rz, float rv) {
        this.v = v;
        this.px = px;
        this.py = py;
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.rv = rv;
        this.pz = pz;
        this.cub = cub;
        renderer = cub.getRd();

        phi = 0;
    }

    @Override
    public void draw(GL3 gl) {
        renderer.pushMatrix(gl);
        renderer.translate(gl, px, py, pz);
        renderer.rotate(gl, phi, rx, ry, rz);
        cub.draw(gl);
        renderer.popMatrix(gl);
    }

    @Override
    public void setRotAngle(double angle) {
        this.phi = (float)angle%360;
    }

    @Override
    public double getRotAngle() {
        return this.phi;
    }

    @Override
    public void setZ(double z) {
        this.pz = (float) z;
        if(pz > 1.0) pz = -100;
    }

    @Override
    public double getZ() {
        return this.pz;
    }

    @Override
    public double getSpeed() {
        return this.speed;
    }

    @Override
    public double getRotSpeed() {
        return this.rotSpeed;
    }

}
