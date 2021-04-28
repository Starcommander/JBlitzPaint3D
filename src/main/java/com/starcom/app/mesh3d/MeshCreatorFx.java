package com.starcom.app.mesh3d;

import java.util.ArrayList;

import javafx.scene.shape.Line;
import javafx.scene.shape.TriangleMesh;
import com.starcom.app.Layer;

@Deprecated
public class MeshCreatorFx
{
  TriangleMesh mesh = new TriangleMesh();

  public MeshCreatorFx(ArrayList<Layer> layers)
  {
    createPolygon(layers.get(0));
  }

  @Deprecated
  private void createPolygon(Layer layer)
  {
    float z = (float) layer.y_3d;
    int p_count = layer.getPaintObjects().size();
    float points[] = new float[p_count*3];
    for (int i=0; i<p_count; i++)
    {
      Line line = (Line) layer.getPaintObjects().get(i).getNodeList().get(0);
      float x = (float) line.getStartX();
      float y = (float) line.getStartY();
      points[i*3] = x;
      points[i*3 + 1] = y;
      points[i*3 + 2] = z;
      //TODO: Triangles!!!
      // int faces[] = { … };
      // mesh.getFaces().addAll(faces);
    }
    mesh.getTexCoords().addAll(0,0); // Any
    mesh.getPoints().addAll(points);
  }

  @Deprecated
  public TriangleMesh getMesh() { return mesh; }
}
