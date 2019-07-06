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
	  
	  @Query("select s from SignalBewertung s INNER JOIN s.signalAlgorithmus sa INNER JOIN s.indikatorAlgorithmen ia"
		  		+ " WHERE sa.name =?1 AND sa.p1name=?2 AND sa.p1wert=?3"
		  		+ " AND ia.name =?4 AND ia.p1name=?5 AND ia.p1wert=?6")
		  List<SignalBewertung> findBySignalAND1Indikator(String signalName, String sPName1, Object sPWert1, String indikatorName, String iPName1, Object iPWert2);

/*	  @Query("select s from SignalBewertung s INNER JOIN s.signalAlgorithmus sa INNER JOIN s.indikatorAlgorithmen ia "
	  		+ " WHERE sa.name =?1 AND sa.p1name=?2 AND sa.p1wert=?2"
			+ " AND ia.name =?4 AND ia.p1name=?5 AND i.p1wert=?6")
	  List<SignalBewertung> findBySignalAnd1Indikator(String signalName, String sPName1, String sPWert1, String indikatorName, 
			String iPName1, String iPWert1);
*/
	  @Query("select s from SignalBewertung s INNER JOIN s.indikatorAlgorithmen i WHERE i.name =?1 "
			  	+ "AND i.p1name=?2 AND i.p1wert=?3")
	  List<SignalBewertung> findByIndikator(String indikatorName, 
			String typ1, String wert1);
	  
	  @Query("select s from SignalBewertung s INNER JOIN s.indikatorAlgorithmen i WHERE i.name =?1"
			  + " AND i.p1name=?2 AND i.p1wert=?3"
			  + " AND i.p2name=?4 AND i.p2wert=?5")
	  List<SignalBewertung> findByIndikator(String indikatorName, 
			  String typ1, String wert1,
			  String typ2, String wert2);
	  
/*	  @Query("select s from SignalBewertung s INNER JOIN s.indikatorAlgorithmen i WHERE i.name =?1"
			  + " AND i.p1name=?2 AND i.p1wert=?3"
			  + " AND i.p2name=?4 AND i.p2wert=?5"
			  + " AND i.p3name=?6 AND i.p3wert=?7")
	  List<SignalBewertung> findByIndikator(String indikatorName, 
			  String typ1, String wert1,
			  String typ2, String wert2,
			  String typ3, String wert3);
	  
	  @Query("select s from SignalBewertung s INNER JOIN s.indikatorAlgorithmen i WHERE i.name =?1"
			  + " AND i.p1name=?2 AND i.p1wert=?3" 
			  + " AND i.p2name=?4 AND i.p2wert=?5"
			  + " AND i.p3name=?6 AND i.p3wert=?7"
			  + " AND i.p4name=?8 AND i.p4wert=?9")
	  List<SignalBewertung> findByIndikator(String indikatorName, 
			  String typ1, String wert1,
			  String typ2, String wert2,
			  String typ3, String wert3,
			  String typ4, String wert4);
	  
	  @Query("select s from SignalBewertung s INNER JOIN s.indikatorAlgorithmen i WHERE i.name =?1"
			  + " AND i.p1name=?2 AND i.p1wert=?3"
			  + " AND i.p2name=?4 AND i.p2wert=?5"
			  + " AND i.p3name=?6 AND i.p3wert=?7"
			  + " AND i.p4name=?8 AND i.p4wert=?9"
			  + " AND i.p5name=?10 AND i.p5wert=?11")
	  List<SignalBewertung> findByIndikator(String indikatorName, 
			  String typ1, String wert1,
			  String typ2, String wert2,
			  String typ3, String wert3,
			  String typ4, String wert4,
			  String typ5, String wert5);
*/
	  
}
