// See LICENSE

package chipyard.stage.phases

import chisel3.RawModule
import chisel3.stage.ChiselGeneratorAnnotation
import firrtl.AnnotationSeq
import firrtl.options.Viewer.view
import firrtl.options.{Dependency, Phase, StageOptions}
import chipyard.stage._

/** Constructs a generator function that returns a top module with given config parameters */
class PreElaboration extends Phase with PreservesAll with HasChipyardStageUtils {

  override val prerequisites = Seq(Dependency[Checks])
  override val optionalPrerequisiteOf = Seq(Dependency[chisel3.stage.phases.Elaborate])

  override def transform(annotations: AnnotationSeq): AnnotationSeq = {

    val stageOpts = view[StageOptions](annotations)
    val rOpts = view[ChipyardOptions](annotations)
    val topMod = rOpts.topModule.get

    val gen = () => 
      topMod 
        .getConstructor() 
        .newInstance() match { 
          case a: RawModule => a 
      }

    ChiselGeneratorAnnotation(gen) +: annotations
  }

}
