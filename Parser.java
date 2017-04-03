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


public class Parser {

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


            Create create = new Create();
            create.outputdoc = outputdoc;
            create.rootElement = rootElement;
            create.stmt = stmt;

            create.create(cList);

            outputdoc = create.outputdoc;
            rootElement = create.rootElement;
            stmt = create.stmt;

            System.out.println("----------------------------");
            Transfer transfer = new Transfer();
            transfer.outputdoc = outputdoc;
            transfer.rootElement = rootElement;
            transfer.stmt = stmt;
            transfer.transfer(tList);


            System.out.println("----------------------------");
            System.out.println(bList.getLength());
            Balance b = new Balance();
            b.outputdoc = outputdoc;
            b.rootElement = rootElement;
            b.stmt = stmt;
            b.balance(bList);




            System.out.println("QUERY----------------------------");
            Query q = new Query();
            q.outputdoc = outputdoc;
            q.rootElement = rootElement;
            q.stmt = stmt;
            q.query(qList);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(outputdoc);
            StreamResult result = new StreamResult(new File(output));

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
        }
    }
}


