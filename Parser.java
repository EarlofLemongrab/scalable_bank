/**
 * Created by Dong on 17/3/28.
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
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

public class Parser {
    private static final Map<String, String> relationMap = createMap();
    private static Map<String, String> createMap()
    {
        Map<String,String> myMap = new HashMap<String,String>();
        myMap.put("equal", "=");
        myMap.put("greater", ">");
        myMap.put("less", "<");
        return myMap;
    }
    private static final Map<String, String> columnMap = createmap();
    private static Map<String, String> createmap()
    {
        Map<String,String> myMap = new HashMap<String,String>();
        myMap.put("from", "from_account");
        myMap.put("to", "to_account");
        myMap.put("amount", "amount");
        return myMap;
    }
    public static void parse(String input, String output) {

        try {

            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return;

        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/bank", "dl208",
                    "longdong");


        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;

        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }

        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate("DROP table IF EXISTS Account");
            stmt.executeUpdate("DROP table IF EXISTS Transaction ");
            String create_account = "CREATE TABLE IF NOT EXISTS Account " +
                    "(ID SERIAL PRIMARY KEY      NOT NULL," +
                    " account_num           INT    NOT NULL, " +
                    " Balance            real)";
            stmt.executeUpdate(create_account);
            String create_transaction = "CREATE TABLE IF NOT EXISTS Transaction " +
                    "(ID SERIAL PRIMARY KEY      NOT NULL," +
                    " From_account           INT    NOT NULL, " +
                    " To_account           INT    NOT NULL, " +
                    " Amount            real)";
            stmt.executeUpdate(create_transaction);


        }catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }

















        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        ArrayList<Account> accounts = new ArrayList<Account>();
        DocumentBuilderFactory outputFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder outputBuilder = null;
        try {
            outputBuilder = outputFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }


        try {
            File fXmlFile = new File(input);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(fXmlFile);
            //output xml setting


            Document outputdoc = outputBuilder.newDocument();
            Element rootElement = outputdoc.createElement("result");
            outputdoc.appendChild(rootElement);




            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getAttribute("reset"));

            NodeList cList = doc.getElementsByTagName("create");
            NodeList tList = doc.getElementsByTagName("transfer");
            NodeList bList = doc.getElementsByTagName("balance");
            NodeList qList = doc.getElementsByTagName("query");

            System.out.println("----------------------------");
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
                    accounts.add(acct);
                    String update_account = "INSERT INTO Account(account_num,balance) VALUES ("+acct.getAccount()+","+Double.parseDouble(acct.getBalance())+");";
                    stmt.executeUpdate(update_account);
                    Element create_success = outputdoc.createElement("Success");
                    rootElement.appendChild(create_success);

                    Attr attr = outputdoc.createAttribute("ref");
                    attr.setValue(eElement.getAttribute("ref"));
                    create_success.setAttributeNode(attr);
                    create_success.appendChild(outputdoc.createTextNode("created"));

                }
            }
            System.out.println("----------------------------");
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
                    transactions.add(t);
                    String update_transaction = "INSERT INTO transaction(from_account,to_account,amount) VALUES ("+t.getFrom()+","+t.getTo()+","+Double.parseDouble(t.getBalance())+");";
                    ResultSet rs = stmt.executeQuery("Select balance from Account where account_num = "+t.getFrom());
                    rs.next();
                    System.out.print(rs.getString("balance"));
                    stmt.executeUpdate("Update ACCOUNT Set balance = "+Double.toString(rs.getDouble("balance")-Double.parseDouble(t.getBalance()))+"where account_num = "+t.getFrom());
                    rs = stmt.executeQuery("Select balance from Account where account_num = "+t.getTo());
                    rs.next();
                    stmt.executeUpdate("Update ACCOUNT Set balance = "+Double.toString(rs.getDouble("balance")+Double.parseDouble(t.getBalance()))+"where account_num = "+t.getTo());

                    stmt.executeUpdate(update_transaction);

                    int cur_from = -1;
                    int cur_to = -1;
                    for(int i=0;i<accounts.size();i++){
                        if(t.getFrom().equals(accounts.get(i).getAccount()))cur_from = i;
                        if(t.getTo().equals(accounts.get(i).getAccount()))cur_to = i;
                    }
                    System.out.println("from "+accounts.get(cur_from).getAccount());
                    System.out.println("to "+accounts.get(cur_to).getAccount());
                    System.out.println("balance "+accounts.get(cur_from).getBalance());
                    System.out.println("need "+t.getBalance());
                    if(cur_from==-1||cur_to==-1||Double.parseDouble(accounts.get(cur_from).getBalance())<Double.parseDouble(t.getBalance())){
                        Element transaction_error = outputdoc.createElement("Error");
                        rootElement.appendChild(transaction_error);

                        Attr attr = outputdoc.createAttribute("ref");
                        attr.setValue(eElement.getAttribute("ref"));
                        transaction_error.setAttributeNode(attr);
                        transaction_error.appendChild(outputdoc.createTextNode("Transaction fail"));
                    }
                    else{
                        accounts.get(cur_from).setBalance(Double.toString(Double.parseDouble(accounts.get(cur_from).getBalance())-Double.parseDouble(t.getBalance())));
                        accounts.get(cur_to).setBalance(Double.toString(Double.parseDouble(accounts.get(cur_to).getBalance())+Double.parseDouble(t.getBalance())));
                        Element transaction_success = outputdoc.createElement("Success");
                        rootElement.appendChild(transaction_success);

                        Attr attr = outputdoc.createAttribute("ref");
                        attr.setValue(eElement.getAttribute("ref"));
                        transaction_success.setAttributeNode(attr);
                        transaction_success.appendChild(outputdoc.createTextNode("Transferred"));
                    }


                }
            }

            System.out.println("----------------------------");
            System.out.println(bList.getLength());
            for (int temp = 0; temp < bList.getLength(); temp++) {

                Node nNode = bList.item(temp);

                System.out.println("\nCurrent Element :" + nNode.getNodeName());
                Element eElement = (Element) nNode;
                if (nNode.getNodeType() == Node.ELEMENT_NODE && !eElement.getAttribute("ref").equals("")) {//stupid same name tag

                    System.out.println("op ref : " + eElement.getAttribute("ref"));

                    ResultSet rs = stmt.executeQuery("Select balance from Account where account_num = "+eElement.getElementsByTagName("account").item(0).getTextContent());
                    rs.next();
                    System.out.println("database has "+rs.getString("balance"));


                    for (int i = 0; i < accounts.size(); i++) {

                        if (accounts.get(i).getAccount().equals(eElement.getElementsByTagName("account").item(0).getTextContent())) {
                            Element balance_success = outputdoc.createElement("Success");
                            rootElement.appendChild(balance_success);

                            Attr attr = outputdoc.createAttribute("ref");
                            attr.setValue(eElement.getAttribute("ref"));
                            balance_success.setAttributeNode(attr);
                            balance_success.appendChild(outputdoc.createTextNode(accounts.get(i).getBalance()));
                        }
                    }
                    System.out.println("account : " + eElement.getElementsByTagName("account").item(0).getTextContent());


                }
            }



            System.out.println("QUERY----------------------------");

            for (int temp = 0; temp < qList.getLength(); temp++) {

                Node nNode = qList.item(temp);
                Node childNode = nNode.getFirstChild();


                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    System.out.println("op ref : " + eElement.getAttribute("ref"));
                    String or_prefix = "";
                    String not_prefix= "";
                    String and_prefix= "";




                    NodeList or = eElement.getElementsByTagName("or");
                    NodeList not = eElement.getElementsByTagName("not");
                    NodeList and = eElement.getElementsByTagName("and");

                    if(or.getLength()>0) {
                        for (int i = 0; i < or.getLength(); i++) {//OR SECTION
                            Node or_node = or.item(i);
                            or_prefix = "Select * from Transaction where ";
                            System.out.println("Attribute is " + or_node.getNodeName());
                            Element or_element = (Element) or_node;
                            NodeList or_equal = or_element.getElementsByTagName("equals");
                            System.out.print("equals size " + or_equal.getLength());
                            for (int j = 0; j < or_equal.getLength(); j++) {//or equal
                                //System.out.println("cur node "+or_equal.item(j).getAttributes().getNamedItem());
                                Node or_equal_from = or_equal.item(j).getAttributes().getNamedItem("from");
                                Node or_equal_to = or_equal.item(j).getAttributes().getNamedItem("to");
                                Node or_equal_account = or_equal.item(j).getAttributes().getNamedItem("amount");
                                if (or_equal_from != null) {
                                    System.out.println("or from equals " + or_equal.item(j).getAttributes().getNamedItem("from").getNodeValue());
                                    or_prefix = or_prefix + "from_account = " + or_equal.item(j).getAttributes().getNamedItem("from").getNodeValue() + " OR ";
                                    continue;
                                }
                                if (or_equal_to != null) {
                                    System.out.println("or to equals " + or_equal.item(j).getAttributes().getNamedItem("to").getNodeValue());
                                    or_prefix = or_prefix + "to_account = " + or_equal.item(j).getAttributes().getNamedItem("to").getNodeValue() + " OR ";
                                    continue;
                                }
                                if (or_equal_account != null) {
                                    System.out.println("or account equals " + or_equal.item(j).getAttributes().getNamedItem("amount").getNodeValue());
                                    or_prefix = or_prefix + "amount = " + or_equal.item(j).getAttributes().getNamedItem("amount").getNodeValue() + " OR ";
                                    continue;
                                }
                            }
                            NodeList or_great = or_element.getElementsByTagName("greater");
                            for (int j = 0; j < or_great.getLength(); j++) {//or equal
                                //System.out.println("cur node "+or_equal.item(j).getAttributes().getNamedItem());
                                Node or_great_from = or_great.item(j).getAttributes().getNamedItem("from");
                                Node or_great_to = or_great.item(j).getAttributes().getNamedItem("to");
                                Node or_great_account = or_great.item(j).getAttributes().getNamedItem("amount");
                                if (or_great_from != null) {
                                    System.out.println("or from equals " + or_great.item(j).getAttributes().getNamedItem("from").getNodeValue());
                                    or_prefix = or_prefix + "from_account > " + or_great.item(j).getAttributes().getNamedItem("from").getNodeValue() + " OR ";
                                    continue;
                                }
                                if (or_great_to != null) {
                                    System.out.println("or to greater " + or_great.item(j).getAttributes().getNamedItem("to").getNodeValue());
                                    or_prefix = or_prefix + "to_account > " + or_great.item(j).getAttributes().getNamedItem("to").getNodeValue() + " OR ";
                                    continue;
                                }
                                if (or_great_account != null) {
                                    System.out.println("or account great " + or_great.item(j).getAttributes().getNamedItem("amount").getNodeValue());
                                    or_prefix = or_prefix + "amount > " + or_great.item(j).getAttributes().getNamedItem("amount").getNodeValue() + " OR ";
                                    continue;
                                }
                            }
                            NodeList or_less = or_element.getElementsByTagName("less");
                            for (int j = 0; j < or_less.getLength(); j++) {//or equal
                                //System.out.println("cur node "+or_equal.item(j).getAttributes().getNamedItem());
                                Node or_less_from = or_less.item(j).getAttributes().getNamedItem("from");
                                Node or_less_to = or_less.item(j).getAttributes().getNamedItem("to");
                                Node or_less_account = or_less.item(j).getAttributes().getNamedItem("amount");
                                if (or_less_from != null) {
                                    System.out.println("or from less " + or_less.item(j).getAttributes().getNamedItem("from").getNodeValue());
                                    or_prefix = or_prefix + "from_account < " + or_less.item(j).getAttributes().getNamedItem("from").getNodeValue() + " OR ";
                                    continue;
                                }
                                if (or_less_to != null) {
                                    System.out.println("or to less " + or_less.item(j).getAttributes().getNamedItem("to").getNodeValue());
                                    or_prefix = or_prefix + "to_account < " + or_less.item(j).getAttributes().getNamedItem("to").getNodeValue() + " OR ";
                                    continue;
                                }
                                if (or_less_account != null) {
                                    System.out.println("or account less " + or_less.item(j).getAttributes().getNamedItem("amount").getNodeValue());
                                    or_prefix = or_prefix + "amount < " + or_less.item(j).getAttributes().getNamedItem("amount").getNodeValue() + " OR ";
                                    continue;
                                }
                            }
                            or_prefix = or_prefix.substring(0, or_prefix.length() - 3);
                            ResultSet rs = stmt.executeQuery(or_prefix);
                            while (rs.next()) {
                                System.out.println(rs.getDouble("amount"));

                            }


                            System.out.println("final or is" + or_prefix);
                        }
                    }











                    if(not.getLength()>0) {
                        for (int i = 0; i < not.getLength(); i++) {//NOT SECTION
                            Node not_node = not.item(i);
                            not_prefix = "Select * from Transaction where ";
                            System.out.println("Attribute is " + not_node.getNodeName());
                            Element not_element = (Element) not_node;
                            NodeList not_equal = not_element.getElementsByTagName("equals");
                            System.out.print("equals size " + not_equal.getLength());
                            for (int j = 0; j < not_equal.getLength(); j++) {//or equal
                                //System.out.println("cur node "+or_equal.item(j).getAttributes().getNamedItem());
                                Node not_equal_from = not_equal.item(j).getAttributes().getNamedItem("from");
                                Node not_equal_to = not_equal.item(j).getAttributes().getNamedItem("to");
                                Node not_equal_account = not_equal.item(j).getAttributes().getNamedItem("account");
                                if (not_equal_from != null) {
                                    System.out.println("or from equals " + not_equal.item(j).getAttributes().getNamedItem("from").getNodeValue());
                                    not_prefix = not_prefix + "from_account != " + not_equal.item(j).getAttributes().getNamedItem("from").getNodeValue() + " AND ";
                                    continue;
                                }
                                if (not_equal_to != null) {
                                    System.out.println("or to equals " + not_equal.item(j).getAttributes().getNamedItem("to").getNodeValue());
                                    not_prefix = not_prefix + "to_account != " + not_equal.item(j).getAttributes().getNamedItem("to").getNodeValue() + " AND ";
                                    continue;
                                }
                                if (not_equal_account != null) {
                                    System.out.println("or account equals " + not_equal.item(j).getAttributes().getNamedItem("account").getNodeValue());
                                    not_prefix = not_prefix + "amount != " + not_equal.item(j).getAttributes().getNamedItem("amount").getNodeValue() + " AND ";
                                    continue;
                                }
                            }
                            NodeList not_great = not_element.getElementsByTagName("greater");

                            for (int j = 0; j < not_great.getLength(); j++) {//or greater
                                //System.out.println("cur node "+or_equal.item(j).getAttributes().getNamedItem());
                                Node not_great_from = not_great.item(j).getAttributes().getNamedItem("from");
                                Node not_great_to = not_great.item(j).getAttributes().getNamedItem("to");
                                Node not_great_account = not_great.item(j).getAttributes().getNamedItem("amount");

                                if (not_great_from != null) {
                                    System.out.println("not from equals " + not_great.item(j).getAttributes().getNamedItem("from").getNodeValue());
                                    not_prefix = not_prefix + "from_account <= " + not_great.item(j).getAttributes().getNamedItem("from").getNodeValue() + " AND ";
                                    continue;
                                }
                                if (not_great_to != null) {
                                    System.out.println("not to greater " + not_great.item(j).getAttributes().getNamedItem("to").getNodeValue());
                                    not_prefix = not_prefix + "to_account <= " + not_great.item(j).getAttributes().getNamedItem("to").getNodeValue() + " AND ";
                                    continue;
                                }
                                if (not_great_account != null) {
                                    System.out.println("not account great " + not_great.item(j).getAttributes().getNamedItem("amount").getNodeValue());
                                    not_prefix = not_prefix + "amount <= " + not_great.item(j).getAttributes().getNamedItem("amount").getNodeValue() + " AND ";
                                    continue;
                                }
                            }
                            System.out.println(not_prefix);
                            NodeList not_less = not_element.getElementsByTagName("less");
                            for (int j = 0; j < not_less.getLength(); j++) {//or equal
                                //System.out.println("cur node "+or_equal.item(j).getAttributes().getNamedItem());
                                Node not_less_from = not_less.item(j).getAttributes().getNamedItem("from");
                                Node not_less_to = not_less.item(j).getAttributes().getNamedItem("to");
                                Node not_less_account = not_less.item(j).getAttributes().getNamedItem("amount");
                                if (not_less_from != null) {
                                    System.out.println("not from less " + not_less.item(j).getAttributes().getNamedItem("from").getNodeValue());
                                    not_prefix = not_prefix + "from_account >= " + not_less.item(j).getAttributes().getNamedItem("from").getNodeValue() + " AND ";
                                    continue;
                                }
                                if (not_less_to != null) {
                                    System.out.println("not to less " + not_less.item(j).getAttributes().getNamedItem("to").getNodeValue());
                                    not_prefix = not_prefix + "to_account >= " + not_less.item(j).getAttributes().getNamedItem("to").getNodeValue() + " AND ";
                                    continue;
                                }
                                if (not_less_account != null) {
                                    System.out.println("not account less " + not_less.item(j).getAttributes().getNamedItem("amount").getNodeValue());
                                    not_prefix = not_prefix + "amount >= " + not_less.item(j).getAttributes().getNamedItem("amount").getNodeValue() + " AND ";
                                    continue;
                                }
                            }
                            not_prefix = not_prefix.substring(0, not_prefix.length() - 4);
                            ResultSet rs = stmt.executeQuery(not_prefix);
                            while (rs.next()) {
                                System.out.println(rs.getDouble("amount"));

                            }


                            System.out.println("final not is" + not_prefix);
                        }
                    }





                    if(and.getLength()>0) {
                        for (int i = 0; i < and.getLength(); i++) {//AND SECTION
                            Node and_node = not.item(i);
                            and_prefix = "Select * from Transaction where ";
                            System.out.println("Attribute is " + and_node.getNodeName());
                            Element and_element = (Element) and_node;
                            NodeList and_equal = and_element.getElementsByTagName("equals");
                            System.out.print("equals size " + and_equal.getLength());
                            for (int j = 0; j < and_equal.getLength(); j++) {//or equal
                            //System.out.println("cur node "+or_equal.item(j).getAttributes().getNamedItem());
                                Node and_equal_from = and_equal.item(j).getAttributes().getNamedItem("from");
                                Node and_equal_to = and_equal.item(j).getAttributes().getNamedItem("to");
                                Node and_equal_account = and_equal.item(j).getAttributes().getNamedItem("account");
                                if (and_equal_from != null) {
                                    System.out.println("and from equals " + and_equal.item(j).getAttributes().getNamedItem("from").getNodeValue());
                                    and_prefix = and_prefix + "from_account = " + and_equal.item(j).getAttributes().getNamedItem("from").getNodeValue() + " AND ";
                                    continue;
                                }
                                if (and_equal_to != null) {
                                    System.out.println("and to equals " + and_equal.item(j).getAttributes().getNamedItem("to").getNodeValue());
                                    and_prefix = and_prefix + "to_account = " + and_equal.item(j).getAttributes().getNamedItem("to").getNodeValue() + " AND ";
                                    continue;
                                }
                                if (and_equal_account != null) {
                                    System.out.println("and account equals " + and_equal.item(j).getAttributes().getNamedItem("amount").getNodeValue());
                                    and_prefix = and_prefix + "amount = " + and_equal.item(j).getAttributes().getNamedItem("amount").getNodeValue() + " AND ";
                                    continue;
                                }
                            }
                            NodeList and_great = and_element.getElementsByTagName("greater");

                            for (int j = 0; j < and_great.getLength(); j++) {//or greater
                            //System.out.println("cur node "+or_equal.item(j).getAttributes().getNamedItem());
                                Node and_great_from = and_great.item(j).getAttributes().getNamedItem("from");
                                Node and_great_to = and_great.item(j).getAttributes().getNamedItem("to");
                                Node and_great_account = and_great.item(j).getAttributes().getNamedItem("amount");

                                if (and_great_from != null) {
                                    System.out.println("not from equals " + and_great.item(j).getAttributes().getNamedItem("from").getNodeValue());
                                    and_prefix = and_prefix + "from_account > " + and_great.item(j).getAttributes().getNamedItem("from").getNodeValue() + " AND ";
                                    continue;
                                }
                                if (and_great_to != null) {
                                    System.out.println("not to greater " + and_great.item(j).getAttributes().getNamedItem("to").getNodeValue());
                                    and_prefix = and_prefix + "to_account > " + and_great.item(j).getAttributes().getNamedItem("to").getNodeValue() + " AND ";
                                    continue;
                                }
                                if (and_great_account != null) {
                                    System.out.println("not account great " + and_great.item(j).getAttributes().getNamedItem("amount").getNodeValue());
                                    and_prefix = and_prefix + "amount > " + and_great.item(j).getAttributes().getNamedItem("amount").getNodeValue() + " AND ";
                                    continue;
                                }
                            }
                            System.out.println(and_prefix);
                            NodeList and_less = and_element.getElementsByTagName("less");
                            for (int j = 0; j < and_less.getLength(); j++) {//or equal
                            //System.out.println("cur node "+or_equal.item(j).getAttributes().getNamedItem());
                                Node and_less_from = and_less.item(j).getAttributes().getNamedItem("from");
                                Node and_less_to = and_less.item(j).getAttributes().getNamedItem("to");
                                Node and_less_account = and_less.item(j).getAttributes().getNamedItem("amount");
                                if (and_less_from != null) {
                                    System.out.println("not from less " + and_less.item(j).getAttributes().getNamedItem("from").getNodeValue());
                                    and_prefix = and_prefix + "from_account < " + and_less.item(j).getAttributes().getNamedItem("from").getNodeValue() + " AND ";
                                    continue;
                                }
                                if (and_less_to != null) {
                                    System.out.println("not to less " + and_less.item(j).getAttributes().getNamedItem("to").getNodeValue());
                                    and_prefix = and_prefix + "to_account < " + and_less.item(j).getAttributes().getNamedItem("to").getNodeValue() + " AND ";
                                    continue;
                                }
                                if (and_less_account != null) {
                                    System.out.println("not account less " + and_less.item(j).getAttributes().getNamedItem("amount").getNodeValue());
                                    and_prefix = and_prefix + "amount < " + and_less.item(j).getAttributes().getNamedItem("amount").getNodeValue() + " AND ";
                                    continue;
                                }
                            }
                            and_prefix = and_prefix.substring(0, and_prefix.length() - 4);
                            ResultSet rs = stmt.executeQuery(and_prefix);
                            while (rs.next()) {
                                System.out.println(rs.getDouble("amount"));

                            }


                            System.out.println("final and is" + and_prefix);
                        }
                    }
                    //other part
                    String other_prefix = "Select * from Transaction where ";
                    while(childNode!=null){
                        System.out.println(childNode.getNodeName());
                        if(childNode.getNodeName().equals("greater")){
                            Node other_great_from = childNode.getAttributes().getNamedItem("from");
                            Node other_great_to = childNode.getAttributes().getNamedItem("to");
                            Node other_great_account = childNode.getAttributes().getNamedItem("amount");

                            if (other_great_from != null) {
                                //System.out.println("not from equals " + and_great.item(j).getAttributes().getNamedItem("from").getNodeValue());
                                other_prefix = other_prefix + "from_account > " + other_great_from.getNodeValue() + " AND ";
                                //continue;
                            }
                            if (other_great_to != null) {
                                //System.out.println("not to greater " + and_great.item(j).getAttributes().getNamedItem("to").getNodeValue());
                                other_prefix = other_prefix + "to_account > " + other_great_to.getNodeValue() + " AND ";
                                //continue;
                            }
                            if (other_great_account != null) {
                                //System.out.println("not account great " + and_great.item(j).getAttributes().getNamedItem("amount").getNodeValue());
                                other_prefix = other_prefix + "amount > " + other_great_account.getNodeValue() + " AND ";
                                //continue;
                            }
                        }
                        else if(childNode.getNodeName().equals("less")){
                            Node other_less_from = childNode.getAttributes().getNamedItem("from");
                            Node other_less_to = childNode.getAttributes().getNamedItem("to");
                            Node other_less_account = childNode.getAttributes().getNamedItem("amount");

                            if (other_less_from != null) {
                                //System.out.println("not from equals " + and_great.item(j).getAttributes().getNamedItem("from").getNodeValue());
                                other_prefix = other_prefix + "from_account < " + other_less_from.getNodeValue() + " AND ";
                                //continue;
                            }
                            if (other_less_to != null) {
                                //System.out.println("not to greater " + and_great.item(j).getAttributes().getNamedItem("to").getNodeValue());
                                other_prefix = other_prefix + "to_account < " + other_less_to.getNodeValue() + " AND ";
                                //continue;
                            }
                            if (other_less_account != null) {
                                //System.out.println("not account great " + and_great.item(j).getAttributes().getNamedItem("amount").getNodeValue());
                                other_prefix = other_prefix + "amount < " + other_less_account.getNodeValue() + " AND ";
                                //continue;
                            }
                        }

                        else if(childNode.getNodeName().equals("equals")){
                            Node other_equal_from = childNode.getAttributes().getNamedItem("from");
                            Node other_equal_to = childNode.getAttributes().getNamedItem("to");
                            Node other_equal_account = childNode.getAttributes().getNamedItem("amount");

                            if (other_equal_from != null) {
                                //System.out.println("not from equals " + and_great.item(j).getAttributes().getNamedItem("from").getNodeValue());
                                other_prefix = other_prefix + "from_account = " + other_equal_from.getNodeValue() + " AND ";
                                //continue;
                            }
                            if (other_equal_to != null) {
                                //System.out.println("not to greater " + and_great.item(j).getAttributes().getNamedItem("to").getNodeValue());
                                other_prefix = other_prefix + "to_account = " + other_equal_to.getNodeValue() + " AND ";
                                //continue;
                            }
                            if (other_equal_account != null) {
                                //System.out.println("not account great " + and_great.item(j).getAttributes().getNamedItem("amount").getNodeValue());
                                other_prefix = other_prefix + "amount = " + other_equal_account.getNodeValue() + " AND ";
                                //continue;
                            }
                        }
                        childNode = childNode.getNextSibling();

                    }
                    other_prefix = other_prefix.substring(0,other_prefix.length()-4);
                    String query_sql="";
                    if(or_prefix.length()>0)query_sql = query_sql+ or_prefix+ " INTERSECT ";
                    if(and_prefix.length()>0)query_sql = query_sql+and_prefix+" INTERSECT ";
                    if(not_prefix.length()>0)query_sql = query_sql+not_prefix+" INTERSECT ";
                    if(!other_prefix.equals("Select * from Transaction whe"))query_sql = query_sql+other_prefix;




                    System.out.println(query_sql);

                    ResultSet rs = stmt.executeQuery(query_sql);
                    while(rs.next()){
                        String f = rs.getString("from_account");
                        String t = rs.getString("to_account");
                        Double balance = rs.getDouble("amount");
                        Element query = outputdoc.createElement("transfer");
                        rootElement.appendChild(query);

                        Element from = outputdoc.createElement("from");
                        query.appendChild(from);
                        from.appendChild(outputdoc.createTextNode(f));
                        Element to = outputdoc.createElement("to");
                        query.appendChild(to);
                        to.appendChild(outputdoc.createTextNode(t));
                        Element am = outputdoc.createElement("amount");
                        query.appendChild(am);
                        am.appendChild(outputdoc.createTextNode(Double.toString(balance)));


                    }















                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(outputdoc);
            StreamResult result = new StreamResult(new File("/Users/Dong/Desktop/output.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            return;
        } catch (SAXException e) {
            System.out.print("ERROR "+e.getMessage());
            Document doc = outputBuilder.newDocument();
            Element rootElement = doc.createElement("ERROR");
            rootElement.setNodeValue(e.getMessage());
            doc.appendChild(rootElement);
            rootElement.appendChild(doc.createTextNode(e.getMessage()));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = null;
            try {
                transformer = transformerFactory.newTransformer();
            } catch (TransformerConfigurationException tce) {
                tce.printStackTrace();
            }
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(output));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            try {
                transformer.transform(source, result);
            } catch (TransformerException te) {
                te.printStackTrace();
            }
            e.printStackTrace();
            return;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


