OUT = out
SOURCES = $(wildcard src/shell/*.java)
TESTS = $(wildcard tests/shell/*.java)
ARGS =

.PHONY: build run test clean

build:
	javac -encoding UTF-8 -d $(OUT) $(SOURCES)

run: build
	java -cp $(OUT) shell.Main $(ARGS)

test: build
	javac -encoding UTF-8 -cp $(OUT) -d $(OUT) $(TESTS)
	java -cp $(OUT) shell.ShellTest
	java -cp $(OUT) shell.ConfigTest

clean:
	rm -rf $(OUT)
	java -cp $(OUT) shell.VfsTest