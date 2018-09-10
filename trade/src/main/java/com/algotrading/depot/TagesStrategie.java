package com.algotrading.depot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.util.Parameter;
import com.algotrading.util.Util;

/**
 * Eine Strategie, die täglich anhand der situation im Depot entscheidet, ob gehandelt wird
 * @author oskar
 *
 */
public abstract class TagesStrategie extends Parameter {
	private static final Logger log = LogManager.getLogger(Util.class);

	
	/**
	 * Täglich wird gepräft, ob gehandelt wird. 
	 * @param depot
	 * @return
	 */
	public abstract Order entscheideTaeglich (Depot depot);
	
}
