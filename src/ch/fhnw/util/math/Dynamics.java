package ch.fhnw.util.math;

/**
 * Created by benjamin on 02.12.2015.
 */
public abstract class Dynamics {

    public double[] euler(double[] x, double dt) {
        return f(x);
    }

    abstract double[] f(double[] x);
}
