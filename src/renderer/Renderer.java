package renderer;

import model.Scene;
import model.Solid;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import rasterize.Raster;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;
import transforms.Vec3D;

import java.util.ArrayList;
import java.util.Optional;

public class Renderer implements GrafficRendered {

    private final LineRasterizer lineRasterizer;
    private final Raster raster;

    private Mat4 model;
    private Mat4 view;
    private Mat4 projection;

    public Renderer(Raster raster) {
        this.raster = raster;
        this.lineRasterizer = new LineRasterizerGraphics(raster);

        model = new Mat4Identity();
        view = new Mat4Identity();
        projection = new Mat4Identity();
    }

    @Override

    public void draw(Scene scene) {

        ArrayList<Solid> solids = scene.getSolids();
        for (Solid solid : solids) {

            ArrayList<Point3D> vertexBuffer = (ArrayList<Point3D>) solid.getVertexBuffer();
            ArrayList<Integer> indexBuffer = (ArrayList<Integer>) solid.getIndexBuffer();

            for (int i = 0; i < indexBuffer.size(); i += 2) {
                Integer i1 = indexBuffer.get(i);
                Integer i2 = indexBuffer.get(i + 1);
                Point3D p1 = vertexBuffer.get(i1);
                Point3D p2 = vertexBuffer.get(i2);
                transformLine(p1, p2, solid.getColor(), solid.movement());
            }
        }
    }

    private void transformLine(Point3D p1, Point3D p2, int color, char movement) {
        // fixovani transformace
        switch(movement) {
            case 'y':
                p1 = p1.mul(new Mat4Identity()).mul(view).mul(projection);
                p2 = p2.mul(new Mat4Identity()).mul(view).mul(projection);
                break;
            case 'n':
                p1 = p1.mul(model).mul(view).mul(projection);
                p2 = p2.mul(model).mul(view).mul(projection);
        }

        // dehomogenizace
        Optional<Vec3D> dehomogA = p1.dehomog();
        Optional<Vec3D> dehomogB = p2.dehomog();

        if (dehomogA.isEmpty() || dehomogB.isEmpty()) return;

        Vec3D v1 = dehomogA.get();
        Vec3D v2 = dehomogB.get();

        // orez
        if (Math.min(v1.getX(),v2.getX()) < -1 || Math.max(v1.getX(),v2.getX()) > 1) {
            return;
        }
        if (Math.min(v1.getY(),v2.getY()) < -1 || Math.max(v1.getY(),v2.getY()) > 1) {
            return;
        }
        if (Math.min(v1.getZ(),v2.getZ()) < 0 || Math.max(v1.getZ(),v2.getZ()) > 1) {
            return;
        }

        Vec3D vv1 = transformToWindow(v1);
        Vec3D vv2 = transformToWindow(v2);

        lineRasterizer.rasterize(
                (int) Math.round(vv1.getX()),
                (int) Math.round(vv1.getY()),
                (int) Math.round(vv2.getX()),
                (int) Math.round(vv2.getY()),
                color
        );
    }

    private Vec3D transformToWindow(Vec3D vec) {
        return vec
                .mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D(raster.getWidth() / 2f, raster.getHeight() / 2f, 1));
    }

    @Override
    public void setModel(Mat4 model) {
        this.model = model;
    }

    @Override
    public void setView(Mat4 view) {
        this.view = view;
    }

    @Override
    public void setProjection(Mat4 projection) {
        this.projection = projection;
    }
}
