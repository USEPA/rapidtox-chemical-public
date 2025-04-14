package gov.epa.ccte.api.chemical.integration;

import gov.epa.ccte.api.chemical.service.CaffeineFixConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.Test;

@SpringBootTest
public class CaffeineFixTest {

    @Autowired
    CaffeineFixConversionService service;

    @Test
    public void TestCaffeineString (){
        List<String> suggestions = service.CaffeineFixName("Caffiene");

        assertThat(suggestions.isEmpty()).isFalse();

    }

}
