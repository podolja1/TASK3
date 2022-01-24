package model;

import transforms.Point3D;

/**
 * Vykresleni osi
 */
public class Axis extends Solid {
        public Axis(double x, double y, double z, int color) {
        super();

        vertexBuffer.add(new Point3D(0,0,0));   // pocatecni bod, 0
        vertexBuffer.add(new Point3D(x,y,z));           // koncovy bod, 1

        addIndices(0,1);                                // spojeni obou bodu

        this.color = color;                             // barva cary (osi)

        movement = 'y';                                 // fixovani transformace
    }
}