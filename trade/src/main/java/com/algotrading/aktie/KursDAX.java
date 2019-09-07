package com.algotrading.aktie;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "DAX")
@DiscriminatorValue("dax")
public class KursDAX extends KursAktie {

}
