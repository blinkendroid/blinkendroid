package org.cbase.blinkendroid.serverui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.cbase.blinkendroid.network.ConnectionListener;
import org.cbase.blinkendroid.network.udp.ClientSocket;
import org.cbase.blinkendroid.player.bml.BLMHeader;
import org.cbase.blinkendroid.player.bml.BLMManager.BLMManagerListener;
import org.cbase.blinkendroid.player.image.ImageHeader;
import org.cbase.blinkendroid.player.image.ImageManager.ImageManagerListener;
import org.cbase.blinkendroid.server.ClipAroundEffect;
import org.cbase.blinkendroid.server.ITouchEffect;
import org.cbase.blinkendroid.server.InverseEffect;
import org.cbase.blinkendroid.serverui.BlinkendroidSwingServer;
import org.cbase.blinkendroid.server.PlayerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlinkendroidFrame extends JFrame implements ImageManagerListener, BLMManagerListener, ConnectionListener {

	private static final Logger logger = LoggerFactory.getLogger(BlinkendroidFrame.class);

	private static final long serialVersionUID = 1L;
	BlinkendroidSwingServer server = null;

	private final DefaultListModel clientListModel = new DefaultListModel();
	// Controls
	private JButton clipButton = null;
	private JButton globalTimerButton = null;
	private JButton moleButton = null;
	private JButton pinBtn = null;
	private JButton removeClient = null;
	private JButton singleclipButton = null;
	private JButton startStopButton = null;
	private JComboBox imagesList = null;
	private JComboBox moviesList = null;
	private JComboBox touchEffectsList = null;
	private JLabel clientsLbl = null;
	private JLabel imagesLbl = null;
	private JLabel moviesLbl = null;
	private JLabel pinLbl = null;
	private JLabel titleLbl = null;
	private JLabel touchEffectsLbl = null;
	private JList clientsList = null;
	private JScrollPane clientsListScrollPane = null;
	private JTextField pinTxt = null;
	Container[] startedControls = null;
	Container[] stoppedControls = null;

	public void imagesReady() { // TODO: this was synchronized which blocked initializion
		// JOptionPane.showMessageDialog(this, "Images Ready");
		DefaultComboBoxModel imgCbModel = (DefaultComboBoxModel) imagesList.getModel();
		imgCbModel.removeAllElements();
		for (ImageHeader imgHead : server.getImageManager().getImageHeader())
			imgCbModel.addElement(imgHead);
	}

	public void moviesReady() { // TODO: this was synchronized which blocked initializion
		DefaultComboBoxModel movCbModel = (DefaultComboBoxModel) moviesList.getModel();
		movCbModel.removeAllElements();
		for (BLMHeader movHead : server.getBlmManager().getBlmHeader())
			movCbModel.addElement(movHead);
	}

	public void connectionClosed(ClientSocket clientSocket) {
		synchronized (clientListModel) {
			PlayerClient p = server.getPlayerManager().getPlayerClientByClientSocket(clientSocket);
			clientListModel.removeElement(p);
		}
	}

	public void connectionOpened(ClientSocket clientSocket) {
		synchronized (clientListModel) {
			PlayerClient p = server.getPlayerManager().getPlayerClientByClientSocket(clientSocket);
			clientListModel.addElement(p);
		}
	}

	/**
	* This is the default constructor
	*
	* @param server
	*/
	public BlinkendroidFrame(BlinkendroidSwingServer server) {
		super();
		this.server = server;
		initialize();
		repaint();
		pack();
		setVisible(true);
	}

	private void initializeEffects() {
		if(server.getPlayerManager() != null) {
			ITouchEffect[] effects = new ITouchEffect [] {
				new InverseEffect(server.getPlayerManager()),
				new ClipAroundEffect(server.getPlayerManager()),
				};

			((DefaultComboBoxModel)touchEffectsList.getModel()).removeAllElements();

			for(ITouchEffect effect : effects) {
				((DefaultComboBoxModel)touchEffectsList.getModel()).addElement(effect);
			}
		}
	}

	/**
	* This method initializes this
	*
	* @return void
	*/
	private void initialize() {

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(new MainWindowListener());
		this.setTitle("Blinkendroid SwingUI");
		this.setPreferredSize(new Dimension(400, 500));
		this.setResizable(false);

		ActionListener actionListener = new FormActionListener();

		clientsLbl = new JLabel("Clients: ");
		clientsLbl.setLocation(10, 120);
		clientsLbl.setSize(100, 20);
		clientsList = new JList(clientListModel);
		clientsListScrollPane = new JScrollPane(clientsList);
		clientsListScrollPane.setLocation(120, 120);
		clientsListScrollPane.setSize(200, 170);
		clipButton = new JButton("tile");
		clipButton.addActionListener(actionListener);
		clipButton.setActionCommand(Commands.CLIP.toString());
		clipButton.setLocation(20, 380);
		clipButton.setSize(80, 30);
		globalTimerButton = new JButton("stop timer");
		globalTimerButton.addActionListener(actionListener);
		globalTimerButton.setActionCommand(Commands.GLOBALTIMER.toString());
		globalTimerButton.setLocation(220, 380);
		globalTimerButton.setSize(120, 30);
		imagesLbl = new JLabel("Images:");
		imagesLbl.setLocation(10, 60);
		imagesLbl.setSize(100, 20);
		imagesList = new JComboBox(new String[] { "Images" });
		imagesList.addActionListener(actionListener);
		imagesList.setActionCommand(Commands.IMAGES_SELECTION.toString());
		imagesList.setLocation(120, 60);
		imagesList.setSize(200, 20);
		moleButton = new JButton("start mole");
		moleButton.addActionListener(actionListener);
		moleButton.setActionCommand(Commands.MOLE.toString());
		moleButton.setLocation(20, 430);
		moleButton.setSize(120, 30);
		moviesLbl = new JLabel("Movies:");
		moviesLbl.setLocation(10, 30);
		moviesLbl.setSize(100, 20);
		moviesList = new JComboBox(new String[] { "Movies" });
		moviesList.addActionListener(actionListener);
		moviesList.setActionCommand(Commands.MOVIES_SELECTION.toString());
		moviesList.setLocation(120, 30);
		moviesList.setSize(200, 20);
		pinBtn = new JButton("set");
		pinBtn.addActionListener(actionListener);
		pinBtn.setActionCommand(Commands.REFRESH_TICKET.toString());
		pinBtn.setLocation(230, 300);
		pinBtn.setSize(60, 30);
		removeClient = new JButton("-");
		removeClient.addActionListener(actionListener);
		removeClient.setActionCommand(Commands.REMOVE_CLIENT.toString());
		removeClient.setLocation(325, 260);
		removeClient.setSize(50, 30);
		singleclipButton = new JButton("replicate");
		singleclipButton.addActionListener(actionListener);
		singleclipButton.setActionCommand(Commands.SINGLECLIP.toString());
		singleclipButton.setLocation(110, 380);
		singleclipButton.setSize(100, 30);
		startStopButton = new JButton("start Server");
		startStopButton.addActionListener(actionListener);
		startStopButton.setActionCommand(Commands.START_STOP.toString());
		startStopButton.setLocation(120, 340);
		startStopButton.setSize(200, 30);
		pinLbl = new JLabel("Pin: ");
		pinLbl.setLocation(10, 300);
		pinLbl.setSize(100, 20);
		pinTxt = new JTextField("1337");
		pinTxt.addFocusListener(new TicketFocusListener());
		pinTxt.setLocation(120, 300);
		pinTxt.setSize(100, 30);
		titleLbl = new JLabel("Blinkendroid SwingUI");
		titleLbl.setLocation(10, 0);
		titleLbl.setSize(150, 20);
		touchEffectsLbl = new JLabel("Touch Effects: ");
		touchEffectsLbl.setLocation(10, 90);
		touchEffectsLbl.setSize(100, 20);
		touchEffectsList = new JComboBox();
		touchEffectsList.addActionListener(actionListener);
		touchEffectsList.setActionCommand(Commands.EFFECT_CHANGED.toString());
		touchEffectsList.setLocation(120, 90);
		touchEffectsList.setSize(200, 20);

		Container jContentPane = getContentPane();
		jContentPane.setLayout(null);
		jContentPane.add(pinBtn);
		jContentPane.add(pinLbl);
		jContentPane.add(pinTxt);
		jContentPane.add(touchEffectsLbl);
		jContentPane.add(clientsLbl);
		jContentPane.add(titleLbl);
		jContentPane.add(moviesLbl);
		jContentPane.add(moviesList);
		jContentPane.add(imagesLbl);
		jContentPane.add(imagesList);
		jContentPane.add(touchEffectsList);
		jContentPane.add(clientsListScrollPane);
		jContentPane.add(startStopButton);
		jContentPane.add(clipButton);
		jContentPane.add(singleclipButton);
		jContentPane.add(globalTimerButton);
		jContentPane.add(moleButton);
		jContentPane.add(removeClient);

		singleclipButton.setEnabled(false);
		globalTimerButton.setEnabled(false);
		moleButton.setEnabled(false);
		removeClient.setEnabled(false);
		clipButton.setEnabled(false);


		startedControls = new Container[]{clipButton, globalTimerButton, moleButton, removeClient, singleclipButton, imagesList, moviesList, touchEffectsList, clientsList, clientsListScrollPane, };
		stoppedControls = new Container[]{pinTxt, pinBtn, };
		for (Container c: startedControls)
			c.setEnabled(false);
	}

	private enum Commands {
		START_STOP, MOVIES_SELECTION, IMAGES_SELECTION, REMOVE_CLIENT, CLIP, SINGLECLIP, GLOBALTIMER, MOLE, EFFECT_CHANGED, REFRESH_TICKET;
	}

	private class TicketFocusListener implements FocusListener {
		public void focusLost(FocusEvent e) {
			int pin = -1;
			try {
				pin = Integer.parseInt(pinTxt.getText());
			}
			catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(BlinkendroidFrame.this, "Invalid amount of tickets, using default");
			}
			server.setPin(pin);
			logger.info("pin set");
		}

		public void focusGained(FocusEvent e) {
		}
	}

	private class FormActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			switch (Commands.valueOf(e.getActionCommand())) {
				case START_STOP:
					if (server.isRunning()) {
						((JButton) e.getSource()).setText("start Server");
						for (Container c: stoppedControls)
							c.setEnabled(true);
						for (Container c: startedControls)
							c.setEnabled(false);
						server.stopServer();
						logger.info("server stoped");
					}
					else {
						((JButton) e.getSource()).setText("stop Server");
						for (Container c: stoppedControls)
							c.setEnabled(false);
						for (Container c: startedControls)
							c.setEnabled(true);
						server.start();
						initializeEffects();
						logger.info("server started");
					}
				break;
				case IMAGES_SELECTION:
					Object selectedImage = ((JComboBox) e.getSource()).getModel().getSelectedItem();
					if (selectedImage != null && server.isRunning() && selectedImage instanceof ImageHeader) {
						server.switchImage((ImageHeader) selectedImage);
						logger.info("image switched");
					}

				break;
				case MOVIES_SELECTION:
					Object selectedMovie = ((JComboBox) e.getSource()).getModel().getSelectedItem();
					if (selectedMovie != null && server.isRunning() && selectedMovie instanceof BLMHeader) {
						logger.info(((BLMHeader) selectedMovie).author);
						server.switchMovie((BLMHeader) selectedMovie);
						logger.info("movie switched");
					}
				break;
				case EFFECT_CHANGED:
					Object selectedEffect = ((JComboBox) e.getSource()).getModel().getSelectedItem();
					if (selectedEffect != null && server.isRunning() && selectedEffect instanceof ITouchEffect) {
						server.switchEffect((ITouchEffect)selectedEffect);
						logger.info("effect switched");
					}
				break;
				case REMOVE_CLIENT:
					int idx = clientsList.getSelectedIndex();
					if (idx >= 0) {
						Object o = clientListModel.get(idx);
						if (o != null && o instanceof PlayerClient) {
							PlayerClient p = (PlayerClient)o;
							logger.info("removing client " + p + " TODO: not fully implemented");
							//server.getPlayerManager().removeClientFromMatrix(p);
						}
					}
				break;
				case CLIP:
					server.clip();
					logger.info("switched to clip");
				break;
				case SINGLECLIP:
					server.singleclip();
					logger.info("switched to singleclip");
				break;
				case GLOBALTIMER:
					if (server.globalTimer())
						((JButton) e.getSource()).setText("stop timer");
					else
						((JButton) e.getSource()).setText("start timer");
					logger.info("timer toggled");
				break;
				case MOLE:
					if (server.mole())
						((JButton) e.getSource()).setText("stop mole");
					else
						((JButton) e.getSource()).setText("start mole");
					logger.info("mole toggled");
				break;
				case REFRESH_TICKET:
					int pin = Integer.parseInt(pinTxt.getText());
					server.setPin(pin);
					logger.info("pin set");
				break;
				default:
					logger.info("unknown command");

			}

		}

	}

	private class MainWindowListener implements WindowListener {


		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		public void windowClosing(WindowEvent e) {
			logger.info("Closing main window");
			BlinkendroidFrame.this.server.stopServer();
		}

		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
		}
	}
}
