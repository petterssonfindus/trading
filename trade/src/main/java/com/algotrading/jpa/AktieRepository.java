package com.algotrading.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.CrudRepository;

import com.algotrading.aktie.Aktie;

public interface AktieRepository extends CrudRepository<Aktie, Long> {

	public List<Aktie> findByNameLikeIgnoreCase(String name);

	// @EntityGraph(attributePaths = { "kurse" })  // AD-HOC EntityGraph funktioniert auch
	@EntityGraph(value = "aktie.kurs", type = EntityGraphType.LOAD)
	public Optional<Aktie> findById(Long id);

	List<Aktie> findByQuelle(int quelle);

}
