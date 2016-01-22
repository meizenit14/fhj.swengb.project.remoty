package fhj.swengb.project.remoty

import java.io.IOException
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Files, Path, SimpleFileVisitor}

/**
  * Created by chris on 22.01.2016.
  */
class VisitorForDelete  extends SimpleFileVisitor[Path]{

  override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
    Files.deleteIfExists(file)
     FileVisitResult.CONTINUE
  }


  override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
    Files.deleteIfExists(dir)
     FileVisitResult.CONTINUE
  }
}
