package fhj.swengb.project.remoty

import java.io.File
import java.nio.file._
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.TreeItem

/**
  * Created by chris on 19.01.2016.
  */
class PathTreeItem (pathItem:PathItem) extends TreeItem[PathItem] {


 var isLeaf1:Boolean = false
  var isFirstTimeChildren:Boolean = true
  var isFirstTimeLeft:Boolean = true

  /*def PathTreeItem(pathItem:PathItem): Unit ={
    super.pathItem
  }*/

  def createNode(pathItem:PathItem):TreeItem[PathItem] = {
    new PathTreeItem(pathItem)
  }

  override def getChildren:ObservableList[TreeItem[PathItem]] = {
    if (isFirstTimeChildren) {
      isFirstTimeChildren = false
      super.getChildren.setAll(buildChildren(this))
    }
    super.getChildren
  }

  override def isLeaf:Boolean = {
    if(isFirstTimeLeft){
      isFirstTimeLeft = false
      try {
        val path: Path = getValue.getPath
        isLeaf1 = !Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)
      }
      catch {
        case io:NullPointerException => io.printStackTrace()
      }
    }
    isLeaf1
  }

  def buildChildren(treeItem:TreeItem[PathItem]):ObservableList[TreeItem[PathItem]] = {
    val path:Path = treeItem.getValue.getPath


    if(path != null && Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)){
      val children:ObservableList[TreeItem[PathItem]] = FXCollections.observableArrayList()


      val file = treeItem.getValue.asInstanceOf[File]
      val files = file.listFiles()

      for(child <- files){
        children.add(createNode(child.asInstanceOf[PathItem]))
      }




      return children
    }
    FXCollections.emptyObservableList()
  }



}
