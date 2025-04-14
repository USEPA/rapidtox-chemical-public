package gov.epa.ccte.api.chemical.repository;

import gov.epa.ccte.api.chemical.domain.ChemicalSearch;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChemicalSearchRepository extends JpaRepository<ChemicalSearch, Integer> {

	@Query(value
			= """
			select id, dtxsid, search_name, search_value, modified_value, search_group, rank, dtxcid, casrn, smiles, 
			  has_structure_image, preferred_name, is_markush, has_tox 
			  from ( 
				select row_number() over (partition by modified_value, dtxsid order by rank asc) rnk, id, 
				  dtxsid, 
				  search_name, 
				  search_value, 
				  modified_value, 
				  rank, 
				  dtxcid, 
				  casrn, 
				  smiles, 
				  search_group, 
				  has_structure_image, 
				  preferred_name, 
				  is_markush, 
				  has_tox 
				from rapidtox.search_chemical c 
				where modified_value like :word%)
			as aa where rnk = 1
			""",
			nativeQuery = true)
	List<ChemicalSearch> getStartWith(String word);

	List<ChemicalSearch> findAllByModifiedValueInIgnoreCase(List<String> suggestions);

	List<ChemicalSearch> findTop20ByModifiedValueStartsWithOrderByRankAscSearchValueAsc(String word);

	@Query(value
			= """
			select id, 
                   dtxsid,
			       case
			           when lag(modified_value) over (partition by modified_value) is not null OR
			                lead(modified_value) over (partition by modified_value) is not null
			             then search_group || ' - WARNING: Synonym mapped to two or more chemicals'
			             else search_group 
                   end as search_group,
			       preferred_name, 
                   search_name, 
                   search_value, 
                   modified_value, 
                   dtxcid, 
                   rank, 
                   has_structure_image, 
                   casrn, 
                   smiles, 
                   is_markush, 
                   has_tox
			from (
			         select row_number() over (partition by modified_value, dtxsid order by rank asc) as rnk,
			                id, 
                            search_name, 
                            search_group, 
                            preferred_name, 
                            search_value,
                            modified_value,
                            rank,
                            dtxsid,
                            dtxcid,
                            has_structure_image, 
                            casrn, 
                            smiles, 
                            is_markush, 
                            has_tox
			         from rapidtox.search_chemical c
			         where c.modified_value in (:searchWords)
			         group by id, search_name, search_group, preferred_name, 
                       search_value, modified_value, rank, dtxsid, dtxcid, 
                       has_structure_image, casrn, smiles, has_tox
			         order by modified_value
			     ) x
			where rnk = 1
        """,
			nativeQuery = true)
	List<ChemicalSearch> getIdentifierResult(@Param("searchWords") Collection<String> searchWords);

}
