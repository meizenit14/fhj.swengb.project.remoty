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


