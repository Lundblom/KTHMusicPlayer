import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import javax.swing.ImageIcon;

import java.io.*;

/**
 * A class to hold details of audio tracks. Individual tracks may be played.
 * 
 * @author Martin Eklund and Andreas Lundblom
 * @version 2014.05.15
 */
public class MusicOrganizer extends JFrame implements ListSelectionListener,
		ActionListener {

	// An ArrayList for storing music tracks.
	private ArrayList<Track> tracks, playlistTracks;
	// A player for the music tracks.
	private MusicPlayer player;
	// A reader that can read music files and load them as tracks.
	private TrackReader reader;
	// labels
	private JLabel infoLabel, playingLabel;
	// list for tracks to be shown, used for graphic
	private JList trackList, playlist;
	// helplist for the playlist. contains tracks to be listed in playlist. used
	// in graphic
	private DefaultListModel<Track> playlistHelpList, trackListHelpList;
	// selected track
	private Track selectedTrack;

	private TrackParser parser = new TrackParser();

	/**
	 * Create a MusicOrganizer
	 */
	public MusicOrganizer() {

		// Creates the necessary directories
		File f = new File(parser.PATH_NAME);

		boolean success = false;

		if (!f.exists()) {
			success = f.mkdirs();
			if (!success)
				System.out.println("Couldn't create directories");
		}

		tracks = new ArrayList<Track>();
		playlistTracks = new ArrayList<Track>();
		trackListHelpList = new DefaultListModel<Track>();
		player = new MusicPlayer();
		reader = new TrackReader();

		makeFrame();
	}

	/**
	 * Add a track file to the collection.
	 * 
	 * @param filename
	 *            The file name of the track to be added.
	 */
	public void addFile(String filename) {
		tracks.add(new Track(filename));
	}

	/**
	 * Add a track to the collection.
	 * 
	 * @param track
	 *            The track to be added.
	 */
	public void addTrack(Track track) {
		tracks.add(track);
	}

	/**
	 * Add a track to playlist.
	 * 
	 * @param track
	 *            The track to be added.
	 */
	public void addToPlaylist(Track track) {
		playlistTracks.add(track);
	}

	/**
	 * Remove a track to playlist.
	 * 
	 * @param track
	 *            The track to be Removed.
	 */
	public void removeFromPlaylist(Track track) {
		playlistTracks.remove(track);
	}

	/**
	 * Play the first track in the collection, if there is one.
	 */
	public void playFirst() {
		if (tracks.size() > 0) {
			selectedTrack = tracks.get(0);
			player.startPlaying(selectedTrack.getFilename());
			updatePlayingSong("Now playing: " + selectedTrack.getTitle());
		}
	}

	/**
	 * Play a track in the collection.
	 * 
	 * @param index
	 *            The index of the track to be played.
	 */
	public void playTrackIndex(int index) {
		if (indexValid(index)) {

			Track track = tracks.get(index);
			player.startPlaying(track.getFilename());
			updatePlayingSong("Now playing: " + track.getTitle());
		}
	}

	/**
	 * plays the specified track
	 * 
	 * @param track
	 *            the track to be played
	 */
	public void playTrack(Track track) {

		player.startPlaying(track.getFilename());
		updatePlayingSong("Now playing: " + track.getTitle());
	}

	/**
	 * Return the number of tracks in the collection.
	 * 
	 * @return The number of tracks in the collection.
	 */
	public int getNumberOfTracks() {
		return tracks.size();
	}

	/**
	 * List a track from the collection.
	 * 
	 * @param index
	 *            The index of the track to be listed.
	 */
	public void listTrack(int index) {
		System.out.print("Track " + index + ": ");
		Track track = tracks.get(index);
		System.out.println(track.getDetails());
	}

	/**
	 * Show a list of all the tracks in the collection.
	 */
	public ArrayList<String> getAllTracks() {
		ArrayList<String> trackList = new ArrayList<String>();
		for (Track track : tracks) {
			trackList.add(track.getDetails() + "\n");
		}

		return trackList;
	}

	/**
	 * Show a list of all the tracks in the collection.
	 */
	public ArrayList<String> getAllTracksPlaylist() {
		ArrayList<String> trackList = new ArrayList<String>();
		for (Track track : playlistTracks) {
			trackList.add(track.getDetails() + "\n");
		}

		return trackList;
	}

	/**
	 * List all tracks by the given artist.
	 * 
	 * @param artist
	 *            The artist's name.
	 */
	public void listByArtist(String artist) {
		for (Track track : tracks) {
			if (track.getArtist().contains(artist)) {
				System.out.println(track.getDetails());
			}
		}
	}

	/**
	 * Remove a track from the collection.
	 * 
	 * @param index
	 *            The index of the track to be removed.
	 */
	public void removeTrack(int index) {
		if (indexValid(index)) {
			tracks.remove(index);
		}
	}

	/**
	 * Stop the player.
	 */
	public void stopPlaying() {
		updatePlayingSong("");
		player.stop();
	}

	/**
	 * Determine whether the given index is valid for the collection. Print an
	 * error message if it is not.
	 * 
	 * @param index
	 *            The index to be checked.
	 * @return true if the index is valid, false otherwise.
	 */
	private boolean indexValid(int index) {
		// The return value.
		// Set according to whether the index is valid or not.
		boolean valid;

		if (index < 0) {
			System.out.println("Index cannot be negative: " + index);
			valid = false;
		} else if (index >= tracks.size()) {
			System.out.println("Index is too large: " + index);
			valid = false;
		} else {
			valid = true;
		}
		return valid;
	}

	private void addFolderToLibrary(String folderName) {

		ArrayList<Track> tempTracks = reader.readTracks(folderName, ".mp3");
		for (Track track : tempTracks) {
			addTrack(track);
			trackListHelpList.addElement(track);
		}
	}

	private void addFileToLibrary(String filePath) {

		Track addedTrack = reader.readTrack(filePath);
		addTrack(addedTrack);
		trackListHelpList.addElement(addedTrack);

	}

	/**
	 * returns array of all available tracks
	 */
	private Track[] getTrackList() {

		Track[] arraytrack = tracks.toArray(new Track[tracks.size()]);
		return arraytrack;
	}

	/**
	 * Load playlist from a file
	 * 
	 * @param name
	 *            Name of the playlist file
	 * 
	 */
	private ArrayList<Track> loadPlayList(String name) {
		return parser.readList(name);
	}

	/**
	 * Save playlist to file
	 * 
	 * @param name
	 *            Name of the playlist file
	 */

	private void savePlayList(String name) {
		parser.saveList(playlistTracks, name);
	}

	/**
	 * Graphics
	 */
	private void makeFrame() {

		// Color scheme
		Color background = new Color(48, 48, 48);
		Color brighterBackground = new Color(64, 64, 64); // used to get a
															// little contrast
															// on buttons
		Color foreground = new Color(100, 200, 0);

		// frame
		final JFrame frame = new JFrame("MusicOrganizer");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Container contentPane = frame.getContentPane();
		contentPane.setBackground(background);
		contentPane.setLayout(new BorderLayout());

		// Jpanels
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(background);
		buttonPanel.setLayout(new FlowLayout());

		JPanel listPanel1 = new JPanel();
		listPanel1.setBackground(background);
		listPanel1.setLayout(new BorderLayout());
		listPanel1.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		

		JPanel listPanel2 = new JPanel();
		listPanel2.setBackground(background);
		listPanel2.setLayout(new BorderLayout());
		listPanel2.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		// Menubar
		{
			// File menu
			JMenuBar mb = new JMenuBar();
			JMenu file = new JMenu("File");
			JMenu help = new JMenu("Help");
			mb.add(file);
			mb.add(help);

			// simple help text menubutton
			final JMenuItem getHelp = new JMenuItem("Help");
			getHelp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JOptionPane
							.showMessageDialog(
									frame,
									"Press file in the top menu to add music from a file or folder. "
											+ "\n You can also save and load playlists."
											+ "\n\n The left list (the library list) contains all the tracks that have been imported into the musicplayer."
											+ "\n The right list is the playlist, to which you can add and remove tracks that are in the librarylist.");
				}
			});
			help.add(getHelp);

			// about menubutton
			JMenuItem about = new JMenuItem("About...");
			about.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(frame,
							"Made by:\n Martin Eklund \n Andreas Lundblom");
				}
			});
			help.add(about);

			// add folder or file menubutton
			final JMenuItem addFolder = new JMenuItem("Add file or folder...");
			final JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			addFolder.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					int returnVal = fc.showOpenDialog(addFolder);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						try {
							addFolderToLibrary(file.toString());
						} catch (Exception ex) {
							addFileToLibrary(file.getPath());

						} finally {
							updateInfoLabel();
						}

					}

				}
			});
			file.add(addFolder);

			// save playlist menubutton
			JMenuItem savePlaylist = new JMenuItem("Save playlist");
			savePlaylist.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					String s = (String) JOptionPane.showInputDialog(frame,
							"Enter playlist name: ", null);

					if (s == null || s.equals(""))
						return;

					savePlayList(s + ".xml");

				}
			});

			file.add(savePlaylist);

			// load playlist menubutton
			final JMenuItem loadPlaylist = new JMenuItem("Load playlist");
			String path = (System.getenv("APPDATA") + "/" + ".KTHMusic");
			final JFileChooser fc2 = new JFileChooser(path);
			fc2.setFileSelectionMode(JFileChooser.FILES_ONLY);

			loadPlaylist.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {

					int returnVal = fc2.showOpenDialog(loadPlaylist);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc2.getSelectedFile();
						try {
							// clear old playlist out of the way
							playlistTracks.clear();
							playlistHelpList.clear();
							// read the new playlist
							ArrayList<Track> temp = new ArrayList<Track>(
									loadPlayList(file.getName()));
							System.out.println(temp);
							for (Track track : temp) {
								System.out.println(track.getFilename());
								addFileToLibrary(track.getFilename());
								addToPlaylist(track);
								playlistHelpList.addElement(track);

							}
						} catch (Exception ex) {

						}

					}
				}
			});
			file.add(loadPlaylist);

			// exit menubutton
			JMenuItem exit = new JMenuItem("Exit");
			exit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					System.exit(0);
				}
			});
			file.add(exit);

			frame.setJMenuBar(mb);

		}

		// Labels
		{
			infoLabel = new JLabel();
			updateInfoLabel();
			infoLabel.setForeground(foreground);

			playingLabel = new JLabel("Nothing playing");
			playingLabel.setForeground(foreground);

		}
		// imported tracks list
		{
			trackListHelpList = new DefaultListModel<Track>();
			for (Track track : getTrackList())
				trackListHelpList.addElement(track);

			trackList = new JList(trackListHelpList);
			trackList.setForeground(foreground);
			trackList.setBackground(brighterBackground);
			trackList.setCellRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList<?> list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					Component renderer = super.getListCellRendererComponent(
							list, value, index, isSelected, cellHasFocus);
					if (renderer instanceof JLabel && value instanceof Track) {
						// only show artist and Title of tracks
						((JLabel) renderer).setText(((Track) value).getArtist()
								+ ((Track) value).getTitle());
					}
					return renderer;
				}
			});

			trackList
					.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			JScrollPane jsp = new JScrollPane(trackList);
			jsp.setPreferredSize(new Dimension(400, 200));
			jsp.setBorder(null);
			listPanel1.add(jsp, BorderLayout.CENTER);
			trackList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					selectedTrack = (Track) trackList.getSelectedValue();
				}

			});
		}

		// Playlist track list
		{
			playlistHelpList = new DefaultListModel<Track>();
			playlist = new JList(playlistHelpList);
			playlist.setForeground(foreground);
			playlist.setBackground(brighterBackground);
			playlist.setCellRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList<?> list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					Component renderer = super.getListCellRendererComponent(
							list, value, index, isSelected, cellHasFocus);
					if (renderer instanceof JLabel && value instanceof Track) {
						// only show artist and Title of tracks
						((JLabel) renderer).setText(((Track) value).getArtist()
								+ ((Track) value).getTitle());
					}
					return renderer;
				}
			});
			playlist.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

			JScrollPane jsp2 = new JScrollPane(playlist);
			jsp2.setBorder(null);
			jsp2.setPreferredSize(new Dimension(400, 200));
			listPanel2.add(jsp2, BorderLayout.CENTER);
			playlist.addListSelectionListener(this);
			playlist.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					selectedTrack = (Track) playlist.getSelectedValue();
				}

			});
		}

		// Stop button
		{
			ImageIcon stopButtonIcon = new ImageIcon("stop.png");
			JButton stopButton = new JButton("", stopButtonIcon);
			stopButton.setOpaque(false);
			stopButton.setContentAreaFilled(false);
			stopButton.setBorderPainted(false);
			buttonPanel.add(stopButton);
			stopButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					stopPlaying();
				}
			});
		}

		// Play button
		{
			ImageIcon playButtonIcon = new ImageIcon("play.png");
			JButton playButton = new JButton("", playButtonIcon);
			playButton.setOpaque(false);
			playButton.setContentAreaFilled(false);
			playButton.setBorderPainted(false);
			buttonPanel.add(playButton);
			playButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					stopPlaying(); // stop playing old track before playing new
									// track
					if (selectedTrack == null)
						playFirst();
					else
						playTrack(selectedTrack);
				}
			});
		}

		// add to playlist button
		{
			JButton addToPlaylistButton = new JButton("Add to playlist");
			addToPlaylistButton.setBorderPainted(false);
			addToPlaylistButton.setForeground(foreground);
			addToPlaylistButton.setBackground(brighterBackground);
			buttonPanel.add(addToPlaylistButton);
			addToPlaylistButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectedTrack = (Track) trackList.getSelectedValue();
					addToPlaylist(selectedTrack);
					playlistHelpList.addElement(selectedTrack);

				}
			});
		}

		// remove from playlist button
		{
			JButton removeFromPlaylistButton = new JButton(
					"Remove from playlist");
			removeFromPlaylistButton.setBorderPainted(false);
			removeFromPlaylistButton.setForeground(foreground);
			removeFromPlaylistButton.setBackground(brighterBackground);
			buttonPanel.add(removeFromPlaylistButton);
			removeFromPlaylistButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectedTrack = (Track) playlist.getSelectedValue();
					removeFromPlaylist(selectedTrack);
					playlistHelpList.removeElement(selectedTrack);

				}
			});
		}
		// Next button
		{
			JButton nextInPlaylistButton = new JButton("Next");
			nextInPlaylistButton.setBorderPainted(false);
			nextInPlaylistButton.setForeground(foreground);
			nextInPlaylistButton.setBackground(brighterBackground);
			buttonPanel.add(nextInPlaylistButton);
			nextInPlaylistButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int currentIndex = playlistTracks.indexOf(selectedTrack);
					try {
						selectedTrack = playlistTracks.get(currentIndex + 1);
					} catch (IndexOutOfBoundsException d) {
						selectedTrack = playlistTracks.get(0);
					}
					stopPlaying(); // stop playing old track before playing new
					// track
					playTrack(selectedTrack);

				}
			});
		}
		// Previous button
		{
			JButton previousInPlaylistButton = new JButton("Previous");
			previousInPlaylistButton.setBorderPainted(false);
			previousInPlaylistButton.setForeground(foreground);
			previousInPlaylistButton.setBackground(brighterBackground);
			buttonPanel.add(previousInPlaylistButton);
			previousInPlaylistButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int currentIndex = playlistTracks.indexOf(selectedTrack);
					try {
						selectedTrack = playlistTracks.get(currentIndex - 1);
					} catch (IndexOutOfBoundsException d) {
						selectedTrack = playlistTracks.get(playlistTracks
								.size() - 1);
					}
					stopPlaying(); // stop playing old track before playing new
					// track
					playTrack(selectedTrack);

				}
			});
		}

		// add labels to panels
		JLabel libraryLabel = new JLabel("Library");
		libraryLabel.setForeground(foreground);
		JLabel playlistLabel = new JLabel("Playlist");
		playlistLabel.setForeground(foreground);
		listPanel1.add(infoLabel, BorderLayout.PAGE_START);
		listPanel2.add(playingLabel, BorderLayout.PAGE_START);
		listPanel1.add(libraryLabel, BorderLayout.PAGE_END);
		listPanel2.add(playlistLabel, BorderLayout.PAGE_END);
		
		
		// add the different panels
		contentPane.add(buttonPanel, BorderLayout.PAGE_END);
		contentPane.add(listPanel1, BorderLayout.LINE_START);
		contentPane.add(listPanel2, BorderLayout.LINE_END);

		frame.pack();
		frame.setVisible(true);

	}

	private void updateInfoLabel() {
		infoLabel.setText("Music library loaded.  " + getNumberOfTracks()
				+ " tracks.");
	}

	private void updatePlayingSong(String playingSong) {
		playingLabel.setText(playingSong);
	}

	public static void main(String[] arg) {
		new MusicOrganizer();
	}

	public void actionPerformed(ActionEvent evt) {
	}

	public void valueChanged(ListSelectionEvent e) {
	}

}
