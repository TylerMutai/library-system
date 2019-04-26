package librarysystem.resources;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class Resources {
	
	public static final String ENGLISH_LANGUAGE_FILE = "english";
	private static String lang = "";
	private static HashMap<String,Object> parsedXML = new HashMap<>();
	public static final String RESOURCES_FOLDER = "resources";
	public static final String IMAGES_FOLDER = "images";
	
	
	public static final void setLanguage(String langs) {
		lang = langs;
		loadFile();
	}
	
	private static void loadFile() {
		DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
				try(BufferedReader reader = new BufferedReader(new FileReader("langs/" + (lang == "" ? ENGLISH_LANGUAGE_FILE:lang) + ".xml" ))) {
					   StringBuilder xmlStringBuilder = new StringBuilder();
					   String line;
					   while ((line = reader.readLine()) != null) {
						   xmlStringBuilder.append(line);
					   }
					   
					   ByteArrayInputStream input = new ByteArrayInputStream(
							   xmlStringBuilder.toString().getBytes("UTF-8"));
					   
					   DocumentBuilder builder = factory.newDocumentBuilder();
					   Document doc = builder.parse(input);
					   NodeList elements = doc.getElementsByTagName("string");
					   
					   for(int i=0; i<elements.getLength();i++) {
						   Element element = (Element) elements.item(i);
						   parsedXML.put(element.getAttribute("name"), element.getTextContent());
					   }
					
				} catch (ParserConfigurationException | SAXException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	public static String getString(String stringId) {
		if(parsedXML.isEmpty()) {
			throw new NullPointerException("You need to call method: setLanguage first");
		}
		return (String) parsedXML.get(stringId);
	}
	
	public static Integer getInteger(String stringId) {
		if(parsedXML.isEmpty()) {
			throw new NullPointerException("You need to call method: setLanguage first");
		}
		return (Integer) parsedXML.get(stringId);
	}

}
