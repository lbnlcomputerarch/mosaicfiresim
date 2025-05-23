---
title: MoSAICFireSim
version: v0.3.0
---

[[_TOC_]]

# FireSim Wrapper for MoSAIC

Simple FireSim without Chipyard wrapper for MoSAIC.

## FIRRTL2 Layer

Due to FireSim using Chisel v3.6, we need to add a FIRRTL2 shim that will provide the translation of MoSAIC's Chisel 6 wrapper to be consumed by FireSim's Chisel 3.6.

To see the generated FIRRTL:

```bash
make firrtl
```

You'll find the generated FIRRTL in `build`.

## FireSim Integration

To integrate the Chisel Wrapper for MoSAIC with FireSim, make sure you have `$FIRESIM_ROOT` defined in your environment. `$FIRESIM_ROOT` points to where your FireSim checkout is located in your environment.

```bash
# Make sure $FIRESIM_ROOT is defined
make firesim
```

Follow the directions for [FireSim without Chipyard](https://docs.fires.im/en/main/Advanced-Usage/FireSim-without-Chipyard.html) for details on how to build and run MoSAIC within Verilator and FireSim.

### Running `SimpleMoSAIC` on Verilator

Here are some basic instructions for running the `SimpleMoSAIC` harness within Verilator.

```bash
cd $FIRESIM_ROOT/sim
make TARGET_PROJECT=mosaic PLATFORM=xilinx_alveo_u250 run-verilator
```

You will see the following output upon completion:

```text
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

# Chisel Wrapper for MoSAIC

Simple Chisel 6 wrapper for MoSAIC.

## Dependencies

This project can be built with the following build tools:

- [`scala-cli`](https://scala-cli.virtuslab.org/install) is the easiest way to build Chisel.
- [Mill](https://mill-build.com/), a modern Scala build tool that's more user friendly than SBT. The instructions for installing `mill` can be found [here](https://www.chisel-lang.org/docs/installation#mill).

## Build Verilog

```bash
make verilog
```

You'll find the generated SystemVerilog in `build`.

## References

- [FireSim without Chipyard](https://docs.fires.im/en/main/Advanced-Usage/FireSim-without-Chipyard.html)
- [Chisel Documentation](https://www.chisel-lang.org/docs)
- [Chisel Template](https://github.com/chipsalliance/chisel-template)
- [Chisel Playground](https://github.com/OSCPU/chisel-playground)
