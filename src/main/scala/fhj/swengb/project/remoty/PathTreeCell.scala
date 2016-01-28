package fhj.swengb.project.remoty

import java.awt.Desktop
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
import javafx.scene.input._
import javafx.stage.Stage


/**
  * Created by chris on 22.01.2016.
  */


object PathTreeCell{
  var source:Path = null
}
  /**
    * Class PathTreeCell which extends a TreeCell.
    * In order to be able to call the built-in functions of the TreeCell and also to provide a ContextMenu for every single TreeCell
    */
  class PathTreeCell(owner: Stage, messageProp: StringProperty) extends TreeCell[PathItem] {

    var textField: TextField = _
    var editingPath: Path = _
    var dirMenu: ContextMenu = new ContextMenu()
    var fileMenu: ContextMenu =  new ContextMenu()



      //expanding a directory and only the next nested level of the directory
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


    /**
      * A recursive function which is expanding all Items which are nested under the clicked Item.
 *
      * @param item
      */
    def expandTreeItem(item: TreeItem[PathItem]): Unit = item match {
        case leaf if item.isLeaf =>
        case noLeaf if !item.isLeaf => {
          item.setExpanded(true)
          val children: List[TreeItem[PathItem]] = TreeViewUtil.mkList(item.getChildren)

          children.filter(child => !child.isLeaf).foreach(child => expandTreeItem(child))

        }

      }

      //renaming the Directory
      val renameDirMenu: MenuItem = new MenuItem("Rename")
      renameDirMenu.setOnAction(new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          getTreeView.edit(getTreeItem)
        }
      })

      //renaming the file
      val renameFileMenu: MenuItem = new MenuItem("Rename")
      renameFileMenu.setOnAction(new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          getTreeView.edit(getTreeItem)
        }
      })

      //opens all Files with the default programme
      val openMenu: MenuItem = new MenuItem("Open")
      openMenu.setOnAction(new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          //from java.awt --> get the default programme to open the specific File
          Desktop.getDesktop.open(getItem.getPath.toFile)
        }
      })


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


    /**
      * Creating a new Directory with the current path where clicked and the name of the parent Item
      * Is being used in the MenuItem
 *
      * @return
      */
    def createNewDirectory(): Path = {

          val path: Path = getTreeItem.getValue.getPath
          val newDir = Paths.get(path.toAbsolutePath.toString, "New Directory " + getItem.toString)
          try {
            Files.createDirectory(newDir)
          }
          catch {
            case a: FileAlreadyExistsException => println("File already exists!") //maybe change the println with message pop up etc..
            case b: IOException => cancelEdit(); messageProp.setValue(s"Creating directory(${newDir.getFileName}) failed")
          }
          newDir
        }



      //DeleteMenu when clicked on a directory
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

    //MenuItem for deleting for the Files
    //! We need two MenuItems which can delete because one MenuItem can't be added to two ContextMenus
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


      //add all the MenuItems created to the two ContextMenus
      dirMenu.getItems.addAll(expandMenu, expandAllMenu,renameDirMenu, addMenu, deleteMenuDir)
      fileMenu.getItems.addAll(openMenu,renameFileMenu,deleteMenuFile)



    /**
      * The updateItem function is called when the Tree is build for every single TreeItem it is generating a TreeCell and setting the
      * TextField, graphic and also a ContextMenu.
      * To be able to call this updateItem function it is necessary to set the cellFactory for the TreeView.
 *
      * @param item
      * @param empty
      */
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


    /**
      * This function is a function of TreeCell which is called automatically when double-clicked on a TreeItem
      */
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


    /**
      * When canceling the Editing process (built in function of TreeCell class)
      */
    override def cancelEdit(): Unit = {
      super.cancelEdit()
      setText(getString)

      //set graphic
      graphicChooser()
    }

    private def getString: String = getItem.toString


    /**
      * Creating a new TextField which is being called when the startEdit() function starts
      * The textfield is being set when the Enter button is pressed or the cancelEdit() is called when the Escape button is fired.
      */
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


    setOnDragDetected(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = {
        if(!isEmpty){
          PathTreeCell.source = getItem.getPath
          val db = startDragAndDrop(TransferMode.MOVE)

          val cc = new ClipboardContent
          cc.putString(getItem.toString)
          db.setContent(cc)
        }
      }
    })

    setOnDragOver(new EventHandler[DragEvent] {
      override def handle(event: DragEvent): Unit = {
        if(Files.isDirectory(getItem.getPath)) {
          event.acceptTransferModes(TransferMode.MOVE)
          getTreeItem.setExpanded(true)
        }
      }
    })


    setOnDragEntered(new EventHandler[DragEvent] {
      override def handle(event: DragEvent): Unit = {
        if(Files.isDirectory(getItem.getPath)) {
          setStyle("-fx-background-color: yellow;")
          event.acceptTransferModes(TransferMode.MOVE)
        }
      }
    })

    setOnDragExited(new EventHandler[DragEvent] {
      override def handle(event: DragEvent): Unit = {
        setStyle("")
      }
    })

    setOnDragDropped(new EventHandler[DragEvent] {
      override def handle(event: DragEvent): Unit = {
        Files.move(PathTreeCell.source, Paths.get(getItem.getPath.toString +"\\"+ PathTreeCell.source.getFileName.toString), StandardCopyOption.REPLACE_EXISTING)
        Files.delete(PathTreeCell.source)
        }
    })




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
