package loanclient.pattern;

import com.owlike.genson.Genson;
import loanclient.model.LoanReply;
import loanclient.model.LoanRequest;

public class LoanSerializer {

    private static Genson genson = new Genson();

    public static String requestToString(LoanRequest request) {
        return genson.serialize(request);
    }

    public static LoanReply replyFromString(String string) {
        return genson.deserialize(string,LoanReply.class);
    }

}
