package gov.epa.ccte.api.chemical.service;

import gov.epa.ccte.api.chemical.domain.ChemicalSearch;
import gov.epa.ccte.api.chemical.domain.NioshLinks;
import gov.epa.ccte.api.chemical.domain.OdorThreshold;
import gov.epa.ccte.api.chemical.domain.SafetyLinks;
import gov.epa.ccte.api.chemical.repository.ChemicalSearchRepository;
import gov.epa.ccte.api.chemical.repository.NioshLinksRepository;
import gov.epa.ccte.api.chemical.repository.OdorThresholdRepository;
import gov.epa.ccte.api.chemical.repository.SafetyLinksRepository;
import gov.epa.ccte.api.chemical.web.rest.ResultWithSuggestions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChemicalSearchService {

    private final ChemicalSearchRepository searchRepository;
    private final NioshLinksRepository nioshLinksRepository;
    private final SafetyLinksRepository safetyLinksRepository;
    private final OdorThresholdRepository odorThresholdRepository;
    private final CaffeineFixConversionService caffeineFixService;

    public List<ChemicalSearch> chemicalSearch(String rawSearchTerms) {

        if (rawSearchTerms == null) {
            rawSearchTerms = "";
        }

        final String[] searchTerms = preprocessingSearchWords(rawSearchTerms.split("\n"));
        log.debug("multi chemical search for {} chemicals", searchTerms.length);

        List<ChemicalSearch> searchResult = searchRepository.getIdentifierResult(Arrays.asList(searchTerms));

        insertNotFound(searchResult, searchTerms);

        // get a list of all the discovered DTXSIDs
        final List<String> searchResultsDtxsids = searchResult.stream()
                .filter(result -> result.getDtxsid() != null)
                .map(ChemicalSearch::getDtxsid)
                .collect(Collectors.toList());

        // Add NIOSH Links If present
        List<NioshLinks> nioshLinks = nioshLinksRepository.findAllByDtxsidInIgnoreCase(searchResultsDtxsids);
        decorateWithNioshLinks(searchResult, nioshLinks);

        // Add Safety Links if present
        List<SafetyLinks> safetyLinks = safetyLinksRepository.findAllByDtxsidInIgnoreCase(searchResultsDtxsids);
        decorateWithSafetyLinks(searchResult, safetyLinks);

        // Add Odor data if present
        List<OdorThreshold> odors = odorThresholdRepository.findAllByDtxsidInIgnoreCase(searchResultsDtxsids);
        decorateWithOdorThresholds(searchResult, odors);

        return searchResult;
    }

    private void insertNotFound(List<ChemicalSearch> searchResults, String[] searchTerms) {
        HashSet<String> notFoundSearchTerms = new HashSet<>(Arrays.asList(searchTerms));
        for (ChemicalSearch searchResult : searchResults) {
            if (notFoundSearchTerms.contains(searchResult.getModifiedValue())) {
                notFoundSearchTerms.remove(searchResult.getModifiedValue());
            }
        }
        // add NOT FOUND records
        for (String searchTerm : notFoundSearchTerms) {
            searchResults.add(createNotFoundResultFor(searchTerm));
        }
    }

    private ChemicalSearch createNotFoundResultFor(String search) {
        ChemicalSearch chemicalSearch = new ChemicalSearch();
        chemicalSearch.setSearchGroup("NOT FOUND");
        chemicalSearch.setSearchValue(search);
        return chemicalSearch;
    }

    private void decorateWithSafetyLinks(List<ChemicalSearch> searchResults, List<SafetyLinks> safetyLinks) {
        searchResults.forEach(result -> {
            String resultDtxsid = result.getDtxsid();
            Optional<SafetyLinks> findSafetyLink = safetyLinks.stream().filter(safetyLink -> safetyLink.getDtxsid().equals(resultDtxsid)).findFirst();
            if (findSafetyLink.isPresent()) {
                result.setSafetyLink(findSafetyLink.get().getDtxsid());
            }
        });
    }

    private void decorateWithNioshLinks(List<ChemicalSearch> searchResults, List<NioshLinks> nioshLinks) {
        searchResults.forEach(result -> {
            String resultDtxsid = result.getDtxsid();
            Optional<NioshLinks> findNioshLink = nioshLinks.stream().filter(nioshLink -> nioshLink.getDtxsid().equals(resultDtxsid)).findFirst();
            if (findNioshLink.isPresent()) {
                result.setNioshLink(findNioshLink.get().getLink());
            }
        });
    }

    private void decorateWithOdorThresholds(List<ChemicalSearch> searchResults, List<OdorThreshold> odors) {
        searchResults.forEach(result -> {
            String resultDtxsid = result.getDtxsid();
            Optional<OdorThreshold> findOdor = odors.stream().filter(odor -> odor.getDtxsid().equals(resultDtxsid)).findFirst();
            if (findOdor.isPresent()) {
                result.setOdorThreshold(findOdor.get().getOdorThreshold());
            }
        });
    }

    public List<ChemicalSearch> simpleStartsWithSearch(String word, Integer top) {
        final String searchTerm = preprocessingSearchWord(word);
        List<ChemicalSearch> searchResult = searchRepository.getStartWith(searchTerm);
        log.debug("found {} results", searchResult.size());
        if (top != null && top > 1) {
            searchResult = searchResult.stream().limit(top).toList();
        }
        log.debug("returning {} results", searchResult.size());
        return searchResult;
    }

    public ResultWithSuggestions startsWithSearchWithSuggestions(String word, Integer top) {
        final String searchTerm = preprocessingSearchWord(word);
        List<ChemicalSearch> result = searchRepository.getStartWith(searchTerm);
        if (top != null && top > 0) {
            result = result.stream().limit(top).toList();
        }
        if (result.isEmpty()) {
            return handleEmptySearchResult(word, top);
        } else {
            return handleSearchResults(word, result);
        }
    }

    private ResultWithSuggestions handleEmptySearchResult(String word, Integer top) {
        List<String> caffieneFixSuggestions = caffeineFixService.CaffeineFixName(word);
        List<ChemicalSearch> suggestions = null;
        if (caffieneFixSuggestions != null) {
            suggestions = searchRepository.findAllByModifiedValueInIgnoreCase(caffieneFixSuggestions);
            if (top != null && top > 0) {
                suggestions = suggestions.stream().limit(top).toList();
            }
            return ResultWithSuggestions.builder().suggestions(suggestions).build();
        }
        return new ResultWithSuggestions();
    }

    private ResultWithSuggestions handleSearchResults(String word, List<ChemicalSearch> result) {
        if (isDtxsid(word) && resultListContainsDtxsid(result, word)) {
            return ResultWithSuggestions.builder()
                    .result(buildSingleItemListWithFirstMatchingDtxsidEntry(result, word))
                    .build();
        } else {
            return ResultWithSuggestions.builder().result(result).build();
        }
    }

    public static String[] preprocessingSearchWords(String[] words) {
        String[] searchWords = new String[words.length];
        int i = 0;
        for (String word : words) {
            searchWords[i] = preprocessingSearchWord(word);
            i++;
        }
        return searchWords;
    }

    public static boolean isCasrn(String casrn) {
        return casrn.matches("^\\d{1,7}-\\d{2}-\\d$");
    }

    public static String preprocessingSearchWord(String searchWord) {
        // From https://confluence.epa.gov/display/CCTEA/Search+Requirements
        StringBuilder searchWordBuilder = new StringBuilder(searchWord);
        // RAPIDTOX-258
        // Set character to upper case. String.toUpperCase causes an issue with certain foreign locale characters
        for (int index = 0; index < searchWord.length(); index++) {
            searchWordBuilder.setCharAt(index, Character.toUpperCase(searchWord.charAt(index)));
        }
        String newSearchString = searchWordBuilder.toString().trim();
        if (isCasrn(newSearchString)) {
            log.debug("{} is a casrn ", newSearchString);
            return newSearchString;
        } else {
            newSearchString = newSearchString.replaceAll("-", " ");
            log.debug("preprocessed search word = {}", newSearchString);
            return newSearchString;
        }
    }

    private static List<ChemicalSearch> buildSingleItemListWithFirstMatchingDtxsidEntry(List<ChemicalSearch> result, String dtxsid) {
        List<ChemicalSearch> resultList = new ArrayList<>();
        resultList.add(result.stream().filter(o -> o.getDtxsid().equals(dtxsid)).findFirst().orElse(null));
        return resultList;
    }

    private static boolean isDtxsid(String word) {
        return word.toUpperCase().contains("DTXSID");
    }

    private static boolean resultListContainsDtxsid(List<ChemicalSearch> result, String dtxsid) {
        return result.stream().filter(o -> o.getDtxsid().equals(dtxsid)).findFirst().isPresent();
    }

}
