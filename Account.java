/**
 * Created by Dong on 17/3/29.
 */
public class Account {
    public Account(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    private String account;
    private String balance = "0";
}
