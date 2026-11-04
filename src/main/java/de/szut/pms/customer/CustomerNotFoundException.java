package de.szut.pms.customer;

import de.szut.pms.common.ResourceNotFoundException;

public class CustomerNotFoundException extends ResourceNotFoundException {

    public CustomerNotFoundException(Long customerId) {
        super("Customer with ID " + customerId + " doesn't exist!");
    }
}