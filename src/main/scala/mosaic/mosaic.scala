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

  val mosaicChiselDir = System.getProperty("user.dir")
  val mosaicVsrcDir   = s"${mosaicChiselDir}/src/main/resources/mosaic/vsrc"
  val mosaicGitDir    = s"${mosaicVsrcDir}/MoSAIC-P38"
  val mosaicGitHash   = s"chisel_wrapper"
  val mosaicFileList  = s"${mosaicGitDir}/icarus/file_list.txt"

  if (! new File(mosaicGitDir).exists()) {
    val gitClone = 
      s"git clone -b ${mosaicGitHash} https://github.com/lbnlcomputerarch/MoSAIC-P38.git ${mosaicGitDir}"
    require(gitClone.! == 0, "Failed to clone MoSAIC-P38")
  }  else {
    val gitUpdate = 
      s"cd ${mosaicGitDir} && git pull origin ${mosaicGitHash}"
    require(gitUpdate.! == 0, "Failed to update MoSAIC-P38")
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

  // pre-process the verilog to remove "includes" and combine into one file
  val make =
    s"make -C ${mosaicVsrcDir} default MOSAIC_PERL_SCRIPT=\"${mosaicConfig}\" ALL_VSRCS=\"${fileList.mkString(" ")}\""
  require(make.! == 0, "Failed to run preprocessing step")

  // add wrapper/blackbox after it is pre-processed
  addPath(s"${mosaicVsrcDir}/mosaic.preprocessed.sv")
}
