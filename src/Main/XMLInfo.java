package Main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import AssistStaff.Mission;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;
import javax.xml.xpath.*;

public class XMLInfo {
	private static File XML = new File(System.getProperty("user.home") + File.separatorChar + "List.xml");
	private static void CreateXML(Mission m){
		try {
			XML.createNewFile();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			document.setXmlVersion("1.0");
			
			Element root = document.createElement("Missions");
			root.setAttribute("id_num", "1");
			document.appendChild(root);
			Element pageElement = document.createElement("Mission");
			pageElement.setAttribute("id", String.valueOf(m.ID));
			Element titleElement = document.createElement("title");
			titleElement.setTextContent(m.Title);
			pageElement.appendChild(titleElement);
			Element authorElement = document.createElement("author");
			authorElement.setTextContent(m.Author);
			pageElement.appendChild(authorElement);
			Element fileElement = document.createElement("file");
			fileElement.setTextContent(m.Path);
			pageElement.appendChild(fileElement);
			Element urlElement = document.createElement("url");
			urlElement.setTextContent(m.Url);
			pageElement.appendChild(urlElement);
			Element typeElement = document.createElement("type");
			typeElement.setTextContent(m.Type);
			pageElement.appendChild(typeElement);
			Element webtypeElement = document.createElement("webtype");
			webtypeElement.setTextContent(String.valueOf(m.webType));
			pageElement.appendChild(webtypeElement);
			Element doneElement = document.createElement("done");
			doneElement.setTextContent("no");
			pageElement.appendChild(doneElement);
			Element dateElement = document.createElement("date");
			dateElement.setTextContent("");
			pageElement.appendChild(dateElement);
			
			
			root.appendChild(pageElement);
			
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transFormer = transFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			
			FileOutputStream out = new FileOutputStream(XML);
			StreamResult xmlResult = new StreamResult(out);
			transFormer.transform(domSource, xmlResult);
			
			out.close();
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	public static void Add(Mission m){
		if (!XML.exists()){
			CreateXML(m);
			return;
		}
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		try {
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db=factory.newDocumentBuilder();
			Document document=db.parse(XML);
			Element root=document.getDocumentElement();
			root.setAttribute("id_num", String.valueOf(Integer.parseInt(root.getAttribute("id_num"))+1));
			Element pageElement = document.createElement("Mission");
			pageElement.setAttribute("id", String.valueOf(m.ID));
			Element titleElement = document.createElement("title");
			titleElement.setTextContent(m.Title);
			pageElement.appendChild(titleElement);
			Element authorElement = document.createElement("author");
			authorElement.setTextContent(m.Author);
			pageElement.appendChild(authorElement);
			Element fileElement = document.createElement("file");
			fileElement.setTextContent(m.Path);
			pageElement.appendChild(fileElement);
			Element urlElement = document.createElement("url");
			urlElement.setTextContent(m.Url);
			pageElement.appendChild(urlElement);
			Element typeElement = document.createElement("type");
			typeElement.setTextContent(m.Type);
			pageElement.appendChild(typeElement);
			Element webtypeElement = document.createElement("webtype");
			webtypeElement.setTextContent(String.valueOf(m.webType));
			pageElement.appendChild(webtypeElement);
			Element doneElement = document.createElement("done");
			doneElement.setTextContent("no");
			pageElement.appendChild(doneElement);
			Element dateElement = document.createElement("date");
			dateElement.setTextContent("");
			pageElement.appendChild(dateElement);
			
			
			root.appendChild(pageElement);
			saveXML(document);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void Remove(Mission m){
		try {
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db;
			db = factory.newDocumentBuilder();
			Document xmldoc=db.parse(XML);
			Element root=xmldoc.getDocumentElement();
			Element Mission=(Element) selectSingleNode("/Missions/Mission[@id='" + m.ID + "']", root);
			root.setAttribute("id_num", String.valueOf(Integer.parseInt(root.getAttribute("id_num"))-1));
			Mission.getParentNode().removeChild(Mission);
			saveXML(xmldoc);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void Done(Mission m){
		try {
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db;
			db = factory.newDocumentBuilder();
			Document xmldoc=db.parse(XML);
			Element root=xmldoc.getDocumentElement();
			Element Mission=(Element) selectSingleNode("/Missions/Mission[@id='" + m.ID + "']", root);
			Mission.getElementsByTagName("done").item(0).setTextContent("yes");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			m.Date = df.format(new Date());
			Mission.getElementsByTagName("date").item(0).setTextContent(df.format(new Date()));
			saveXML(xmldoc);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean Have(Mission m){
		if (!XML.exists()){
			return false;
		}
		try {
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db;
			db = factory.newDocumentBuilder();
			Document xmldoc=db.parse(XML);
			Element root=xmldoc.getDocumentElement();
			Element Mission=(Element) selectSingleNode("/Missions/Mission[url='" + m.Url + "']", root);
			if (Mission == null)
				return false;
			Element Missionx=(Element) selectSingleNode("/Missions/Mission[type='" + m.Type + "']", Mission);
			if (Missionx == null)
				return false;
			else
				return true;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void Restart(Mission m){
		try {
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db;
			db = factory.newDocumentBuilder();
			Document xmldoc=db.parse(XML);
			Element root=xmldoc.getDocumentElement();
			Element Mission=(Element) selectSingleNode("/Missions/Mission[@id='" + m.ID + "']", root);
			Mission.getElementsByTagName("done").item(0).setTextContent("no");
			Mission.getElementsByTagName("date").item(0).setTextContent("");
			saveXML(xmldoc);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void RemoveAll(){
		XML.delete();
	}
	
	public static int ReturnNodes(){
		if (!XML.exists()){
			return 0;
		}
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		try {
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db=factory.newDocumentBuilder();
			Document document=db.parse(XML);
			Element root=document.getDocumentElement();
			NodeList Nodes = root.getElementsByTagName("Mission");
			return Nodes.getLength();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static ArrayList<Mission> ReturnList(){
		try {
			ArrayList <Mission> ml = new ArrayList<Mission>();
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db;
			db = factory.newDocumentBuilder();
			Document xmldoc=db.parse(XML);
			Element root=xmldoc.getDocumentElement();
			NodeList Nodes = root.getElementsByTagName("Mission");
			for (int j =0; j < Nodes.getLength(); j++){
				NodeList MNodes = Nodes.item(j).getChildNodes();
				Mission m = new Mission();
				m.ID = Integer.valueOf(((Element)Nodes.item(j)).getAttribute("id"));
				for (int i =0; i < MNodes.getLength(); i++){
					Node M = MNodes.item(i);
					if (M.getNodeName() == "title")
						m.Title = M.getFirstChild().getNodeValue();
					else if (M.getNodeName() == "author")
						m.Author = M.getFirstChild().getNodeValue();
					else if (M.getNodeName() == "file")
						m.Path = M.getFirstChild().getNodeValue();
					else if (M.getNodeName() == "url")
						m.Url = M.getFirstChild().getNodeValue();
					else if (M.getNodeName() == "type")
						m.Type = M.getFirstChild().getNodeValue();
					else if (M.getNodeName() == "webtype")
						m.webType = Integer.valueOf(M.getFirstChild().getNodeValue());
					else if (M.getNodeName() == "done")
						if (M.getFirstChild().getNodeValue().equals("yes")){
							m.Date = M.getNodeValue();
							m.Done = true;
						}
						else {
							m.Done = false;
						}
				}
				ml.add(m);
			}
			return ml;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Node selectSingleNode(String express, Object source) {
		Node result=null;
		XPathFactory xpathFactory=XPathFactory.newInstance();
		XPath xpath=xpathFactory.newXPath();
		try {
			result=(Node) xpath.evaluate(express, source, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	private static void saveXML( Document doc) {
		TransformerFactory transFactory=TransformerFactory.newInstance();
		try {
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty("indent", "yes");
			
			DOMSource source=new DOMSource();
			source.setNode(doc);
			StreamResult result=new StreamResult();
			result.setOutputStream(new FileOutputStream(XML));
			
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}