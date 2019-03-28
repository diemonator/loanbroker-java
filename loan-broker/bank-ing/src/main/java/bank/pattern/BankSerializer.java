package bank.pattern;

import bank.model.BankInterestReply;
import bank.model.BankInterestRequest;
import com.owlike.genson.Genson;

public class BankSerializer {

    private static Genson genson = new Genson();

    public static BankInterestRequest requestFromString(String string) {
        return genson.deserialize(string,BankInterestRequest.class);
    }

    public static String replyToString(BankInterestReply reply) {
        return genson.serialize(reply);
    }
}
