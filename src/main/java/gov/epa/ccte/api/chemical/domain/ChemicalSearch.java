package gov.epa.ccte.api.chemical.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

import jakarta.persistence.*;

@Entity
@Data
@Immutable
@Table(name = "search_chemical", schema = "rapidtox")
public class ChemicalSearch {
    @Id
    Integer id;

    @Column(name = "dtxsid")
    @JsonProperty("dtxsid")
    private String dtxsid;

    @Column(name = "dtxcid")
    @JsonProperty("dtxcid")
    private String dtxcid;

    // column for UI - it is not in table
    @Transient
    @JsonProperty("selected")
    private Boolean selected;

    // column for UI - it is not in table
    @Transient
    @JsonProperty("nioshLink")
    private String nioshLink;

    // column for UI - it is not in table
    @Transient
    @JsonProperty("safetyLink")
    private String safetyLink;
    
    // column for UI - it is not in table
    @Transient
    @JsonProperty("odorThreshold")
    private String odorThreshold;

    @Column(name = "preferred_name")
    @JsonProperty("preferredName")
    private String preferredName;

    @Column(name = "search_name")
    @JsonProperty(value = "searchMatch")
    private String searchName;

    @Column(name = "search_group")
    @JsonProperty(value = "searchGroup")
    private String searchGroup;

    @Column(name = "search_value")
    @JsonProperty("searchWord")
    private String searchValue;

    @Column(name = "modified_value")
    @JsonProperty("modifiedValue")
    private String modifiedValue;

    @Column(name = "rank")
    @JsonProperty("rank")
    private Integer rank;

    @Column(name = "has_structure_image")
    @JsonProperty("hasStructureImage")
    private Boolean hasStructureImage;

    @Column(name = "casrn")
    @JsonProperty("casrn")
    private String casrn;

    @Column(name = "smiles")
    @JsonProperty("smiles")
    private String smiles;

    @Column(name = "is_markush")
    @JsonProperty("isMarkush")
    private Boolean isMarkush;

    @Column(name = "has_tox")
    @JsonProperty("hasTox")
    private Boolean hasTox;


}
