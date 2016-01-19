package fhj.swengb.project.remoty

import javafx.collections.{FXCollections, ObservableList}
import javafx.event.EventHandler
import javafx.scene.control.{TextField, TreeView, TreeCell}
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.util.Callback
import scala.collection.JavaConversions
import JavaConversions._

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
    * We need to save the path we want to display (e.g. "C:\") in a List or even better in an ObservableList
    * ObservableLists are capable of listening to changes in the List (for example changing, deleting, etc..)
    */


  def createObservableList[T](iterable: Iterable[T] = List()): ObservableList[T] = {
    //val arrayList = new java.util.ArrayList[T]
    //arrayList.addTll(iterable)
    FXCollections.observableList(new java.util.ArrayList[T](iterable))
  }


  /**
    * Now we can also use a ChangeListener for every ObservableValue in our ObservableList to track changes
    * An ObservableValue wraps values and fires the changes to a ChangeListener.
    * The ChangeListener interface receives at the end all the changes of a TreeItem, TreeView and can be used with an "addChangeListener" onto the Treeview
    */


  //coming soon...


  /**
    *
    * With this method we are updating after the Callback our TreeCell so we are calling Treecells
    *
    * Source: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Cell.html
    */

  def toString[T](con: T => String)(f: TreeView[T]): TreeCell[T] = {
    class Cell extends TreeCell[T] {
      override protected def updateItem(t: T, empty: Boolean): Unit = {
        super.updateItem(t, empty)
        if (t != null) {
          setText(con(t))
        }
        else setText(null)
      }
    }
    new Cell
  }


  /**
    * tried to make a TreeCellImplementation to rename TreeItems
    */

  class TextFieldTreeCellImpl extends TreeCell[String] {

      var textfield: TextField = _

      override def startEdit(): Unit = {
        super.startEdit()
        if (textfield == null) {
          createTextField()
        }
        setText(null)
        setGraphic(textfield)
        textfield.selectAll()
      }


      override def cancelEdit() {
        super.cancelEdit()
        setText(getItem)
        setGraphic(getTreeItem.getGraphic)
      }

      override def updateItem(item: String, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (empty) {
          setText(null)
          setGraphic(null)
        }
        else {
          if (isEditing) {
            if (textfield != null) {
              textfield.setText(getString)
            }
            setText(null)
            setGraphic(textfield)
          } else {
            setText(getString)
            setGraphic(getTreeItem.getGraphic)
          }
        }
      }

      private def createTextField() {
        textfield = new TextField(getString)
        textfield.setOnKeyReleased(new EventHandler[KeyEvent]() {

          override def handle(t: KeyEvent) {
            if (t.getCode == KeyCode.ENTER) {
              commitEdit(textfield.getText)
            } else if (t.getCode == KeyCode.ESCAPE) {
              cancelEdit()
            }
          }
        })
      }

      private def getString(): String = {
        if (getItem == null) "" else getItem.toString
      }


    }
  }

