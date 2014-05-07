import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import javax.swing.ImageIcon;

/**
 * A class to hold details of audio tracks.
 * Individual tracks may be played.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2011.07.31
 */
public class MusicOrganizer extends JFrame
implements ListSelectionListener, ActionListener
{
    // An ArrayList for storing music tracks.
    private ArrayList<Track> tracks;
    // An ArrayList for storing music tracks in a playlist.
    private ArrayList<Track> playlistTracks;
    // A player for the music tracks.
    private MusicPlayer player;
    // A reader that can read music files and load them as tracks.
    private TrackReader reader;
    //the frame
    private JFrame frame;
    //list for tracks to be shown
    private JList trackList, playlist;
    //index of selected track in list
    private int selectedTrackIndex;
 
    /**
     * Create a MusicOrganizer
     */
    public MusicOrganizer()
    {
        tracks = new ArrayList<Track>();
        playlistTracks = new ArrayList<Track>();
        player = new MusicPlayer();
        reader = new TrackReader();
        readLibrary("audio");
        readLibrary("F:\\Musik\\Led Zeppelin - Greatest hits\\CD 2");

        makeFrame();
    }

    /**
     * Add a track file to the collection.
     * @param filename The file name of the track to be added.
     */
    public void addFile(String filename)
    {
        tracks.add(new Track(filename));
    }

    /**
     * Add a track to the collection.
     * @param track The track to be added.
     */
    public void addTrack(Track track)
    {
        tracks.add(track);
    }

    /**
     * Add a track to playlist.
     * @param track The track to be added.
     */
    public void addToPlaylist(Track track){
        playlistTracks.add(track);
    }

    /**
     * Play a track in the collection.
     * @param index The index of the track to be played.
     */
    public void playTrack(int index)
    {
        if(indexValid(index)) {
            Track track = tracks.get(index);
            player.startPlaying(track.getFilename());
            System.out.println("Now playing: " + track.getArtist() + " - " + track.getTitle());
        }
    }

    /**
     * Return the number of tracks in the collection.
     * @return The number of tracks in the collection.
     */
    public int getNumberOfTracks()
    {
        return tracks.size();
    }

    /**
     * List a track from the collection.
     * @param index The index of the track to be listed.
     */
    public void listTrack(int index)
    {
        System.out.print("Track " + index + ": ");
        Track track = tracks.get(index);
        System.out.println(track.getDetails());
    }

    /**
     * Show a list of all the tracks in the collection.
     */
    public ArrayList<String> getAllTracks()
    {
        ArrayList<String> trackList = new ArrayList<String>();
        for(Track track : tracks) {
            trackList.add(track.getDetails()+"\n");
        }

        return trackList;
    }
    
      /**
     * Show a list of all the tracks in the collection.
     */
    public ArrayList<String> getAllTracksPlaylist()
    {
        ArrayList<String> trackList = new ArrayList<String>();
        for(Track track : playlistTracks) {
            trackList.add(track.getDetails()+"\n");
        }

        return trackList;
    }

    /**
     * List all tracks by the given artist.
     * @param artist The artist's name.
     */
    public void listByArtist(String artist)
    {
        for(Track track : tracks) {
            if(track.getArtist().contains(artist)) {
                System.out.println(track.getDetails());
            }
        }
    }

    /**
     * Remove a track from the collection.
     * @param index The index of the track to be removed.
     */
    public void removeTrack(int index)
    {
        if(indexValid(index)) {
            tracks.remove(index);
        }
    }

    /**
     * Play the first track in the collection, if there is one.
     */
    public void playFirst()
    {
        if(tracks.size() > 0) {
            player.startPlaying(tracks.get(0).getFilename());
        }
    }

    /**
     * Stop the player.
     */
    public void stopPlaying()
    {
        player.stop();
    }

    /**
     * Determine whether the given index is valid for the collection.
     * Print an error message if it is not.
     * @param index The index to be checked.
     * @return true if the index is valid, false otherwise.
     */
    private boolean indexValid(int index)
    {
        // The return value.
        // Set according to whether the index is valid or not.
        boolean valid;

        if(index < 0) {
            System.out.println("Index cannot be negative: " + index);
            valid = false;
        }
        else if(index >= tracks.size()) {
            System.out.println("Index is too large: " + index);
            valid = false;
        }
        else {
            valid = true;
        }
        return valid;
    }

    private void readLibrary(String folderName)
    {
        ArrayList<Track> tempTracks = reader.readTracks(folderName, ".mp3");

        // Put all thetracks into the organizer.
        for(Track track : tempTracks) {
            addTrack(track);
        }
    }

    /**
     * draw stuff
     */
    private void makeFrame(){
        //frame
        frame = new JFrame("MusicOrganiizer");
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new FlowLayout());
        
        //Label
        JLabel label = new JLabel("Music library loaded.  "+ getNumberOfTracks() + " tracks.");
        contentPane.add(label);

        //imported tracks list
        trackList= new JList(showTrackList());
        trackList.setVisibleRowCount(5); //FIXME
        trackList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        contentPane.add(new JScrollPane(trackList));
        trackList.addListSelectionListener(this);
        
        //Playlist track list
         playlist= new JList(showPlaylist());
        playlist.setVisibleRowCount(5); //FIXME
        playlist.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        contentPane.add(new JScrollPane(playlist));
        playlist.addListSelectionListener(this);

        //Stop button
        ImageIcon stopButtonIcon = new ImageIcon("stop.png");
        JButton stopButton = new JButton("", stopButtonIcon);
        stopButton.setSize(new Dimension(50,50));
        contentPane.add(stopButton);
        stopButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    stopPlaying(); 
                }
            });

        //Play button
        ImageIcon playButtonIcon = new ImageIcon("play.png");
        JButton playButton = new JButton("", playButtonIcon);
        contentPane.add(playButton);
        playButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    stopPlaying(); //stop playing old track before playing new track
                    playTrack(selectedTrackIndex);
                   

                }
            });

        //add to playlist button     
        JButton addToPlaylistButton = new JButton("add");
        contentPane.add(addToPlaylistButton);
        addToPlaylistButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    addToPlaylist(tracks.get(selectedTrackIndex));
                    playlist.addElement(tracks.get(selectedTrackIndex));
                }
            });

        frame.pack();
        frame.setVisible(true);

    }

    /**
     * if item in the list is selected
     */
    public void valueChanged(ListSelectionEvent e) {     
         selectedTrackIndex = trackList.getSelectedIndex();
         //selectedTrack = (Track)trackList.getSelectedValue(); 
      
    } 

    /**
     * returns array of all available tracks
     */
    private String[] showTrackList() {
        ArrayList<String> temp = getAllTracks();               
        String [] arraytrack = temp.toArray(new String[temp.size()]);
        return arraytrack;
    }
    
      private String[] showPlaylist() {
        ArrayList<String> temp = getAllTracksPlaylist();               
        String [] arraytrack = temp.toArray(new String[temp.size()]);
        return arraytrack;
    }

    public void actionPerformed(ActionEvent evt) {
    }

}
