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
   initializeALl()
  }


  /**
    * set a value for the picture of an folder Icon or file icon and use it for TreeItems
    */

  val pictureFolder: Image = new Image("/fhj/swengb/project/remoty/folder.png")
  val pictureFile: Image = new Image("/fhj/swengb/project/remoty/file.png")


  /**
    * Makes a rootItem
    * second argument in TreeItem is a new ImageView with the "picture" value in it and then it will show an folder icon in the treeview
    * with "System.getenv("SystemDrive") you can get the letter of the system drive...
    */

  val rootItem: TreeItem[String] = new TreeItem(System.getenv("SystemDrive"), new ImageView(pictureFolder))
  //the rootItem is expanded in default case
  rootItem.setExpanded(true)



  /**
    *
    * Iterates over the files and directories of the given directory and enters it in the treeview
    * EDIT: IT WORKS LIKE A CHARM except a lot of NullPointerExceptions!!
    *
    **/

val path: String = """C:\"""
  //first set the directory as string
  val directoryPath: File = new File(path)

  //use the array to store all files which are in the directory with list files
  displayDirectoryContent(directoryPath)

  //iterate trough files and set them as subItems to the RootItem "C:"
  def displayDirectoryContent(dir: File,parent: TreeItem[String] = rootItem): Unit = {
    try {
      val files: Array[File] = dir.listFiles()
      for (content <- files) {
        if (content.isFile && !content.isHidden) {
          val file = new TreeItem[String](content.getAbsolutePath, new ImageView(pictureFile))
          parent.getChildren.add(file)
        }
        else if (content.isDirectory && !content.isHidden) {
          val subdir = new TreeItem[String](content.getAbsolutePath, new ImageView(pictureFolder))
          parent.getChildren.add(subdir)
          displayDirectoryContent(content,subdir)
        }
      }
    } catch {
      case e: IOException => e.printStackTrace()
      case n: NullPointerException => n.printStackTrace()
    }

  }


  //a mouseEventHandler which is for the TreeView
  val mouseEvent: EventHandler[_ >: MouseEvent] = new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = {
      event.getSource match {
        case clicked: TreeItem[String] => msg_out.setText("You clicked on:  " + clicked.getValue)
        case clicked2: TreeView[String] => msg_out.setText("You clicked on:  " + clicked2.getSelectionModel.getSelectedItem.getValue)
      }
    }
  }





def initializeALl(): Unit = {
  //set the rootItem to the tree_view
  tree_view.setRoot(rootItem)

  //initialize the mouseEventHandler on the TreeView
  tree_view.setOnMouseClicked(mouseEvent)
}





}




