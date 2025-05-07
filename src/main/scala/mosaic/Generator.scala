package mosaic

import firrtl.options.{StageMain}
import mosaic.stage.MoSAICStage

object Generator extends StageMain(new MoSAICStage)
