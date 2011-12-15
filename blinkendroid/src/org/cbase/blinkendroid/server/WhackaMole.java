package org.cbase.blinkendroid.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;

import org.cbase.blinkendroid.network.udp.BlinkendroidProtocol;
import org.cbase.blinkendroid.network.udp.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhackaMole extends Thread implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(WhackaMole.class);
    private boolean running = false;
    private int moleCounter;
    private int points;
    private Random random;
    private PlayerManager playerManager;
    private long startTime = 0;

    public WhackaMole(PlayerManager pM) {
	this.playerManager = pM;
    }

    @Override
    public synchronized void start() {
	moleCounter = 0;
	points = 0;
	random = new Random(System.currentTimeMillis());
	running = true;
	logger.info("Whackamole running");
	super.start();
    }

    @Override
    public void run() {
	float x;
	float y;
	int style;
	int sleep;
	int moleDuration;
	try {
	    startTime = System.currentTimeMillis();
	    do {

		if (moleCounter < 50)
		    moleDuration = random.nextInt((int) (5000 - moleCounter * 100));
		else
		    moleDuration = random.nextInt(1000);

		if (moleDuration < 500)
		    moleDuration = 500;

		try {
		    // sleep random time but a little less if moleCounter is
		    // high
		    if (moleCounter < 50)
			sleep = random.nextInt(10000 - moleCounter * 200);
		    else
			sleep = random.nextInt(500);

		    if (sleep < 500)
			sleep = 500;
		    Thread.sleep(sleep);
		} catch (InterruptedException ie) {
		    logger.error("WhackaMole interrupt fucked up", ie);
		}
		// send a mole to x=0.0-1.0 y=0.0-1.0
		x = random.nextFloat();
		y = random.nextFloat();
		// set different style
		style = random.nextInt(10);
		mole(x, y, style, moleCounter, moleDuration, points);
		moleCounter++;
	    } while (running);
	} catch (Exception e) {
	    logger.error("WhackaMole Thread crashed", e);
	}
    }

    public void mole(float x, float y, int style, int moleCounter, int duration, int points) {
	PlayerClient pc = playerManager.getPlayer(x, y);
	if (null != pc && null != pc.getBlinkenProtocol())
	    pc.getBlinkenProtocol().mole(style, moleCounter, duration, points);
    }

    public void shutdown() {
	logger.info("WhackaMole: initiating shutdown");
	running = false;
	interrupt();
	try {
	    join();
	} catch (final InterruptedException x) {
	}
	logger.info("WhackaMole shutdown");
    }

    public boolean isRunning() {
	return running;
    }

    public void handle(SocketAddress from, ByteBuffer in) throws IOException {
	int command = in.getInt();
	// PlayerClient pc = playerManager.getPlayerClientBySocketAddress(from);
	if (command == BlinkendroidProtocol.COMMAND_HITMOLE) {
	    points++;
	    logger.info("hitMole " + points);
	} else if (command == BlinkendroidProtocol.COMMAND_MISSEDMOLE) {
	    points--;
	    logger.info("missedMole " + points);
	    if (moleCounter > 5 && points == 0) {
		final long gameTime = System.currentTimeMillis() - startTime;
		logger.info("game over in " + gameTime / 1000.00);
		new Thread() {
		    @Override
					public void run() {
			try {
			    Thread.sleep(5000);
			    List<PlayerClient> clients = playerManager.getAllClients();
			    for (PlayerClient pc : clients) {
				if (null != pc && null != pc.getBlinkenProtocol())
				    pc.getBlinkenProtocol().mole(random.nextInt(10), moleCounter++, 1000 * 60,
					    (int) gameTime - 1);
			    }
			} catch (Exception e) {
			    logger.error("gameover failed", e);
			}
		    }
		}.start();
		shutdown();
	    }
	}
    }
}
