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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlinkendroidFrame extends JFrame implements ImageManagerListener,
	BLMManagerListener, ConnectionListener {

	private static final Logger logger = LoggerFactory.getLogger(BlinkendroidFrame.class);

    private static final long serialVersionUID = 1L;
    BlinkendroidSwingServer server;

    // Controls
    private JButton startStopButton = null;
    private JButton singleclipButton = null;
    private JButton clipButton = null;
    private JButton globalTimerButton = null;
    private JButton moleButton = null;
    private JButton removeClient = null;
    private JComboBox moviesList = null;
    private JComboBox imagesList = null;
    private JComboBox touchEffectsList = null;
    private final DefaultListModel clientListModel = new DefaultListModel();
    private JLabel titleLbl, moviesLbl, imagesLbl, clientsLbl, ticketsLbl, touchEffectsLbl;
    private JList clientsList;
    private JTextField ticketsTxt;
    private JButton refreshTickets = null;

    public synchronized void imagesReady() {
	// JOptionPane.showMessageDialog(this, "Images Ready");

	DefaultComboBoxModel imgCbModel = (DefaultComboBoxModel) imagesList
		.getModel();
	imgCbModel.removeAllElements();

	for (ImageHeader imgHead : server.getImageManager().getImageHeader()) {
	    imgCbModel.addElement(imgHead);
	}
    }

    public synchronized void moviesReady() {
	DefaultComboBoxModel movCbModel = (DefaultComboBoxModel) moviesList
		.getModel();
	movCbModel.removeAllElements();

	for (BLMHeader movHead : server.getBlmManager().getBlmHeader()) {
	    movCbModel.addElement(movHead);
	}
    }

    public void connectionClosed(ClientSocket clientSocket) {
	synchronized (clientListModel) {
	    clientListModel.removeElement(clientSocket);
	}
    }

    public void connectionOpened(ClientSocket clientSocket) {
	synchronized (clientListModel) {
	    clientListModel.addElement(clientSocket);
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
	    ITouchEffect[] effects = new ITouchEffect [] {new InverseEffect(server.getPlayerManager()),
		    new ClipAroundEffect(server.getPlayerManager())};
	    
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

	titleLbl = new JLabel("Blinkendroid SwingUI");
	moviesLbl = new JLabel("Movies:");
	imagesLbl = new JLabel("Images:");
	touchEffectsLbl = new JLabel("Touch Effects: ");
	clientsLbl = new JLabel("Clients: ");
	ticketsLbl = new JLabel("Pin: ");
	ticketsTxt = new JTextField("1337");
	refreshTickets = new JButton("set");

	clientsList = new JList(clientListModel);
	JScrollPane clientsListScrollPane = new JScrollPane(clientsList);

	ActionListener actionListener = new FormActionListener();
	imagesList = new JComboBox(new String[] { "Images" });
	imagesList.setActionCommand(Commands.IMAGES_SELECTION.toString());
	imagesList.addActionListener(actionListener);
	moviesList = new JComboBox(new String[] { "Movies" });
	moviesList.setActionCommand(Commands.MOVIES_SELECTION.toString());
	moviesList.addActionListener(actionListener);
	touchEffectsList = new JComboBox();
	touchEffectsList.setActionCommand(Commands.EFFECT_CHANGED.toString());
	touchEffectsList.addActionListener(actionListener);

	startStopButton = new JButton("start Server");
	startStopButton.setActionCommand(Commands.START_STOP.toString());
	startStopButton.addActionListener(actionListener);

	clipButton = new JButton("clip");
	clipButton.setActionCommand(Commands.CLIP.toString());
	clipButton.addActionListener(actionListener);

	singleclipButton = new JButton("1clip");
	singleclipButton.setActionCommand(Commands.SINGLECLIP.toString());
	singleclipButton.addActionListener(actionListener);

	globalTimerButton = new JButton("GTimer");
	globalTimerButton.setActionCommand(Commands.GLOBALTIMER.toString());
	globalTimerButton.addActionListener(actionListener);

	moleButton = new JButton("Whacka");
	moleButton.setActionCommand(Commands.MOLE.toString());
	moleButton.addActionListener(actionListener);

	removeClient = new JButton("-");
	removeClient.setActionCommand(Commands.REMOVE_CLIENT.toString());
	removeClient.addActionListener(actionListener);

	ticketsTxt.addFocusListener(new TicketFocusListener());

	this.setPreferredSize(new Dimension(350, 500));
	this.setResizable(false);

	Container jContentPane = getContentPane();
	jContentPane.setLayout(null);

	titleLbl.setLocation(0, 0);
	titleLbl.setSize(150, 20);

	moviesLbl.setLocation(0, 30);
	moviesLbl.setSize(100, 20);

	imagesLbl.setLocation(0, 60);
	imagesLbl.setSize(100, 20);

	touchEffectsLbl.setLocation(0, 90);
	touchEffectsLbl.setSize(100, 20);
	
	clientsLbl.setLocation(0, 120);
	clientsLbl.setSize(100, 20);

	ticketsLbl.setLocation(0, 300);
	ticketsLbl.setSize(100, 20);

	moviesList.setLocation(120, 30);
	moviesList.setSize(200, 20);

	imagesList.setLocation(120, 60);
	imagesList.setSize(200, 20);

	touchEffectsList.setLocation(120, 90);
	touchEffectsList.setSize(200, 20);
	
	clientsListScrollPane.setLocation(120, 120);
	clientsListScrollPane.setSize(200, 170);

	removeClient.setLocation(325, 270);
	removeClient.setSize(20, 20);

	ticketsTxt.setLocation(120, 300);
	ticketsTxt.setSize(100, 30);

	refreshTickets.setLocation(230, 305);
	refreshTickets.setSize(60, 20);

	startStopButton.setLocation(120, 340);
	startStopButton.setSize(200, 30);

	clipButton.setLocation(20, 380);
	clipButton.setSize(80, 30);

	singleclipButton.setLocation(110, 380);
	singleclipButton.setSize(80, 30);

	globalTimerButton.setLocation(200, 380);
	globalTimerButton.setSize(80, 30);

	moleButton.setLocation(20, 430);
	moleButton.setSize(80, 30);

	jContentPane.add(refreshTickets);
	jContentPane.add(ticketsLbl);
	jContentPane.add(ticketsTxt);
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
    }

    protected void shutdown() {
	logger.info("Closing main window");
	server.shutdown();
    }

    private enum Commands {
	START_STOP, MOVIES_SELECTION, IMAGES_SELECTION, REMOVE_CLIENT, CLIP, SINGLECLIP, GLOBALTIMER, MOLE, EFFECT_CHANGED;
    }

    private class TicketFocusListener implements FocusListener {

	public void focusLost(FocusEvent e) {
	    int maxClients = -1;

	    try {
		maxClients = Integer.parseInt(ticketsTxt.getText());
	    } catch (NumberFormatException nfe) {
		JOptionPane.showMessageDialog(BlinkendroidFrame.this,
			"Invalid amount of tickets, using default");
	    }

	    server.setPin(maxClients);
	}

	public void focusGained(FocusEvent e) {
	}
    }

    private class FormActionListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {

	    switch (Commands.valueOf(e.getActionCommand())) {
	    case START_STOP:
		if (!server.isRunning()) {
		    ((JButton) e.getSource()).setText("stop Server");
		    server.start();
		    initializeEffects();
		} else {
		    ((JButton) e.getSource()).setText("start Server");
		    server.start();
		}
		break;
	    case IMAGES_SELECTION:
		Object selectedImage = ((JComboBox) e.getSource()).getModel()
			.getSelectedItem();

		if (selectedImage == null || !server.isRunning()) {
		    return;
		}

		if (selectedImage instanceof ImageHeader) {
		    server.switchImage((ImageHeader) selectedImage);
		}

		break;
	    case MOVIES_SELECTION:
		Object selectedMovie = ((JComboBox) e.getSource()).getModel()
			.getSelectedItem();

		if (selectedMovie == null || !server.isRunning()) {
		    return;
		}

		if (selectedMovie instanceof BLMHeader) {
		    logger.info(((BLMHeader) selectedMovie).author);
		    server.switchMovie((BLMHeader) selectedMovie);
		}
		break;
	    case EFFECT_CHANGED:
		logger.info("Effect changed start");
		Object selectedEffect = ((JComboBox) e.getSource()).getModel().getSelectedItem();
		
		if(selectedEffect == null || !server.isRunning()) {
		    return;
		}
		
		if(selectedEffect instanceof ITouchEffect) {
		    logger.info("Switching effect");
		    server.switchEffect((ITouchEffect)selectedEffect);
		}
		break;
	    case REMOVE_CLIENT:

		if (clientsList.getSelectedIndex() == -1) {
		    return;
		}

		Object selectedClient = clientListModel.get(clientsList
			.getSelectedIndex());

		if (selectedClient == null
			|| !(selectedClient instanceof ClientSocket)) {
		    return;
		}

		// TODO: Missing call to something which actually removed the
		// client

		break;
	    case CLIP:
		server.clip();
		break;
	    case SINGLECLIP:
		server.singleclip();
		break;
	    case GLOBALTIMER:
		server.globalTimer();
		break;
	    case MOLE:
		server.mole();
		break;

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
	    BlinkendroidFrame.this.shutdown();
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
