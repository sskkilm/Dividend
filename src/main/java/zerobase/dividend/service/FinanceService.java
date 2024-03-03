package zerobase.dividend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zerobase.dividend.constants.CacheKey;
import zerobase.dividend.domain.Company;
import zerobase.dividend.domain.Dividend;
import zerobase.dividend.dto.CompanyDto;
import zerobase.dividend.dto.DividendDto;
import zerobase.dividend.dto.ScrapedResult;
import zerobase.dividend.exception.impl.NoCompanyException;
import zerobase.dividend.repository.CompanyRepository;
import zerobase.dividend.repository.DividendRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    // 요청이 자주 들어오는가?
    // 자주 변경되는 데이터인가?
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("search company -> " + companyName);
        // 회사명을 기준으로 회사 정보를 조회
        Company company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new NoCompanyException());

        // 조회된 회사 아이디로 배당금을 조회
        List<Dividend> dividendList = dividendRepository.findAllByCompanyId(company.getId());

        // 결과 조합 후 반환
        return ScrapedResult.builder()
                .companyDto(CompanyDto.fromEntity(company))
                .dividendDtos(dividendList.stream()
                        .map(DividendDto::fromEntiy)
                        .collect(Collectors.toList()))
                .build();
    }

}
