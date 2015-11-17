package ch.grisu118.pfcs.a4;// ----------  Bumerang-Modell  --------------------------


// F. Schramka (2014)
// Inspiriert vom Bumerang aus Zelda: Ocarina of Time

// adaptiert durch E.Gutknecht Nov. 2015
// (OpenGL3, und Pkte H1 und O, Holzfarbe)

import ch.fhnw.pfcs.MyRenderer1;
import ch.grisu118.pfcs.util.Animatable;

import javax.media.opengl.GL2;
import javax.media.opengl.GL3;

/**
 * F. Schramka (2014)
 * Inspiriert vom Bumerang aus Zelda: Ocarina of Time
 * <p>
 * adaptiert durch E.Gutknecht Nov. 2015
 * (OpenGL3, und Pkte H1 und O, Holzfarbe)
 *
 * @author F.Schramka, E.Gutkneckt, Benjamin Leber
 */
public class SturmBumerang implements Animatable {

    //  ---------  globale Variabeln  -----------------

    MyRenderer1 rd;
    int nVertices;

    //Animatable
    private float alpha = 0; //Rotation auf der Bahn
    private float beta = 0;//Math.round(Math.random()*360); //Rotation des Bummerangs
    private float gamma; //Kippwinkel der Bahn
    private float radius;



    //  --------  Methoden  --------------------------

    public SturmBumerang(MyRenderer1 rd, float radius, float gamma) {
        this.rd = rd;
        this.gamma = gamma;
        this.radius = radius;
    }


    private void putVertexd(double x, double y, double z) {
        rd.putVertex((float) x, (float) y, (float) z);
    }


    private void putPolygon(double... x) {
        for (int i = 1; i < x.length / 3 - 1; i++) {
            putVertexd(x[0], x[1], x[2]);
            putVertexd(x[i * 3], x[i * 3 + 1], x[i * 3 + 2]);
            putVertexd(x[i * 3 + 3], x[i * 3 + 4], x[i * 3 + 5]);
            nVertices += 3;
        }
    }


