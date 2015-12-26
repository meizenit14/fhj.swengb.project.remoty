package fhj.swengb.project.remoty


import java.net.URL
import java.util.ResourceBundle
import javafx.application.Application
import javafx.fxml.{FXML, Initializable, FXMLLoader}
import javafx.scene.control.{TreeView,TreeItem}
import javafx.scene.image.{ImageView, Image}
import javafx.scene.layout.{Pane, StackPane, BorderPane}
import javafx.scene.{Scene, Parent}
import javafx.stage.Stage

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
  @FXML var tree_view: TreeView[String] = _


  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    //set the rootItem to the tree_view
    tree_view.setRoot(rootItem)


  }

  //set a value for the picture of an folder Icon and use it for TreeItems
  val picture: Image = new Image("/fhj/swengb/project/remoty/genericFolderYellow.png")


  //make a root
  //second argument in TreeItem is a new ImageView with the "picture" value in it and then it will show an folder icon in the treeview
  val rootItem: TreeItem[String] = new TreeItem("Root",new ImageView(picture))

  //make subroots
  val item: TreeItem[String] = new TreeItem[String]("Subroot")
  //add subroots to rootItem
  rootItem.getChildren.addAll(item)







}

