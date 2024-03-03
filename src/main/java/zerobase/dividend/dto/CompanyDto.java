package zerobase.dividend.dto;

import lombok.*;
import zerobase.dividend.domain.Company;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CompanyDto {

    private String ticker;

    private String name;

    public static CompanyDto fromEntity(Company company) {
        return CompanyDto.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build();
    }
}
