package jsprit.examples;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

public class XmlToJsonConversion {

    public static void main(String[] args) {

        try {
            File inputFile = new File("C:\\\\Users\\\\souhail\\\\Desktop\\\\IRISTI\\\\Output.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            JSONArray solutionCostsArray = new JSONArray();

   

            NodeList solutionList = doc.getElementsByTagName("solution");
            for (int temp = 0; temp < solutionList.getLength(); temp++) {
                Node nNode = solutionList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    double solutionCost = Double.parseDouble(eElement.getElementsByTagName("cost").item(0).getTextContent());

                    JSONObject solutionCosts = new JSONObject();
                    solutionCosts.put("PrixSansregrouprent", solutionCost);

                    solutionCostsArray.put(solutionCosts);
                }
            }

            JSONObject finalJson = new JSONObject();
            finalJson.put("solutionCosts", solutionCostsArray);

            // Write JSON to file
            try (FileWriter file = new FileWriter("C:\\\\Users\\\\souhail\\\\Desktop\\\\IRISTI\\\\Output.json")) {
                file.write(finalJson.toString(4)); // Pretty print with an indent of 4
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Json in path   C:\\Users\\souhail\\Desktop\\IRISTI\\Output.json");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
