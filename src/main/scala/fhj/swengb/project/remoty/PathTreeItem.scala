package fhj.swengb.project.remoty

import java.io.BufferedReader
import java.nio.file.{DirectoryStream, Path, LinkOption, Files}
import java.util
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.TreeItem
import javafx.scene.image.{Image, ImageView}

/**
  * Created by Amar on 22.01.2016.
  */
object PathTreeItem {


  /**
    * Creating a new TreeItem and checking if it is a leaf or has got children (=no Leaf)
    *
    * @param pi
    * @return
    */
  def createNode(pi: PathItem): TreeItem[PathItem] = {
    new TreeItem[PathItem](pi){
      var isLeaf1: Boolean = _
      var isFirstTimeChildren: Boolean = true
      var isFirstTimeLeaf = true


      override def getChildren: ObservableList[TreeItem[PathItem]] = {
        if(isFirstTimeChildren){
          isFirstTimeChildren = false
          super.getChildren.setAll(buildChildren(this))
        }
        super.getChildren
      }


      override def isLeaf: Boolean = {
        if(isFirstTimeLeaf){
          isFirstTimeLeaf = false
          isLeaf1 = Files.isRegularFile(this.getValue.getPath, LinkOption.NOFOLLOW_LINKS)
        }
        isLeaf1
      }

      /**
        * creates new TreeItems and its childs if it is not a Leaf
        * @param treeItem
        * @return
        */
      def buildChildren(treeItem: TreeItem[PathItem]): ObservableList[TreeItem[PathItem]] = {
        //creating root TreeItem
        val path:Path = treeItem.getValue.getPath

        //set the "folder-open" picture
        treeItem.setGraphic(new ImageView(pictureFolderOpen))

        if(path != null && Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)){
          val directories:ObservableList[TreeItem[PathItem]] = FXCollections.observableArrayList()
          val files:ObservableList[TreeItem[PathItem]] = FXCollections.observableArrayList()
          val result:ObservableList[TreeItem[PathItem]] = FXCollections.observableArrayList()

          //ruft die Funktion vom File "TreeViewUtil" auf und wandelt einen DirectoryStream in eine Liste um
          val dirs = TreeViewUtil.stringer(Files.newDirectoryStream(path))


          for(dir: Path <- dirs){
            if(!Files.isHidden(dir) && Files.isReadable(dir)) {
              val child = createNode(new PathItem(dir))
              if (Files.isRegularFile(dir, LinkOption.NOFOLLOW_LINKS)) {
                dir.toString match {
                  case pdf if pdf.endsWith(".pdf") => child.setGraphic(new ImageView(picturePDFFile))
                  case music if music.endsWith(".mp3") || music.endsWith(".aac") => child.setGraphic(new ImageView(pictureMP3File))
                  case video if video.endsWith(".mp4") || video.endsWith(".avi") || video.endsWith(".mkv") || video.endsWith(".flv") => child.setGraphic(new ImageView(pictureVideoFile))
                  case picture if picture.endsWith(".jpg") || picture.endsWith(".JPG") || picture.endsWith(".jpeg") || picture.endsWith(".png") || picture.endsWith(".PNG") || picture.endsWith(".gif") || picture.endsWith(".ico") ||  picture.endsWith(".bmp") => child.setGraphic(new ImageView(picturePictureFile))
                  case word if word.endsWith(".doc") || word.endsWith(".docx") || word.endsWith(".odt") || word.endsWith(".pages") => child.setGraphic(new ImageView(pictureWordFile))
                  case excel if excel.endsWith(".xls") || excel.endsWith(".xlsx") || excel.endsWith(".numbers") => child.setGraphic(new ImageView(pictureExcelFile))
                  case powerpoint if powerpoint.endsWith(".ppt") || powerpoint.endsWith(".pptx") || powerpoint.endsWith(".key") => child.setGraphic(new ImageView(picturePowerpointFile))
                  case exe if exe.endsWith(".exe") || exe.endsWith(".EXE") || exe.endsWith(".msi") || exe.endsWith(".pkg") => child.setGraphic(new ImageView(pictureExeFile))
                  case zip if zip.endsWith(".zip") || zip.endsWith(".7z") || zip.endsWith(".rar") || zip.endsWith(".tar") || zip.endsWith(".gz") => child.setGraphic(new ImageView(pictureZIPFile))
                  case _ => child.setGraphic(new ImageView(pictureFile))
                }

                files.add(child)

              }

              else {
                child.setGraphic(new ImageView(pictureFolder))
                directories.add(child)
              }

            }
          }
          result.addAll(directories)
          result.addAll(files)
          return result
        }
        FXCollections.emptyObservableList()
      }
    }
  }



  /**
    * Setting the pictures for the TreeItems...
    */
  lazy val pictureFolderOpen: Image = new Image("/fhj/swengb/project/remoty/folder-open.png")
  lazy val pictureFolder: Image = new Image("/fhj/swengb/project/remoty/folder.png")
  lazy val pictureFile: Image = new Image("/fhj/swengb/project/remoty/file.png")
  lazy val picturePDFFile: Image = new Image("/fhj/swengb/project/remoty/pdf-file.png")
  lazy val pictureMP3File: Image = new Image("/fhj/swengb/project/remoty/mp3-file.png")
  lazy val pictureVideoFile: Image = new Image("/fhj/swengb/project/remoty/video-file.png")
  lazy val picturePictureFile: Image = new Image("/fhj/swengb/project/remoty/picture-file.png")
  lazy val pictureWordFile: Image = new Image("/fhj/swengb/project/remoty/word-file.png")
  lazy val pictureExcelFile: Image = new Image("/fhj/swengb/project/remoty/excel-file.png")
  lazy val picturePowerpointFile: Image = new Image("/fhj/swengb/project/remoty/powerpoint-file.png")
  lazy val pictureExeFile: Image = new Image("/fhj/swengb/project/remoty/exe-file.png")
  lazy val pictureZIPFile: Image = new Image("/fhj/swengb/project/remoty/zip-file.png")



}
