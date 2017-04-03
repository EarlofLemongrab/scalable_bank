import org.w3c.dom.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Dong on 17/4/2.
 */
public class Transfer {
    public Element rootElement;
    public Document outputdoc;
    public Statement stmt;
    public void transfer(NodeList tList){
        try{
            for (int temp = 0; temp < tList.getLength(); temp++) {

                Node nNode = tList.item(temp);

                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    String tref = eElement.getAttribute("ref");
                    String from_account = eElement.getElementsByTagName("from").item(0).getTextContent();
                    String to_account = eElement.getElementsByTagName("to").item(0).getTextContent();
                    String amount = eElement.getElementsByTagName("amount").item(0).getTextContent();

                    Transaction t = new Transaction(eElement.getElementsByTagName("from").item(0).getTextContent(), eElement.getElementsByTagName("to").item(0).getTextContent(), eElement.getElementsByTagName("amount").item(0).getTextContent());
                    System.out.println("op ref : " + eElement.getAttribute("ref"));
                    System.out.println("to : " + eElement.getElementsByTagName("to").item(0).getTextContent());
                    System.out.println("From : " + eElement.getElementsByTagName("from").item(0).getTextContent());
                    System.out.println("amount : " + eElement.getElementsByTagName("amount").item(0).getTextContent());
                    NodeList Tags = eElement.getElementsByTagName("tag");
                    for (int i = 0; i < Tags.getLength(); i++) {
                        System.out.println("Tag : " + Tags.item(i).getTextContent());

                    }
                    //transactions.add(t);
                    String update_transaction = "INSERT INTO transaction(from_account,to_account,amount) VALUES ("+t.getFrom()+","+t.getTo()+","+Double.parseDouble(t.getBalance())+");";
                    ResultSet rs = stmt.executeQuery("Select balance from Account where account_num = "+t.getFrom());
                    System.out.println("Select balance from Account where account_num = "+t.getFrom());
                    if(!rs.next()){
                        Element transaction_error = outputdoc.createElement("Error");
                        rootElement.appendChild(transaction_error);

                        Attr attr = outputdoc.createAttribute("ref");
                        attr.setValue(eElement.getAttribute("ref"));
                        transaction_error.setAttributeNode(attr);
                        transaction_error.appendChild(outputdoc.createTextNode("Transaction fail"));
                        continue;
                    }
                    double from_balance = rs.getDouble("balance")-Double.parseDouble(t.getBalance());
                    rs = stmt.executeQuery("Select balance from Account where account_num = "+t.getTo());
                    if(!rs.next()||from_balance<0){
                        Element transaction_error = outputdoc.createElement("Error");
                        rootElement.appendChild(transaction_error);

                        Attr attr = outputdoc.createAttribute("ref");
                        attr.setValue(eElement.getAttribute("ref"));
                        transaction_error.setAttributeNode(attr);
                        transaction_error.appendChild(outputdoc.createTextNode("Transaction fail"));
                        continue;
                    }
                    double to_balance = rs.getDouble("balance")+Double.parseDouble(t.getBalance());

                    stmt.executeUpdate("Update ACCOUNT Set balance = "+Double.toString(from_balance)+"where account_num = "+t.getFrom());
                    stmt.executeUpdate("Update ACCOUNT Set balance = "+Double.toString(to_balance)+"where account_num = "+t.getTo());

                    stmt.executeUpdate(update_transaction);



                    Element transaction_success = outputdoc.createElement("Success");
                    rootElement.appendChild(transaction_success);

                    Attr attr = outputdoc.createAttribute("ref");
                    attr.setValue(eElement.getAttribute("ref"));
                    transaction_success.setAttributeNode(attr);
                    transaction_success.appendChild(outputdoc.createTextNode("Transferred"));



                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
