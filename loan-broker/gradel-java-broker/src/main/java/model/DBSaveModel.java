package model;

public class DBSaveModel {
    private int ssn; // unique client number
    private int amount; // the ammount to borrow
    private String bank; // the unique quote identification of the bank which makes the offer
    private double interest; // the interest that the bank offers for the requested loan
    private int time; // the time-span of the loan in years

    public DBSaveModel(int ssn, int amount, String bank, double interest, int time) {
        this.ssn = ssn;
        this.amount = amount;
        this.bank = bank;
        this.interest = interest;
        this.time = time;
    }

    public DBSaveModel() {
    }

    public int getSsn() {
        return ssn;
    }

    public void setSsn(int ssn) {
        this.ssn = ssn;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
