package gov.epa.ccte.api.chemical.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Data
@Table(name = "safety_chemicals", schema = "rapidtox")
public class SafetyLinks {
    @Id
    Integer id;

    @Column(name = "dtxsid", length = 20, nullable = false)
    @JsonProperty("dtxsid")
    private String dtxsid;
}
