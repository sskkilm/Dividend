package zerobase.dividend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zerobase.dividend.domain.Dividend;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DividendRepository extends JpaRepository<Dividend, Long> {

    List<Dividend> findAllByCompanyId(Long id);

    boolean existsByCompanyIdAndDate(Long id, LocalDateTime date);

    @Transactional
    void deleteAllByCompanyId(Long id);

}
