package gov.epa.ccte.api.chemical.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChemicalSearchRequest {
    private String searchItems; // EOL separated
}
