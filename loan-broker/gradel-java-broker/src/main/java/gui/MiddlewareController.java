package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.*;
import pattern.BankAppGateway;
import pattern.LoanClientAppGateway;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MiddlewareController implements Initializable {

    private HashMap<BankInterestRequest,LoanRequest> map = new HashMap<>();

    @FXML
    public ListView<ListViewLine> lvListViewLine;

    private BankAppGateway bankAppGateway = new BankAppGateway() {
        @Override
        public void onBankReplyArrived(BankInterestReply reply, BankInterestRequest request) {
            ListViewLine loan = getRequestReply(getLoanRequest(request));
            LoanRequest loanRequest = map.get(request);
            loanClientAppGateway.sendLoanRequest(loanRequest,new LoanReply(reply.getInterest(),reply.getQuoteId()));
            loan.setBankInterestReply(reply);
            lvListViewLine.refresh();
        }
    };

    private LoanClientAppGateway loanClientAppGateway = new LoanClientAppGateway() {
        @Override
        public void onLoanRequestArrived(LoanRequest request, ClientHistory history) {
            BankInterestRequest bankInterestRequest = new BankInterestRequest(request.getAmount(),request.getTime(),history.getCreditScore(),history.getHistory());
            lvListViewLine.getItems().add(new ListViewLine(request));
            bankAppGateway.sendBankRequest(bankInterestRequest);
            map.put(bankInterestRequest,request);
        }
    };

    /**
     * This method returns the line of lvMessages which contains the given loan request.
     * @param request BankInterestRequest for which the line of lvMessages should be found and returned
     * @return The ListView line of lvMessages which contains the given request
     */
    private ListViewLine getRequestReply(LoanRequest request) {
        for (int i = 0; i < lvListViewLine.getItems().size(); i++) {
            ListViewLine rr =  lvListViewLine.getItems().get(i);
            if (rr.getLoanRequest() != null && rr.getLoanRequest() == request) {
                return rr;
            }
        }
        return null;
    }

    private LoanRequest getLoanRequest(BankInterestRequest request) {
        for (BankInterestRequest bankInterestRequest: map.keySet()) {
            if (bankInterestRequest.hashCode() == request.hashCode()) {
                return map.get(bankInterestRequest);
            }
        }
        return null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
