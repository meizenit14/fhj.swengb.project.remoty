package fhj.swengb.project.remoty

import java.io.{File, IOException}
import java.net.URL
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import java.util
import java.util.ResourceBundle
import javafx.application.Application
import javafx.event.{EventType, Event, EventHandler}
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.control.TreeItem.TreeModificationEvent
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.{Parent, Scene}
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


  import FilePathTreeItem._
  //remove if not needed
  import scala.collection.JavaConversions._

  object FilePathTreeItem {

    val pictureFolder: Image = new Image("/fhj/swengb/project/remoty/folder.png")
    val pictureFolderOpen: Image = new Image("/fhj/swengb/project/remoty/folder.png")
    val pictureFile: Image = new Image("/fhj/swengb/project/remoty/file.png")
  }

  case class FilePathTreeItem(file: Path) extends TreeItem[String](file.toString) {

    private var fullPath: String = file.toString

    def getFullPath(): String = (this.fullPath)

     var isFolder: Boolean = _

    //def isFolder(): Boolean = (this.isFolder())

    if (Files.isDirectory(file)) {
      this.isFolder = true
      this.setGraphic(new ImageView(pictureFolder))
    } else {
      this.isFolder = false
      this.setGraphic(new ImageView(pictureFile))
    }

    if (!fullPath.endsWith(File.separator)) {
      val value = file.toString
      val indexOf = value.lastIndexOf(File.separator)
      if (indexOf > 0) {
        this.setValue(value.substring(indexOf + 1))
      } else {
        this.setValue(value)
      }
    }

    this.addEventHandler(TreeItem.branchExpandedEvent[String], new EventHandler[TreeModificationEvent[String]] {

      override def handle(e: TreeModificationEvent[String]) {
        val source = e.getSource.asInstanceOf[FilePathTreeItem]
        if (source.isFolder && source.isExpanded) {
          val iv = source.getGraphic.asInstanceOf[ImageView]
          iv.setImage(pictureFolder)
        }
        try {
          if (source.getChildren.isEmpty) {
            val path = Paths.get(source.getFullPath)
            val attribs = Files.readAttributes(path, classOf[BasicFileAttributes])
            if (attribs.isDirectory) {
              val dir = Files.newDirectoryStream(path)
              for (file <- dir) {
                val treeNode = new FilePathTreeItem(file)
                source.getChildren.add(treeNode)
              }
            }
          } else {
          }
        } catch {
          case x: IOException => x.printStackTrace()
        }
      }
    })

    this.addEventHandler(TreeItem.branchCollapsedEvent[String], new EventHandler[TreeModificationEvent[String]] {
      override def handle(e: TreeModificationEvent[String]) {
        val source = e.getSource.asInstanceOf[FilePathTreeItem]
        if (source.isFolder && !source.isExpanded) {
          val iv = source.getGraphic.asInstanceOf[ImageView]
          iv.setImage(pictureFolderOpen)
        }
      }
    })
  }





  def initializeALl(): Unit = {

    val rootPath: String = "C:/Test/"
    val path1 = Paths.get(rootPath)
    val root: TreeItem[String] = new TreeItem[String](rootPath, new ImageView(new Image ("/fhj/swengb/project/remoty/folder.png")))
    //the rootItem is expanded in default case
    root.setExpanded(true)

    for(path <- path1) {
      val treeNode: FilePathTreeItem = new FilePathTreeItem(path)
      root.getChildren.add(treeNode)
    }
  tree_view.setRoot(root)
  //printRecursive(path1)
  //initialize the mouseEventHandler on the TreeView
  //tree_view.setOnMouseClicked(mouseEvent)
  }



}





