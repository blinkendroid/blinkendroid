#!/usr/bin/make -f
.PHONY: all run clean doc dist dbg gen

all: gen
	ant -emacs

run: all
	#ant -emacs run
	#java -cp lib/slf4j-api-1.6.1.jar:lib/slf4j-simple-1.6.1.jar:classes org.cbase.blinkendroid.serverui.BlinkendroidSwingServer > /tmp/3.log 2>&1
	java -jar bin/blinkenserver.jar

dbg:
	ant -emacs dbg


clean:
	ant -emacs clean
	find src -type l | xargs rm -v

gen:
	cd src/org/cbase/blinkendroid && ln -fs ../../../../../blinkendroid/src/org/cbase/blinkendroid/BlinkendroidApp.java .
	mkdir -p src/org/cbase/blinkendroid/network
	cd src/org/cbase/blinkendroid/network && ln -fs ../../../../../../blinkendroid/src/org/cbase/blinkendroid/network/ConnectionListener.java .
	mkdir -p src/org/cbase/blinkendroid/network/udp
	cd src/org/cbase/blinkendroid/network/udp && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/network/udp/ClientSocket.java .
	cd src/org/cbase/blinkendroid/network/udp && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/network/udp/UDPDirectConnection.java .
	cd src/org/cbase/blinkendroid/network/udp && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/network/udp/BlinkendroidServerProtocol.java .
	cd src/org/cbase/blinkendroid/network/udp && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/network/udp/BlinkendroidProtocol.java .
	cd src/org/cbase/blinkendroid/network/udp && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/network/udp/ClientSocket.java .
	cd src/org/cbase/blinkendroid/network/udp && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/network/udp/ConnectionState.java .
	cd src/org/cbase/blinkendroid/network/udp && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/network/udp/CommandHandler.java .
	cd src/org/cbase/blinkendroid/network/udp && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/network/udp/UDPServerProtocolManager.java .
	cd src/org/cbase/blinkendroid/network/udp && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/network/udp/UDPAbstractBlinkendroidProtocol.java .
	mkdir -p src/org/cbase/blinkendroid/network/broadcast
	cd src/org/cbase/blinkendroid/network/broadcast && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/network/broadcast/ReceiverThread.java .
	cd src/org/cbase/blinkendroid/network/broadcast && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/network/broadcast/IPeerHandler.java .
	mkdir -p src/org/cbase/blinkendroid/network/tcp
	cd src/org/cbase/blinkendroid/network/tcp && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/network/tcp/DataServer.java .
	cd src/org/cbase/blinkendroid/network/tcp && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/network/tcp/BlinkendroidDataServerProtocol.java .
	mkdir -p src/org/cbase/blinkendroid/server
	cd src/org/cbase/blinkendroid/server && ln -fs ../../../../../../blinkendroid/src/org/cbase/blinkendroid/server/ClipAroundEffect.java .
	cd src/org/cbase/blinkendroid/server && ln -fs ../../../../../../blinkendroid/src/org/cbase/blinkendroid/server/ITouchEffect.java .
	cd src/org/cbase/blinkendroid/server && ln -fs ../../../../../../blinkendroid/src/org/cbase/blinkendroid/server/InverseEffect.java .
	cd src/org/cbase/blinkendroid/server && ln -fs ../../../../../../blinkendroid/src/org/cbase/blinkendroid/server/BlinkendroidServer.java .
	cd src/org/cbase/blinkendroid/server && ln -fs ../../../../../../blinkendroid/src/org/cbase/blinkendroid/server/PlayerManager.java .
	cd src/org/cbase/blinkendroid/server && ln -fs ../../../../../../blinkendroid/src/org/cbase/blinkendroid/server/PlayerClient.java .
	cd src/org/cbase/blinkendroid/server && ln -fs ../../../../../../blinkendroid/src/org/cbase/blinkendroid/server/WhackaMole.java .
	cd src/org/cbase/blinkendroid/server && ln -fs ../../../../../../blinkendroid/src/org/cbase/blinkendroid/server/EffectManager.java .
	cd src/org/cbase/blinkendroid/server && ln -fs ../../../../../../blinkendroid/src/org/cbase/blinkendroid/server/TicketManager.java .
	cd src/org/cbase/blinkendroid/server && ln -fs ../../../../../../blinkendroid/src/org/cbase/blinkendroid/server/ClientQueueListener.java .
	mkdir -p src/org/cbase/blinkendroid/player/bml
	cd src/org/cbase/blinkendroid/player/bml && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/player/bml/BLMHeader.java .
	cd src/org/cbase/blinkendroid/player/bml && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/player/bml/BLMManager.java .
	mkdir -p src/org/cbase/blinkendroid/player/image
	cd src/org/cbase/blinkendroid/player/image && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/player/image/ImageHeader.java .
	cd src/org/cbase/blinkendroid/player/image && ln -fs ../../../../../../../blinkendroid/src/org/cbase/blinkendroid/player/image/ImageManager.java .

