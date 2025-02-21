# Chisel Wrapper for MoSAIC

Simple Chisel 6 wrapper for MoSAIC.

## Dependencies

This project can be built with the following build tools:

- [`scala-cli`](https://scala-cli.virtuslab.org/install) is the easiest way to build Chisel.
- [Mill](https://mill-build.com/), a modern Scala build tool that's more user friendly than SBT. The instructions for installing `mill` can be found [here](https://www.chisel-lang.org/docs/installation#mill).

## Build Verilog

### `scala-cli`

```bash
scala-cli src/main/scala/mosaic/MoSAICChisel.scala src/main/scala/mosaic/MoSAICIO.scala src/main/scala/mosaic/mosaic.scala
```

The generated SystemVerilog will be displayed to the console.

### `mill`

```bash
make verilog
```

You'll find the generated SystemVerilog in `build`.

## FireSim Integration

To integrate the Chisel Wrapper for MoSAIC with FireSim, make sure you have `$FIRESIM_ROOT` defined in your environment. `$FIRESIM_ROOT` points to where your FireSim checkout is located in your environment.

```bash
# Make sure $FIRESIM_ROOT is defined
./firesim-install.sh
```

Follow the directions for [FireSim without Chipyard](https://docs.fires.im/en/main/Advanced-Usage/FireSim-without-Chipyard.html) for details on how to build and run MoSAIC within Verilator and FireSim.

### Running `SimpleMoSAIC` on Verilator

Here are some basic instructions for running the `SimpleMoSAIC` harness within Verilator.

```bash
cd $FIRESIM_ROOT/sim
make TARGET_PROJECT=mosaic PLATFORM=xilinx_alveo_u250 run-verilator
```

You will see the following output upon completion:

```
...
MoSAIC has reset successfully
MoSAIC has reset successfully
MoSAIC has reset successfully

Simulation complete.
*** FAILED *** simulation timed out after 10001 cycles

Emulation Performance Summary
------------------------------
Wallclock Time Elapsed: 0.3 s
Host Frequency: 29.128 KHz
Target Cycles Emulated: 10001
Effective Target Frequency: 29.061 KHz
FMR: 1.00
Note: The latter three figures are based on the fastest target clock.
make: *** [/home/bxeuser/chipyard/sims/firesim/sim/src/main/makefrag/mosaic/metasim.mk:16: run-verilator] Error 1
```

This is not an Error, as the simulation timed out. In fact, this shows the MoSAIC successfully resetting within Verilator.

## References

- [Chisel Documentation](https://www.chisel-lang.org/docs)
- [Chisel Template](https://github.com/chipsalliance/chisel-template)
- [Chisel Playground](https://github.com/OSCPU/chisel-playground)
