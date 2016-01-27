package fhj.swengb.project.remoty


import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.{TreeView, TreeCell}
import javafx.util.Callback
import scala.collection.JavaConversions
import JavaConversions._
import java.nio.file._
import javafx.collections.ObservableList


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
    * Helper method to convert an ObservableList into a normal List
    * @param input
    * @tparam T
    * @return
    */
  def mkList[T](input:ObservableList[T]) :List[T] = input.toList


  /**
    * We need to save the path we want to display (e.g. "C:\") in a List or even better in an ObservableList
    * ObservableLists are capable of listening to changes in the List (for example changing, deleting, etc..)
    */


  def createObservableList[T](iterable: DirectoryStream[T]): ObservableList[T] = {
    //val arrayList = new java.util.ArrayList[T]
    //arrayList.addTll(iterable)
    FXCollections.observableList(new java.util.ArrayList[T](iterable.toList))
  }


  }


