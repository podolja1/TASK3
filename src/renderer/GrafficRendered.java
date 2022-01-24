package renderer;

import model.Scene;
import transforms.Mat4;

public interface GrafficRendered {

    void draw(Scene scene);

    void setModel(Mat4 model);

    void setView(Mat4 view);

    void setProjection(Mat4 projection);
}
