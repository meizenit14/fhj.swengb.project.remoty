package fhj.swengb.project.remoty


import java.io.{FileNotFoundException, IOException, File}
import java.net.URL
import java.nio.charset.MalformedInputException
import java.nio.file.Files._
import java.nio.file.attribute.{BasicFileAttributeView, BasicFileAttributes}
import java.nio.file._
import java.util.{Scanner, ResourceBundle}
import javafx.application.Application
import javafx.beans.property.SimpleStringProperty
import javafx.collections.{FXCollections, ObservableList}
import javafx.event.{EventType, ActionEvent, EventHandler}
import javafx.fxml.{FXML, Initializable, FXMLLoader}
import javafx.geometry.{Insets, Pos}
import javafx.scene.control._
import javafx.scene.image.{ImageView, Image}
import javafx.scene.input.{MouseButton, ContextMenuEvent, MouseEvent}
import javafx.scene.layout._
import javafx.scene.{Scene, Parent}
import javafx.stage.{DirectoryChooser, Stage}
import javafx.util.Callback
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.io.Source
import scala.util.{Try, Success, Failure}
import scala.util.control.NonFatal
import scala.collection.JavaConversions._
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.text.TextAlignment

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
  val Fxml = "/fhj/swengb/project/remoty/GUI_1.0.fxml"


  val loader = new FXMLLoader(getClass.getResource(Fxml))

  override def start(stage: Stage): Unit =
    try {
      stage.setTitle("Remoty")
      loader.load[Parent]() // side effect
      val scene = new Scene(loader.getRoot[Parent]) //loads the default scene
      stage.setScene(scene)
      stage.setResizable(false) //window cannot be rescaled

      //set the stage for the controller
      val controller1 = loader.getController[RemotyAppController]
      controller1.setStage(stage)
      stage.getScene.getStylesheets.add(Css)

      stage.show()
    } catch {
      case NonFatal(e) => e.printStackTrace()
    }
}


class RemotyAppController extends Initializable {

  @FXML var pane_view: Pane = _
  @FXML var tree_view: TreeView[PathItem] = _
  //a label to show the actions of the mouseEventHandler
  @FXML var msg_out: Label = _
  @FXML var chooserButton: Button = _
  @FXML var rootLabel: Label = _
  @FXML var details: ListView[String] = _
  //set a refresh button
  @FXML var refresh_btn : Button = _
  //@FXML var textArea : TextArea = _
  @FXML var Songlabel: Label = _
  @FXML var ppButton: Button = _


  //needed for the cellfactory
  lazy val messageProp: SimpleStringProperty = new SimpleStringProperty()


