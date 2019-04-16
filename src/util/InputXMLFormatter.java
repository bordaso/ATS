package util;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class InputXMLFormatter {
	
	public static Map<String, String> getPositionValues(String inputXML)
	{
		Map<String, String>  valueMap = new HashMap<String, String>();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document document=null;
		Element rootElement=null;
		
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new InputSource(new StringReader(inputXML)));
			rootElement = document.getDocumentElement();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}


        NodeList nodeList = document.getElementsByTagName("Position");
        
        for(int x=0,size= nodeList.getLength(); x<size; x++) {
            valueMap.put(nodeList.item(x).getAttributes().getNamedItem("Symbol").getNodeValue(), nodeList.item(x).getAttributes().getNamedItem("Volume").getNodeValue());
        }


		
		return valueMap;
	}

}
