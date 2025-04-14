package gov.epa.ccte.api.chemical.web.rest;

import gov.epa.ccte.api.chemical.domain.ChemicalSearchRequest;
import gov.epa.ccte.api.chemical.domain.ChemicalSearch;
import gov.epa.ccte.api.chemical.service.ChemicalSearchService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for getting the
 * {@link gov.epa.ccte.api.chemical.domain.ChemicalSearch}s.
 */
@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class ChemicalSearchResource {

    private final ChemicalSearchService svc;

    @GetMapping("test")
    public String greeting() {
        return "chemical";
    }

    @GetMapping("search/start-with/{word}")
    List<ChemicalSearch> startWith(
            @Parameter(required = true, description = "Starting characters for search word.",
                    examples = {
                        @ExampleObject(name = "DSSTox Substance Identifier", value = "DTXSID7020182", description = "Starting part of DTXSID"),
                        @ExampleObject(name = "DSSTox Compound Identifier", value = "DTXCID505", description = "Starting part of DTXCID"),
                        @ExampleObject(name = "Synonym Starting characters for bpa", value = "bpa", description = "Synonym for Bisphenol A"),
                        @ExampleObject(name = "Synonym Starting characters for atrazine", value = "atraz", description = "URLencoded starting characters of chemical name"),
                        @ExampleObject(name = "CASRN", value = "1912-24", description = "Starting part of CASRN"),
                        @ExampleObject(name = "InChIKey", value = "MXWJVTOOROXGIU", description = "For InChIKey starting 13 characters are needed")
                    })
            @PathVariable("word") String word,
            @Parameter(description = "Limit the number of returned results.", examples = @ExampleObject(value = "20"))
            @RequestParam(name = "top", required = false, defaultValue = "500") Integer top) {

        log.debug("search - start-with={}", word);

        return svc.simpleStartsWithSearch(word, top);
    }


    @GetMapping("search/start-with2/{word}")
    ResultWithSuggestions startWithSuggestions(@PathVariable("word") String word,
            @Parameter(description = "Limit the number of returned results.", examples = @ExampleObject(value = "20"))
            @RequestParam(name = "top", required = false, defaultValue = "500") Integer top) {

        log.debug("search word = {}", word);
        log.debug("top = {}", top);

        return svc.startsWithSearchWithSuggestions(word, top);
    }


    @PostMapping("search")
    public @ResponseBody
    List<ChemicalSearch> searchChemicals(@RequestBody ChemicalSearchRequest request) {
        String rawSearchTerms = request.getSearchItems();

        log.debug("multi search = {}", rawSearchTerms);

        return svc.chemicalSearch(rawSearchTerms);
    }

}
