package zerobase.dividend.domain;

import lombok.*;
import zerobase.dividend.dto.DividendDto;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "DIVIDEND")
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"companyId", "date"}
                )
        }
)
public class Dividend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;

    private LocalDateTime date;

    private String dividend;

    public static Dividend from(Long companyId, DividendDto dividendDto) {
        return Dividend.builder()
                .companyId(companyId)
                .date(dividendDto.getDate())
                .dividend(dividendDto.getDividend())
                .build();
    }
}
