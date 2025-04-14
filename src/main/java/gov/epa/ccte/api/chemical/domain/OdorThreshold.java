package gov.epa.ccte.api.chemical.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "odor_thresholds", schema = "rapidtox")
@Entity
public class OdorThreshold {

    @Id
    @Column(name = "dtxsid")
    String dtxsid;
    @Column(name = "odor")
    String odor;
    @Column(name = "odor_threshold")
    String odorThreshold;
}
