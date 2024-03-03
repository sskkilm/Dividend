package zerobase.dividend.scraper;

import zerobase.dividend.dto.CompanyDto;
import zerobase.dividend.dto.ScrapedResult;

public interface Scraper {

    ScrapedResult scrap(CompanyDto companyDto);

    CompanyDto scrapCompanyByTicker(String ticker);

}
