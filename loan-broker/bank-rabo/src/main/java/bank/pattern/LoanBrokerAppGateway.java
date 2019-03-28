package bank.pattern;

import bank.model.BankInterestReply;
import bank.model.BankInterestRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.HashMap;

public abstract class LoanBrokerAppGateway {

    private MessageSenderGateway sender = new MessageSenderGateway("bankMiddleware");
    private MessageReceiverGateway receiver = new MessageReceiverGateway("middlewareRabo");
    private HashMap<BankInterestRequest,String[]> map = new HashMap<>();

    public LoanBrokerAppGateway() {
        receiver.setListener(message -> {
            try {
                TextMessage deseialized = (TextMessage) message;
                BankInterestRequest bankInterestRequest = BankSerializer.requestFromString(deseialized.getText());

                map.put(bankInterestRequest,new String[] {
                        message.getJMSMessageID(),
                        Integer.toString(message.getIntProperty("aggregation"))
                });
                onLoanRequestArrived(bankInterestRequest);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    public void sendBankInterest(BankInterestReply reply, BankInterestRequest request) {
        String json = BankSerializer.replyToString(reply);
        Message message = sender.createTextMessage(json);
        for (BankInterestRequest item : map.keySet()) {
            if (item.hashCode() == request.hashCode())
            {
                try {
                    message.setJMSCorrelationID(map.get(item)[0]);
                    message.setIntProperty("aggregation", Integer.parseInt(map.get(item)[1]));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        sender.send(message);
    }

    public abstract void onLoanRequestArrived(BankInterestRequest request);
}
