package gov.epa.ccte.api.chemical.repository;

import gov.epa.ccte.api.chemical.domain.OdorThreshold;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OdorThresholdRepository extends JpaRepository<OdorThreshold, String> {

	List<OdorThreshold> findAllByDtxsidInIgnoreCase(List<String> dtxsids);
}
