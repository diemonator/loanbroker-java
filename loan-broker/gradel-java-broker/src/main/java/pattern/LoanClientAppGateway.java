package pattern;

import model.ClientHistory;
import model.DBSaveModel;
import model.LoanReply;
import model.LoanRequest;
import org.glassfish.jersey.client.ClientConfig;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashMap;

public abstract class LoanClientAppGateway {
    private MessageSenderGateway sender = new MessageSenderGateway("middlewareClient");
    private MessageReceiverGateway receiver = new MessageReceiverGateway("clientMiddleware");
    private static final String archiveUrl = "http://localhost:8080/archive/rest/accepted";
    private static final String historyUrl = "http://localhost:8080/credit/rest/history";
    private HashMap<LoanRequest,String> map = new HashMap<>();

    public LoanClientAppGateway() {
        receiver.setListener(message -> {
            try {
                TextMessage dematerialized = (TextMessage) message;
                LoanRequest loanRequest = LoanSerializer.requestFromString(dematerialized.getText());
                ClientHistory history = getCreditHistory(loanRequest.getSsn());
                map.put(loanRequest,message.getJMSMessageID());
                onLoanRequestArrived(loanRequest,history);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    public abstract void onLoanRequestArrived(LoanRequest request, ClientHistory history);

    public void sendLoanRequest(LoanRequest request, LoanReply reply) {
        String id = map.get(request);
        String json = LoanSerializer.replyToString(reply);
        Message message = sender.createTextMessage(json);
        try {
            message.setJMSCorrelationID(id);
        } catch (JMSException e) { e.printStackTrace(); }
        if (reply.getInterest() > 0) {
            DBSaveModel save = new DBSaveModel(request.getSsn(),request.getAmount(),reply.getQuoteID(),reply.getInterest(),request.getTime());
            Entity<DBSaveModel> entity = Entity.entity(save,MediaType.APPLICATION_JSON);
            saveToArchive(entity);
        }
        sender.send(message);
    }

    private void saveToArchive(Entity entity) {
        URI baseUri = UriBuilder.fromUri(archiveUrl).build();
        Client client = ClientBuilder.newClient(new ClientConfig());
        WebTarget serviceTarget = client.target(baseUri);
        Invocation.Builder requestBuilder = serviceTarget.request();
        Response response = requestBuilder.post(entity);
        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
            System.out.println("Saved ok");
        } else {
            System.out.println("Not saved");
        }
    }

    private ClientHistory getCreditHistory(int ssn) {
        URI baseUri = UriBuilder.fromUri(historyUrl).build();
        Client client = ClientBuilder.newClient(new ClientConfig());
        WebTarget serviceTarget = client.target(baseUri);
        Invocation.Builder requestBuilder = serviceTarget.path(Integer.toString(ssn)).request().accept(MediaType.APPLICATION_JSON);
        Response response = requestBuilder.get();
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.readEntity(ClientHistory.class);
        }
        return null;
    }
}
