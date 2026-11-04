package de.szut.pms.customer;

import org.springframework.stereotype.Component;

/**
 * Placeholder until the actual customer service is available:
 * Any non-zero identifier is considered to exist. Replace this class with
 * an implementation that queries the customer service via RestClient
 * as soon as it becomes available—the {@link CustomerClient} interface
 * remains the same, so you don't need to change anything in the call sites.
 */
@Component
public class DummyCustomerClient implements CustomerClient {

    @Override
    public boolean existsById(Long customerId) {
        return customerId != null;
    }
}
