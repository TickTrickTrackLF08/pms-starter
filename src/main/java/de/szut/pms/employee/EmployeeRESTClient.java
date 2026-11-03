package de.szut.pms.employee;
import de.szut.pms.security.TokenForwarding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
//import org.springframework.web.client.ResourceAccessException

@Component
public class EmployeeRESTClient implements EmployeeClient {

    private final TokenForwarding token;

    public EmployeeRESTClient(TokenForwarding token){
        this.token = token;
    }

    private final RestClient restClient = RestClient.create();

    @Value("${employee-service.base-url}")
    private String employeeURL;


    @Override
    public EmployeeDto findById(Long id) {
        try {
            String key = token.getToken();
            EmployeeDto employee = restClient.get()    //HTTP Request
                    .uri(employeeURL + "/employees/" + id)
                    .header("Authorization", "Bearer "+ key)
                    .retrieve() //sendet request
                    .body(EmployeeDto.class);   //JACKSON wandelt es in das DTO um
            return employee;
        }catch(Exception e){
            throw new EmployeeServiceNotAvailableException("Der Employee-Service ist nicht erreichbar ('" + employeeURL + "/employees/" + id + "').");
        }
    }
}



