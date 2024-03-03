package zerobase.dividend.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import zerobase.dividend.domain.Company;
import zerobase.dividend.domain.Dividend;
import zerobase.dividend.dto.CompanyDto;
import zerobase.dividend.dto.ScrapedResult;
import zerobase.dividend.exception.impl.AlreadyExistCompanyException;
import zerobase.dividend.exception.impl.NoCompanyException;
import zerobase.dividend.repository.CompanyRepository;
import zerobase.dividend.repository.DividendRepository;
import zerobase.dividend.scraper.Scraper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;


    public CompanyDto save(String ticker) {

        if (companyRepository.existsByTicker(ticker)) {
            throw new AlreadyExistCompanyException();
        }

        return storeCompanyAndDividend(ticker);
    }

    public Page<CompanyDto> getAllCompany(Pageable pageable) {
        return new PageImpl<>(companyRepository.findAll(pageable).stream()
                .map(CompanyDto::fromEntity)
                .collect(Collectors.toList())
        );
    }

    private CompanyDto storeCompanyAndDividend(String ticker) {
        // ticker를 기준으로 회사를 스크래핑
        CompanyDto companyDto = yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (companyDto == null) {
            throw new NoCompanyException();
        }

        // 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = yahooFinanceScraper.scrap(companyDto);


        // 스크래핑 결과 저장
        Company company = companyRepository.save(Company.from(companyDto));
        List<Dividend> dividendList = scrapedResult.getDividendDtos().stream()
                .map(e -> Dividend.from(company.getId(), e))
                .toList();
        dividendRepository.saveAll(dividendList);

        return companyDto;
    }

    public Page<String> getCompanyNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);
        return new PageImpl<>(companyRepository.findByNameStartingWithIgnoreCase(keyword, limit)
                .stream().map(Company::getName).toList());
    }

    public void addAutoCompleteKeyword(String keyword) {
        trie.put(keyword, null);
    }

    public List<String> autoComplete(String keyword) {
        return (List<String>) trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    }

    public void deleteAutoCompleteKeyword(String keyword) {
        trie.remove(keyword);
    }

    public String deleteCompany(String ticker) {
        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new NoCompanyException());
        dividendRepository.deleteAllByCompanyId(company.getId());
        companyRepository.delete(company);

        deleteAutoCompleteKeyword(company.getName());

        return company.getName();
    }
}
