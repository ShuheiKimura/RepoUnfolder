package sdl

import org.apache.commons.io.FileUtils
import java.nio.file.{Files, Paths}

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

    comList.reverse.zipWithIndex.map(v=>{
      val (c,i) = v
      val commitId = c.getId.getName
      val dest = destFolder.resolve(i + " " + commitId)
      println("r"+ i + "/" + comList.size + " " + commitId)

      manager.git.checkout().setName(commitId).call()
      FileUtils.copyDirectory(repoPath.toFile, dest.toFile)
    })

    // reset repository
    manager.git.checkout().setName("HEAD").call()
  }
}
