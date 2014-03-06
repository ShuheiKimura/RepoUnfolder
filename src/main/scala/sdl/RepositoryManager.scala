package sdl

import org.eclipse.jgit.internal.storage.file.FileRepository
import java.io.File
import org.eclipse.jgit.lib.{ObjectLoader, ObjectId, Repository}
import org.eclipse.jgit.revplot.{PlotCommit, PlotWalk, PlotLane, PlotCommitList}
import org.eclipse.jgit.revwalk.{RevWalk, RevCommit}
import scala.collection.JavaConverters._
import java.nio.file.{Paths, Files, Path}
import org.eclipse.jgit.errors.MissingObjectException
import org.eclipse.jgit.api.Git

class RepositoryManager(val repositoryPath: Path) {

  val repository = new FileRepository(repositoryPath + "/.git")
  val git = new Git(repository)

  // checks whether "repository" is a valid repository
  ///TODO: find correct way
  if( ! repository.getRepositoryState.canCheckout ){
    println("target path is not a valid repository")
    System.exit(0)
  }

  val rw = new RevWalk(repository)

  def getCommitList(rootString: String = "HEAD"): List[RevCommit] = {
    getCommitList(repository.resolve(rootString))
  }

  def getCommitList(rootId: ObjectId): List[RevCommit] = {
    val revWalk = new PlotWalk(repository)
    //    val rootId = repository.resolve(rootString)
    if(rootId!=null){
      val root = revWalk.parseCommit(rootId)
      revWalk.markStart(root)
      val plotCommitList = new PlotCommitList[PlotLane]()
      plotCommitList.source(revWalk)
      plotCommitList.fillTo(Integer.MAX_VALUE)
      plotCommitList.asScala.toList
    }
    else
      List.empty[RevCommit]
  }

  def commitId2RevCommit( c: ObjectId ): RevCommit =
    synchronized{rw.parseCommit(c)}

  // Safely open in case that "id" object has not been checked out
  def safeOpen( id: ObjectId ): Option[ObjectLoader] = {
    try{
      Some(repository.open(id))
    }
    catch{
      case e:MissingObjectException =>
        println("  cannot find id " + id)
        None
    }
  }

  def writeOut( id: ObjectId, path: Path ): Boolean = {
    safeOpen(id) match{
      case None=>
        false
      case Some(loader) =>
        val stream = Files.newOutputStream(path)
        loader.copyTo(stream)
        stream.close()
        println("  write " + path )
        true
    }
  }

}
