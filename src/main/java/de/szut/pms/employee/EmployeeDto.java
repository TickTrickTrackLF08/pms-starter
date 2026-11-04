package de.szut.pms.employee;

import java.util.List;

//Corresponds to Employee-Service response
public record EmployeeDto(
        Long id,
        String lastName,
        String firstName,
        String street,
        String postcode,
        String city,
        String phone,
        List<SkillDto> skillSet
) {
}
