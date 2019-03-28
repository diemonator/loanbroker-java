package pattern;

import com.owlike.genson.Genson;
import model.LoanReply;
import model.LoanRequest;

public class LoanSerializer {
    private static Genson genson = new Genson();

    public static LoanRequest requestFromString(String string) {
        return genson.deserialize(string,LoanRequest.class);
    }

    public static String replyToString(LoanReply reply) {
        return genson.serialize(reply);
    }

}
