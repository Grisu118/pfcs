package ch.grisu118.pfcs.a6.objects;

import ch.fhnw.pfcs.objects.RotKoerper;
import ch.grisu118.pfcs.a6.Storm2Main;
import ch.grisu118.pfcs.util.Animatable;

import javax.media.opengl.GL3;

/**
 * Created by benjamin on 18.11.2015.
 */
public class FlyingBall implements Animatable {
    private RotKoerper ball;
    private Storm2Main renderer;

    private float px, py, pz;
    private float rx, ry, rz;
    private float phi;
    private float speed = (float) (Math.random() * 10);
    private float rotSpeed = (float) (Math.random() * 100);

    public FlyingBall(RotKoerper ball, Storm2Main rd) {
        this.ball = ball;
        this.renderer = rd;

        this.px = ((float) Math.random() - 0.5f) * 4;
        this.py = ((float) Math.random() - 0.5f) * 4;
        this.rx = (float) Math.random();
        this.ry = (float) Math.random();
        this.rz = (float) Math.random();
        this.pz = -100;

        this.phi = 0;
    }

    @Override
    public void update(double dt) {
        setZ(getZ() + getSpeed() * dt * renderer.getSpeedMultiplication());
        setRotAngle(getRotAngle() + getRotSpeed() * dt);
    }

    public void setRotAngle(double angle) {
        this.phi = (float) angle % 360;
    }

    public double getRotAngle() {
        return this.phi;
    }

    public void setZ(double z) {
        this.pz = (float) z;
        if (pz > 1.0) pz = -100;
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

    @Override
    public void draw(GL3 gl) {
        renderer.pushMatrix(gl);
        renderer.translate(gl, px, py, pz);
        renderer.rotate(gl, phi, rx, ry, rz);
        ball.zeichneKugel(gl, 0.02f, 12, 12);
        renderer.popMatrix(gl);
    }
}
