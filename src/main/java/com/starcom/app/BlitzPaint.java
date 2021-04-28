package com.starcom.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class BlitzPaint extends Application
{
  public static void main(String[] args)
  {
    Application.launch(BlitzPaint.class, args);
  }

  @Override
  /** This method is called when initialization is complete **/
  public void init() {}

  @Override
  public void start(Stage stage) throws Exception
  {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BlitzPaint.fxml"));
    Parent root = (Parent)fxmlLoader.load();
    BlitzPaintFrame frame = fxmlLoader.getController();
    stage.setTitle("### JBlitzPaint ###");
    stage.setScene(new Scene(root, (BlitzPaintFrame.SIZE_X*2) + 20, BlitzPaintFrame.SIZE_Y + 20));
    stage.getIcons().add(new Image(getClass().getResourceAsStream("icons/video_display.png")));
    frame.onShowPre(); stage.show(); frame.onShowPost();
  }

}
