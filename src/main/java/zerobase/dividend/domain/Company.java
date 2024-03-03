package zerobase.dividend.domain;

import lombok.*;
import zerobase.dividend.dto.CompanyDto;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "COMPANY")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticker;

    private String name;

    public static Company from(CompanyDto companyDto) {
        return Company.builder()
                .ticker(companyDto.getTicker())
                .name(companyDto.getName())
                .build();
    }
}
