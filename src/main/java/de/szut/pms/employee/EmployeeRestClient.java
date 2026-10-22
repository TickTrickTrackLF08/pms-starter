package de.szut.pms.employee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class EmployeeRestClient implements EmployeeClient {

    private final RestClient restClient = RestClient.create();
   // private final EmployeeMapper employeeMapper;

    @Value("${employee-service.base-url}")
    private String employeeURL;

//    public EmployeeRestClient(RestClient restClient/**, EmployeeMapper employeeMapper*/) {
//        this.restClient = restClient;
//      //  this.employeeMapper = employeeMapper;
//    }

    @Override
    public EmployeeDto findById(Long id) {
        EmployeeDto employee = restClient.get()    //HTTP Request
                .uri(employeeURL + "/employees/"  + id)
            /** .header("Authorization", "Bearer "+ token) Lucas ??? */
                .retrieve() //sendet request
                .body(EmployeeDto.class);   //JACKSON wandelt es in das DTO um
        return employee;
    }
}

