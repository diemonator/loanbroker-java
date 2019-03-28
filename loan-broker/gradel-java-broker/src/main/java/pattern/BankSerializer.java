package pattern;

import model.BankInterestReply;
import model.BankInterestRequest;
import com.owlike.genson.Genson;

public class BankSerializer {

    private static Genson genson = new Genson();

    public static String requestToString(BankInterestRequest request) {
        return genson.serialize(request);
    }

    public static BankInterestReply replyFromString(String string) {
        return genson.deserialize(string,BankInterestReply.class);
    }
}
