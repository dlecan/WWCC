package com.dlecan.sqli.wwcc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

public class QoSChecker {

	private static final DateTimeFormatter DTF = DateTimeFormat.forPattern(
			"dd/MM/yyyy HH:mm:ss").withZone(DateTimeZone.forID("Europe/Paris"));

	private static final Logger LOGGER = LoggerFactory
			.getLogger(QoSChecker.class);

	/**
	 * Durée de fonctionnement théorique (novembre) : (5j (S1) + 3 * 6j (S2, S3,
	 * S4) + 3 (S5)) * 4h * 60min * 60s.
	 */
	private static final long DUREE_FONCTIONNEMENT_THEORIQUE = (5 + 3 * 6 + 3) * 4 * 60 * 60;

	public String extractQoS(File qualityFile) {
		StopWatch stopWatch = new Slf4JStopWatch("extractQoS");

		LOGGER.info("Temps de fonctionnement théorique : {} secondes",
				DUREE_FONCTIONNEMENT_THEORIQUE);

		Multimap<Chocolat, Interval> intervalsChocolats = extractIntervals(qualityFile);

//		intervalsChocolats = mergeIntervals(intervalsChocolats);

		tempsPendantLequelManqueChaqueTypeChocolat(intervalsChocolats);
		tempsPendantLequelManqueAuMoinsUnTypeChocolat(intervalsChocolats);

		stopWatch.stop();
		return "";
	}

	private Multimap<Chocolat, Interval> mergeIntervals(
			Multimap<Chocolat, Interval> intervalsChocolats) {
		StopWatch stopWatch = new Slf4JStopWatch("mergeIntervals");

		Multimap<Chocolat, Interval> result = ArrayListMultimap.create();

		for (Chocolat chocolat : Chocolat.values()) {

			Collection<Interval> intervals = intervalsChocolats.get(chocolat);

			int nbIntervalsAvantFusion = intervals.size();

			Iterator<Interval> iteratorIntervals = intervals.iterator();

			// On a forcément un interval par chocolat, inutile de tester
			// si il y en a au moins un.
			Interval previousInterval = iteratorIntervals.next();

			while (iteratorIntervals.hasNext()) {

				Interval currentInterval = iteratorIntervals.next();

				if (previousInterval.contains(currentInterval)) {
					// L'interval 'current' ne sert à rien, il est inclus
					// dans un autre. On ne le garde pas
					result.put(chocolat, previousInterval);
				} else if (previousInterval.abuts(currentInterval)
						|| previousInterval.overlaps(currentInterval)) {
					// Les 2 intervals se touchent ou se recouvrent.
					// On les fusionnent
					previousInterval = new Interval(
							previousInterval.getStart(), currentInterval
									.getEnd());
					result.put(chocolat, previousInterval);
				} else {
					// Cas du "gap"
					// On garde l'interval le plus ancien et on saute au
					// suivant.
					result.put(chocolat, previousInterval);
					previousInterval = currentInterval;
				}

			}

			LOGGER.debug(
					"Nb intervals pour le chocolat {} || Avant {} || Après {}",
					new Object[] { chocolat, nbIntervalsAvantFusion,
							result.get(chocolat).size() });

		}

		stopWatch.stop();

		return result;
	}

	private void tempsPendantLequelManqueChaqueTypeChocolat(
			Multimap<Chocolat, Interval> intervalsChocolats) {
		StopWatch stopWatch = new Slf4JStopWatch(
				"tempsPendantLequelManqueChaqueTypeChocolat");

		for (Chocolat chocolat : Chocolat.values()) {

			Collection<Interval> intervals = intervalsChocolats.get(chocolat);

			long dureeTotale = 0;

			for (Interval interval : intervals) {
				dureeTotale += interval.toDurationMillis();
			}

			long dureeTotalFinale = TimeUnit.SECONDS.convert(dureeTotale,
					TimeUnit.MILLISECONDS);

			LOGGER.info("Temps de rupture de chocolat {} : {} secondes",
					new Object[] { chocolat.toString(), dureeTotalFinale });

		}

		stopWatch.stop();
	}

	private void tempsPendantLequelManqueAuMoinsUnTypeChocolat(
			Multimap<Chocolat, Interval> intervalsChocolats) {
		StopWatch stopWatch = new Slf4JStopWatch(
				"tempsPendantLequelManqueAuMoinsUnTypeChocolat");

		long dureeTotale = 0;

		long dureeTotalFinale = TimeUnit.SECONDS.convert(dureeTotale,
				TimeUnit.MILLISECONDS);

		LOGGER.info("Temps d'indisponibilité globale : {} secondes",
				dureeTotalFinale);

		stopWatch.stop();
	}

	private SortedSetMultimap<Chocolat, Interval> extractIntervals(
			File qualityFile) {
		StopWatch stopWatch = new Slf4JStopWatch("extractIntervals");
		TreeMultimap<Chocolat, Interval> result = TreeMultimap.create(Ordering
				.natural(), new OlderFirstIntervalComparator());

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(qualityFile));

