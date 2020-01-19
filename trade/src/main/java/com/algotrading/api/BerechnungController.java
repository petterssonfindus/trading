package com.algotrading.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.algotrading.component.Berechnung;
import com.algotrading.component.SignalVerwaltung;
import com.algotrading.signalbewertung.SignalBewertung;
import com.algotrading.signalbewertung.SignalBewertungen;
import com.algotrading.uimodel.UICreateSignalBewertung;
import com.algotrading.uimodel.UISignalBewertung;
import com.algotrading.uimodel.UISignalBewertungen;

@RestController
@RequestMapping("api/v1/berechnungen")
@Validated
public class BerechnungController {

	@Autowired
	private SignalVerwaltung signalVerwaltung;

	@Autowired
	private Berechnung berechnung;

	/**
	 * Eine SignalBewertung anhand der ID 
	 */
	@PostMapping
	@RequestMapping(path = "/berechnung", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<UISignalBewertung> berechneSignale(
			@NonNull @PathVariable("id") String id) {

		SignalBewertung SB = signalVerwaltung.find(Long.parseLong(id));
		UISignalBewertung result = new UISignalBewertung(SB);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PostMapping
	@RequestMapping(path = "/bewertung", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<SignalBewertungen> berechneSignaleBewertung(
			@RequestBody UICreateSignalBewertung uiCreateSignalBewertung) {

		System.out.println("UISignalBewertunginput: " + uiCreateSignalBewertung.toString());

		SignalBewertungen sBs = berechnung.rechneSignale(uiCreateSignalBewertung);

		return new ResponseEntity(new UISignalBewertungen(sBs), HttpStatus.OK);
	}

}
