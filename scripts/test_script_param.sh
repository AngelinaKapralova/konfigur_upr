#!/bin/bash
cd "$(dirname "$0")/.." || exit 1
make -s build
java -cp out shell.Main --script scripts/startup_ok.txt