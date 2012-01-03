package com.dlecan.sqli.wwcc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QoSChecker {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(QoSChecker.class);

	private static final byte ETAT_DISPONIBLE = 0;

	private static final byte ETAT_OUVERT_AUX_VISITES = 1 << 0;

	public static final byte ETAT_CHOCOLAT_BLANC = 1 << 1;

	public static final byte ETAT_CHOCOLAT_NOIR = 1 << 2;

	public static final byte ETAT_CHOCOLAT_LAIT = 1 << 3;

	private static final byte[] ETATS_CHOCOLAT = { ETAT_CHOCOLAT_BLANC,
			ETAT_CHOCOLAT_NOIR, ETAT_CHOCOLAT_LAIT };

	private static final int NB_SECONDES_HEURE = 60 * 60;

	private static final int NB_SECONDES_JOURNEE = 24 * NB_SECONDES_HEURE;

	private static final int NB_JOURS_MOIS_11 = 30;

	private static final int NB_SECONDES_MOIS_11 = NB_JOURS_MOIS_11
			* NB_SECONDES_JOURNEE;

	private static final int DEBUT_VISITE_MATIN = 10 * NB_SECONDES_HEURE;

	private static final int FIN_VISITE_MATIN = 12 * NB_SECONDES_HEURE;

	private static final int DEBUT_VISITE_APRES_MIDI = 14 * NB_SECONDES_HEURE;

	private static final int FIN_VISITE_APRES_MIDI = 16 * NB_SECONDES_HEURE;

	/**
	 * Dur�e de fonctionnement th�orique (novembre), en secondes : (5j (S1) +
	 * 3 * 6j (S2, S3, S4) + 3 (S5)) * 4h * 60min * 60s.
	 */
	private static final int DUREE_FONCTIONNEMENT_THEORIQUE = (5 + 3 * 6 + 3) * 4 * 60 * 60;

	private byte[] donnees;

	private byte[] visites;

	public String[] extractQoS(File qualityFile) {
		StopWatch stopWatch = new Slf4JStopWatch("extractQoS");

		LOGGER.info("Temps de fonctionnement th�orique : {} secondes",
				DUREE_FONCTIONNEMENT_THEORIQUE);

		donnees = new byte[NB_SECONDES_MOIS_11];
		visites = new byte[NB_SECONDES_MOIS_11];

		extraireIntervals(qualityFile);
		construireHeuresVisite();

		mesureQoS();

		stopWatch.stop();
		return new String[] { "" };
	}

	private void construireHeuresVisite() {
		StopWatch stopWatch = new Slf4JStopWatch("construireHeuresVisite");

		for (int i = 0; i < NB_JOURS_MOIS_11; i++) {

			remplirHeuresDeVisiteUneJournee(i);

		}

		stopWatch.stop();
	}

	private void remplirHeuresDeVisiteUneJournee(int numJournee) {

		int offset = numJournee * NB_SECONDES_JOURNEE;

		// On indique les heures d'ouverture du matin pour cette journée
		Arrays.fill(visites, offset + DEBUT_VISITE_MATIN, offset
				+ FIN_VISITE_MATIN, ETAT_OUVERT_AUX_VISITES);

		// Même chose pour l'après-midi
		Arrays.fill(visites, offset + DEBUT_VISITE_APRES_MIDI, offset
				+ FIN_VISITE_APRES_MIDI, ETAT_OUVERT_AUX_VISITES);
	}

	private void mesureQoS() {
		StopWatch stopWatch = new Slf4JStopWatch(
				"tempsPendantLequelManqueChaqueTypeChocolat");

		int[] tempsChaqueChocolat = new int[10];

		int i;
		for (i = 0; i < ETATS_CHOCOLAT.length; i++) {
			tempsChaqueChocolat[ETATS_CHOCOLAT[i]] = 0;
		}

		int tempsAuMoinsUnChocolat = 0;

		for (i = 0; i < NB_SECONDES_MOIS_11; i++) {

			byte donnee = donnees[i];
			byte visite = visites[i];

			if (visite == ETAT_OUVERT_AUX_VISITES && donnee != ETAT_DISPONIBLE) {

				boolean auMoinsUnChocolatIndisponible = false;

				// Extraction des différents chocolats
				for (int j = 0; j < ETATS_CHOCOLAT.length; j++) {

					byte etatChocolatEnCours = ETATS_CHOCOLAT[j];

					if ((donnee & etatChocolatEnCours) == etatChocolatEnCours) {
						tempsChaqueChocolat[etatChocolatEnCours]++;
						auMoinsUnChocolatIndisponible = true;
					}
				}

				if (auMoinsUnChocolatIndisponible) {
					tempsAuMoinsUnChocolat++;
				}

			}
			// else : rien : pas ouvert aux enfants ou machine pas indisponible

		}

		for (i = 0; i < ETATS_CHOCOLAT.length; i++) {
			LOGGER.info("Temps de rupture de chocolat {} : {} secondes",
					new Object[] { Chocolat.fromEtat(ETATS_CHOCOLAT[i]),
							tempsChaqueChocolat[ETATS_CHOCOLAT[i]] });
		}

		LOGGER.info("Temps d'indisponibilite globale : {} secondes",
				tempsAuMoinsUnChocolat);

		float qos = (float) (DUREE_FONCTIONNEMENT_THEORIQUE - tempsAuMoinsUnChocolat)
				/ DUREE_FONCTIONNEMENT_THEORIQUE * 100;

		LOGGER.info("Qualité de Service novembre 2011 : {}%", qos);

		stopWatch.stop();
	}

	private void extraireIntervals(File qualityFile) {
		StopWatch stopWatch = new Slf4JStopWatch("extractIntervals");

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(qualityFile));

			for (String line = br.readLine(); line != null; line = br
					.readLine()) {

				// Le concours porte sur le mois de novembre uniquement
				// On filtre les lignes qui ne nous concerne pas
				// On ne prend pas le risque de parser les autres dates
				// car certaines n'ont pas de sens dans la TZ Paris
				// Exemple : 27/03/2011 02:24:25, car changement d'heure
				// d'�t�
				// A 2h, on saute directement � 3h

				// Si les 3è et 4è caractères sont '1', on est sur le mois de
				// novembre
				if (line.charAt(3) == '1' && line.charAt(4) == '1') {
					// if (line.contains("/11/2011")) {

					String[] s = line.split(";");

					// if (s.length != 3) {
					// LOGGER.warn(
					// "Skip line [{}] because it isn't well-formatted",
					// line);
					// }
					try {
						String strDebut = s[0];
						String strFin = s[1];
						String type = s[2];

						int deltaDebut = Utils.getDelta(strDebut);
						assert deltaDebut >= 0;

						int deltaFin = Utils.getDelta(strFin);
						assert deltaFin >= 0;

						stockerIndisponibiliteChocolat(type, deltaDebut,
								deltaFin);

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
	}

	private void stockerIndisponibiliteChocolat(String type, int deltaDebut,
			int deltaFin) {

		byte etatChocolat = typeChocolatToEtat(type);
		byte aAjouter = etatChocolat;

		int i;
		for (i = deltaDebut; i < deltaFin; i++) {

			byte donnee = donnees[i];

			donnee |= aAjouter;

			donnees[i] = donnee;
		}

	}

	private byte typeChocolatToEtat(String typeChocolat) {
		return Chocolat.fromType(typeChocolat).getEtat();
	}
}
