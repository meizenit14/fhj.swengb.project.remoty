package fhj.swengb.project.remoty


import java.io.{IOException, File}
import java.net.URL
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file._
import java.util.ResourceBundle
import javafx.application.Application
import javafx.embed.swt.SWTFXUtils
import javafx.event.EventHandler
import javafx.fxml.{FXML, Initializable, FXMLLoader}
import javafx.scene.control._
import javafx.scene.image.{ImageView, Image}
import javafx.scene.input.{MouseButton, ContextMenuEvent, MouseEvent}
import javafx.scene.layout.{Pane, StackPane, BorderPane}
import javafx.scene.{Scene, Parent}
import javafx.stage.Stage
import java.util
import scala.collection.mutable
import scala.util.control.NonFatal

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
  val Fxml = "/fhj/swengb/project/remoty/Remoty.fxml"
  val Fxml2 = "/fhj/swengb/project/remoty/TreeViewTest.fxml"


  val loader = new FXMLLoader(getClass.getResource(Fxml2))

  override def start(stage: Stage): Unit =
    try {
      stage.setTitle("Remoty")
      loader.load[Parent]() // side effect
      val scene = new Scene(loader.getRoot[Parent]) //loads the default scene
      stage.setScene(scene)
      stage.setResizable(false) //window cannot be rescaled
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


  override def initialize(location: URL, resources: ResourceBundle): Unit = {
   initializeALl()
  }




  //the rootItem is expanded in default case

  def createNode(pathItem:PathItem):TreeItem[PathItem] = {
    new PathTreeItem(pathItem)
  }

  def initializeALl(): Unit = {

    val pictureFolder: Image = new Image("/fhj/swengb/project/remoty/folder.png")
    val pictureFile: Image = new Image("/fhj/swengb/project/remoty/file.png")

    val rootPath: String = "C:/Test"
    val root = Paths.get(rootPath)
    val pathItem: PathItem = new PathItem(root)
    tree_view.setRoot(createNode(pathItem))

    tree_view.getSelectionModel.setSelectionMode(SelectionMode.SINGLE)

    tree_view.setEditable(true)

  }



}





