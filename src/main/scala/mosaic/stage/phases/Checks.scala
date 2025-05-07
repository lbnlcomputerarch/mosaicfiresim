package mosaic.stage.phases

import firrtl.AnnotationSeq
import firrtl.annotations.Annotation
import firrtl.options.{OptionsException, Phase, TargetDirAnnotation}
import mosaic.stage._

class Checks extends Phase with PreservesAll {

  override def transform(annotations: AnnotationSeq): AnnotationSeq = {
    val targetDir, topModule, configNames, outputBaseName = scala.collection.mutable.ListBuffer[Annotation]()

    annotations.foreach {
      case a: TargetDirAnnotation      => a +=: targetDir
      case a: TopModuleAnnotation      => a +=: topModule
      case a: ConfigsAnnotation        => a +=: configNames
      case a: OutputBaseNameAnnotation => a +=: outputBaseName
      case _ =>
    }

    def required(annoList: scala.collection.mutable.ListBuffer[Annotation], option: String): Unit = {
      if (annoList.size != 1) {
        throw new OptionsException(s"Exactly one $option required")
      }
    }

    def optional(annoList: scala.collection.mutable.ListBuffer[Annotation], option: String): Unit = {
      if (annoList.size > 1) {
        throw new OptionsException(s"Too many $option options have been specified")
      }
    }

    required(targetDir, "target directory")
    required(topModule, "top module")
    required(configNames, "configs string (','-delimited)")

    optional(outputBaseName, "output base name")

    annotations
  }

}
