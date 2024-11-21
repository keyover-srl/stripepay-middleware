package it.keyover.stripepay.service;

import com.stripe.exception.StripeException;
import it.keyover.stripepay.dto.PayoutDTO;
import java.util.Date;
import java.util.List;

/**
 *
 * @author red
 */
public interface IPayoutService {

	List<PayoutDTO> listNotProcessed(Date start, Date end) throws Exception, StripeException;

	List<PayoutDTO> listProcessed(Date start, Date end) throws Exception, StripeException;

	Boolean updateProcessedPayout(String idPayout, Boolean processed) throws Exception, StripeException;

}