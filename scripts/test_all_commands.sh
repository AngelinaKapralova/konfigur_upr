#!/bin/bash
cd "$(dirname "$0")/.." || exit 1
make -s build
java -cp out shell.Main --vfs vfs/deep --script scripts/startup_all.txt