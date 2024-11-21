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

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.BalanceTransaction;
import com.stripe.model.BalanceTransactionCollection;
import com.stripe.model.Charge;
import com.stripe.model.Payout;

import it.keyover.stripepay.IdempiereStripeConstant;
import it.keyover.stripepay.dto.PaymentDTO;
import it.keyover.stripepay.dto.PayoutDTO;
import it.keyover.stripepay.dto.predicati.PredicatePayout;
import it.keyover.stripepay.exception.CustomStripeException;

/**
 *
 * @author red
 */
public class IdempierePayoutService implements IPayoutService {
    
    
      public IdempierePayoutService(String apyKey) {

        Stripe.apiKey = apyKey;
    }

    @Override
    public List<PayoutDTO> listNotProcessed(Date start, Date end) throws CustomStripeException {
        return listPayout(start, end, false);
    }

    @Override
    public List<PayoutDTO> listProcessed(Date start, Date end) throws CustomStripeException {
        return listPayout(start, end, true);
    }

    private List<PayoutDTO> listPayout(Date start, Date end, Boolean isProcessed) throws CustomStripeException {

        List<PayoutDTO> listaPayout = new ArrayList<>();
        try {

            Map<String, Object> optionalMapCreate = new HashMap<String, Object>();
            optionalMapCreate.put("gt", start.getTime() / 1000);
            optionalMapCreate.put("lt", end.getTime() / 1000);
            Map<String, Object> payoutParams = new HashMap<String, Object>();
            payoutParams.put("created", optionalMapCreate);
            payoutParams.put(IdempiereStripeConstant.STRIPE_KEY_STATUS, IdempiereStripeConstant.STRIPE_VALUE_STATUS);

            Payout.list(payoutParams).getData().stream()
                    .filter(PredicatePayout.processati(isProcessed))
                    .forEach(x -> {
                        List<PaymentDTO> listPaymentDTO = new ArrayList<>();
                        try {
                            PayoutDTO payoutDTO = new PayoutDTO();
                            Map<String, Object> balancetransactionParams = new HashMap<String, Object>();
                            balancetransactionParams.put(IdempiereStripeConstant.STRIPE_KEY_PAYOUT, x.getId());

                            payoutDTO.setAmount(new BigDecimal(x.getAmount()/100));
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

    public Boolean updateProcessedPayout(String idPayout, Boolean processed) {
        try {
            Payout payout = Payout.retrieve(idPayout);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put(IdempiereStripeConstant.STRIPE_KEY_METADATA_IDEMPIERE_PROCESSED, "true");
            Map<String, Object> params = new HashMap<>();
            params.put("metadata", metadata);
            payout.update(params);
            return true;

        } catch (StripeException ex) {
            Logger.getLogger(IdempierePaymentService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

}
