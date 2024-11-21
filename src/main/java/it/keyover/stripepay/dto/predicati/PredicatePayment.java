package it.keyover.stripepay.dto.predicati;

import com.stripe.model.PaymentIntent;
import it.keyover.stripepay.IdempiereStripeConstant;
import java.util.function.Predicate;

/**
 *
 * @author red
 */
public class PredicatePayment {

    public static Predicate<PaymentIntent> processed(Boolean isProcessed) {
        if (isProcessed != null) {
            if (!isProcessed) {
                return p -> p.getMetadata().get(IdempiereStripeConstant.STRIPE_KEY_METADATA_IDEMPIERE_PROCESSED) == null || p.getMetadata().get(IdempiereStripeConstant.STRIPE_KEY_METADATA_IDEMPIERE_PROCESSED).equals("false");
            } else {
                return p -> p.getMetadata().get(IdempiereStripeConstant.STRIPE_KEY_METADATA_IDEMPIERE_PROCESSED) != null &&  p.getMetadata().get(IdempiereStripeConstant.STRIPE_KEY_METADATA_IDEMPIERE_PROCESSED).equals("true");
            }
        } else {
            return p -> true;
        }
    }

    public static Predicate<PaymentIntent> authorized(Boolean isAuthorized) {
        if(isAuthorized != null)
        {
            if(isAuthorized)
                return p -> p.getStatus().equals(IdempiereStripeConstant.STRIPE_PAYMENT_SUCCEEDED_STATUS);
            else
                return p -> ! p.getStatus().equals(IdempiereStripeConstant.STRIPE_PAYMENT_SUCCEEDED_STATUS);
        }
        else
            return p-> true;
    }

}
