BUILD_DIR = ./build

PRJ = mosaic

default: verilog

test:
	mill -i $(PRJ).test

verilog: reformat
	$(call git_commit, "generate verilog")
	mkdir -p $(BUILD_DIR)
	sbt ";project $(PRJ);runMain $(PRJ).Elaborate --target-dir $(BUILD_DIR)"

help:
	sbt ";project mosaic;runMain mosaic.Elaborate --help"

reformat:
	mill -i __.reformat

checkformat:
	mill -i __.checkFormat

bsp:
	mill -i mill.bsp.BSP/install

idea:
	mill -i mill.idea.GenIdea/idea

clean:
	-rm -rf $(BUILD_DIR) ./out
	-rm -rf mosaic/src/main/resources/vsrc

.PHONY: test verilog help reformat checkformat clean

sim:
	$(call git_commit, "sim RTL") # DO NOT REMOVE THIS LINE!!!
	@echo "Write this Makefile by yourself."

-include ../Makefile
