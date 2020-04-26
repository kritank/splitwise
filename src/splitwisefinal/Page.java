/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitwisefinal;

import java.io.IOException;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author KRITANK SINGH
 */
public class Page {
    private double xOffset = 0;
    private double yOffset = 0;
    private FXMLLoader loader;
    private ImageView exitButton;
    
    public Page(double xOffset,double yOffset,FXMLLoader loader,ImageView exitButton)
    {
        this.xOffset=xOffset;
        this.yOffset=yOffset;
        this.loader=loader;
        this.exitButton=exitButton;
    }
    void open(String s) 
    {
        try{
         loader=new FXMLLoader();
         loader.setLocation(getClass().getResource(s));
         loader.load();
         Parent root = loader.getRoot();
         Stage stage = new Stage();
        //you can use underdecorated or transparent.
        stage.initStyle(StageStyle.TRANSPARENT);
        //stage.initStyle(StageStyle.UNDERDECORATED);
       
       //grab your root here
             root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        
        //move around here
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        }
        catch(IOException e)
        {
            System.out.println("Can't open "+s);
        }
    }
    
    void close()
    {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
    
}