    private void zeichneSturmBumerang(GL3 gl) {
        nVertices = 0;
        rd.rewindBuffer(gl);
        nVertices = 0;
        rd.setColor(0.85f, 0.263f, 0.153f);      //Holzfarbe
        putPolygon(-5, -0.32, 0,    //A
                -4.44, 0.01, 0,    //B
                -3.5, -0.01, 0.05,   //N1
                -3.63, -0.62, 0.07);  //K1

        putPolygon(-3.3, -0.78, 0,    //V
                -1.4, -1.21, 0,    //G
                -1.17, -1.16, 0.2,   //P
                -3.29, -0.7, 0.09);   //M1

        putPolygon(-3.15, -0.02, 0.06,  //P1
                -0.81, -0.07, 0.2,   //M
                -0.86, 0, 0,     //C
                -3.15, 0.01, 0);    //W

        putPolygon(-3.15, -0.02, 0.06,  //P1
                -3.29, -0.7, 0.09,   //M1
                -1.17, -1.16, 0.2,   //P
                -0.81, -0.07, 0.2);   //M

        putPolygon(-3.15, 0.01, 0,    //W
                -0.86, 0, 0,       //C
                -1.4, -1.21, 0,    //G
                -3.3, -0.78, 0);    //V

        putPolygon(0.04, -3.14, 0,    //D1
                -0.74, -3.39, 0,   //C1
                -0.71, -3.39, 0.07,  //S1
                0.01, -3.15, 0.07);   //T1

        putPolygon(0, -3.5, 0.05,     //V1
                0.03, -3.49, 0,    //Z
                -0.67, -3.72, 0,   //A1
                -0.65, -3.72, 0.05);  //Z1

        putPolygon(-0.86, 0, 0,     //C
                0, 0, 0,          //D
                0.11, -0.68, 0,    //E
                -1.18, -1.4, 0,    //H
                -1.4, -1.21, 0);    //G

        putPolygon(0.03, -0.7, 0.2,    //O
                -1.17, -1.16, 0.2,   //P
                -0.71, -3.39, 0.07,  //S1
                0.01, -3.15, 0.07);   //T1

        putPolygon(0.04, -3.14, 0,    //D1
                0.01, -3.15, 0.07,   //T1
                0.03, -0.7, 0.2,    //O
                0.11, -0.68, 0);    //E

        putPolygon(0.11, -0.68, 0,    //E
                -1.18, -1.4, 0,    //H
                -0.74, -3.39, 0,   //C1
                0.04, -3.14, 0);    //D1

        putPolygon(-0.74, -3.39, 0,   //C1
                -0.71, -3.39, 0.07,  //S1
                -1.17, -1.16, 0.2,   //P
                -1.18, -1.4, 0);    //H

        putPolygon(0, -4.43, 0,     //L
                -0.07, -4.78, 0,   //K
                -0.27, -4.98, 0,   //J
                -0.46, -4.63, 0,   //I
                -0.65, -3.72, 0.05,  //Z1
                0, -3.5, 0.05);     //V1

        putPolygon(0, -4.43, 0,     //L
                0, -3.5, 0.05,     //V1
                0.03, -3.49, 0);    //Z

        putPolygon(-0.46, -4.63, 0,   //I
                -0.65, -3.72, 0.05,  //Z1
                -0.67, -3.72, 0);   //A1

        putPolygon(-0.67, -3.72, 0,   //A1
                0.03, -3.49, 0,    //Z
                0, -4.43, 0,     //L
                -0.07, -4.78, 0,   //K
                -0.27, -4.98, 0,   //J
                -0.46, -4.63, 0);   //I

        putPolygon(-5, -0.32, 0,    //A
                -4.15, -0.59, 0,   //F
                -3.64, -0.7, 0,   //R
                -3.63, -0.62, 0.07);  //K1

        putPolygon(-5, -0.32, 0,    //A
                -4.44, 0.01, 0,    //B
                -3.5, 0.01, 0,   //Q
                -3.64, -0.7, 0);   //R

        putPolygon(-4.44, 0.01, 0,    //B
                -3.5, 0.01, 0,   //Q
                -3.5, -0.01, 0.05);   //N1

        rd.setColor(0.018f, 0.424f, 0.23f);     // Gr�n

        putPolygon(-3.5, -0.01, 0.05,   //N1
                -3.63, -0.62, 0.07,  //K1
                -3.64, -0.7, 0,   //R
                -3.5, 0.01, 0);   //Q

        putPolygon(0, -3.5, 0.05,     //V1
                0.03, -3.49, 0,    //Z
                -0.67, -3.72, 0,   //A1
                -0.65, -3.72, 0.05);  //Z1

        putPolygon(-3.33, 0.01, 0,    //T
                -3.33, -0.02, 0.06,  //O1
                -3.5, -0.01, 0.05);   //N1

        putPolygon(0, -3.5, 0.05,     //V1
                0.01, -3.32, 0.06,   //U1
                0.03, -3.32, 0,    //E1
                0.03, -3.49, 0);    //Z

        putPolygon(0, -3.5, 0.05,     //V1
                0.01, -3.32, 0.06,   //U1
                -0.65, -3.72, 0.05,  //Z1
                -0.68, -3.56, 0.06);  //R1

        putPolygon(-0.65, -3.72, 0.05,  //Z1
                -0.68, -3.56, 0.06,  //R1
                -0.7, -3.56, 0,    //B1
                -0.67, -3.72, 0);   //A1

        putPolygon(-0.7, -3.56, 0,    //B1
                -0.67, -3.72, 0,   //A1
                0.03, -3.32, 0,    //E1
                0.03, -3.49, 0);   //Z

        putPolygon(-3.63, -0.62, 0.07,  //K1
                -3.49, -0.66, 0.08,  //L1
                -3.33, -0.02, 0.06,  //O1
                -3.5, -0.01, 0.05);   //N1

        putPolygon(-3.33, -0.02, 0.06,  //O1
                -3.33, 0.01, 0,    //T
                -3.47, -0.74, 0,    //U
                -3.49, -0.66, 0.08);  //L1

        putPolygon(-3.49, -0.66, 0.08,  //L1
                -3.47, -0.74, 0,    //U
                -3.64, -0.7, 0,     //R
                -3.63, -0.62, 0.07);  //K1

        putPolygon(-3.47, -0.74, 0,    //U
                -3.64, -0.7, 0,   //R
                -3.5, 0.01, 0,   //Q
                -3.33, 0.01, 0);    //T

        putPolygon(-0.68, -3.56, 0.06,  //R1
                -0.7, -3.56, 0,    //B1
                0.01, -3.32, 0.06,   //U1
                0.03, -3.32, 0);    //E1

        rd.setColor(1, 0.93f, 0.4f);       // Gelb
        putPolygon(-3.15, 0.01, 0,    //W
                -3.15, -0.02, 0.06,  //P1
                -3.33, -0.02, 0.06,  //O1
                -3.33, 0.01, 0);    //T

        putPolygon(-0.68, -3.56, 0.06,  //R1
                -0.7, -3.56, 0,     //B1
                0.01, -3.32, 0.06,   //U1
                0.03, -3.32, 0);    //E1

        putPolygon(0.04, -3.14, 0,    //D1
                0.03, -3.32, 0,    //E1
                0.01, -3.32, 0.06,   //U1
                0.01, -3.15, 0.07);   //T1

        putPolygon(0.01, -3.32, 0.06,   //U1
                0.01, -3.15, 0.07,   //T1
                -0.68, -3.56, 0.06,  //R1
                -0.71, -3.39, 0.07);  //S1

        putPolygon(-0.68, -3.56, 0.06,  //R1
                -0.71, -3.39, 0.07,  //S1
                -0.74, -3.39, 0,   //C1
                -0.7, -3.56, 0);    //B1

        putPolygon(-0.74, -3.39, 0,   //C1
                -0.7, -3.56, 0,    //B1
                0.04, -3.14, 0,    //D1
                0.03, -3.32, 0);    //E1

        putPolygon(-3.49, -0.66, 0.08,  //L1
                -3.29, -0.7, 0.09,   //M1
                -3.3, -0.78, 0,    //V
                -3.47, -0.74, 0);    //U

        putPolygon(-0.68, -3.56, 0.06,  //R1
                0.01, -3.32, 0.06,   //U1
                0.03, -3.32, 0,    //E1
                -0.7, -3.56, 0);    //B1

        putPolygon(0.04, -3.14, 0,    //D1
                -0.74, -3.39, 0,   //C1
                -0.71, -3.39, 0.07,  //S1
                0.01, -3.15, 0.07);   //T1

        putPolygon(-3.47, -0.74, 0,    //U
                -3.49, -0.66, 0.08,  //L1
                -3.33, -0.02, 0.06);  //O1

        putPolygon(-3.15, -0.02, 0.06,  //P1
                -3.29, -0.7, 0.09,   //M1
                -3.49, -0.66, 0.08,  //L1
                -3.33, -0.02, 0.06);  //O1

        putPolygon(-3.15, -0.02, 0.06,  //P1
                -3.15, 0.01, 0,    //W
                -3.3, -0.78, 0,    //V
                -3.29, -0.7, 0.09);   //M1

        rd.setColor(0.99f, 0.89f, 0.588f);  // Gold
        putPolygon(-1.18, -1.4, 0,    //H
                -1.17, -1.16, 0.2,   //P
                -1.4, -1.21, 0);    //G

        putPolygon(-0.81, -0.07, 0.2,   //M
                -0.86, 0, 0,     //C
                0, 0, 0,       //D
                -0.07, -0.07, 0.2);   //N

        putPolygon(0, 0, 0,       //D
                -0.07, -0.07, 0.2,   //N
                0.03, -0.7, 0.2,    //O
                0.11, -0.68, 0);    //E

        rd.setColor(0.85f, 0.20f, 0.145f);      // Rot Hell
        putPolygon(-0.81, -0.07, 0.2,   //M
                -0.38, -0.39, 0.3,   //H1
                -1.17, -1.16, 0.2);   //P

        putPolygon(-0.38, -0.39, 0.3,   //H1
                0.03, -0.7, 0.2,    //O
                -0.07, -0.07, 0.2);   //N

        rd.setColor(0.66f, 0.17f, 0.141f);      // Rot Dunkel
        putPolygon(-0.38, -0.39, 0.3,   //H1
                -0.81, -0.07, 0.2,   //M
                -0.07, -0.07, 0.2);   //N

        putPolygon(0.03, -0.7, 0.2,    //O
                -1.17, -1.16, 0.2,   //P
                -0.38, -0.39, 0.3);   //H1
        rd.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL2.GL_TRIANGLES, 0, nVertices);

    }

    @Override
    public void update(double dt) {
        alpha += dt * 50;
        beta += dt * 1000;

        if (alpha > 360) {
            alpha = 0;
        }
        if (beta > 360) {
            beta = 0;
        }
    }

    @Override
    public void draw(GL3 gl) {
        rd.pushMatrix(gl);
        rd.rotate(gl, 90, 1,0,0);
        rd.rotate(gl, gamma, 0,1,0);
        rd.rotate(gl, alpha, 0,0,1);
        rd.translate(gl, radius, 0, 0);
        rd.rotate(gl, 55, 0,1,0);
        rd.rotate(gl, beta, 0,0,1);
        zeichneSturmBumerang(gl);
        rd.popMatrix(gl);
    }
}