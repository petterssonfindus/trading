package com.algotrading.jpa;

import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.algotrading.signal.SignalBewertung;

public interface SignalBewertungRepository extends CrudRepository<SignalBewertung, Long> {
	List<SignalBewertung> findByAktieName(String aktieName);

	List<SignalBewertung> findByAktieNameAndTage(String aktieName, int tage);

	List<SignalBewertung> findByZeitraumBeginn(GregorianCalendar zeitraumBeginn);

	List<SignalBewertung> findByAktieNameAndTageAndZeitraumBeginn(String aktieName, int tage,
			GregorianCalendar zeitraumBeginn);

	@Query("select s from SignalBewertung s")
	List<SignalBewertung> findAllByQuery();

	@Query("select s from SignalBewertung s where s.aktieName = ?1")
	List<SignalBewertung> findByAktieNameByQuery(String aktieName);

	@Query("select s from SignalBewertung s INNER JOIN s.signalAlgorithmus sa"
			+ " WHERE s.aktieName = ?1 AND sa.name =?2 AND s.tage = ?3 AND sa.p1name=?4 AND sa.p1wert=?5")
	List<SignalBewertung> findBySignal(String aktieName, String signalName, int tage, String sPName1, Object sPWert1);

	/*
	 * @Query("SELECT sb FROM (signalbewertungen sb INNER JOIN signal_algorithmus sa ON sb.signal_algoid = sa.id) INNER JOIN indikatoralgo ia ON ia.said = sa.id"
	 * ) List<SignalBewertung> findByAll(String aktieName, String signalName, int
	 * tage, String sPName1, Object sPWert1);
	 */

	/*
	 * @Query("select s from SignalBewertung s INNER JOIN s.signalAlgorithmus sa INNER JOIN s.indikatorAlgorithmen ia"
	 * +
	 * " WHERE s.aktieName = ?1 AND sa.name =?2 AND s.tage = ?3 AND sa.p1name=?4 AND sa.p1wert=?5"
	 * + " AND ia.name =?6 AND ia.p1name=?7 AND ia.p1wert=?8") List<SignalBewertung>
	 * findBySignalAND1Indikator(String aktieName, String signalName, int tage,
	 * String sPName1, Object sPWert1, String indikatorName, String iPName1, Object
	 * iPWert2);
	 */
	/*
	 * @Query("select s from SignalBewertung s INNER JOIN s.signalAlgorithmus sa INNER JOIN s.indikatorAlgorithmen ia INNER JOIN s.indikatorAlgorithmen ia2"
	 * + " WHERE sa.name =?1 AND sa.p1name=?2 AND sa.p1wert=?3" +
	 * " AND ia.name =?4 AND ia.p1name=?5 AND ia.p1wert=?6" +
	 * " AND ia2.name =?7 AND ia2.p1name=?8 AND ia2.p1wert=?9")
	 * List<SignalBewertung> findBySignalAND2Indikator(String signalName, String
	 * sPName1, Object sPWert1, String indikatorName1, String iPName1, Object
	 * iPWert1, String indikatorName2, String iPName2, Object iPWert2);
	 */

	/*
	 * @Query("select s from SignalBewertung s INNER JOIN s.indikatorAlgorithmen i WHERE i.name =?1 "
	 * + "AND i.p1name=?2 AND i.p1wert=?3") List<SignalBewertung>
	 * findByIndikator(String indikatorName, String typ1, String wert1);
	 */

	/*
	 * @Query("select s from SignalBewertung s INNER JOIN s.indikatorAlgorithmen i WHERE i.name =?1"
	 * + " AND i.p1name=?2 AND i.p1wert=?3" + " AND i.p2name=?4 AND i.p2wert=?5")
	 * List<SignalBewertung> findByIndikator(String indikatorName, String typ1,
	 * String wert1, String typ2, String wert2);
	 */
	/*
	 * @Query("select s from SignalBewertung s INNER JOIN s.indikatorAlgorithmen i WHERE i.name =?1"
	 * + " AND i.p1name=?2 AND i.p1wert=?3" + " AND i.p2name=?4 AND i.p2wert=?5" +
	 * " AND i.p3name=?6 AND i.p3wert=?7") List<SignalBewertung>
	 * findByIndikator(String indikatorName, String typ1, String wert1, String typ2,
	 * String wert2, String typ3, String wert3);
	 * 
	 * @Query("select s from SignalBewertung s INNER JOIN s.indikatorAlgorithmen i WHERE i.name =?1"
	 * + " AND i.p1name=?2 AND i.p1wert=?3" + " AND i.p2name=?4 AND i.p2wert=?5" +
	 * " AND i.p3name=?6 AND i.p3wert=?7" + " AND i.p4name=?8 AND i.p4wert=?9")
	 * List<SignalBewertung> findByIndikator(String indikatorName, String typ1,
	 * String wert1, String typ2, String wert2, String typ3, String wert3, String
	 * typ4, String wert4);
	 * 
	 * @Query("select s from SignalBewertung s INNER JOIN s.indikatorAlgorithmen i WHERE i.name =?1"
	 * + " AND i.p1name=?2 AND i.p1wert=?3" + " AND i.p2name=?4 AND i.p2wert=?5" +
	 * " AND i.p3name=?6 AND i.p3wert=?7" + " AND i.p4name=?8 AND i.p4wert=?9" +
	 * " AND i.p5name=?10 AND i.p5wert=?11") List<SignalBewertung>
	 * findByIndikator(String indikatorName, String typ1, String wert1, String typ2,
	 * String wert2, String typ3, String wert3, String typ4, String wert4, String
	 * typ5, String wert5);
	 */

}
