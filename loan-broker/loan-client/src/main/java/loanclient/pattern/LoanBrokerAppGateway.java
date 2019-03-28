package loanclient.pattern;

import loanclient.model.LoanReply;
import loanclient.model.LoanRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.HashMap;

public abstract class LoanBrokerAppGateway {

    private MessageSenderGateway sender = new MessageSenderGateway("clientMiddleware");
    private MessageReceiverGateway receiver = new MessageReceiverGateway("middlewareClient");
    private HashMap<String,LoanRequest> map = new HashMap<>();

    public LoanBrokerAppGateway() {
        receiver.setListener(message -> {
            try {
                TextMessage msgBody = (TextMessage) message;
                LoanReply loanReply = LoanSerializer.replyFromString(msgBody.getText());
                LoanRequest loanRequest = map.get(message.getJMSCorrelationID());
                onLoanReplyArrived(loanRequest,loanReply);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    public void applyForLoan(LoanRequest request) {
        String reply = LoanSerializer.requestToString(request);
        Message message = sender.createTextMessage(reply);
        sender.send(message);
        try {
            map.put(message.getJMSMessageID(),request);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public abstract void onLoanReplyArrived(LoanRequest request, LoanReply reply);
}
