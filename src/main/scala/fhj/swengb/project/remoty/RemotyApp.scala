package fhj.swengb.project.remoty


import java.awt.Font
import java.io.{FileNotFoundException, IOException, File}
import java.net.URL
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file._
import java.util.{Scanner, ResourceBundle}
import javafx.application.Application
import javafx.beans.property.SimpleStringProperty
import javafx.collections.{FXCollections, ObservableList}
import javafx.event.{EventType, ActionEvent, EventHandler}
import javafx.fxml.{FXML, Initializable, FXMLLoader}
import javafx.scene.control._
import javafx.scene.image.{ImageView, Image}
import javafx.scene.input.{MouseButton, ContextMenuEvent, MouseEvent}
import javafx.scene.layout.{HBox, Pane, StackPane, BorderPane}
import javafx.scene.{Scene, Parent}
import javafx.stage.{DirectoryChooser, Stage}
import javafx.util.Callback
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
  val Fxml = "/fhj/swengb/project/remoty/TreeViewTest.fxml"
  val Fxml2 = "/fhj/swengb/project/remoty/GUI_1.0.fxml"


  val loader = new FXMLLoader(getClass.getResource(Fxml2))

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
      //stage.getScene.getStylesheets.add(Css)

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
  //@FXML var textArea : TextArea = _


  //needed for the cellfactory

  private var messageProp: SimpleStringProperty = new SimpleStringProperty()


  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    initializeAll()
  }


  var stage:Stage = null
  def setStage(s:Stage):Unit ={stage = s}

//  <TextArea fx:id="textArea" layoutX="346.0" layoutY="98.0" prefHeight="535.0" prefWidth="629.0" />

  def initializeAll(): Unit = {

    val chooser = new DirectoryChooser


    // set onClickAction on the "Choose Root Button" and open a "directory chooser" dialog
    chooserButton.setOnAction(new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        val selected = chooser.showDialog(stage)
        if(selected != null){
          rootLabel.setText(selected.getAbsolutePath)
          val rootPath: String = rootLabel.getText
          val root = Paths.get(rootPath)
          val item = PathTreeItem.createNode(new PathItem(root))
          item.setExpanded(true)
          item.setGraphic(new ImageView(PathTreeItem.pictureFolder))
          tree_view.setRoot(item)
          tree_view.setEditable(true)

          //setting the cellfactory
          /*
          tree_view.setCellFactory(new Callback[TreeView[PathItem],TreeCell[PathItem]]() {
            override def call(p: TreeView[PathItem]): TreeCell[PathItem] = new PathTreeCell(stage,messageProp)
          })
          */
          var index:Int = 0
          tree_view.setOnMouseClicked(new EventHandler[MouseEvent] {
            override def handle(event: MouseEvent): Unit = {
              if (event.getButton == MouseButton.SECONDARY) {
                println("right click")
              } else {

                println(Files.probeContentType(tree_view.getSelectionModel.getSelectedItem.getValue.getPath))
                val path = tree_view.getSelectionModel.getSelectedItem.getValue.getPath
                try {
                  //check if file is a text based file

                  Files.probeContentType(path) match {
                    case text if(text.startsWith("text")) => {  if(index != 0)
                                                                  pane_view.getChildren.remove(index)

                                                                //create new textArea to show the files content
                                                                val textArea = new TextArea()
                                                                textArea.setLayoutX(346.0)
                                                                textArea.setLayoutY(98.0)
                                                                textArea.setPrefSize(629.0,535.0)
                                                                textArea.setEditable(false)
                                                                pane_view.getChildren.add(textArea)
                                                                index = pane_view.getChildren.indexOf(textArea)
                                                                textArea.setText(Source.fromFile(path.toString).getLines mkString "\n")

                    }
                    case image if(image.startsWith("image")) => { if(index != 0)
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
                    case audio if(audio.startsWith("audio")) => { if(index != 0)
                                                                    pane_view.getChildren.remove(index)

                                                                  val song: Media = new Media(path.toUri.toString)
                                                                  val player: MediaPlayer = new MediaPlayer(song)

                                                                  val borderPane = new BorderPane()
                                                                  borderPane.setLayoutX(346.0)
                                                                  borderPane.setLayoutY(98.0)
                                                                  borderPane.prefHeight(535.0)
                                                                  borderPane.prefWidth(588.0)

                                                                  val label = new Label(path.getFileName.toString)
                                                                  label.prefHeight(92.0)
                                                                  label.prefWidth(413.0)
                                                                  label.setFont(new javafx.scene.text.Font("Calibri",24))
                                                                  label.setTextAlignment(TextAlignment.CENTER)

                                                                  val button = new Button("Play/Pause")
                                                                  button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler[MouseEvent] {
                                                                    override def handle(event: MouseEvent) = {
                                                                      if (player.getStatus.equals(javafx.scene.media.MediaPlayer.Status.PLAYING))
                                                                        player.pause()
                                                                      else
                                                                        player.play()
                                                                    }
                                                                  })

                                                                  borderPane.setTop(label)
                                                                  borderPane.setCenter(button)
                                                                  pane_view.getChildren.add(borderPane)
                                                                  index = pane_view.getChildren.indexOf(borderPane)

                                                                  player.play()
                    }
                    case _ =>
                  }


                }
                catch {
                  case e:NullPointerException => println("Filetype nicht erkannt!")
                }

              }
            }
          })


        }
      }
    })

  }

  
}





