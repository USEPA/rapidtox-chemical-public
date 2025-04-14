package gov.epa.ccte.api.chemical.repository;

import gov.epa.ccte.api.chemical.domain.NioshLinks;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NioshLinksRepository extends JpaRepository<NioshLinks, Integer> {
    List<NioshLinks> findAllByDtxsidInIgnoreCase(List<String> dtxsids);
}
