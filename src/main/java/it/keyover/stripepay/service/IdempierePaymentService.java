/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.keyover.stripepay.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.BalanceTransaction;
import com.stripe.model.BalanceTransactionCollection;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Payout;

import it.keyover.stripepay.IdempiereStripeConstant;
import it.keyover.stripepay.dto.PaymentDTO;
import it.keyover.stripepay.dto.PayoutDTO;
import it.keyover.stripepay.dto.predicati.PredicatePayment;
import it.keyover.stripepay.exception.CustomStripeException;

/**
 *
 * @author red
 */
public class IdempierePaymentService implements IPaymentService {

    public IdempierePaymentService(String apyKey) {

        Stripe.apiKey = apyKey;
    }

    @Override
    public List<PayoutDTO> listPayoutPayment(Date start, Date end, Boolean isProcessed) throws Exception, StripeException {

        List<PayoutDTO> listaPayout = new ArrayList<>();
        try {

            Map<String, Object> payoutParams = new HashMap<String, Object>();
            payoutParams.put(IdempiereStripeConstant.STRIPE_KEY_STATUS, IdempiereStripeConstant.STRIPE_VALUE_STATUS);

            Payout.list(payoutParams).getData().stream()
                    .filter(p -> p.getMetadata().get(IdempiereStripeConstant.STRIPE_KEY_METADATA_IDEMPIERE_PROCESSED) == null || p.getMetadata().get(IdempiereStripeConstant.STRIPE_KEY_METADATA_IDEMPIERE_PROCESSED).equals("false"))
                    .forEach(x -> {
                        List<PaymentDTO> listPaymentDTO = new ArrayList<>();
                        try {
                            PayoutDTO payoutDTO = new PayoutDTO();
                            Map<String, Object> balancetransactionParams = new HashMap<String, Object>();
                            balancetransactionParams.put(IdempiereStripeConstant.STRIPE_KEY_PAYOUT, x.getId());

                            payoutDTO.setAmount(new BigDecimal(x.getAmount()));
                            payoutDTO.setRegistrationDate(new Timestamp(x.getArrivalDate() * 1000));
                            payoutDTO.setIdPayout(x.getId());
                            BalanceTransactionCollection balanceTransaction = BalanceTransaction.list(balancetransactionParams);

                            balanceTransaction.getData().stream()
                                    .filter(t -> t.getType().equals("charge"))
                                    .map(c -> c.getSource())
                                    .forEach(ch -> {

                                        try {
                                            Charge ordine = Charge.retrieve(ch);
                                            if (ordine.getMetadata().containsKey(IdempiereStripeConstant.STRIPE_KEY_METADATA_ORDER)) {
                                                PaymentDTO dto = new PaymentDTO();
                                                dto.setApproved(Boolean.TRUE);
                                                dto.setIdCharge(ch);
                                                dto.setIdPayment(x.getId());
                                                dto.setRegistrationDate(new Timestamp(x.getArrivalDate() * 1000));
                                                dto.setIdOrder(ordine.getMetadata().get(IdempiereStripeConstant.STRIPE_KEY_METADATA_ORDER));
                                                dto.setPaymentDate(new Timestamp(ordine.getCreated() * 1000));
                                                dto.setAmount(new BigDecimal(ordine.getAmount()));
//                                                                                          
                                                if (ordine.getMetadata().containsKey(IdempiereStripeConstant.STRIPE_KEY_METADATA_SALES_CHANNEL)) {
                                                    dto.setSalesChannel(ordine.getMetadata().get(IdempiereStripeConstant.STRIPE_KEY_METADATA_SALES_CHANNEL));
                                                }
                                                listPaymentDTO.add(dto);
                                            }
                                        } catch (StripeException ex) {
                                            Logger.getLogger(IdempierePaymentService.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    });
                            payoutDTO.setPayments(listPaymentDTO);
                            listaPayout.add(payoutDTO);
                            Payout payout = Payout.retrieve(x.getId());
                            Map<String, Object> metadata = new HashMap<>();
                            metadata.put(IdempiereStripeConstant.STRIPE_KEY_METADATA_IDEMPIERE_PROCESSED, "true");
                            Map<String, Object> params = new HashMap<>();
                            params.put("metadata", metadata);
                            payout.update(params);
                        } catch (StripeException ex) {
                            Logger.getLogger(IdempierePaymentService.class.getName()).log(Level.SEVERE, null, ex);

                        }

                    });
//            
        } catch (StripeException ex) {
            throw new CustomStripeException(ex.getMessage());
        }

        return listaPayout;
    }

    @Override
    public List<PaymentDTO> listAllPayment(Date start, Date end) throws Exception, StripeException {
        return listPayment(start, end, null, null);
    }

    @Override
    public List<PaymentDTO> listPaymentNotProcessed(Date start, Date end) throws Exception, StripeException {
        return listPayment(start, end, false, null);
    }

    @Override
    public List<PaymentDTO> listPaymentNotProcessedAndAuthorized(Date start, Date end) throws Exception, StripeException {
        return listPayment(start, end, false, true);
    }

    @Override
    public List<PaymentDTO> listPaymentNotProcessedAndNotAuthorized(Date start, Date end) throws Exception, StripeException {
        return listPayment(start, end, false, false);
    }

    @Override
    public List<PaymentDTO> listPaymentProcessed(Date start, Date end) throws Exception, StripeException {
        return listPayment(start, end, true, null);
    }

    @Override
    public List<PaymentDTO> listPaymentProcessedAndAuthorized(Date start, Date end) throws Exception, StripeException {
        return listPayment(start, end, true, true);
    }

    @Override
    public List<PaymentDTO> listPaymentProcessedAndNotAuthorized(Date start, Date end) throws Exception, StripeException {
        return listPayment(start, end, true, false);
    }

    private List<PaymentDTO> listPayment(Date start, Date end, Boolean isProcessed, Boolean autorizzati) throws Exception, StripeException {

        List<PaymentDTO> listaPayout = new ArrayList<>();
        try {

            Map<String, Object> optionalMapCreate = new HashMap<String, Object>();
            if(start!=null )
                optionalMapCreate.put("gt", start.getTime() / 1000);
            if(end!=null)
                optionalMapCreate.put("lt", end.getTime() / 1000);
            Map<String, Object> payoutParams = new HashMap<String, Object>();
            if(start!=null || end!= null)
                payoutParams.put("created", optionalMapCreate);
            Iterable<PaymentIntent> itCustomers = PaymentIntent.list(payoutParams).autoPagingIterable();
            StreamSupport.stream(itCustomers.spliterator(), false)
                    .filter(PredicatePayment.processed(isProcessed))
                    .filter(PredicatePayment.authorized(autorizzati))
                    .forEach(x -> {
                        if (x.getMetadata().containsKey(IdempiereStripeConstant.STRIPE_KEY_METADATA_ORDER)) {
                            PaymentDTO dto = new PaymentDTO();
                            if (IdempiereStripeConstant.STRIPE_PAYMENT_SUCCEEDED_STATUS.equals(x.getStatus())) {
                                dto.setApproved(Boolean.TRUE);
                            } else {
                                dto.setApproved(Boolean.FALSE);
                            }
                            dto.setIdPayment(x.getId());
                            dto.setRegistrationDate(new Timestamp(x.getCreated() * 1000));
                            dto.setIdOrder(x.getMetadata().get(IdempiereStripeConstant.STRIPE_KEY_METADATA_ORDER));
                            dto.setPaymentDate(new Timestamp(x.getCreated() * 1000));
                            dto.setAmount(new BigDecimal(x.getAmount()));

                            if (x.getMetadata().containsKey(IdempiereStripeConstant.STRIPE_KEY_METADATA_SALES_CHANNEL)) {
                                dto.setSalesChannel(x.getMetadata().get(IdempiereStripeConstant.STRIPE_KEY_METADATA_SALES_CHANNEL));
                            }
                            listaPayout.add(dto);
                        }

                    });

        } catch (StripeException ex) {
            throw new CustomStripeException(ex.getMessage());
        }
        return listaPayout;
    }

    @Override
    public Boolean updateProcessedPayment(String idPayment, Boolean processed) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(idPayment);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put(IdempiereStripeConstant.STRIPE_KEY_METADATA_IDEMPIERE_PROCESSED, processed);
            Map<String, Object> params = new HashMap<>();
            params.put("metadata", metadata);
            paymentIntent.update(params);
            return true;

        } catch (StripeException ex) {
            Logger.getLogger(IdempierePaymentService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

}
