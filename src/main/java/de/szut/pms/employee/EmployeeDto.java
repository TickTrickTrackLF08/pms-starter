package de.szut.pms.employee;

import java.util.List;

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
//Beim Empfanger der Antwort vom Employee-Service, Sping wandelt das JSON in das DTO um
