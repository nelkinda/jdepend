.PHONY: all
all: build

.PHONY: build
build:
	./gradlew $@

.PHONY: wrapper
wrapper:
	./gradlew $@

.PHONY: clean
clean::
	./gradlew $@
