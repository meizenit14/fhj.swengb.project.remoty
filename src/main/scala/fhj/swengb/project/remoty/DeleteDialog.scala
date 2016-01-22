package fhj.swengb.project.remoty

import javafx.event.{EventHandler, ActionEvent}
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
  * Created by Amar on 22.01.2016.
  */

class DeleteDialog(owner: Stage, treeItem: TreeItem[PathItem], prop: ObjectProperty[TreeItem[PathItem]]) {


      val dialog: Stage = new Stage(StageStyle.UTILITY)
      dialog.initOwner(owner)
      dialog.initModality(Modality.APPLICATION_MODAL)
      val root: GridPane = new GridPane()
      root.setPadding(new Insets(30))
      root.setHgap(5)
      root.setVgap(10)
      val label: Label = new Label("Are you sure?");
      val okButton: Button = new Button("OK");
      okButton.setOnAction(new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          prop.set(treeItem)
          dialog.hide()
        }
      })
      val cancelButton: Button = new Button("Cancel");
      cancelButton.setOnAction(new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          dialog.hide()
        }
      });
      root.add(label, 0, 0, 2, 1)
      root.addRow(1, okButton, cancelButton)
      dialog.setScene(new Scene(root))
      dialog.show()




}
