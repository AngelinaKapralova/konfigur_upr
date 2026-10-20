OUT = out
SOURCES = $(wildcard src/shell/*.java)
TESTS = $(wildcard tests/shell/*.java)

.PHONY: build run test clean

build:
	javac -encoding UTF-8 -d $(OUT) $(SOURCES)

run: build
	java -cp $(OUT) shell.Main

test: build
	javac -encoding UTF-8 -cp $(OUT) -d $(OUT) $(TESTS)
	java -cp $(OUT) shell.ShellTest

clean:
	rm -rf $(OUT)