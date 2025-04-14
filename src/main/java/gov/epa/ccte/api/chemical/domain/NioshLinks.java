package gov.epa.ccte.api.chemical.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Data
@Table(name = "niosh_links", schema = "rapidtox")
public class NioshLinks {
    @Id
    Integer id;

    @Column(name = "dtxsid", length = 20, nullable = false)
    @JsonProperty("dtxsid")
    private String dtxsid;

    @Column(name = "link", nullable = false)
    @JsonProperty("link")
    private String link;
}
