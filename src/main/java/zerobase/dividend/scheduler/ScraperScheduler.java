package zerobase.dividend.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zerobase.dividend.constants.CacheKey;
import zerobase.dividend.domain.Company;
import zerobase.dividend.domain.Dividend;
import zerobase.dividend.dto.CompanyDto;
import zerobase.dividend.dto.ScrapedResult;
import zerobase.dividend.repository.CompanyRepository;
import zerobase.dividend.repository.DividendRepository;
import zerobase.dividend.scraper.YahooFinanceScraper;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableCaching
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final YahooFinanceScraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;

    // 일정 주기마다 수행
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started");
        // 저장된 회사 목록 조회
        List<Company> companyList = companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        for (Company company : companyList) {
            log.info("scraping scheduler is started -> " + company.getName());
            ScrapedResult scrapedResult = yahooFinanceScraper.scrap(CompanyDto.fromEntity(company));

            // 스크래핑한 배당금 정보 중 없는 값은 저장
            scrapedResult.getDividendDtos().stream()
                    // dividendDto -> dividend(Entity) mapping
                    .map(e -> Dividend.from(company.getId(), e))
                    // 이미 존재하는 데이터인지 확인
                    .filter(e -> !dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate()))
                    // 없으면 저장
                    .forEach(dividendRepository::save);

            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            try {
                Thread.sleep(3000); // 3 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }

    }

}

