package ch.grisu118.pfcs.a3.objects;

import ch.fhnw.pfcs.MyRenderer1;
import ch.fhnw.pfcs.objects.Cuboid;
import ch.grisu118.pfcs.a3.StormMain;
import ch.grisu118.pfcs.util.Animatable;
import sun.text.resources.cldr.yav.FormatData_yav;

import javax.media.opengl.GL3;

/**
 * Created by benjamin on 04.11.2015.
 */
public class FlyingCuboid implements Animatable{

    float px, py, pz;
    float rx, ry, rz;
    float phi;
    float speed = (float) (Math.random()*10);
    float rotSpeed = (float) (Math.random()*100);
    Cuboid cub;
    StormMain renderer;

    public FlyingCuboid(Cuboid cub,
                        float v, float px, float py, float pz,
                        float rx, float ry, float rz, float rv, StormMain rd) {
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

    public FlyingCuboid(Cuboid cub, StormMain rd) {
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

    @Override
    public void draw(GL3 gl) {
        renderer.pushMatrix(gl);
        renderer.translate(gl, px, py, pz);
        renderer.rotate(gl, phi, rx, ry, rz);
        cub.draw(gl);
        renderer.popMatrix(gl);
    }

    @Override
    public void update(double dt) {
        setZ(getZ() + getSpeed() * dt * renderer.getSpeedMultiplication());
        setRotAngle(getRotAngle() + getRotSpeed() * dt);
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
