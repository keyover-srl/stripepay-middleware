package it.keyover.stripepay.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author red
 */
public class PayoutDTO {

    private String idPayout;
    private Timestamp paymentDate;
    private Timestamp registrationDate;
    private BigDecimal amount;
    private List<PaymentDTO> payments;

    public PayoutDTO() {
        this.payments = new ArrayList<>();
    }
    
    public String getIdPayout() {
        return idPayout;
    }

    public void setIdPayout(String idPayout) {
        this.idPayout = idPayout;
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

    public void setAmount(BigDecimal importoBonifico) {
        this.amount = importoBonifico;
    }

    public List<PaymentDTO> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentDTO> payments) {
        this.payments = payments;
    }
}
