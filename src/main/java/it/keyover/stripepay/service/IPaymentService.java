package it.keyover.stripepay.service;

import java.util.Date;
import java.util.List;

import com.stripe.exception.StripeException;

import it.keyover.stripepay.dto.PaymentDTO;
import it.keyover.stripepay.dto.PayoutDTO;

/**
 *
 * @author red
 */
public interface IPaymentService {


    List<PayoutDTO> listPayoutPayment(Date start, Date end, Boolean isProcessed) throws Exception, StripeException;
    
    List<PaymentDTO> listAllPayment(Date start, Date end) throws Exception, StripeException;

    List<PaymentDTO> listPaymentNotProcessed(Date start, Date end) throws Exception, StripeException;

    List<PaymentDTO> listPaymentNotProcessedAndAuthorized(Date start, Date end) throws Exception, StripeException;

    List<PaymentDTO> listPaymentNotProcessedAndNotAuthorized(Date start, Date end) throws Exception, StripeException;

    List<PaymentDTO> listPaymentProcessed(Date start, Date end) throws Exception, StripeException;

    List<PaymentDTO> listPaymentProcessedAndAuthorized(Date start, Date end) throws Exception, StripeException;

    List<PaymentDTO> listPaymentProcessedAndNotAuthorized(Date start, Date end) throws Exception, StripeException;
   
    Boolean updateProcessedPayment(String idPayout, Boolean processed)throws Exception, StripeException;
}
