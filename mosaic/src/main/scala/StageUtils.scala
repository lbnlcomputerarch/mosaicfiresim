// See LICENSE

package chipyard.stage

import java.io.{File, FileWriter}

trait HasChipyardStageUtils {

  def writeOutputFile(targetDir: String, fname: String, contents: String): File = {
    val f = new File(targetDir, fname)
    val fw = new FileWriter(f)
    fw.write(contents)
    fw.close
    f
  }

}
