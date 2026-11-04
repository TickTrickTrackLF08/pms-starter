package de.szut.pms.customer;

/**
 * Access to customer service. The actual service does not yet exist—
 * it is being developed in parallel by another team. Until then,
 * {@link DummyCustomerClient} will fill this role.
 */
public interface CustomerClient {

    /**
     * Checks whether a customer ID exists. If the ID is unknown,
     * throw a custom business exception as soon as you need it at the
     * call site—the same approach as when checking
     * an employee ID against the Employee Service.
     */
    boolean existsById(Long customerId);
}
