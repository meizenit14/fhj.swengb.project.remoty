package fhj.swengb.project.remoty

import java.net.URL
import java.util.ResourceBundle
import javafx.application.Application
import javafx.fxml.{Initializable, FXMLLoader}
import javafx.scene.{Scene, Parent}
import javafx.stage.Stage

import scala.util.control.NonFatal

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
  val Fxml = "/fhj/swengb/project/remoty/Remoty.fxml"


  val loader = new FXMLLoader(getClass.getResource(Fxml))

  override def start(stage: Stage): Unit =
    try {
      stage.setTitle("Remoty")
      loader.load[Parent]() // side effect
      val scene = new Scene(loader.getRoot[Parent]) //loads the default scene
      stage.setScene(scene)
      stage.setResizable(false) //window cannot be rescaled
      stage.getScene.getStylesheets.add(Css)
      stage.show()
    } catch {
      case NonFatal(e) => e.printStackTrace()
    }
}


class RemotyAppController extends Initializable {


  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    //initialize things here..
  }

}

