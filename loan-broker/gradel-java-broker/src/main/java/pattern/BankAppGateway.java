package pattern;

import model.BankInterestReply;
import model.BankInterestRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class BankAppGateway {

    private MessageSenderGateway sender = new MessageSenderGateway("middlewareBank");
    private MessageSenderGateway senderIng = new MessageSenderGateway("middlewareIng");
    private MessageSenderGateway senderRabo = new MessageSenderGateway("middlewareRabo");
    private MessageReceiverGateway receiver = new MessageReceiverGateway("bankMiddleware");

    private HashMap<String,BankInterestRequest> map = new HashMap<>();
    private ArrayList<BankInterestReply> replies = new ArrayList<>();


    public BankAppGateway() {
        receiver.setListener(message -> {
            TextMessage json = (TextMessage) message;
            try {
                if (message.getIntProperty("aggregation") == 1) {
                    replies.add(BankSerializer.replyFromString(json.getText()));
                    if (replies.size() == 2) {
                        BankInterestReply reply = null;
                        BankInterestRequest request = map.get(message.getJMSCorrelationID());
                        if (replies.get(0).getInterest() < replies.get(1).getInterest()) {
                            reply = replies.get(0);
                        } else {
                            reply = replies.get(1);
                        }
                        onBankReplyArrived(reply,request);
                        replies = new ArrayList<>();
                    }
                } else {
                    BankInterestReply reply = BankSerializer.replyFromString(json.getText());
                    BankInterestRequest request = map.get(message.getJMSCorrelationID());
                    onBankReplyArrived(reply,request);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    public abstract void onBankReplyArrived(BankInterestReply reply, BankInterestRequest request);

    public void sendBankRequest(BankInterestRequest request) {
        String json = BankSerializer.requestToString(request);
        Message message;
        if (request.getAmount() <= 100000 && request.getTime() <= 10) {
            message = senderIng.createTextMessage(json);
            try {
                message.setIntProperty("aggregation",1);
                senderIng.send(message);
                map.put(message.getJMSMessageID(),request);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        if (request.getAmount() >= 200000 && request.getAmount() <= 300000 && request.getTime() <= 20) {
            message = sender.createTextMessage(json);
            try {
                message.setIntProperty("aggregation",1);
                sender.send(message);
                map.put(message.getJMSMessageID(),request);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        if (request.getAmount() <= 250000 && request.getTime() <= 15) {
            message = senderRabo.createTextMessage(json);
            try {
                message.setIntProperty("aggregation",1);
                senderRabo.send(message);
                map.put(message.getJMSMessageID(),request);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
