package ch.fhnw.util.math;

/**
 * Created by benjamin on 02.12.2015.
 */
public abstract class Dynamics {

    /**
     * Vector field
     */
    public abstract double[] f(double[] x);

    public double[] euler(double[] x, double dt) {
        double[] y = f(x);
        double[] xx = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            xx[i] = x[i] + y[i] * dt;
        }
        return xx;
    }

    /**
     * Runge-Kutta
     *
     * @param x
     * @param dt
     * @return
     */
    public double[] runge(double[] x, double dt) {
        double dt2 = (dt / 2);
        double[] y1 = f(x);
        double[] xx = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            xx[i] = x[i] + y1[i] * dt2;
        }
        double[] y2 = f(xx);
        for (int i = 0; i < x.length; i++) {
            xx[i] = x[i] + y2[i] * dt2;
        }
        double[] y3 = f(xx);
        for (int i = 0; i < x.length; i++) {
            xx[i] = x[i] + y3[i] * dt;
        }
        double[] y4 = f(xx);
        double y;
        for (int i = 0; i < x.length; i++) {
            y = (y1[i] + 2 * y2[i] + 2 * y3[i] + y4[i]) / 6;
            xx[i] = x[i] + y * dt;
        }
        return xx;
    }
}
