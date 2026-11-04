package de.szut.pms.customer;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long customerId) {
        super("Customer with ID " + customerId + " doesn't exist!");
    }
}