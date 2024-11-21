package it.keyover.stripepay.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *
 * @author red
 */
public class PaymentDTO {
    
    private String idOrder;
    private Boolean approved;
    private String idCharge;
    private String idTransaction;
    private String idPayment;
    private String salesChannel;
    private Timestamp paymentDate;
    private Timestamp registrationDate;
    private BigDecimal amount;

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getIdCharge() {
        return idCharge;
    }

    public void setIdCharge(String idCharge) {
        this.idCharge = idCharge;
    }

    public String getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(String idTransaction) {
        this.idTransaction = idTransaction;
    }

    public String getIdPayment() {
        return idPayment;
    }

    public void setIdPayment(String idPayment) {
        this.idPayment = idPayment;
    }

   

    public String getSalesChannel() {
        return salesChannel;
    }

    public void setSalesChannel(String canaleVendita) {
        this.salesChannel = canaleVendita;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp dataPagamento) {
        this.paymentDate = dataPagamento;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp dataRegistrazione) {
        this.registrationDate = dataRegistrazione;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal importo) {
        this.amount = importo;
    }
}
