import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import javax.swing.ImageIcon;
import javax.swing.filechooser.*;
import java.io.*;

/**
 * A class to hold details of audio tracks. Individual tracks may be played.
 * 
 * @author David J. Barnes and Michael KÃ¶lling
 * @version 2011.07.31
 */
public class MusicOrganizer extends JFrame implements ListSelectionListener,
		ActionListener {

	// An ArrayList for storing music tracks.
	private ArrayList<Track> tracks, playlistTracks;
	// A player for the music tracks.
	private MusicPlayer player;
	// A reader that can read music files and load them as tracks.
	private TrackReader reader;

	private JLabel infoLabel, playingLabel;
	// list for tracks to be shown
	private JList trackList, playlist;
	// helplist for the playlist. contains tracks to be listed in playlist.
	private DefaultListModel<Track> playlistHelpList, trackListHelpList;
	// index of selected track in list
	private int selectedTrackIndex;
	// selected track
	private Track selectedTrack;

	/**
	 * Create a MusicOrganizer
	 */
	public MusicOrganizer() {
		tracks = new ArrayList<Track>();
		playlistTracks = new ArrayList<Track>();
		trackListHelpList = new DefaultListModel<Track>();
		player = new MusicPlayer();
		reader = new TrackReader();

		readLibrary("F:\\Musik\\Led Zeppelin - Greatest hits\\CD 2");

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
			selectedTrack=tracks.get(0);
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

	private void readLibrary(String folderName) {
		ArrayList<Track> tempTracks = reader.readTracks(folderName, ".mp3");

		// Put all thetracks into the organizer.
		for (Track track : tempTracks) {

			addTrack(track);
		}
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
	 * draw stuff
	 */
	private void makeFrame() {

		// Color scheme
		Color background = new Color(48, 48, 48);
		Color brighterBackground = new Color(64, 64, 64); // used to get a
															// little contrast
															// on buttons
		Color foreground = new Color(100, 200, 0);

		// frame
		JFrame frame = new JFrame("MusicOrganizer");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new FlowLayout());
		contentPane.setBackground(background);

		// Menubar
		{
			JMenuBar mb = new JMenuBar();
			JMenu file = new JMenu("File");
			mb.add(file);
			final JMenuItem addFolder = new JMenuItem("Add file or folder...");
			final JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			addFolder.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					int returnVal = fc.showOpenDialog(addFolder);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						System.out.println(file);
						System.out.println(file.getParent());
						System.out.println(file.getName());
						try {
							addFolderToLibrary(file.toString());
						} catch (Exception ex) {
							// Lägg till en fil, fixa här
							addFileToLibrary(file.getPath());

						} finally {
							updateInfoLabel();
						}

					}

				}
			});
			file.add(addFolder);

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
			contentPane.add(infoLabel);

			int width = 200;
			int height = 20;
			playingLabel = new JLabel();
			playingLabel.setForeground(foreground);
			playingLabel.setPreferredSize(new Dimension(width, height));

		}
		// imported tracks list
		{
			trackListHelpList = new DefaultListModel<Track>();
			for (Track track : getTrackList())
				trackListHelpList.addElement(track);

			trackList = new JList(trackListHelpList);
			trackList.setVisibleRowCount(8);
			trackList.setForeground(foreground);
			trackList.setBackground(background);
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
			contentPane.add(new JScrollPane(trackList));
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
			playlist.setVisibleRowCount(8);
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

			JScrollPane jScrollPane1 = new JScrollPane(playlist);
			jScrollPane1.setBorder(null);
			contentPane.add(jScrollPane1);
			playlist.addListSelectionListener(this);
			playlist.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					selectedTrack = (Track) playlist.getSelectedValue();
				}

			});
		}
		contentPane.add(playingLabel);
		// Stop button
		{
			ImageIcon stopButtonIcon = new ImageIcon("stop.png");
			JButton stopButton = new JButton("", stopButtonIcon);
			stopButton.setOpaque(false);
			stopButton.setContentAreaFilled(false);
			stopButton.setBorderPainted(false);
			contentPane.add(stopButton);
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
			contentPane.add(playButton);
			playButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					stopPlaying(); // stop playing old track before playing new
									// track
					if (selectedTrack==null)
						playFirst();
					else
					playTrack(selectedTrack);
				}
			});
		}

		// add to playlist button
		{
			JButton addToPlaylistButton = new JButton("Add");
			addToPlaylistButton.setBorderPainted(false);
			addToPlaylistButton.setForeground(foreground);
			addToPlaylistButton.setBackground(brighterBackground); // slightly
																	// brighter
			contentPane.add(addToPlaylistButton);
			addToPlaylistButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectedTrack = (Track) trackList.getSelectedValue();
					addToPlaylist(selectedTrack);
					playlistHelpList.addElement(selectedTrack);

					// addToPlaylist(tracks.get(selectedTrackIndex));
					// playlistHelpList.addElement(tracks.get(selectedTrackIndex));
				}
			});
		}

		// remove from playlist button
		{
			JButton removeFromPlaylistButton = new JButton("Remove");
			removeFromPlaylistButton.setBorderPainted(false);
			removeFromPlaylistButton.setForeground(foreground);
			removeFromPlaylistButton.setBackground(brighterBackground);
			contentPane.add(removeFromPlaylistButton);
			removeFromPlaylistButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectedTrack = (Track) trackList.getSelectedValue();
					removeFromPlaylist(selectedTrack);
					playlistHelpList.removeElement(selectedTrack);

	
				}
			});
		}

		{
			JButton nextInPlaylistButton = new JButton("Next");
			nextInPlaylistButton.setBorderPainted(false);
			nextInPlaylistButton.setForeground(foreground);
			nextInPlaylistButton.setBackground(brighterBackground);
			contentPane.add(nextInPlaylistButton);
			nextInPlaylistButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int currentIndex = playlistTracks.indexOf(selectedTrack);
					try{
					selectedTrack = playlistTracks.get(currentIndex + 1);
					}
					catch (IndexOutOfBoundsException d){
						selectedTrack = playlistTracks.get(0);
					}
					stopPlaying(); // stop playing old track before playing new
					// track
					playTrack(selectedTrack);

				
				}
			});
		}

		frame.pack();
		frame.setVisible(true);

	}

	/**
	 * returns array of all available tracks
	 */
	private Track[] getTrackList() {

		Track[] arraytrack = tracks.toArray(new Track[tracks.size()]);
		return arraytrack;
	}

	private Track[] getPlaylist() {

		Track[] arraytrack = playlistTracks.toArray(new Track[playlistTracks
				.size()]);
		return arraytrack;
	}

	private void updateInfoLabel() {
		infoLabel.setText("Music library loaded.  " + getNumberOfTracks()
				+ " tracks.");
	}

	public void updatePlayingSong(String playingSong) {
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
