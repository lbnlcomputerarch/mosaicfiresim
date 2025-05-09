#########################################################################################
# pre-process MoSAIC into a single blackbox file
#########################################################################################
base_dir=$(abspath MoSAIC-P38)
vsrc_dir=$(abspath .)
mosaic_dir=$(base_dir)/src
mosaic_build_dir=$(base_dir)/build
mosaic_icarus_dir=$(base_dir)/icarus
mosaic_tb_dir=$(mosaic_dir)/Testbench
models_dir=$(base_dir)/models
perl_script_dir=$(base_dir)/tools/generate

# name of output pre-processed verilog file
PREPROC_VERILOG = $(vsrc_dir)/mosaic.preprocessed.sv
MOSAIC_PERL_INCUDE=-I$(perl_script_dir)

.PHONY: default build
default: build

build: $(perl_script_dir)/$(MOSAIC_PERL_SCRIPT).pl
	cd $(perl_script_dir) && \
	perl ./$(MOSAIC_PERL_SCRIPT).pl

clean:
	rm -rf $(PREPROC_VERILOG)
	rm -rf ${base_dir}
#	rm -rf $(mosaic_icarus_dir)/file_list.txt
#	-cd $(perl_script_dir) && ./clean_up.sh
