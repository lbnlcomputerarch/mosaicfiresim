#!/bin/bash

# Check if FIRESIM_ROOT is not defined
if [ -z "${FIRESIM_ROOT}" ]; then
  echo "Error: \$FIRESIM_ROOT is not defined."
  exit 1
fi

DIRS_TO_LN=$(ls src/main)

for dir in $DIRS_TO_LN; do
  ln -sf $(pwd)/src/main/${dir}/mosaic ${FIRESIM_ROOT}/sim/src/main/${dir}/.
done

