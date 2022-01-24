package control;

import curves.Curves;
import model.*;
import rasterize.Raster;
import renderer.GrafficRendered;
import renderer.Renderer;
import transforms.*;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class Controller3D implements Controller {

    public static final Color RED = new Color(255,0,0);
    public static final Color GREEN = new Color(0,255,0);
    public static final Color BLUE = new Color(0,0,255);
    public static final Color ORANGE = new Color(240, 90, 30);
    public static final Color BROWN = new Color(220, 200, 130);
    public static final Color PURPLE = new Color(180, 150, 230);

    private final Panel panel;
    private GrafficRendered renderer;
    private Raster raster;

    private Mat4 projection;
    private Mat4 model;
    private Camera camera;

    private Scene mainScene;

    private int pointX, pointY;
    private double ax = 2.1;

    Timer timer;

    public Controller3D(Panel panel) {

        this.panel = panel;
        this.raster = panel.getRaster();
        this.renderer = new Renderer(raster);

        geometricBodies();

        initListeners(panel);

        display();
    }

    private void geometricBodies() {
        model = new Mat4Identity();
        mainScene = new Scene();
        mainScene.getSolids().add(new Axis(ax,0,0, RED.getRGB()));       // Osa X - RED
        mainScene.getSolids().add(new Axis(0, ax,0, GREEN.getRGB()));     // Osa Y - GREEN
        mainScene.getSolids().add(new Axis(0,0, ax, BLUE.getRGB()));      // Osa Z - BLUE
        mainScene.getSolids().add(new Cuboid());
        mainScene.getSolids().add(new Pyramid());

        camera = new Camera()
                .withPosition(new Vec3D(2, -4, 1))
                .withAzimuth(Math.toRadians(60))
                .withZenith(Math.toRadians(50));

        // perspektivni projekce, muzes si zkusit zapnout nejdriv tuto
        /*
        projection = new Mat4PerspRH(
                Math.PI / 2,
                panel.getRaster().getHeight() / (float) panel.getRaster().getWidth(),
                0.1,
                50.0
        );
        */

        // ortonormalni projekce
        projection = new Mat4OrthoRH(10, 10, 0.1, 50.0);

        // ortonormalni projekce, muzes treba i takhle dopocitat
        /*
        projection = new Mat4OrthoRH(
                panel.getRaster().getWidth()/ 70,
                panel.getRaster().getHeight()/70,
                0.1,
                10);
         */

    }

    private void axis() {
        mainScene = new Scene();
        mainScene.getSolids().add(new Axis(ax,0,0, RED.getRGB()));       // Osa X - RED
        mainScene.getSolids().add(new Axis(0,ax,0, GREEN.getRGB()));     // Osa Y - GREEN
        mainScene.getSolids().add(new Axis(0,0,ax, BLUE.getRGB()));      // Osa Z - BLUE
    }

    private void display() {
        raster.clear();
        renderer.setModel(model);
        renderer.setProjection(projection);
        renderer.setView(camera.getViewMatrix());
        renderer.draw(mainScene);
        panel.repaint();
    }

    /**
     * Pridani listeneru
     * Urcuji chovani projekce po interakci s mysi nebo klavesnice
     * @param panel
     */
    @Override
    public void initListeners(Panel panel) {

        // tlacitka a pohyby mysi
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isControlDown()) return;

                if (e.isShiftDown()) {
                // leve tlacitko mysi a zjisteni souradnic
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    pointX = e.getX();
                    pointY = e.getY();

                } else if (SwingUtilities.isMiddleMouseButton(e)) {

                } else if (SwingUtilities.isRightMouseButton(e)) {

                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // dvojklik levym tlacitkem, zobrazeni jen kvadru
                if ((SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2)) {
                    axis();
                    mainScene.getSolids().add(new Cuboid());
                // dvojklik pravym tlacitkem, zobrazeni jen jehlanu
                } else if ((SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 2)) {
                    axis();
                    mainScene.getSolids().add(new Pyramid());
                }
                // dvojklik strednim tlacitkem, zobrazeni obou prvku
                else if ((SwingUtilities.isMiddleMouseButton(e) && e.getClickCount() == 2)) {
                    axis();
                    mainScene.getSolids().add(new Cuboid());
                    mainScene.getSolids().add(new Pyramid());
                    display();
                }
                display();
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (e.isControlDown()) return;
                // podrzeni leveho tlacitka a pohyb po platne
                if (SwingUtilities.isLeftMouseButton(e)) {
                    camera = camera.addAzimuth(Math.PI * ((double) (pointX - e.getX()) / raster.getWidth()));
                    camera = camera.addZenith(Math.PI * ((double) (e.getY() - pointY) / raster.getHeight()));

                    pointX = e.getX();
                    pointY = e.getY();

                // podrzeni praveho tlacitka a pohyb kolem osy XYZ
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    model = model.mul(new Mat4RotXYZ(
                            Math.PI*((double) (pointX - e.getX()) / raster.getWidth()),
                            0,
                            Math.PI*((double) (e.getY() - pointY) / raster.getHeight())));

                    pointX = e.getX();
                    pointY = e.getY();

                } else if (SwingUtilities.isMiddleMouseButton(e)) {

                }
                display();
            }
        });

        /**
         * Zoom mys
         */
        panel.addMouseWheelListener(new MouseAdapter() {
            @Override
            // pouziti kolecka mysi pro zoom
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() > 0) {
                    model = model.mul(new Mat4Scale(1.1));
                } else {
                    model = model.mul(new Mat4Scale(0.9));
                }
                display();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                /**
                 * Kamera
                 */
                 // klavesa W, pohyb nahoru
                if(e.getKeyCode()==KeyEvent.VK_W) {
                    camera = camera.up(0.2);
                    display();

                } // klavesa S, pohyb dolu
                else if(e.getKeyCode()==KeyEvent.VK_S) {
                    camera = camera.down(0.2);
                    display();

                } // klavesa A, pohyb vlevo
                else if(e.getKeyCode()==KeyEvent.VK_A) {
                    camera = camera.left(0.2);
                    display();

                } // klavesa D, pohyb vpravo
                else if(e.getKeyCode()==KeyEvent.VK_D) {
                    camera = camera.right(0.2);
                    display();
                }
                /**
                 * Projekce
                 */
                // klavesa P, perspektivni
                else if(e.getKeyCode()==KeyEvent.VK_P) {
                    projection = new Mat4PerspRH(
                            Math.PI / 2,
                            panel.getRaster().getHeight() / (float) panel.getRaster().getWidth(),
                            0.1,
                            10.0
                    );
                    display();
                }
                // klavesa O, ortogonalni
                else if(e.getKeyCode()==KeyEvent.VK_O) {
                    projection = new Mat4OrthoRH(10, 10, 0.1, 50.0);
                    display();
                }
                /**
                 * Prace s platnem
                 */
                // klavesa delete, vymazani objetu, zustanou osy
                else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    axis();
                    display();
                }
                // klavesa backspace, navrat do puvodni pozie
                else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    geometricBodies();
                    display();
                }

                /**
                 * Zoom
                 */
                else if (e.getKeyCode() == KeyEvent.VK_ADD) {//
                    model = model.mul(new Mat4Scale(1.1));
                    display();
                }
                else if (e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
                    model = model.mul(new Mat4Scale(0.9));
                    display();
                }

                /**
                 * Rotace kolem osi
                 */
                // osa X
                else if (e.getKeyCode() == KeyEvent.VK_X) {
                    model = model.mul(new Mat4RotX(0.2));
                    display();
                }
                // osa Y
                else if (e.getKeyCode() == KeyEvent.VK_Y) {
                    model = model.mul(new Mat4RotY(0.2));
                    display();
                }
                // osa Z
                else if (e.getKeyCode() == KeyEvent.VK_Z) {
                    model = model.mul(new Mat4RotZ(0.2));
                    display();
                }

                /**
                 * Translace v osach
                 */
                // osa X vlevo
                else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    model = model.mul(new Mat4Transl(-0.5,0,0));
                    display();
                } // osa X vpravo
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    model = model.mul(new Mat4Transl(0.5,0,0));
                    display();
                } // osa Y nahoru
                else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    model = model.mul(new Mat4Transl(0,0.5,0));
                    display();
                } // osa Y dolu
                else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    model = model.mul(new Mat4Transl(0,-0.5,0));
                    display();
                } // osa Z dopredu
                else if (e.getKeyCode() == KeyEvent.VK_NUMPAD2) {
                model = model.mul(new Mat4Transl(0,0,-0.5));
                display();
                } // osa Z dozadu
                else if (e.getKeyCode() == KeyEvent.VK_NUMPAD8) {
                model = model.mul(new Mat4Transl(0,0,0.5));
                display();
                }
                /**
                 * Vykresleni krivek v objektu
                 */
                // krivka typ BEIZER
                else if (e.getKeyCode() == KeyEvent.VK_F1) {
                    mainScene.getSolids().add(new Curves(10, "beizer", ORANGE.getRGB()));
                    display();
                }
                // krivka typ COONS
                else if (e.getKeyCode() == KeyEvent.VK_F2) {
                    mainScene.getSolids().add(new Curves(10, "coons", BROWN.getRGB()));
                    display();
                }
                // krivka typ FERGUSON
                else if (e.getKeyCode() == KeyEvent.VK_F3) {
                    mainScene.getSolids().add(new Curves(10, "ferguson",PURPLE.getRGB()));
                    display();
                }
                /**
                 * Animace
                 */
                // start
                else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            double change = 0.02;
                            model = model.mul(new Mat4RotXYZ(-change, change, 0));
                            display();
                        }
                    }, 0, 15);
                } // stop
                else if (e.getKeyCode() == KeyEvent.VK_ALT) {
                    timer.cancel();
                    display();
                }
            }
        });
    }
}
