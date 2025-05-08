package mosaic.stage.phases

import chisel3.RawModule
import chisel3.stage.ChiselGeneratorAnnotation
import firrtl.AnnotationSeq
import firrtl.options.Viewer.view
import firrtl.options.{Dependency, Phase, StageOptions}
import org.chipsalliance.cde.config.{Field, Parameters}
import mosaic.stage._

case object TargetDirKey extends Field[String](".")

class PreElaboration extends Phase with PreservesAll with HasMoSAICStageUtils {

  override val prerequisites          = Seq(Dependency[Checks])
  override val optionalPrerequisiteOf = Seq(Dependency[chisel3.stage.phases.Elaborate])

  override def transform(annotations: AnnotationSeq): AnnotationSeq = {

    val stageOpts = view[StageOptions](annotations)
    val rOpts     = view[MoSAICOptions](annotations)
    val topMod    = rOpts.topModule.get

    val config = getConfig(rOpts.configNames.get).alterPartial { case TargetDirKey =>
      stageOpts.targetDir
    }

    val gen = () =>
      topMod
        .getConstructor(classOf[Parameters])
        .newInstance(config) match {
        case a: RawModule  => a
      }

    ChiselGeneratorAnnotation(gen) +: annotations
  }

}
