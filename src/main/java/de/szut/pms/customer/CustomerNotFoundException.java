package de.szut.pms.customer;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long customerId) {
        super("Kunde mit der ID " + customerId + " existiert nicht!");
    }
}