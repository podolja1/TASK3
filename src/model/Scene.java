package model;

import java.util.ArrayList;

public class Scene {

    private final ArrayList<Solid> solids;

    public Scene() {
        this(new ArrayList<>());
    }

    public Scene(ArrayList<Solid> solids) {
        this.solids = solids;
    }

    public ArrayList<Solid> getSolids() {
        return solids;
    }
}
