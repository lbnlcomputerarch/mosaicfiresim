package mosaic.stage.phases

import circt.stage.{CIRCTTarget, CIRCTTargetAnnotation, ChiselStage}
import firrtl.options.PhaseManager.PhaseDependency
import firrtl.options.Shell
import firrtl.options.Viewer.view
import firrtl.AnnotationSeq
import firrtl.stage.FirrtlCircuitAnnotation
import firrtl.options.{Dependency, Phase, PhaseManager, Shell, Stage, StageError, StageMain, StageOptions}
import mosaic.stage._

final class LegacyFirrtl2Emission extends Phase with PreservesAll with HasMoSAICStageUtils {
  override val prerequisites = Seq(Dependency[mosaic.stage.MoSAICChiselStage])

  override def transform(annotations: AnnotationSeq): AnnotationSeq = {
    val targetDir = view[StageOptions](annotations).targetDir
    val fileName  = s"${view[MoSAICOptions](annotations).longName.get}.sfc.fir"

    val annos = annotations.filterNot(_.isInstanceOf[firrtl.options.TargetDirAnnotation])

    val converted = firrtl2.bridge.ChiselBridge.annosToState(annos)
    val emitter   = new firrtl2.ChirrtlEmitter
    val circuit = converted.copy(annotations =
      converted.annotations ++ Seq(
        firrtl2.EmitCircuitAnnotation(classOf[firrtl2.ChirrtlEmitter])
      )
    )
    val emitted = emitter.execute(circuit)
    val emittedCircuit = emitted.annotations.collectFirst { case firrtl2.EmittedFirrtlCircuitAnnotation(circuit) =>
      circuit
    }.get
    writeOutputFile(targetDir, fileName, emittedCircuit.value)

    annotations
  }
}
