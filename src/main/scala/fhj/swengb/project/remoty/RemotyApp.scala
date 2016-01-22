package fhj.swengb.project.remoty


import java.io.{IOException, File}
import java.net.URL
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file._
import java.util.ResourceBundle
import javafx.application.Application
import javafx.beans.property.SimpleStringProperty
import javafx.collections.{FXCollections, ObservableList}
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.{FXML, Initializable, FXMLLoader}
import javafx.scene.control._
import javafx.scene.image.{ImageView, Image}
import javafx.scene.input.{MouseButton, ContextMenuEvent, MouseEvent}
import javafx.scene.layout.{HBox, Pane, StackPane, BorderPane}
import javafx.scene.{Scene, Parent}
import javafx.stage.{DirectoryChooser, Stage}
import javafx.util.Callback
import scala.util.{Try, Success, Failure}
import scala.util.control.NonFatal
import scala.collection.JavaConversions._

/**
  * Created by Amar on 19.12.2015.
  */

object RemotyApp {
  def main(args: Array[String]) {
    Application.launch(classOf[RemotyApp], args: _*)
  }
}


class RemotyApp extends javafx.application.Application {


  val Css = "/fhj/swengb/project/remoty/Style.css"
  val Fxml = "/fhj/swengb/project/remoty/TreeViewTest.fxml"
  val Fxml2 = "/fhj/swengb/project/remoty/GUI_1.0.fxml"


  val loader = new FXMLLoader(getClass.getResource(Fxml2))

  override def start(stage: Stage): Unit =
    try {
      stage.setTitle("Remoty")
      loader.load[Parent]() // side effect
      val scene = new Scene(loader.getRoot[Parent]) //loads the default scene
      stage.setScene(scene)
      stage.setResizable(false) //window cannot be rescaled

      //set the stage for the controller
      val controller1 = loader.getController[RemotyAppController]
      controller1.setStage(stage)
      //stage.getScene.getStylesheets.add(Css)
      stage.show()
    } catch {
      case NonFatal(e) => e.printStackTrace()
    }
}


class RemotyAppController extends Initializable {

  @FXML var pane_view: Pane = _
  @FXML var tree_view: TreeView[PathItem] = _
  //a label to show the actions of the mouseEventHandler
  @FXML var msg_out: Label = _
  @FXML var chooserButton: Button = _
  @FXML var rootLabel: Label = _
  //needed for the cellfactory

  private var messageProp: SimpleStringProperty = new SimpleStringProperty()


  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    initializeAll()
  }


  var stage:Stage = null
  def setStage(s:Stage):Unit ={stage = s}


  def initializeAll(): Unit = {

    val chooser = new DirectoryChooser

    // set onClickAction on the "Choose Root Button" and open a "directory chooser" dialog
    chooserButton.setOnAction(new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        val selected = chooser.showDialog(stage)
        if(selected != null){
          rootLabel.setText(selected.getAbsolutePath)
          val rootPath: String = rootLabel.getText
          val root = Paths.get(rootPath)
          val item = PathTreeItem.createNode(new PathItem(root))
          item.setExpanded(true)
          item.setGraphic(new ImageView(PathTreeItem.pictureFolder))
          tree_view.setRoot(item)
          tree_view.setEditable(true)

          //setting the cellfactory
          /*
          tree_view.setCellFactory(new Callback[TreeView[PathItem],TreeCell[PathItem]]() {
            override def call(p: TreeView[PathItem]): TreeCell[PathItem] = new PathTreeCell(stage,messageProp)
          })
          */

          tree_view.setOnMouseClicked(new EventHandler[MouseEvent] {
            override def handle(event: MouseEvent): Unit = {
              if (event.getButton == MouseButton.SECONDARY) {
                println("right click")
              } else {
                println("left click")
              }
            }
          })
        }
      }
    })

  }

  
}





