package fhj.swengb.project.remoty

import java.io.IOException
import java.nio.file.{FileAlreadyExistsException, Files, Paths, Path}
import java.util.function.Predicate
import javafx.beans.property.{SimpleObjectProperty, ObjectProperty, StringProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.ObservableList
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control._
import javafx.stage.Stage

/**
  * Created by chris on 22.01.2016.
  */

/**
  * Class PathTreeCell which is implementing a ContextMenu to make actions with it.
  * You have to set this up on the TreeView with the .setCellValueFactory()
  */
object PathTreeCell{
  def apply(owner:Stage):PathTreeCell = PathTreeCell(owner)
}

class PathTreeCell(owner: Stage) extends TreeCell[PathItem] {

  private var textField: TextField = _
  private var editingPath: Path = _
  private var messageProp: StringProperty = _
  private var dirMenu: ContextMenu = _
  private var fileMenu: ContextMenu = _


  def PathTreeCell(owner: Stage, messageProp: StringProperty): Unit = {


    val expandMenu: MenuItem = new MenuItem("Expand")
    expandMenu.setOnAction(new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        getTreeItem.setExpanded(true)
      }
    })

    val expandAllMenu: MenuItem = new MenuItem("Expand All")
    expandAllMenu.setOnAction(new EventHandler[ActionEvent]() {

      override def handle(e: ActionEvent) {
        expandTreeItem(getTreeItem)
      }
    })



  /**
    *
    * @param item the active TreeItem
    */
  def expandTreeItem(item: TreeItem[PathItem]): Unit = item match {
    case leaf if item.isLeaf =>
    case noLeaf if !item.isLeaf => {
      item.setExpanded(true)
      val children: List[TreeItem[PathItem]] = TreeViewUtil.mkList(item.getChildren)

      children.filter(child => !child.isLeaf).foreach(child => expandTreeItem(child))

    }

  }


  val addMenu: MenuItem = new MenuItem("Add Directory")
  addMenu.setOnAction(new EventHandler[ActionEvent] {
    override def handle(event: ActionEvent): Unit = {
      val newDir: Path = createNewDirectory()
      if(newDir != null) {
        val addItem = PathTreeItem.createNode(new PathItem(newDir))
        getTreeItem.getChildren.add(addItem)
      }
    }
  })


  def createNewDirectory(): Path = {
    var newDir: Path = null
    while(true) {
      val path: Path = getTreeItem.getValue.getPath
      newDir = Paths.get(path.toAbsolutePath.toString, "New Directory " + String.valueOf(getItem))//.getCountNewDir))
      try {
        Files.createDirectory(newDir)
      }
      catch {
        case a:FileAlreadyExistsException => println("File already exists!") //maybe change the println with message pop up etc..
        case b:IOException => cancelEdit(); messageProp.setValue(s"Creating directory(${newDir.getFileName}) failed")
      }
    }
     newDir
  }


  val deleteMenu: MenuItem = new MenuItem("Delete")
  deleteMenu.setOnAction(new EventHandler[ActionEvent] {
    override def handle(event: ActionEvent): Unit = {
      val prop: ObjectProperty[TreeItem[PathItem]] = new SimpleObjectProperty[TreeItem[PathItem]]()

      new DeleteDialog(owner, getTreeItem, prop) // <-- doesn't work with owner
      prop.addListener(new ChangeListener[TreeItem[PathItem]] {
        override def changed(observable: ObservableValue[_ <: TreeItem[PathItem]], oldItem: TreeItem[PathItem], newItem: TreeItem[PathItem]): Unit = {
          try{
            Files.walkFileTree(newItem.getValue.getPath, new VisitorForDelete())

            if(getTreeItem.getParent == null){}
            else getTreeItem.getParent.getChildren.remove(newItem)
          }
          catch{
            case e:IOException => println("Delete failed!")
          }
        }
      })

      dirMenu.getItems.addAll(deleteMenu, addMenu, expandMenu, expandAllMenu )
      fileMenu.getItems.addAll(deleteMenu)



      /**
        * TO BE CONTINUED....
        */
    }
  })
  }

}
