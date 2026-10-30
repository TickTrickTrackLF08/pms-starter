package de.szut.pms.employee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
//import org.springframework.web.client.ResourceAccessException

@Component
public class EmployeeRESTClient implements EmployeeClient {

    private final RestClient restClient = RestClient.create();

    @Value("${employee-service.base-url}")
    private String employeeURL;

    @Override
    public EmployeeDto findById(Long id) {
        try {
            EmployeeDto employee = restClient.get()    //HTTP Request
                    .uri(employeeURL + "/employees/" + id)
                    /** .header("Authorization", "Bearer "+ token) Lucas ??? */
                    .retrieve() //sendet request
                    .body(EmployeeDto.class);   //JACKSON wandelt es in das DTO um
            return employee;
        }catch(Exception e){
//      }catch(ResourceAccessException e){
            throw new EmployeeServiceNotAvailableException("Der Employee-Service ist nicht erreichbar ('" + employeeURL + "/employees/" + id + "').");
        }
// gerade wird noch die Service-nicht-verfügbar geworfen, egal was schief geht. Überarbeiten, wenn der JWT dazukommt!!!!!!!!!!

    }
}



