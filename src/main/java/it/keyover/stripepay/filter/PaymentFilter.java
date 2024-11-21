/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.keyover.stripepay.filter;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author red
 */
public enum PaymentFilter {

    CREATED("created"), CREATED_OPTION_GT("created.gt"), CREATED_OPTION_GTE("created.gte"), CREATED_OPTION_LT("created.lt"), CREATED_OPTION_LTE("created.lte"), ENDING_BEFORE("ending_before"), LIMIT("limit"), STARTING_AFTER("starting_after");

    private String name;
    private static final Map<String, PaymentFilter> lookup = new HashMap<String, PaymentFilter>();

    static {
        for (PaymentFilter d : PaymentFilter.values()) {
            lookup.put(d.getName(), d);
        }
    }

    private PaymentFilter(String c) {
        name = c;
    }

    public String getName() {
        return name;
    }

}
