package gov.epa.ccte.api.chemical.repository;

import gov.epa.ccte.api.chemical.domain.SafetyLinks;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SafetyLinksRepository extends JpaRepository<SafetyLinks, Integer> {

	List<SafetyLinks> findAllByDtxsidInIgnoreCase(List<String> dtxsids);
}
