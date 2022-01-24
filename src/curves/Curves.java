package curves;

import model.Solid;
import transforms.Cubic;
import transforms.Point3D;

/**
 * Vykresleni krivek v kvadru
 */
public class Curves extends Solid {
    public Curves(int p,String type,int color) {
        super();

        // umisteni bodu v prostoru
        Point3D p1 = new Point3D(0,0, 0);           // Cuboid roh 0
        Point3D p2 = new Point3D(0.75,0, 1);
        Point3D p3 = new Point3D(0,1, 1.5);
        Point3D p4 = new Point3D(1,1, 2);           // Cuboid roh 6

        Point3D p5 = new Point3D(-1,1, 5);          // Cuboid roh 3
        Point3D p6 = new Point3D(1,1, 1);
        Point3D p7 = new Point3D(2,-5, 6.5);        // Cuboid roh 7

        Point3D p8 = new Point3D(1,1, 1);           // Cuboid hrana 2-6
        Point3D p9 = new Point3D(0,0, 1);
        Point3D p10 = new Point3D(0,0, 0.5);
        Point3D p11 = new Point3D(0,0, 2);          // Cuboid hrana 0-4

        Cubic cubic = null;
        
        if (type == "beizer") {
            cubic = new Cubic(Cubic.BEZIER, p1, p2, p3, p4);
        } else if (type == "coons") {
            cubic = new Cubic(Cubic.COONS, p5, p3, p6, p7);
        } else if (type == "ferguson") {
            cubic = new Cubic(Cubic.FERGUSON, p8, p9, p10, p11);
        }

        // spojeni bodu, vytvoreni krivky
        int i = 0;
        while (i < p) {
            getIndexBuffer().add(i);
            getIndexBuffer().add(i + 1);
            i++;
        }

        // propocet prubehu krivky
        int n = 0;
        while (n <= p) {
            Point3D computed = cubic.compute((double) n / p);
            getVertexBuffer().add(computed);
            n++;
        }

        this.color = color;

        movement = 'n';     // fixovani transformace
    }
}
