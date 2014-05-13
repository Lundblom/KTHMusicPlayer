import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * An XML parser that writes tracks to a file or reads from a file
 * @author lundblom
 *
 */
public class TrackParser 
{
	
	private final String DIRECTORY_NAME = ".KTHMusic";
	
	public final String PATH_NAME = System.getenv("APPDATA") +"/" + DIRECTORY_NAME;
	
	public TrackParser()
	{
		
	}
	
	/**
	 * Method that returns all tracks found in the specified XML-file located in the APPDATA directory
	 * The method will return an empty ArrayList if no tracks were found.
	 * @param fileName The name of the file where the tracks are stored
	 * @return An arraylist containing all the tracks in the file
	 */
	public ArrayList<Track> readList(String fileName)
	{
		//Retrieve the users AppData folder
		File xmlFile = new File(PATH_NAME + "/" + fileName);
		
		ArrayList<Track> list = new ArrayList<>();
		
		try 
		{
			//Initializing the document
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory  .newInstance();  
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();  
			Document doc = documentBuilder.parse(xmlFile);  
			  
			doc.getDocumentElement().normalize();
			//tracks is the name of the root element
		    NodeList nodeList = doc.getElementsByTagName("track");  
			  
			//Iterate over all found elements  
			for (int temp = 0; temp < nodeList.getLength(); temp++)
			{  
				
				Node node = nodeList.item(temp);  
				
				//If the node is actually anything useful
				if (node.getNodeType() == Node.ELEMENT_NODE) 
				{  
				  	Element track = (Element) node;
				  	String artist = null;
				   	String title = null;
				   	String filename = null;
					    	
				   	artist = track.getAttribute("artist");
				   	title = track.getAttribute("title");
				   	fileName = track.getAttribute("fileName");
				    	
				   	Track t = new Track(artist, title, filename);
					    	
				   	list.add(t);
				} 
			}
		}
		
		catch(IOException|SAXException|ParserConfigurationException e)
		{
			
		}
		
		return list;
	}
	
	/**
	 * Method that creates a new XML file with elements in the ArrayList sent to the method.
	 * There is no way to add new elements
	 * @param list The list with the tracks to save
	 * @param fileName The filename, e.g. "file.xml"
	 */
	public void saveList(ArrayList<Track> list, String fileName) 
	{
		try 
		{  
			//Initialize the document
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();  
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();  
			Document document = documentBuilder.newDocument();
			
			//The root element's name is "tracks"
			Element rootElement = document.createElement("tracks");  
			document.appendChild(rootElement);  
			
			//Iterate over the list of tracks
			for(int i = 0; i < list.size(); i++)
			{
				Element e = document.createElement("track");
				
				//artist elements  
				Element lastname = document.createElement("artist");  
				lastname.appendChild(document.createTextNode(list.get(i).getArtist()));  
				e.appendChild(lastname);  
				
				//title elements  
				Element title = document.createElement("title");  
				title.appendChild(document.createTextNode(list.get(i).getTitle()));  
				e.appendChild(title);  
				  
				//filename elements  
				Element filename = document.createElement("filename");  
				filename.appendChild(document.createTextNode(list.get(i).getFilename()));  
				e.appendChild(filename);  
				
				rootElement.appendChild(e);
			}
			   
			  
			//Creating and writing to XML file  
			TransformerFactory transformerFactory = TransformerFactory.newInstance();  
			Transformer transformer = transformerFactory.newTransformer();  
			DOMSource domSource = new DOMSource(document);  
			StreamResult streamResult = new StreamResult(new File(PATH_NAME + "/" + fileName));
			
			//All done
			transformer.transform(domSource, streamResult);  
			  
			System.out.println("File saved to specified path!");  
			  
		} 
		catch (ParserConfigurationException|TransformerException e) 
		{  
			e.printStackTrace();  
		} 
	}
}
