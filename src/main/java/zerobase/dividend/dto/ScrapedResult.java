package zerobase.dividend.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ScrapedResult {

    private CompanyDto companyDto;

    @Builder.Default
    private List<DividendDto> dividendDtos = new ArrayList<>();

}
