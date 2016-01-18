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
    * SET THE PATH which you want to be shown in the TreeView
    * path -> is the path chosen from your own system. HERE YOU HAVE TO SET ANY PATH YOU WANT!
    * directoryPath -> makes a new File out of the given path and gives it to the recursive function
    */





  /*/**
    * Makes a rootItem
    * second argument in TreeItem is a new ImageView with the "picture" value in it and then it will show an folder icon in the treeview
    * with "System.getenv("SystemDrive") you can get the letter of the system drive...
    */

  val rootItem: TreeItem[String] = new TreeItem[String](path, new ImageView(pictureFolder))
  //the rootItem is expanded in default case
  rootItem.setExpanded(true)



  /**
    *
    * Iterates over the files and directories of the given directory and enters it in the treeview
    * EDIT: IT WORKS LIKE A CHARM except a lot of NullPointerExceptions!!
    *
    **/

  //use the array to store all files which are in the directory with list files
  displayDirectoryContent(directoryPath,parent = rootItem)

  //iterate trough files and set them as subItems to the RootItem "C:"
  def displayDirectoryContent(dir: File,parent: TreeItem[String] = rootItem): Unit = {
    try {
      val files: Array[File] = dir.listFiles()
      for (content <- files) {
        if (content.isFile && !content.isHidden) {
          val file = new TreeItem[String](content.getName, new ImageView(pictureFile))
          parent.getChildren.add(file)
        }
        else if (content.isDirectory && !content.isHidden) {
          val subdir = new TreeItem[String](content.getName, new ImageView(pictureFolder))
          parent.getChildren.add(subdir)
          displayDirectoryContent(content,subdir)
        }
      }
    } catch {
      case e: IOException => e.printStackTrace()
      case n: NullPointerException => n.printStackTrace()
    }

  }*/


  /**
    * A mouseEventHandler for the TreeView which
    * @return the path of the TreeItem clicked on
    */

      val mouseEvent: EventHandler[_ >: MouseEvent] = new EventHandler[MouseEvent] {
        override def handle(event: MouseEvent): Unit = {
          event.getSource match {
            case clicked: TreeView[String] => treeRecursive(Paths.get(clicked.getSelectionModel.getSelectedItem.getValue))
            //case clicked: TreeView[String] => println((clicked.getSelectionModel.getSelectedItem.getValue))
          }
        }
      }


  /**
    * Makes a rootItem
    * second argument in TreeItem is a new ImageView with the "picture" value in it and then it will show an folder icon in the treeview
    * with "System.getenv("SystemDrive") you can get the letter of the system drive...
    */


  //val rootPath: String = "C:/"
  //first set the directory as string
  //val directoryPath: File = new File(path)

  val rootPath: String = "C:/Test/"
  val path1 = Paths.get(rootPath)
  val root: TreeItem[String] = new TreeItem[String](rootPath, new ImageView(pictureFolder))
  //the rootItem is expanded in default case
  root.setExpanded(true)


 /* val nodes: scala.collection.mutable.Map[String, TreeItem] = scala.collection.mutable.Map[String, TreeItem]()
  val children: scala.collection.mutable.Map[TreeItem, util.List[String]] = scala.collection.mutable.Map[TreeItem, util.List[String]]()

  def treeRecursive(path: Path): Unit = {
    Files.walkFileTree(path, new SimpleFileVisitor[Path]() {

      override def preVisitDirectory(path: Path, attrs: BasicFileAttributes): FileVisitResult = {

        val parent: Option[TreeItem] = nodes.get(path.getParent().toString())
        var item:TreeItem[String] = null

        if (parent.get == null) {
           item = root
        }
        else {
           item = new TreeItem[String](parent.get.toString(), new ImageView(pictureFolder))
        }

        item.setValue(path.getFileName().toString())

        return  FileVisitResult.CONTINUE
      }

      override def visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult = {

        val parent = nodes.get(path.getParent().toString())

        if(children.get(parent.get) == null){
          children.put(parent.get, new util.List[String])
        }

        children.get(parent.get).addString(new StringBuilder(path.getFileName().toString()))


        return  FileVisitResult.CONTINUE
      }



    })*/


      val opts = util.EnumSet.of(FileVisitOption.FOLLOW_LINKS)

      def treeRecursive(path: Path): Unit = {

          Files.walkFileTree(path,opts, 1, new SimpleFileVisitor[Path]() {

            override def visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult = {


              if (attrs.isDirectory()){
                val subdir = new TreeItem[String](path.toString, new ImageView(pictureFolder))
                root.getChildren.add(subdir)
              }
              else{
                val file = new TreeItem[String](path.toString, new ImageView(pictureFile))
                root.getChildren.add(file)
              }

              FileVisitResult.CONTINUE
            }

            override def visitFileFailed(path: Path, exc: IOException): FileVisitResult = {
              println(path + " failed")
              FileVisitResult.CONTINUE;
            }

          } )
        }




  def initializeALl(): Unit = {

   /* treeRecursive(path1)

    for((k,v)<- children){

      for(child <- children.get(k)){

        var item = new TreeItem[String](k.toString(), new ImageView(pictureFile))
      }
    }*/

  //set the rootItem to the tree_view
  tree_view.setRoot(root)
  //printRecursive(path1)
  //initialize the mouseEventHandler on the TreeView
  tree_view.setOnMouseClicked(mouseEvent)
  }



}





