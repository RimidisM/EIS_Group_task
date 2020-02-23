
package currency;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author rimid
 */
public class XmlParser {

    public XmlParser() {
    }

    public List<CurencyValueModel> parseData(String line) throws IOException {

        List<CurencyValueModel> results = new ArrayList();

        try {

            InputSource is = new InputSource(new StringReader(line));

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = (Document) dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList nodes = doc.getElementsByTagName("CcyAmt");

            for (int i = 0; i < nodes.getLength(); i++) {
                CurencyValueModel cvm = new CurencyValueModel();
                Node node = nodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    cvm.curencyName = getValue("Ccy", element);
                    cvm.curencyRatio = Double.parseDouble(getValue("Amt", element));
                }
                results.add(cvm);
            }
        } catch (IOException | ParserConfigurationException | org.xml.sax.SAXException ex) {
        }
        return results;
    }

    private static String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        return node.getNodeValue();
    }

}
