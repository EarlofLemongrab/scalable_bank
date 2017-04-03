import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Dong on 17/4/2.
 */
public class Query {
    public Element rootElement;
    public Document outputdoc;
    public Statement stmt;

    public void query(NodeList qList) {
        try {

            for (int temp = 0; temp < qList.getLength(); temp++) {

                Node nNode = qList.item(temp);
                Node childNode = nNode.getFirstChild();


                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    System.out.println("op ref : " + eElement.getAttribute("ref"));
                    String or_prefix = "";
                    String not_prefix = "";
                    String and_prefix = "";


                    NodeList or = eElement.getElementsByTagName("or");
                    NodeList not = eElement.getElementsByTagName("not");
                    NodeList and = eElement.getElementsByTagName("and");

                    if (or.getLength() > 0) {
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


                    if (not.getLength() > 0) {
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


                    if (and.getLength() > 0) {
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
                    while (childNode != null) {
                        System.out.println(childNode.getNodeName());
                        if (childNode.getNodeName().equals("greater")) {
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
                        } else if (childNode.getNodeName().equals("less")) {
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
                        } else if (childNode.getNodeName().equals("equals")) {
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
                    other_prefix = other_prefix.substring(0, other_prefix.length() - 4);
                    String query_sql = "";
                    if (or_prefix.length() > 0) query_sql = query_sql + or_prefix + " INTERSECT ";
                    if (and_prefix.length() > 0) query_sql = query_sql + and_prefix + " INTERSECT ";
                    if (not_prefix.length() > 0) query_sql = query_sql + not_prefix + " INTERSECT ";
                    if (!other_prefix.equals("Select * from Transaction whe")) query_sql = query_sql + other_prefix;


                    System.out.println(query_sql);

                    ResultSet rs = stmt.executeQuery(query_sql);
                    while (rs.next()) {
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
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
