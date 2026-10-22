package de.szut.pms.employee;

public interface EmployeeClient {
    EmployeeDto findById(Long id);
}
//Projektservice der anderen -> Employeeclient -> Employee-Service
//Repository wird druch Employee-Client ersetzet

