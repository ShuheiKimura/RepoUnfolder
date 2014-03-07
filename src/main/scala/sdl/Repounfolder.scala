package sdl

import org.apache.commons.io.FileUtils
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import scala.collection.immutable.HashSet
import scala.collection.mutable.ListBuffer
import java.io.{FileInputStream, InputStreamReader, BufferedReader}

object Repounfolder {

  def main( args: Array[String] ): Unit ={

    if(args.size < 2)
      println("usage: outPath repoPath")

    val outPath = Paths.get(args(0))
    val repoPath = Paths.get(args(1))

    val manager = new RepositoryManager(repoPath)
    val comList = manager.getCommitList()

    if(!Files.exists(outPath)){
      println("output directory does not exist")
      System.exit(1)
    }

    val destFolder = outPath.resolve(repoPath.getFileName)
    var prevDest = Paths.get("")

    comList.reverse.zipWithIndex.map(v=>{
      val (c,i) = v
      val commitId = c.getId.getName
      val dest = destFolder.resolve(i + " " + commitId)
      println("r"+ i + "/" + comList.size + " " + commitId)

      manager.git.checkout().setName(commitId).call()
      FileUtils.copyDirectory(repoPath.toFile, dest.toFile)

      // compress by using symbolic link
      if(i >= 1){

        class ListFile(d:String) extends SimpleFileVisitor[Path] {
          val files = collection.mutable.HashSet.empty[String]

          override def visitFile(path: Path, attr: BasicFileAttributes): FileVisitResult = {
            files += path.toString.replaceAll(d, dest.toString)
            FileVisitResult.CONTINUE
          }
          def getFiles(): Set[String] = files.toSet
        }

        val lf = new ListFile(dest.toString)
        val prevLf = new ListFile(prevDest.toString)

        Files.walkFileTree(dest, lf)
        Files.walkFileTree(prevDest, prevLf)

        val fileList = lf.getFiles()
        val prevFileList = prevLf.getFiles()

        fileList.map(p=>{
          if(prevFileList.contains(p)){
            val newP = dest.resolve(p)
            val oldP = prevDest.resolve(p)

            def fileLoader(path: Path): List[String] = {
              val list = new ListBuffer[String]
              val file = path.toFile
              val is = new FileInputStream(file)
              val ir = new InputStreamReader(is)
              val br = new BufferedReader(ir)
              var str = br.readLine()
              while (str != null) {
                list += str
                str = br.readLine()
              }
              is.close()
              ir.close()
              br.close()
              list.toList
            }

            val newFile = fileLoader(newP)
            val oldFile = fileLoader(oldP)

            if( newFile == oldFile ){
              Files.delete(newP)
              Files.createSymbolicLink(oldP, newP)
            }
          }
        })
      }

      prevDest = dest
    })

    // reset repository
    manager.git.checkout().setName("HEAD").call()
  }
}
