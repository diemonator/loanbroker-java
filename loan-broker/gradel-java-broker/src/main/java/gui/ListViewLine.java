package gui;

import model.BankInterestReply;
import model.BankInterestRequest;
import model.LoanRequest;

/**
 * This class is an item/line for a ListView. It makes it possible to put both BankInterestRequest and BankInterestReply object in one item in a ListView.
 */
class ListViewLine {
	
	private LoanRequest loanRequest;
	private BankInterestReply bankInterestReply;
	
	public ListViewLine(LoanRequest bankInterestRequest) {
		setLoanRequest(bankInterestRequest);
		setBankInterestReply(null);
	}

	public LoanRequest getLoanRequest() {
		return loanRequest;
	}
	
	private void setLoanRequest(LoanRequest bankInterestRequest) {
		this.loanRequest = bankInterestRequest;
	}
	
	public BankInterestReply getBankInterestReply() {
		return bankInterestReply;
	}

	public void setBankInterestReply(BankInterestReply bankInterestReply) {
		this.bankInterestReply = bankInterestReply;
	}

    /**
     * This method defines how one line is shown in the ListView.
     * @return
     *  a) if BankInterestReply is null, then this item will be shown as "bankInterestRequest.toString ---> waiting for loan reply..."
     *  b) if BankInterestReply is not null, then this item will be shown as "bankInterestRequest.toString ---> bankInterestReply.toString"
     */
	@Override
	public String toString() {
	   return loanRequest.toString() + "  --->  " + ((bankInterestReply !=null)? bankInterestReply.toString():"waiting for loan reply...");
	}
	
}
