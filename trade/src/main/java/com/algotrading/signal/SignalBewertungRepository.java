package com.algotrading.signal;

import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SignalBewertungRepository extends CrudRepository<SignalBewertung, Long> {
	  List<SignalBewertung> findByAktieName(String aktieName);
	  List<SignalBewertung> findByAktieNameAndTage(String aktieName, int tage);
	  List<SignalBewertung> findByZeitraumBeginn(GregorianCalendar zeitraumBeginn);
	  List<SignalBewertung> findByAktieNameAndTageAndZeitraumBeginn(String aktieName, int tage,GregorianCalendar zeitraumBeginn);
	  
	  @Query("select s from SignalBewertung s")
	  List<SignalBewertung> findAllByQuery();
	  
	  @Query("select s from SignalBewertung s where s.aktieName = ?1")
	  List<SignalBewertung> findByAktieNameByQuery(String aktieName);
	  
}
