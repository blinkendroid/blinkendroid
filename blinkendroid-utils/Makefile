#!/usr/bin/make -f
.PHONY: all run clean doc dist dbg gen

all: gen
	ant -emacs

clean:
	ant -emacs clean
	find src -type l | xargs rm -v

run:
	ant -emacs run

dbg:
	ant -emacs dbg

gen:
	find src -type l | xargs rm -vf
	mkdir -p src/org/cbase/blinkendroid/player/bml
	cd src/org/cbase/blinkendroid/player/bml && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/player/bml/BLM.java .
	cd src/org/cbase/blinkendroid/player/bml && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/player/bml/BLMHeader.java .
	cd src/org/cbase/blinkendroid/player/bml && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/player/bml/BMLParser.java .

