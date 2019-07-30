package com.algotrading.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorAlgorithmusDAO;

@Service
public class Verwaltung {
	
		static final Logger log = LogManager.getLogger(Parameter.class);
		
		@Autowired
		IndikatorAlgorithmusDAO iADAO; 
		
		public IndikatorAlgorithmus findByUUID(String uuid) {
			return iADAO.findByUUID(uuid);
			
		}

}
