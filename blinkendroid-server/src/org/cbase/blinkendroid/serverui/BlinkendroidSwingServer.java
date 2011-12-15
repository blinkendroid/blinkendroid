package org.cbase.blinkendroid.serverui;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.cbase.blinkendroid.BlinkendroidApp;
import org.cbase.blinkendroid.network.broadcast.ReceiverThread;
import org.cbase.blinkendroid.player.bml.BLMHeader;
import org.cbase.blinkendroid.player.bml.BLMManager;
import org.cbase.blinkendroid.player.image.ImageHeader;
import org.cbase.blinkendroid.player.image.ImageManager;
import org.cbase.blinkendroid.server.BlinkendroidServer;
import org.cbase.blinkendroid.server.ITouchEffect;
import org.cbase.blinkendroid.server.PlayerManager;
import org.cbase.blinkendroid.server.TicketManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BlinkendroidSwingServer {

	private static final Logger logger = LoggerFactory
			.getLogger(BlinkendroidSwingServer.class);

	// private static boolean JNOTIFY_FOUND = true;
	private ReceiverThread receiverThread;
	private TicketManager ticketManager;
	private BlinkendroidServer blinkendroidServer;

	private BLMManager blmManager;
	private ImageManager imageManager;
	private BlinkendroidFrame serverUI;

	private String moviesPath = null;
	private String imagesPath = null;

	// private int moviesWatchId = -1;
	// private int imagesWatchId = -1;

	public ImageManager getImageManager() {
		return imageManager;
	}

	public BLMManager getBlmManager() {
		return blmManager;
	}

	public BlinkendroidFrame getUI() {
		return serverUI;
	}

	public PlayerManager getPlayerManager() {
		if (blinkendroidServer != null) {
			return blinkendroidServer.getPlayerManager();
		}

		return null;
	}

	public void setPin(int pin) {
		if (pin < 0) {
			return;
		}

		ticketManager.setPin(pin);
		logger.info("Pin changed to " + pin);
	}

	public int getMaxClients() {
		return ticketManager.getMaxClients();
	}

	public void setUI(BlinkendroidFrame serverUI) {
		this.serverUI = serverUI;
	}

	public boolean isRunning() {
		if (blinkendroidServer != null) {
			return blinkendroidServer.isRunning();
		} else {
			return false;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame.setDefaultLookAndFeelDecorated(true);
				BlinkendroidSwingServer server = new BlinkendroidSwingServer();

				JFrame.setDefaultLookAndFeelDecorated(true);
				BlinkendroidFrame frame = new BlinkendroidFrame(server);
				server.setUI(frame);
				frame.setVisible(true);

				server.loadMedia();
			}
		});
	}

	public BlinkendroidSwingServer() {
		super();

		loadConfiguration();

		blmManager = new BLMManager();
		imageManager = new ImageManager();
		ticketManager = new TicketManager();
		ticketManager.setServerName("Blinkendroid Worldrecord Server");
		ticketManager.setMaxClients(200);

		// if (JNOTIFY_FOUND) {
		// createFsWatchers();
		// }
	}

	/**
	 * Loads server.properties, sets configured image and movie dir and checks
	 * for presence of JNotify native libraries
	 */
	private void loadConfiguration() {
		Properties serverProps = new Properties();
		URL propsUrl = ClassLoader
				.getSystemResource("org/cbase/blinkendroid/serverui/server.properties");

		if (propsUrl == null) {
			logger.error("Loading config failed");
			System.exit(1);
		}

		try {
			serverProps.load(propsUrl.openStream());
		} catch (IOException e) {
			logger.warn("load failed", e);
		}

		// Checking for JNotify libs
		// try {
		// int testId = JNotify.addWatch(".", JNotify.FILE_ANY, false, new
		// MediaDirListener());
		// JNotify.removeWatch(testId);
		// } catch (UnsatisfiedLinkError ule) {
		// logger
		// .error("Disabling JNotify functionality: "
		// + ule.getMessage());
		// JNOTIFY_FOUND = false;
		// } catch (Exception e) {
		// logger.warn("failed", e);
		// }

		// setting paths
		logger.debug("Configured Images path:"
				+ serverProps.getProperty("blinkendroid.images.path"));
		logger.debug("Configured Movies path:"
				+ serverProps.getProperty("blinkendroid.movies.path"));
		this.imagesPath = serverProps.getProperty("blinkendroid.images.path");
		this.moviesPath = serverProps.getProperty("blinkendroid.movies.path");
	}

	/**
	 * Creates JNotify listeners for movies and images directories
	 */
	// public void createFsWatchers() {
	// try {
	// MediaDirListener moviesListener = new MediaDirListener();
	// moviesWatchId = JNotify.addWatch(moviesPath, JNotify.FILE_ANY,
	// false, moviesListener);
	// } catch (JNotifyException e) {
	// logger.error("Unable to create JNotify watcher for movies: "
	// + e.getMessage());
	// logger.warn("failed", e);
	// } catch (UnsatisfiedLinkError ule) {
	// logger.error(ule.getMessage());
	// }
	//
	// try {
	// MediaDirListener imagesListener = new MediaDirListener();
	// imagesWatchId = JNotify.addWatch(imagesPath, JNotify.FILE_ANY,
	// false, imagesListener);
	// } catch (JNotifyException e) {
	// logger.error("Unable to create JNotify watcher for images: "
	// + e.getMessage());
	// logger.warn("failed", e);
	// }
	// }
	//
	// public void removeFsWatchers() throws JNotifyException {
	// JNotify.removeWatch(moviesWatchId);
	// JNotify.removeWatch(imagesWatchId);
	// }

	public void loadMedia() {
		loadMovies();
		loadImages();
	}

	private void loadImages() {
		logger.debug("Reloading Images");
		getImageManager().readImages(getUI(), imagesPath);
	}

	private void loadMovies() {
		logger.debug("Reloading Movies");
		getBlmManager().readMovies(getUI(), moviesPath);
	}

	public void switchMovie(BLMHeader movieHeader) {
		blinkendroidServer.switchMovie(movieHeader);
	}

	public void switchImage(ImageHeader imgHeader) {
		blinkendroidServer.switchImage(imgHeader);
	}

	public void switchEffect(ITouchEffect effect) {
		blinkendroidServer.setTouchEffect(effect);
	}

	public void start() {
		if (null == blinkendroidServer) {
			ticketManager.start();
			// start recieverthread
			receiverThread = new ReceiverThread(
					BlinkendroidApp.BROADCAST_ANNOUCEMENT_SERVER_PORT,
					BlinkendroidApp.CLIENT_BROADCAST_COMMAND);
			receiverThread.addHandler(ticketManager);
			receiverThread.start();

			blinkendroidServer = new BlinkendroidServer(
					BlinkendroidApp.BROADCAST_SERVER_PORT);
			blinkendroidServer.addConnectionListener(ticketManager);
			blinkendroidServer.addConnectionListener(getUI());

			blinkendroidServer.start();
		} else {
			stopServer();
		}
	}

	public void clip() {
		if (null != blinkendroidServer) {
			blinkendroidServer.clip();
		}
	}

	public void singleclip() {
		if (null != blinkendroidServer) {
			blinkendroidServer.singleclip();
		}
	}

	public void globalTimer() {
		if (null != blinkendroidServer)
			blinkendroidServer.toggleTimeThread();
	}

	public void mole() {
		if (null != blinkendroidServer)
			blinkendroidServer.toggleWhackaMole();
	}

	public void shutdown() {
		// // if (JNOTIFY_FOUND) {
		// // try {
		// // removeFsWatchers();
		// // } catch (JNotifyException e) {
		// // }
		// logger.warn("failed", e);
		// }
		//
		// stopServer();
	}

	private void stopServer() {
		logger.info("Shutting down");

		try {
			if (receiverThread != null) {
				receiverThread.shutdown();
				receiverThread = null;
			}

			if (blinkendroidServer != null) {
				blinkendroidServer.shutdown();
				blinkendroidServer = null;
			}

			ticketManager.reset();
		} catch (Exception e) {
			logger.warn("stopServer failed" + e, e);
		}
	}

	/**
	 * Recieves and handles Filesystem update notifications and triggers reload
	 * of media
	 */
	// private class MediaDirListener implements JNotifyListener {
	//
	// public long lastChange = 0;
	// private static final int DEFERRER_INTERVAL = 2000;
	// private static final int DEFERRER_SLEEP = 200;
	//
	// @Override
	// public void fileCreated(int wid, String dir, String filename) {
	// reloadMediaByWid(wid);
	// }
	//
	// @Override
	// public void fileDeleted(int wid, String dir, String filename) {
	// reloadMediaByWid(wid);
	// }
	//
	// @Override
	// public void fileModified(int wid, String dir, String filename) {
	// }
	//
	// @Override
	// public void fileRenamed(int wid, String dir, String origFilename,
	// String newFilename) {
	// reloadMediaByWid(wid);
	// }
	//
	// /**
	// * This method is called whenever changes occur the the watched
	// * directory and calls triggers the reload of the media files in a
	// * deferred way
	// *
	// * @param watchId
	// * id of updates dir
	// */
	// private void reloadMediaByWid(final int watchId) {
	//
	// long origLastChange = lastChange;
	// lastChange = System.currentTimeMillis();
	//
	// // skipping this update
	// if (System.currentTimeMillis() - origLastChange < DEFERRER_INTERVAL) {
	// return;
	// }
	//
	// logger.debug("Creating new deferred updater");
	//
	// new Thread() {
	// @Override
	// public void run() {
	// logger.debug("Deferrer running");
	// try {
	//
	// while (System.currentTimeMillis() - lastChange < DEFERRER_INTERVAL) {
	// Thread.sleep(DEFERRER_SLEEP);
	// }
	//
	// logger.debug("Reloading for wid: " + watchId);
	// if (watchId == BlinkendroidSwingServer.this.moviesWatchId) {
	// BlinkendroidSwingServer.this.loadMovies();
	// } else if (watchId == BlinkendroidSwingServer.this.imagesWatchId) {
	// BlinkendroidSwingServer.this.loadImages();
	// }
	// } catch (InterruptedException e) {
	// logger
	// .error("DeferrerThread was interrupted while sleeping", e);
	// }
	// }
	// }.start();
	//
	// }
	// }
}
