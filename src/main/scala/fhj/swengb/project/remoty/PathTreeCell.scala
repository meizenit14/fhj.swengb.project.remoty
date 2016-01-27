package fhj.swengb.project.remoty

import java.io.{File, IOException}
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.Predicate
import javafx.beans.property.{SimpleObjectProperty, ObjectProperty, StringProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.ObservableList
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.{MouseButton, MouseEvent, KeyCode, KeyEvent}
import javafx.stage.Stage

/**
  * Created by chris on 22.01.2016.
  */


  /**
    * Class PathTreeCell which extends a TreeCell.
    * It is necessary in order to be able to rename each TreeItem which is a TreeCell at the end
    */
  class PathTreeCell(owner: Stage, messageProp: StringProperty) extends TreeCell[PathItem] {

    var textField: TextField = _
    var editingPath: Path = _
    var dirMenu: ContextMenu = new ContextMenu()
    var fileMenu: ContextMenu =  new ContextMenu()


    /**
      * This function provides additional funtionality and implements a ContextMenu with
      * different MenuItems which will be called at updateItem for every TreeCell
      */

      // expand one level of the item
      val expandMenu: MenuItem = new MenuItem("Expand")
      expandMenu.setOnAction(new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          getTreeItem.setExpanded(true)
        }
      })

      // expand all levels
      val expandAllMenu: MenuItem = new MenuItem("Expand All")
      expandAllMenu.setOnAction(new EventHandler[ActionEvent]() {

        override def handle(e: ActionEvent) {
          expandTreeItem(getTreeItem)
        }
      })


      // recursive function to expand all levels of a treeitem
      def expandTreeItem(item: TreeItem[PathItem]): Unit = item match {
        case leaf if item.isLeaf =>
        case noLeaf if !item.isLeaf => {
          item.setExpanded(true)
          val children: List[TreeItem[PathItem]] = TreeViewUtil.mkList(item.getChildren)

          children.filter(child => !child.isLeaf).foreach(child => expandTreeItem(child))

        }

      }

      // add a directory
      val addMenu: MenuItem = new MenuItem("Add Directory")
      addMenu.setOnAction(new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          val newDir: Path = createNewDirectory()
          if (newDir != null) {
            val addItem = PathTreeItem.createNode(new PathItem(newDir))
            getTreeItem.getChildren.add(addItem)
          }
        }
      })


      def createNewDirectory(): Path = {

          val path: Path = getTreeItem.getValue.getPath
          val newDir = Paths.get(path.toAbsolutePath.toString, "New Directory " + String.valueOf(getItem))
          try {
            Files.createDirectory(newDir)
          }
          catch {
            case a: FileAlreadyExistsException => println("File already exists!") //maybe change the println with message pop up etc..
            case b: IOException => cancelEdit(); messageProp.setValue(s"Creating directory(${newDir.getFileName}) failed")
          }
          newDir
        }




      val deleteMenuDir: MenuItem = new MenuItem("Delete")
      deleteMenuDir.setOnAction(new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          val prop: ObjectProperty[TreeItem[PathItem]] = new SimpleObjectProperty[TreeItem[PathItem]]()

          new DeleteDialog(owner, getTreeItem, prop)
          prop.addListener(new ChangeListener[TreeItem[PathItem]] {
            override def changed(observable: ObservableValue[_ <: TreeItem[PathItem]], oldItem: TreeItem[PathItem], newItem: TreeItem[PathItem]): Unit = {
              try {
                Files.walkFileTree(newItem.getValue.getPath, new SimpleFileVisitor[Path](){
                  override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
                    Files.deleteIfExists(file)
                    FileVisitResult.CONTINUE
                  }
                  override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
                    Files.deleteIfExists(dir)
                    FileVisitResult.CONTINUE
                  }
                })

                if (getTreeItem.getParent == null) {}
                else getTreeItem.getParent.getChildren.remove(newItem)
              }
              catch {
                case e: IOException => println("Delete failed!")
              }
            }
          })

        }
      })

    val deleteMenuFile: MenuItem = new MenuItem("Delete")
    deleteMenuFile.setOnAction(new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        val prop: ObjectProperty[TreeItem[PathItem]] = new SimpleObjectProperty[TreeItem[PathItem]]()

        new DeleteDialog(owner, getTreeItem, prop)
        prop.addListener(new ChangeListener[TreeItem[PathItem]] {
          override def changed(observable: ObservableValue[_ <: TreeItem[PathItem]], oldItem: TreeItem[PathItem], newItem: TreeItem[PathItem]): Unit = {
            try {
              Files.delete(newItem.getValue.getPath)


              if (getTreeItem.getParent == null) {}
              else getTreeItem.getParent.getChildren.remove(newItem)
            }
            catch {
              case e: IOException => println("Delete failed!")
            }
          }
        })

      }
    })


      dirMenu.getItems.addAll(expandMenu, expandAllMenu, addMenu, deleteMenuDir)
      fileMenu.getItems.addAll(deleteMenuFile)



    override def updateItem(item: PathItem, empty: Boolean): Unit = {
      super.updateItem(item, empty)
      if (empty) {
        setText(null)
        setGraphic(null)
      } else {
        val itemString: String = getString
        if (isEditing) {
          if (textField != null) {
            textField.setText(itemString)
          }
          setText(null)
          setGraphic(textField)
        } else {
          setText(itemString)

          //set graphic
          graphicChooser()


          if (!getTreeItem.isLeaf) {
            //here the ContextMenu is getting called
            //! This function provides already an eventhandler for secondary mouse button clicked
            setContextMenu(dirMenu)
          } else {
            //here the ContextMenu is getting called
            //! This function provides already an eventhandler for secondary mouse button clicked
            setContextMenu(fileMenu)
          }


        }
      }
    }


    override def startEdit(): Unit = {
      super.startEdit()
      if (textField == null) {
        createTextField()
      }
      setText(null)
      setGraphic(textField)
      textField.selectAll()
      if (getItem == null) editingPath = null
      else editingPath = getItem.getPath
    }


    override def cancelEdit(): Unit = {
      super.cancelEdit()
      setText(getString)

      //set graphic
      graphicChooser()
    }

    private def getString: String = getItem.toString

    private def createTextField(): Unit = {
      textField = new TextField(getString)
      textField.setOnKeyReleased(new EventHandler[KeyEvent] {
        override def handle(event: KeyEvent): Unit = {
          if (event.getCode == KeyCode.ENTER) {
            val path: Path = Paths.get(getItem.getPath.getParent.toAbsolutePath.toString, textField.getText)
            commitEdit(new PathItem(path))
            Files.move(editingPath, path)
          } else if (event.getCode == KeyCode.ESCAPE) {
            cancelEdit()
          }
        }
      })
    }


    /**
      * A function which sets the Image for every TreeCell based on the extension
      */
    def graphicChooser(): Unit = {
      if (Files.isRegularFile(getItem.getPath, LinkOption.NOFOLLOW_LINKS)) {
        getItem.toString match {
          case pdf if pdf.endsWith(".pdf") => setGraphic(new ImageView(picturePDFFile))
          case music if music.endsWith(".mp3") || music.endsWith(".aac") => setGraphic(new ImageView(pictureMP3File))
          case video if video.endsWith(".mp4") || video.endsWith(".avi") || video.endsWith(".mkv") || video.endsWith(".flv") => setGraphic(new ImageView(pictureVideoFile))
          case picture if picture.endsWith(".jpg") || picture.endsWith(".JPG") || picture.endsWith(".jpeg") || picture.endsWith(".png") || picture.endsWith(".PNG") || picture.endsWith(".gif") || picture.endsWith(".ico") || picture.endsWith(".bmp") => setGraphic(new ImageView(picturePictureFile))
          case word if word.endsWith(".doc") || word.endsWith(".docx") || word.endsWith(".odt") || word.endsWith(".pages") => setGraphic(new ImageView(pictureWordFile))
          case excel if excel.endsWith(".xls") || excel.endsWith(".xlsx") || excel.endsWith(".numbers") => setGraphic(new ImageView(pictureExcelFile))
          case powerpoint if powerpoint.endsWith(".ppt") || powerpoint.endsWith(".pptx") || powerpoint.endsWith(".key") => setGraphic(new ImageView(picturePowerpointFile))
          case exe if exe.endsWith(".exe") || exe.endsWith(".EXE") || exe.endsWith(".msi") || exe.endsWith(".pkg") => setGraphic(new ImageView(pictureExeFile))
          case zip if zip.endsWith(".zip") || zip.endsWith(".7z") || zip.endsWith(".rar") || zip.endsWith(".tar") || zip.endsWith(".gz") => setGraphic(new ImageView(pictureZIPFile))
          case _ => setGraphic(new ImageView(pictureFile))
        }
      }
      else {
        setGraphic(new ImageView(pictureFolder))
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