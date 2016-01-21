package fhj.swengb.project.remoty


import java.io.{IOException, File}
import java.net.URL
import java.util.ResourceBundle
import javafx.application.Application
import javafx.collections.{FXCollections, ObservableList}
import javafx.event.EventHandler
import javafx.fxml.{FXML, Initializable, FXMLLoader}
import javafx.scene.control._
import javafx.scene.image.{ImageView, Image}
import javafx.scene.input.{MouseButton, ContextMenuEvent, MouseEvent}
import javafx.scene.layout.{Pane, StackPane, BorderPane}
import javafx.scene.{Scene, Parent}
import javafx.stage.Stage
import javafx.util.Callback

import fhj.swengb.project.remoty.TreeViewUtil.TextFieldTreeCellImpl

//is important import in order to check if it isDirectory...wtf
import scala.collection.JavaConversions._

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
    testinit()
  }


  /**
    * set a value for the picture of an folder Icon or file icon and use it for TreeItems
    */

  val pictureFolder: Image = new Image("/fhj/swengb/project/remoty/folder.png")
  val pictureFile: Image = new Image("/fhj/swengb/project/remoty/file.png")


  /**
    * Set the path you want to display in the TreeView here!
    */
  val path: String = "C:/Users/Amar".replace("/","\\").trim


  /**
    * Calls the method from the TreeViewUtil file and makes an ObservableList which contains all Files of the given Path.
    *
    * @param pathAsString
    * @return ObservableList[File]
    */
  def pathFiles(pathAsString: String): ObservableList[File] = TreeViewUtil.createObservableList(new File(pathAsString).listFiles())





  /**
    * Makes a rootItem
    * second argument in TreeItem is a new ImageView with the "picture" value in it and then it will show an folder icon in the TreeView
    * with "System.getenv("SystemDrive") you can get the letter of the system drive...
    */

  var rootItem: TreeItem[String] = new TreeItem[String] //new ImageView(pictureFolder))
  //the rootItem is expanded in default case
  rootItem.setExpanded(true)


  /**
    * Initializes all necessary things for the initialize override method at the beginning
    */

  def initializeALl(): Unit = {
    //set the rootItem to the tree_view
    tree_view.setRoot(rootItem)

    //set the cellValueFactories
    // tree_view.setCellFactory(TreeViewUtil.cellFactoryCaller(TreeViewUtil.toString))

    //initialize the mouseEventHandler on the TreeView
    //tree_view.setOnMouseClicked(mouseEvent)

  }



  //####################TRY OUT STUFF###########################

/*
  //use the array to store all files which are in the directory with list files
  displayDirectoryContent(directoryPath,parent = rootItem)

  //iterate trough files and set them as subItems to the RootItem "C:"
  def displayDirectoryContent(dir: File,parent: TreeItem[File] = rootItem): Unit = {
    try {
      val files: Array[File] = dir.listFiles
      for (content <- files) {
        if (content.isFile && !content.isHidden) {
          parent.getChildren.add(new TreeItem[File](content, new ImageView(pictureFile)))
        }
        else if (content.isDirectory && !content.isHidden) {
          val subdir = new TreeItem[File](content, new ImageView(pictureFolder))
          parent.getChildren.add(subdir)
          displayDirectoryContent(content, subdir)
        }
      }
    }catch{
      case e: IOException => e.printStackTrace()
      case n: NullPointerException => n.printStackTrace()
    }
  }
*/




  def testTreeItems(): Unit = {
    val array: Array[File] = new File("C:\\Users\\Amar").listFiles()
    for (i <- array) {
      if (i.isDirectory) {
        val item = new TreeItem[String](i.getName, new ImageView(pictureFolder))
        rootItem.getChildren.add(item)
      }
      else rootItem.getChildren.add(new TreeItem[String](i.getName,new ImageView(pictureFile)))
    }
  }


  def testinit(): Unit = {

    tree_view.setEditable(true)
    tree_view.setCellFactory(TreeViewUtil.cellFactoryCaller {
      return new TextFieldTreeCellImpl
    })

  }

testTreeItems()





}





