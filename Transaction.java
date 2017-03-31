/**
 * Created by Dong on 17/3/29.
 */
public class Transaction {

    private String from;
    private String to;
    private String balance;

    public Transaction(String from,String to,String balance) {
        this.from = from;
        this.to = to;
        this.balance = balance;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getBalance() {
        return balance;
    }
}
