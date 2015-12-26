package fhj.swengb.project.remoty


import java.io.{IOException, File}
import java.net.URL
import java.util.ResourceBundle
import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.{FXML, Initializable, FXMLLoader}
import javafx.scene.control.{Label, TreeView, TreeItem}
import javafx.scene.image.{ImageView, Image}
import javafx.scene.input.MouseEvent
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
  //a label to show the actions of the mouseEventHandler
  @FXML var msg_out: Label = _


  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    //set the rootItem to the tree_view
    tree_view.setRoot(rootItem)

    //initialize the mouseEventHandler on the TreeView
    tree_view.setOnMouseClicked(mouseEvent)
  }


  //set a value for the picture of an folder Icon and use it for TreeItems
  val pictureFolder: Image = new Image("/fhj/swengb/project/remoty/folder.png")
  val pictureFile: Image = new Image("/fhj/swengb/project/remoty/file.png")


  //make a root
  //second argument in TreeItem is a new ImageView with the "picture" value in it and then it will show an folder icon in the treeview
  //with "System.getenv("SystemDrive") you can get the letter of drive...
  val rootItem: TreeItem[String] = new TreeItem(System.getenv("SystemDrive"),new ImageView(pictureFolder))
  //the rootItem is expanded in default case
  rootItem.setExpanded(true)

  /*
  //make subroots
  val item: TreeItem[String] = new TreeItem[String]("Subroot")
  //add subroots to rootItem
  rootItem.getChildren.addAll(item)
*/

//a mouseEventHandler which is for the TreeView
  val mouseEvent: EventHandler[_ >: MouseEvent] = new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = {
      event.getSource match {
        case clicked: TreeView[String] => msg_out.setText("You clicked on:  " + clicked.getSelectionModel.getSelectedItem.getValue)
      }
    }
  }


  /**
    * 
    * Iterates over the files and directories of the given directory and enters it in the treeview
    *
    */

  //first set the directory as string
  val directory: File = new File("""C:\""")

  //use the array to store all files which are in the directory with list files
  displayDirectoryContent(directory)

  //iterate trough files and set them as subItems to the RootItem "C:"
  def displayDirectoryContent(dir: File): Unit = {
    try{
    val files: Array[File] = dir.listFiles()
    for(content <- files){
      if(content.isFile && !content.isHidden){
        val item = new TreeItem[String](content.getAbsolutePath,new ImageView(pictureFile))
        rootItem.getChildren.add(item)
      }
      else if(content.isDirectory && !content.isHidden){
        val item2 = new TreeItem[String](content.getAbsolutePath,new ImageView(pictureFolder))
        rootItem.getChildren.add(item2)
        displayDirectoryContent(content)

      }
    }

  }catch {
      case e: IOException => e.printStackTrace()
      case n: NullPointerException => n.printStackTrace()
    }

  }



  }



}

