package com.algotrading.api;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.component.AktieVerwaltung;
import com.algotrading.uimodel.UIAktie;
import com.algotrading.uimodel.UIAktie2Kurse;
import com.algotrading.uimodel.UIKurs;
import com.algotrading.uimodel.UIKursListe;
import com.algotrading.util.DateUtil;
import com.algotrading.util.RestApplicationException;

@RestController
@RequestMapping("api/v1/aktie")
@Validated
public class AktieController {

	@Autowired
	private AktieVerwaltung aktieVerwaltung;

	/**
	 * Eine Aktie anhand der ID 
	 */
	@GetMapping
	@RequestMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<UIAktie> getAktieById(@NonNull @PathVariable("id") String id) {
		Aktie aktie = aktieVerwaltung.getAktieLazy(Long.parseLong(id));
		UIAktie uiAktie = new UIAktie(aktie);
		return new ResponseEntity<UIAktie>(uiAktie, HttpStatus.OK);
	}

	/**
	 * Aktien-Suche nach Name
	 */
	@GetMapping
	@RequestMapping(path = "/name/{name}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody UIAktie getAktieByName(@NonNull @NotBlank @PathVariable("name") String name) {
		Aktie aktie = aktieVerwaltung.getAktieLazy(name);
		UIAktie uiAktie = new UIAktie(aktie);
		return uiAktie;
	}

	/**
	 * Liste aller Aktien 
	 */
	@GetMapping
	@RequestMapping(method = RequestMethod.GET, path = "/liste", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<UIAktie> getAktieListe() {
		List<Aktie> liste = aktieVerwaltung.getAktienListe();
		List<UIAktie> listUI = mapListAktieToListUIAktie(liste);
		return listUI;
	}

	/**
	 * Liste aller Aktien mit Kurs Start-Ende
	 */
	@GetMapping
	@RequestMapping(method = RequestMethod.GET, path = "/list/firstlast", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<UIAktie2Kurse> getAktieListeBeginnEnde() {
		List<UIAktie2Kurse> liste = aktieVerwaltung.getAktienListe2Kurse();
		return liste;
	}

	/**
	 * Datum erster Kurs 
	 */
	@GetMapping
	@RequestMapping(path = "/{id}/value/firstdate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> getDatumErsterKurs(@NonNull @NotBlank @PathVariable("id") String id) {
		GregorianCalendar datum = aktieVerwaltung.getDatumErsterKurs(Long.parseLong(id));
		if (datum != null) {
			String x = DateUtil.formatDate(datum, ":");
			return new ResponseEntity<String>(x, HttpStatus.OK);
		} else {
			throw new RestApplicationException("keine Kurse vorhanden", HttpStatus.NO_CONTENT);
		}
	}

	/**
	 * der erste Kurs 
	 */
	@GetMapping
	@RequestMapping(path = "/{id}/value/first", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<UIKurs> getErsterKurs(@NonNull @NotBlank @PathVariable("id") String id) {
		Kurs kurs = aktieVerwaltung.getErsterKurs(Long.parseLong(id));
		if (kurs != null) {
			UIKurs uiKurs = new UIKurs(kurs);
			return new ResponseEntity<UIKurs>(uiKurs, HttpStatus.OK);
		} else {
			throw new RestApplicationException("keine Kurse vorhanden", HttpStatus.NO_CONTENT);
		}
	}

	/**
	 * der erste Kurs und der letzte Kurs
	 */
	@GetMapping
	@RequestMapping(path = "/{id}/value/firstlast", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<UIKursListe> getErsterLetzterKurs(
			@NonNull @NotBlank @PathVariable("id") String id) {
		List<Kurs> kursListe = aktieVerwaltung.getErsterLetzterKurs(Long.parseLong(id));
		if (kursListe == null || kursListe.size() != 2) {
			throw new RestApplicationException("keine Kurse vorhanden", HttpStatus.NO_CONTENT);
		}
		UIKursListe uiKursListe = new UIKursListe(kursListe);
		return new ResponseEntity<>(uiKursListe, HttpStatus.OK);
	}

	/**
	 * Anzahl vorhandener Kurse 
	 */
	@GetMapping
	@RequestMapping(path = "/{id}/value/count", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Integer> getAnzahlKurse(@NonNull @NotBlank @PathVariable("id") String id) {

		Integer result = aktieVerwaltung.getAnzahlKurse(Long.parseLong(id));
		if (result != null) {
			return new ResponseEntity<Integer>(result, HttpStatus.OK);
		} else {
			throw new RestApplicationException("keine Kurse vorhanden", HttpStatus.NO_CONTENT);
		}
	}

	@PostMapping
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<UIAktie> createAktie(@RequestBody UIAktie uiAktie) {
		System.out.println("UIAktie: " + uiAktie.toString());
		Aktie aktie = aktieVerwaltung.createAktie(uiAktie.mapToAktie());
		if (aktie != null) {
			return new ResponseEntity<>(new UIAktie(aktie), HttpStatus.OK);
		} else {
			throw new RestApplicationException("Fehler beim Create", HttpStatus.NO_CONTENT);
		}
	}

	@PutMapping
	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<UIAktie> updateAktie(@RequestBody UIAktie uiAktie) {
		System.out.println("UIAktie: " + uiAktie.toString());
		Aktie aktie = aktieVerwaltung.updateAktie(uiAktie.mapToAktie());
		if (aktie != null) {
			return new ResponseEntity<>(new UIAktie(aktie), HttpStatus.OK);
		} else {
			throw new RestApplicationException("Fehler beim Update", HttpStatus.NO_CONTENT);
		}
	}

	@DeleteMapping
	@RequestMapping(method = RequestMethod.DELETE, path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Integer> deleteAktie(@NonNull @NotBlank @PathVariable("id") String id) {
		aktieVerwaltung.deleteAktieByID(Long.parseLong(id));
		return new ResponseEntity<>(new Integer(1), HttpStatus.OK);
	}

	private static List<UIAktie> mapListAktieToListUIAktie(List<Aktie> listAktie) {
		List<UIAktie> listUIAktie = new ArrayList<UIAktie>();
		for (Aktie aktie : listAktie) {
			listUIAktie.add(new UIAktie(aktie));
		}
		return listUIAktie;
	}

}
