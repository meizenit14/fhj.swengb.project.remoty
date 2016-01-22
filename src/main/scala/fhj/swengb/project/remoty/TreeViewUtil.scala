package fhj.swengb.project.remoty

import fhj.swengb.project.remoty.DeleteDialog
import fhj.swengb.project.remoty.RemotyApp
import javafx.collections.{FXCollections, ObservableList}
import javafx.event.{EventHandler, ActionEvent}
import javafx.scene.control.{TextField, TreeView, TreeCell}
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.util.Callback
import scala.collection.JavaConversions
import JavaConversions._
import java.io.IOException;
import java.nio.file._
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.{ChangeListener, ObservableValue}
;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
  * Created by Amar on 15.01.2016.
  */
object TreeViewUtil {

  /**
    * Cell represents a "row" of TreeView for example and also behaviour and the visual look.
    * Then we have CellFactories. We call them if we need to render new Cells for new files in our Filebrowser trough a Callback.
    * For example: We have 10 million items, but only render the first 1 Mio. because the system will be stressed too much.
    * Then we decide to browse other directories and therefore we need to render them again and this happens trough the
    * Callback which is called and renders new Cells for the new Items.
    */

  //The cell factory mechanism is used for generating TreeCell instances to represent a single TreeItem in the TreeView.
  // Using cell factories is particularly helpful when your application operates with an excessive amount of data that is changed dynamically or added on demand.

  def cellFactoryCaller[T](func: TreeView[T] => TreeCell[T]): Callback[TreeView[T], TreeCell[T]] = {
    //making a Callback which is needed for making new CellFactories which makes new Cells.
    new Callback[TreeView[T], TreeCell[T]] {
      def call(convert: TreeView[T]): TreeCell[T] = func(convert)
    }
  }


  /**
    * Helper method for the PathTreeItem in order to convert the DirectoryStream to a List
    *
    * @param input
    * @tparam T
    * @return
    */
  def stringer[T](input: DirectoryStream[T]): List[T] = input.toList


  /**
    * We need to save the path we want to display (e.g. "C:\") in a List or even better in an ObservableList
    * ObservableLists are capable of listening to changes in the List (for example changing, deleting, etc..)
    */


  def createObservableList[T](iterable: DirectoryStream[T]): ObservableList[T] = {
    //val arrayList = new java.util.ArrayList[T]
    //arrayList.addTll(iterable)
    FXCollections.observableList(new java.util.ArrayList[T](iterable.toList))
  }


  /**
    * Now we can also use a ChangeListener for every ObservableValue in our ObservableList to track changes
    * An ObservableValue wraps values and fires the changes to a ChangeListener.
    * The ChangeListener interface receives at the end all the changes of a TreeItem, TreeView and can be used with an "addChangeListener" onto the Treeview
    */


  //coming soon...


  /**
    * Class PathTreeCell which is implementing a ContextMenu to make actions with it.
    * You have to set this up on the TreeView with the .setCellValueFactory()
    */


  class PathTreeCell extends TreeCell[PathItem] {

    private var textField: TextField = _
    private var editingPath: Path = _
    private var messageProp: StringProperty = _
    private var dirMenu: ContextMenu = _
    private var fileMenu: ContextMenu = _


    def PathTreeCell(owner: Stage, messageProp: StringProperty): Unit = {
      this.messageProp = messageProp

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

      }

    def expandTreeItem(item: TreeItem[PathItem]): Unit = item match {
      case leaf if item.isLeaf =>
      case noLeaf if !item.isLeaf => {
        item.setExpanded(true)
        val children: ObservableList[TreeItem[PathItem]] = item.getChildren
        children.toList.filter(child => !child.isLeaf).foreach(child => expandTreeItem(child))
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
        case FileAlreadyExistsException => println("File already exists!") //maybe change the println with message pop up etc..
        case IOException => cancelEdit(); messageProp.setValue(s"Creating directory(${newDir.getFileName}) failed")
      }
      }
      return newDir
      }


      val deleteMenu: MenuItem = new MenuItem("Delete")
      deleteMenu.setOnAction(new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          val prop: ObjectProperty[TreeItem[PathItem]] = new SimpleObjectProperty[TreeItem[PathItem]]()
          new DeleteDialog(owner,getTreeItem,prop) // <-- doesn't work with owner
          prop.addListener(new ChangeListener[TreeItem[PathItem]] {
            override def changed(observable: ObservableValue[_ <: TreeItem[PathItem]], oldValue: TreeItem[PathItem], newValue: TreeItem[PathItem]): Unit = ???
          })


        }
      })

    }


  }


