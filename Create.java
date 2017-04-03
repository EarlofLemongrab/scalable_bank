/**
 * Created by Dong on 17/4/2.
 */
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
public class Create {
    public Element rootElement;
    public Document outputdoc;
    public Statement stmt;
    public void create(NodeList cList){
        System.out.println("----------------------------"+cList.getLength());

        for (int temp = 0; temp < cList.getLength(); temp++) {

            Node nNode = cList.item(temp);

            System.out.println("\nCurrent Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;

                System.out.println("op ref : " + eElement.getAttribute("ref"));
                System.out.println("account : " + eElement.getElementsByTagName("account").item(0).getTextContent());
                Account acct = new Account(eElement.getElementsByTagName("account").item(0).getTextContent());
                if (eElement.getElementsByTagName("balance").getLength() > 0) {
                    System.out.println("Balance : " + eElement.getElementsByTagName("balance").item(0).getTextContent());
                    acct.setBalance(eElement.getElementsByTagName("balance").item(0).getTextContent());
                }
                //accounts.add(acct);
                String update_account = "INSERT INTO Account(account_num,balance) VALUES ("+acct.getAccount()+","+Double.parseDouble(acct.getBalance())+");";
                try {
                    stmt.executeUpdate(update_account);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Element create_success = outputdoc.createElement("Success");
                rootElement.appendChild(create_success);

                Attr attr = outputdoc.createAttribute("ref");
                attr.setValue(eElement.getAttribute("ref"));
                create_success.setAttributeNode(attr);
                create_success.appendChild(outputdoc.createTextNode("created"));


            }
        }
        return;

    }
}
