package de.szut.pms.employee;

import de.szut.pms.common.ResourceNotFoundException;
import de.szut.pms.security.TokenForwarding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class EmployeeRESTClient implements EmployeeClient {

    private final RestClient restClient = RestClient.create();
    private final TokenForwarding tokenForwarding;

    @Value("${employee-service.base-url}")
    private String employeeURL;

    public EmployeeRESTClient(TokenForwarding tokenForwarding) {
        this.tokenForwarding = tokenForwarding;
    }

    @Override
    public EmployeeDto findById(Long id) {
        try {
            return restClient.get()
                    .uri(employeeURL + "/employees/" + id)
                    .header("Authorization", "Bearer " + tokenForwarding.getToken())
                    .retrieve()
                    .body(EmployeeDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Employee with ID " + id + " not found");
        } catch (ResourceAccessException e) {
            throw new EmployeeServiceNotAvailableException(
                    "The Employee Service is unavailable ('" + employeeURL + "/employees/" + id + "').");
        }
    }
}