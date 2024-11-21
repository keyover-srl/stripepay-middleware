package it.keyover.stripepay.dto.predicati;

import java.util.function.Predicate;

import com.stripe.model.Payout;

import it.keyover.stripepay.IdempiereStripeConstant;

/**
 *
 * @author red
 */
public class PredicatePayout {
    
     public static Predicate<Payout> processati(Boolean isProcessed) {
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
    
}
