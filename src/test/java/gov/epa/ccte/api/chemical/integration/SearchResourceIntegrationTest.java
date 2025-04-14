package gov.epa.ccte.api.chemical.integration;

import gov.epa.ccte.api.chemical.ChemicalApplication;
import gov.epa.ccte.api.chemical.domain.ChemicalSearch;
import gov.epa.ccte.api.chemical.repository.ChemicalSearchRepository;
import gov.epa.ccte.api.chemical.web.rest.ResultWithSuggestions;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {ChemicalApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class SearchResourceIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ChemicalSearchRepository searchRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        int id = 1;
        searchRepository.save(bpa(id++));
        searchRepository.save(caffine(id++));
        searchRepository.save(caffeine(id++));
        List<ChemicalSearch> list = searchRepository.findAll();
        assertThat(list.size()).isEqualTo(3);
    }

    private ChemicalSearch caffeine(final int id) {
        ChemicalSearch cs = caffine(id);
        cs.setSearchValue("Caffeine");
        cs.setModifiedValue("CAFFEINE");
        return cs;
    }

    private ChemicalSearch bpa(Integer id) {
        ChemicalSearch cs = new ChemicalSearch();
        cs.setId(id);
        cs.setCasrn("80-05-7");
        cs.setDtxsid("DTXSID7020182");
        cs.setDtxcid("DTXCID30182");
        cs.setHasStructureImage(true);
        cs.setHasTox(true);
        cs.setIsMarkush(false);
        cs.setModifiedValue("BPA");
        cs.setNioshLink("http://niosh.link.epa.gov");
        cs.setOdorThreshold("odor threshold");
        cs.setPreferredName("Bisphenol A");
        cs.setRank(15);
        cs.setSafetyLink("http://safety.link.epa.gov");
        cs.setSearchGroup("Synonym");
        cs.setSmiles("CC(C)(C1=CC=C(O)C=C1)C1=CC=C(O)C=C1");
        cs.setSearchGroup("Synonym");
        cs.setSearchName("Synonym");
        cs.setSearchValue("BPA");
        cs.setModifiedValue("BPA");
        return cs;
    }

    private ChemicalSearch caffine(Integer id) {
        ChemicalSearch cs = new ChemicalSearch();
        cs.setId(id);
        cs.setCasrn("58-08-2");
        cs.setDtxsid("DTXSID0020232");
        cs.setDtxcid("DTXCID40232");
        cs.setHasStructureImage(true);
        cs.setHasTox(true);
        cs.setIsMarkush(false);
        cs.setModifiedValue("CAFFINE");
        cs.setNioshLink("http://niosh.link.epa.gov");
        cs.setOdorThreshold("odor threshold");
        cs.setPreferredName("Caffeine");
        cs.setRank(15);
        cs.setSafetyLink("http://safety.link.epa.gov");
        cs.setSearchGroup("Synonym");
        cs.setSmiles("CN1C=NC2=C1C(=O)N(C)C(=O)N2C");
        cs.setSearchGroup("Synonym");
        cs.setSearchName("Synonym");
        cs.setSearchValue("Caffine");
        cs.setModifiedValue("CAFFINE");
        return cs;
    }

    @Test
    public void returnSearchChemicalWithHttpStatusCode200() throws Throwable {

        try {

            ResponseEntity<List<ChemicalSearch>> result
                    = testRestTemplate.exchange(startWithUrl("BPA"),
                            HttpMethod.GET,
                            null, chemicalSearchList());
            assertEquals(200, result.getStatusCode().value());
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertThat(result.hasBody()).isTrue();
            assertThat(result.getBody().isEmpty()).isFalse();

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenChemical_shouldReturnResults() throws Throwable {

        try {

            ResponseEntity<ResultWithSuggestions> result
                    = testRestTemplate.exchange(startWithUrl2("BPA"),
                            HttpMethod.GET,
                            null, ResultWithSuggestions.class);
            assertEquals(200, result.getStatusCode().value());
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertThat(result.hasBody()).isTrue();
            assertThat(result.getBody().getResult().isEmpty()).isFalse();

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenMisspelledChemical_shouldReturnSuggestions() throws Throwable {

        try {

            ResponseEntity<ResultWithSuggestions> result
                    = testRestTemplate.exchange(startWithUrl2("caffiene"),
                            HttpMethod.GET,
                            null, ResultWithSuggestions.class);
            assertEquals(200, result.getStatusCode().value());
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertThat(result.hasBody()).isTrue();
            assertThat(result.getBody().getSuggestions().isEmpty()).isFalse();

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void returnSearchChemicalWithHttpStatusCode404() throws Throwable {
        try {
            ResponseEntity<String> result = testRestTemplate.getForEntity(baseUrl() + "/124", String.class);
            assertEquals(404, result.getStatusCode().value());
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }

    }

    @Test
    @DisplayName("search resource not found returning empty array")
    public void returnSearchChemicalEmptyByDtxsidWithHttpStatusCode200() throws Throwable {
        try {
            ResponseEntity<List<ChemicalSearch>> result = testRestTemplate.exchange(startWithUrl("B2Q"),
                    HttpMethod.GET,
                    null,
                    chemicalSearchList());
            assertEquals(200, result.getStatusCode().value());
            assertThat(result.hasBody()).isTrue();
            assertThat(result.getBody().isEmpty()).isTrue();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }

    }

    private static ParameterizedTypeReference<List<ChemicalSearch>> chemicalSearchList() {
        return new ParameterizedTypeReference<List<ChemicalSearch>>() {
        };
    }

    private String startWithUrl(String startWith) {
        return baseUrl() + "/search/start-with/" + startWith;
    }

    private String startWithUrl2(String startWith) {
        return baseUrl() + "/search/start-with2/" + startWith;
    }

    private String baseUrl() {
        log.debug("{} is random port ", port);
        return "http://localhost:" + port;
    }

}