			for (String line = br.readLine(); line != null; line = br
					.readLine()) {

				// Le concours porte sur le mois de novembre uniquement
				// On filtre les lignes qui ne nous concerne pas
				// On ne prend pas le risque de parser les autres dates
				// car certaines n'ont pas de sens dans la TZ Paris
				// Exemple : 27/03/2011 02:24:25, car changement d'heure d'été
				// A 2h, on saute directement à 3h
				if (line.indexOf("/11/2011") != -1) {

					String[] s = line.split(";");

					if (s.length != 3) {
						LOGGER
								.warn(
										"Skip line [{}] because it isn't well-formatted",
										line);
					}
					try {
						String strDebut = s[0];
						String strFin = s[1];
						String type = s[2];

						DateTime debut = DTF.parseDateTime(strDebut);
						DateTime fin = DTF.parseDateTime(strFin);

						Collection<Interval> intervals = extraireIntervalsEnTenantCompteHeureDeVisite(
								debut, fin);

						Chocolat chocolat = Chocolat.fromType(type);
						result.putAll(chocolat, intervals);

					} catch (IllegalArgumentException e) {
						LOGGER.warn("Skip line [{}] because : {}",
								new Object[] { line, e.getMessage() });
					}
				}
			}

		} catch (FileNotFoundException e) {
			throw new QoSCheckerException(e);
		} catch (IOException e) {
			throw new QoSCheckerException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
		stopWatch.stop();
		return result;
	}

	private char[] reformatString(String m) {
		char[] strChar0 = new char[] {
		// yyyy
				m.charAt(6), m.charAt(7), m.charAt(8), m.charAt(9),
				// -
				'-',
				// MM
				m.charAt(3), m.charAt(4),
				// -
				'-',
				// dd
				m.charAt(0), m.charAt(1),
				// ' '
				' ',
				// HH
				m.charAt(11), m.charAt(12),
				// :
				':',
				// mm
				m.charAt(14), m.charAt(15),
				// :
				':',
				// ss
				m.charAt(17), m.charAt(18), };
		return strChar0;
	}

	private Collection<Interval> extraireIntervalsEnTenantCompteHeureDeVisite(
			DateTime debut, DateTime fin) {

		Interval intervalDeVisiteMatin = intervalDeVisiteMatin(debut);
		Interval intervalDeVisiteMidi = intervalDeVisiteMidi(debut);
		Interval intervalDeVisiteApresMidi = intervalDeVisiteApresMidi(debut);

		Collection<Interval> result = Lists.newArrayList();

		if (intervalDeVisiteMatin.contains(debut)) {

			DateTime debutReel = debut;

			DateTime finRelle;
			if (intervalDeVisiteMatin.contains(fin)) {
				finRelle = fin;

			} else if (intervalDeVisiteMidi.contains(fin)) {
				finRelle = intervalDeVisiteMatin.getEnd();

			} else if (intervalDeVisiteApresMidi.contains(fin)) {

				finRelle = intervalDeVisiteMatin.getEnd();

				result.add(new Interval(debutReel, finRelle));

				// Besoin d'un 2è interval
				result.add(new Interval(intervalDeVisiteApresMidi.getStart(),
						fin));
			} else {
				finRelle = intervalDeVisiteMatin.getEnd();

				result.add(new Interval(debutReel, finRelle));

				// Besoin d'un 2è interval
				result.add(intervalDeVisiteApresMidi);
			}

		} else if (intervalDeVisiteMatin.isAfter(debut)) {

			DateTime debutReel = intervalDeVisiteMatin.getStart();

			DateTime finRelle;
			if (intervalDeVisiteMatin.contains(fin)) {
				finRelle = fin;

			} else if (intervalDeVisiteMidi.contains(fin)) {
				finRelle = intervalDeVisiteMatin.getEnd();

			} else if (intervalDeVisiteApresMidi.contains(fin)) {

				finRelle = intervalDeVisiteMatin.getEnd();

				result.add(new Interval(debutReel, finRelle));

				// Besoin d'un 2è interval
				result.add(new Interval(intervalDeVisiteApresMidi.getStart(),
						fin));
			} else {
				finRelle = intervalDeVisiteMatin.getEnd();

				result.add(new Interval(debutReel, finRelle));

				// Besoin d'un 2è interval
				result.add(intervalDeVisiteApresMidi);
			}

		} else if (intervalDeVisiteMidi.contains(debut)) {
			// Interval du matin avant la date de debut
			// Visite du matin pas effectée par la panne

			// On regarde ce que cela donne pour la visite de l'après-midi

			DateTime debutReel = intervalDeVisiteApresMidi.getStart();

			if (intervalDeVisiteApresMidi.contains(fin)) {

				result.add(new Interval(debutReel, fin));

			} else {

				result.add(new Interval(debutReel, intervalDeVisiteApresMidi
						.getEnd()));

			}
		}

		return result;
	}

	private Interval intervalDeVisiteMatin(DateTime date) {
		return intervalDeVisite(date, 10, 12);
	}

	private Interval intervalDeVisiteMidi(DateTime date) {
		return intervalDeVisite(date, 12, 14);
	}

	private Interval intervalDeVisiteApresMidi(DateTime date) {
		return intervalDeVisite(date, 14, 16);
	}

	private Interval intervalDeVisite(DateTime date, int debut, int fin) {
		int annee = date.getYear();
		int mois = date.getMonthOfYear();
		int jour = date.getDayOfMonth();
		return new Interval(new DateTime(annee, mois, jour, debut, 00, 00),
				new DateTime(annee, mois, jour, fin, 00, 00));
	}

}
