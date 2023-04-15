package util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.*;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by rgupta on 3/21/2018.
 */
public class XMLParser {

    /* THE TAG NAME IS THE NODE ELEMENT NAME FOR EXAMPLE
    <RelatedGraphics>
    <RelatedGraphic GraphicId="69697" Label="table 1" />
    TAGNAME SHOULD BE "RelatedGraphic"
     */
    public static ArrayList<String> getListOfTheAttributeValuesInTag(String xml, String tagName, String attributeName)  {
        ArrayList<String> listOfAttriburteValue = new ArrayList<String>();
        NamedNodeMap metaAttributes = null;
        try {
            // get the meat file
            xml.trim();
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));
            doc.getDocumentElement().normalize();

            // Get the root element
            Node meta = doc.getFirstChild();
            metaAttributes = meta.getAttributes();
            System.out.println("PARSING XSML .....>  LOOKING FOR "+attributeName+" IN CONTENT ID "+(metaAttributes.getNamedItem("TopicKey")).getNodeValue());

            NodeList nListTagName = doc.getElementsByTagName(tagName);
            for (int temp = 0; temp < nListTagName.getLength(); temp++) {
                Node nAttributeName = nListTagName.item(temp);
                nAttributeName.getFirstChild();
                if(nAttributeName.getNodeType()==Node.ELEMENT_NODE){
                    Element element = (Element) nAttributeName;
                    listOfAttriburteValue.add(element.getAttribute(attributeName));
                }
            }
            for(String s: listOfAttriburteValue){
               System.out.println("GraphicID : "+s);
           }



        } catch(SAXException  e){
            System.out.println("FELL INTO SAXException EXCEPTION WHILE PARSING XML");
            //System.out.println("Check if XML is valid and exists "+ xml);
           // System.out.println("Check if Tag Name is valid and present in xml "+ tagName);
           // System.out.println("Check if attribute name provide is valid and present in xml "+ attributeName);
            System.out.println(e);
        }
        catch(IOException e) {
            System.out.println("FELL INTO IOException EXCEPTION WHILE PARSING XML");
            System.out.println("Check if XML is valid and exists " + xml);
           // System.out.println("Check if Tag Name is valid and present in xml " + tagName);
            //System.out.println("Check if attribute name provide is valid and present in xml " + attributeName);
            System.out.println(e);
        }
        catch(ParserConfigurationException  e) {
            System.out.println("FELL INTO ParserConfigurationException EXCEPTION WHILE PARSING XML");
            // System.out.println("Check if XML is valid and exists "+ xml);
            System.out.println("Check if Tag Name is valid and present in xml " + tagName);
            System.out.println("Check if attribute name provide is valid and present in xml " + attributeName);
            System.out.println(e);
        }

        return listOfAttriburteValue;
    }
}
