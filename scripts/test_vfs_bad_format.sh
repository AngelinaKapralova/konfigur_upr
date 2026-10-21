#!/bin/bash
cd "$(dirname "$0")/.." || exit 1
make -s build
java -cp out shell.Main --vfs vfs/minimal/hello.txt \
    --script scripts/startup_vfs_info.txt