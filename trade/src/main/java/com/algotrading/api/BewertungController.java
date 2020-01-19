package com.algotrading.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.algotrading.aktie.Aktie;
import com.algotrading.component.AktieVerwaltung;
import com.algotrading.component.SignalVerwaltung;
import com.algotrading.signalbewertung.SignalBewertung;
import com.algotrading.signalbewertung.SignalBewertungen;
import com.algotrading.uimodel.UIFileText;
import com.algotrading.uimodel.UISignalBewertung;
import com.algotrading.uimodel.UISignalBewertungen;
import com.algotrading.uimodel.UIText;

@RestController
@RequestMapping("api/v1/bewertungen")
@Validated
public class BewertungController {

	@Autowired
	private SignalVerwaltung signalVerwaltung;

	@Autowired
	private AktieVerwaltung aktieVerwaltung;

	/**
	 * Eine SignalBewertung anhand der ID 
	 */
	@GetMapping
	@RequestMapping(path = "/bewertung/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<UISignalBewertung> getSignalBewertungById(
			@NonNull @PathVariable("id") String id) {
		SignalBewertung SB = signalVerwaltung.find(Long.parseLong(id));
		UISignalBewertung result = new UISignalBewertung(SB);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * Eine SignalBewertung anhand der ID 
	 * mit Pfad und Strings
	 */
	@GetMapping
	@RequestMapping(path = "/bewertung/{id}/csv", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<UIFileText> getSignalBewertungByIdCSV(
			@NonNull @PathVariable("id") String id) {

		SignalBewertung bw = signalVerwaltung.find(Long.parseLong(id));
		Aktie aktie = aktieVerwaltung.getAktieMurKurse(bw.getAktieName());
		return new ResponseEntity<>(aktieVerwaltung.writeFileKursIndikatorSignal(aktie), HttpStatus.OK);
	}

	/**
	 * Eine Sammlung von SignalBewertungen anhand der ID 
	 */
	@GetMapping
	@RequestMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<UISignalBewertungen> getSignalBewertungenById(
			@NonNull @PathVariable("id") String iD) {
		SignalBewertungen SB = signalVerwaltung.getSignalBewertungen(Long.parseLong(iD));
		UISignalBewertungen result = new UISignalBewertungen(SB);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * Eine Sammlung von SignalBewertungen anhand der ID 
	 * Als CSV-Datei mit Pfadangabe und als Text
	 */
	@GetMapping
	@RequestMapping(path = "/{id}/csv", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<UIFileText> getSignalBewertungenByIdCSV(
			@NonNull @PathVariable("id") String iD) {
		UIFileText result = signalVerwaltung.writeFileSignalBewertungen(Long.parseLong(iD));
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * Eine Sammlung von SignalBewertungen anhand der ID 
	 * Als Text
	 */
	@GetMapping
	@RequestMapping(path = "/{id}/string", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<UIText> getSignalBewertungenByIdString(
			@NonNull @PathVariable("id") String iD) {
		List<String> strings = signalVerwaltung.writeStringSignalBewertungen(Long.parseLong(iD));
		return new ResponseEntity<>(new UIText(strings), HttpStatus.OK);
	}

}
