package model;

import transforms.Point3D;

import java.awt.*;

/**
 * Vytvoreni jehlanu
 */
public class Pyramid extends Solid {

    public static final Color YELLOW = new Color(255,255,100);

    public Pyramid() {
        color = YELLOW.getRGB();                        // barva jehlanu

        vertexBuffer.add(new Point3D(0,0,0));   // 0
        vertexBuffer.add(new Point3D(2,0,0));   // 1
        vertexBuffer.add(new Point3D(2,0,2));   // 2
        vertexBuffer.add(new Point3D(0,0,2));   // 3
        vertexBuffer.add(new Point3D(1,2,1));   // 4

        addIndices(0,1,1,2,2,3,3,0);                    // spojeni bodu, vytvoreni car
        addIndices(0,4,1,4,2,4,3,4);

        movement = 'n';                                 // fixovani transformace

        /*
                   4
                 * * *
               *   *   *
              3  *  *  * 2
            *       *  *
          *          *
        0  *  *  *  1

        */
    }
}