  var stage:Stage = null
  def setStage(s:Stage):Unit ={stage = s}

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    initializeAll()
  }


  def initializeAll(): Unit = {

    /*
    //set the background for the pane_view
    val bg = new BackgroundImage(new Image("PFAD ZUM BILD",1500,1500,false,true),
      BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)
    pane_view.setBackground(new Background(bg))
    */


    refresh_btn.setDisable(true)
    //set a image for the refresh button
    refresh_btn.setGraphic(new ImageView(new Image("/fhj/swengb/project/remoty/refresh.png")))

    //set the action for the refresh button
    refresh_btn.setOnAction(new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        //rebuild the tree in order to refresh it
        try{
          buildTree()
          tree_view.refresh()
        }
        catch {
          case e:AccessDeniedException => println("Acces denied!")
        }

      }
    })

    val chooser = new DirectoryChooser
    // set onClickAction on the "Choose Root Button" and open a "directory chooser" dialog
    chooserButton.setOnAction(new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        val selected = chooser.showDialog(stage)
        if(selected != null){
          rootLabel.setText(selected.getAbsolutePath)
          //call the buildTree function
          buildTree()
          refresh_btn.setDisable(false)

          //set the cellfactory for the tree_view in order to dynamically change things
          tree_view.setCellFactory(new Callback[TreeView[PathItem],TreeCell[PathItem]]() {
            override def call(p: TreeView[PathItem]): TreeCell[PathItem] = {
              val cell: PathTreeCell = new PathTreeCell(stage,messageProp)
              return cell
            }
          })


          var index:Int = 0
          var players: scala.collection.mutable.MutableList[MediaPlayer] = scala.collection.mutable.MutableList()
          tree_view.setOnMouseClicked(new EventHandler[MouseEvent] {
            override def handle(event: MouseEvent): Unit = {

              if(event.getButton == MouseButton.PRIMARY){

                try {
                  val path = tree_view.getSelectionModel.getSelectedItem.getValue.getPath

                  //check if file is a text based file

                  Files.probeContentType(path) match {
                    case text if text.startsWith("text") => {
                      if (index != 0) {
                        pane_view.getChildren.remove(index)
                      }
                      //create new textArea to show the files content
                      val textArea = new TextArea()
                      textArea.setLayoutX(346.0)
                      textArea.setLayoutY(98.0)
                      textArea.setPrefSize(629.0, 535.0)
                      textArea.setEditable(false)
                      pane_view.getChildren.add(textArea)
                      index = pane_view.getChildren.indexOf(textArea)
                      //get the text from file and set it to the text area, then close the stream to prevent "file already in use" errors
                      val text = Source.fromFile(path.toString)
                      textArea.setText(text.getLines() mkString "\n")
                      text.close()
                    }
                    case image if image.startsWith("image") => {
                      if (index != 0)
                        pane_view.getChildren.remove(index)

                      //create new imageViw to show the image
                      val imageView = new ImageView()
                      imageView.setLayoutX(346.0)
                      imageView.setLayoutY(98.0)

                      imageView.setImage(new Image(path.toUri.toString))
                      imageView.setFitHeight(535.0)
                      imageView.setFitWidth(629.0)
                      pane_view.getChildren.add(imageView)
                      index = pane_view.getChildren.indexOf(imageView)

                    }
                    case audio if audio.startsWith("audio") => {
                      if (index != 0){
                        pane_view.getChildren.remove(index)
                        index = 0
                      }

                      val pane = new Pane()
                      pane.setLayoutX(346.0)
                      pane.setLayoutY(346.0)
                      pane.setPrefSize(629.0, 535.0)

                      val songName: Label = new Label(path.getFileName.toString)
                      songName.setFont(new javafx.scene.text.Font("Calibri",24))
                      songName.setTextAlignment(TextAlignment.CENTER)

                      val startButton: Button = new Button()
                      startButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler[MouseEvent] {
                        override def handle(event: MouseEvent) = {

                          if (index != 0){
                            pane_view.getChildren.remove(index)
                            index = 0
                          }
                          if (players.nonEmpty) {
                            players.head.stop()
                            players = scala.collection.mutable.MutableList()
                          }
                          val song: Media = new Media(path.toUri.toString)
                          val player: MediaPlayer = new MediaPlayer(song)
                          players += player

                          Songlabel.setText(path.getFileName.toString)
                          Songlabel.setVisible(true)

                          ppButton.setGraphic(new ImageView(new Image("/fhj/swengb/project/remoty/play_pause.png")))
                          ppButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler[MouseEvent] {
                            override def handle(event: MouseEvent) = {
                              val player: MediaPlayer = players.head
                              if (player.getStatus.equals(javafx.scene.media.MediaPlayer.Status.PLAYING))
                                player.pause()
                              else
                                player.play()
                            }
                          })
                          ppButton.setVisible(true)
                          players.head.play()
                        }
                        startButton.setGraphic(new ImageView(new Image("/fhj/swengb/project/remoty/play.png")))
                        startButton.setLayoutX(100.0)
                        startButton.setLayoutY(50.0)
                        pane.getChildren.add(songName)
                        pane.getChildren.add(startButton)
                        pane_view.getChildren.add(pane)
                        index = pane_view.getChildren.indexOf(pane)
                      })

                    }
                    case _ => {
                      if (index != 0){
                        pane_view.getChildren.remove(index)
                        index = 0
                      }

                    }
                  }

                  def sizeString(size: Long): String =  size match {
                    case kilobyte if kilobyte < 1048576 => val calcSize = size / 1024 ; return s"$calcSize KB"
                    case megabyte if megabyte >= 1048576 => val calcSize = (size / 1024) / 1024 ; return s"$calcSize MB"
                    case gigabyte if gigabyte >= 1073741824 => val calcSize = ((size / 1024) / 1024) / 1024 ; return s"$calcSize GB"
                    case _ => return s"$size Byte"
                  }

                  val attrs = getFileAttributeView(path, classOf[BasicFileAttributeView])
                  val lastModified = "Last modified: " + attrs.readAttributes().lastModifiedTime().toString.take(19).replace("T", " ")
                  val fileSize = "Filesize: " + sizeString(attrs.readAttributes().size)
                  val creationTime = "Creation time: " + attrs.readAttributes().creationTime().toString.take(19).replace("T", " ")
                  val data = FXCollections.observableArrayList(lastModified, fileSize, creationTime)
                  details.setItems(data)

                }catch {
                  case e: NullPointerException => {
                    if (index != 0){
                      pane_view.getChildren.remove(index)
                      index = 0
                    }
                    val attrs = getFileAttributeView(tree_view.getSelectionModel.getSelectedItem.getValue.getPath, classOf[BasicFileAttributeView])
                    val lastModified = "Last modified: " + attrs.readAttributes().lastModifiedTime().toString.take(19).replace("T", " ")
                    val creationTime = "Creation time: " + attrs.readAttributes().creationTime().toString.take(19).replace("T", " ")
                    val data = FXCollections.observableArrayList(lastModified, creationTime)
                    details.setItems(data)}
                  case x: MalformedInputException => println("Unexpected error occured!")
                  case fnf: FileNotFoundException => println("File not found! Deleted or corrupt!")
                }

              }
            }
          })


        }
      }
    })

  }


  def buildTree() {
    val rootPath: String = rootLabel.getText
    val root = Paths.get(rootPath)
    val item = PathTreeItem.createNode(new PathItem(root))
    item.setExpanded(true)
    tree_view.setRoot(item)
    tree_view.setEditable(true)
  }

}





