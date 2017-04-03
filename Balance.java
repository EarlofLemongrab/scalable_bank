import org.w3c.dom.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Dong on 17/4/2.
 */
public class Balance {
    public Element rootElement;
    public Document outputdoc;
    public Statement stmt;
    public void balance(NodeList bList){
        try{
            for (int temp = 0; temp < bList.getLength(); temp++) {

                Node nNode = bList.item(temp);

                System.out.println("\nCurrent Element :" + nNode.getNodeName());
                Element eElement = (Element) nNode;
                if (nNode.getNodeType() == Node.ELEMENT_NODE && !eElement.getAttribute("ref").equals("")) {//stupid same name tag

                    System.out.println("op ref : " + eElement.getAttribute("ref"));

                    ResultSet rs = stmt.executeQuery("Select balance from Account where account_num = "+eElement.getElementsByTagName("account").item(0).getTextContent());
                    if(!rs.next()){
                        Element transaction_error = outputdoc.createElement("Error");
                        rootElement.appendChild(transaction_error);

                        Attr attr = outputdoc.createAttribute("ref");
                        attr.setValue(eElement.getAttribute("ref"));
                        transaction_error.setAttributeNode(attr);
                        transaction_error.appendChild(outputdoc.createTextNode("Balance fail"));
                    };

                    //System.out.println("database has "+rs.getString("balance"));
                    Element balance_success = outputdoc.createElement("Success");
                    rootElement.appendChild(balance_success);

                    Attr attr = outputdoc.createAttribute("ref");
                    attr.setValue(eElement.getAttribute("ref"));
                    balance_success.setAttributeNode(attr);
                    balance_success.appendChild(outputdoc.createTextNode(rs.getString("balance")));
                    return;





                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
