package mosaic

import sys.process._
import scala.io.Source
import java.io.File

import chisel3._
import chisel3.util.HasBlackBoxPath
import chisel3.experimental._ // To enable experimental features

class mosaic(
  bw:           Int        = 32,
  ddr4Config:   DDR4Config = new DDR4Config(),
  mosaicConfig: String     = "mosaic_2x2_firesim")
    extends BlackBox(
      Map(
        "BW" -> IntParam(bw)
      )
    )
    with HasBlackBoxPath {
  val ddr4Ctrl: Boolean = mosaicConfig.contains("ddr")
  val bwb:      Int     = bw / 8

  val io = IO(
    new SVMoSAICIO(
      ddr4Ctrl   = ddr4Ctrl,
      bw         = bw,
      ddr4Config = ddr4Config
    )
  )

  val mosaicRootDir = System.getProperty("user.dir")
  val mosaicChiselDir = s"${mosaicRootDir}/mosaic"
  val mosaicVsrcDir   = s"${mosaicChiselDir}/src/main/resources/mosaic/vsrc"
  val mosaicGitDir    = s"${mosaicVsrcDir}/MoSAIC-P38"
  val mosaicBuildDir  = s"${mosaicGitDir}/build"
  val mosaicGitHash   = s"chisel_wrapper"
  val mosaicFileList  = s"${mosaicGitDir}/icarus/file_list.txt"
  val mosaicPreProcOutputFile = s"${mosaicVsrcDir}/mosaic.preprocessed.sv"

  if (! new File(mosaicGitDir).exists()) {
    val gitClone = 
      s"git clone -b ${mosaicGitHash} https://github.com/lbnlcomputerarch/MoSAIC-P38.git ${mosaicGitDir}"
    require(gitClone.! == 0, "Failed to clone MoSAIC-P38")
  }

  val perlMake =
    s"make -C ${mosaicVsrcDir} build MOSAIC_PERL_SCRIPT=\"${mosaicConfig}\""
  require(perlMake.! == 0, "Failed to run MoSAIC Perl Build step")

  val fileList = Source
    .fromFile(mosaicFileList)
    .getLines()
    .toList
    .filter(_.nonEmpty)
    .filterNot(_.startsWith("#"))
    .filterNot(_.contains("Testbench"))
    .map(_.replaceAll("^\\.{2}(.*)$","$1"))
    .map(mosaicGitDir +  _)
    .map(new File(_))

  // pre-process the verilog to remove "includes" and combine into one file
  utils.VerilogPreprocessor.preprocessVerilog(
    outputFile = new File(mosaicPreProcOutputFile),
    allVsrcs = fileList,
    preprocessDefines = Seq(
      "MOSAIC_FIRESIM"
      ),
    verilatorLintoffDefines = Seq(
      "UNUSEDPARAM",
      "PINCONNECTEMPTY",
      "GENUNNAMED",
      "IMPLICIT", 
      "WIDTHEXPAND",
      "WIDTHTRUNC",
      "UNSIGNED",
      "CASEINCOMPLETE",
      "BLKSEQ",
      "SYNCASYNCNET"
    ),
    buildDir = new File(mosaicBuildDir)
  )

  // add wrapper/blackbox after it is pre-processed
  addPath(mosaicPreProcOutputFile)
}
