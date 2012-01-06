package com.dlecan.sqli.wwcc;

import static com.dlecan.sqli.wwcc.Etat.ETATS_CHOCOLAT;
import static com.dlecan.sqli.wwcc.Etat.ETAT_CHOCOLAT_BLANC;
import static com.dlecan.sqli.wwcc.Etat.ETAT_CHOCOLAT_LAIT;
import static com.dlecan.sqli.wwcc.Etat.ETAT_CHOCOLAT_NOIR;
import static com.dlecan.sqli.wwcc.Etat.ETAT_OUVERT_AUX_VISITES;
import static com.dlecan.sqli.wwcc.Utils.NB_JOURS_MOIS_11;
import static com.dlecan.sqli.wwcc.Utils.NB_SECONDES_HEURE;
import static com.dlecan.sqli.wwcc.Utils.NB_SECONDES_JOURNEE;
import static com.dlecan.sqli.wwcc.Utils.NB_SECONDES_MOIS_11;
import static com.dlecan.sqli.wwcc.Utils.contient;

import java.util.Arrays;

/**
 * Le constructeur de {@link TimeLine}.
 * 
 * @author dlecan
 */
public final class TimeLineBuilder {

    private static int DEBUT_VISITE_MATIN;

    private static int FIN_VISITE_MATIN;

    private static int DEBUT_VISITE_APRES_MIDI;

    private static int FIN_VISITE_APRES_MIDI;

    /**
     * Duree de fonctionnement theorique (novembre), en secondes.
     */
    private static int DUREE_FONCTIONNEMENT_THEORIQUE;

    private static int PREMIER_DIMANCHE_MOIS_11;

    private static int NB_JOURS_SEMAINE;

    private byte[] donnees;

    private TimeLine timeLine;

    /**
     * Constructeur.
     */
    public TimeLineBuilder() {
        init();
    }

    /**
     * Indique pour quel mois la time line doit etre construite.
     * 
     * @param month
     *            Numero standard du mois (1 = janvier, 12 = decembre).
     * @return Le builder courant.
     */
    public TimeLineBuilder forMonth(int month) {
        return this;
    }

    /**
     * Ajoute une visite d'enfant : heure de debut et heure de fin.
     * <p>
     * Ajoute une visite tous les jours sauf le dimanche.
     * </p>
     * <p>
     * A vous de decouper les heures en nombre d'heures et nombre de minutes.
     * </p>
     * 
     * @param heureDebut
     *            Heure de debut de la visite.
     * @param minuteDebut
     *            Minutes de debut de la visite.
     * @param heureFin
     *            Heure de fin de la visite.
     * @param minuteFin
     *            Minutes de fin de la visite.
     * @return Le builder courant.
     */
    public TimeLineBuilder withVisiteEnfant(int heureDebut, int minuteDebut,
            int heureFin, int minuteFin) {

        return this;
    }

    /**
     * Indique que le paramétrage statique est terminé.
     */
    public void finParametrageStatique() {

        construireHeuresVisite();
    }

    /**
     * Ajoute une indisponibilite.
     * 
     * @param typeChocolat
     *            Type de chocolat concerné.
     * @param deltaDebut
     *            Delta de début depuis le 01/11.
     * @param deltaFin
     *            Delta de fin depuis le 01/11.
     * @return Le builder courant.
     */
    public TimeLineBuilder withIntervalIndispoDepuisDebutDuMoisPourChocolatDonne(
            byte typeChocolat, int deltaDebut, int deltaFin) {

        stockerIndisponibiliteChocolat(typeChocolat, deltaDebut, deltaFin);

        return this;
    }

    /**
     * Construit la timeline. A appeler quand toutes les donnees ont ete
     * renseignees.
     * 
     * @return La {@link TimeLine}.
     */
    public TimeLine build() {

        calculerToutesLesDonnees();

        return timeLine;
    }

    private void init() {
        // (5j (S1) + 3 * 6j (S2, S3, S4) + 3 (S5)) * 4h * 60min * 60s
        DUREE_FONCTIONNEMENT_THEORIQUE = (5 + 3 * 6 + 3) * 4 * 60 * 60;
        DEBUT_VISITE_MATIN = 10 * NB_SECONDES_HEURE;
        FIN_VISITE_MATIN = 12 * NB_SECONDES_HEURE;
        DEBUT_VISITE_APRES_MIDI = 14 * NB_SECONDES_HEURE;
        FIN_VISITE_APRES_MIDI = 16 * NB_SECONDES_HEURE;
        PREMIER_DIMANCHE_MOIS_11 = 6;
        NB_JOURS_SEMAINE = 7;

        donnees = new byte[NB_SECONDES_MOIS_11];

        timeLine = new TimeLine();

        timeLine
                .setTempsFonctionnementTheorique(DUREE_FONCTIONNEMENT_THEORIQUE);
    }

    private void construireHeuresVisite() {
        for (int numJourDuMois = 1; numJourDuMois <= NB_JOURS_MOIS_11; numJourDuMois++) {

            // Pour savoir si un numero de jour donne est un dimanche,
            // on ramene ce numero en base 7 (nb jours de la semaine) via modulo
            // Ensuite, il suffit de comparer le resultat avec le numero du
            // premier jour du mois qui porte le nom du jour que l'on cherche
            // numJourDuMois est un dimanche sur novembre 2011 si :
            // numJourDuMois % 7 == 6
            // car 6 => numero du premier dimanche de novembre 2011

            // Pas de visite le dimanche
            if (numJourDuMois % NB_JOURS_SEMAINE != PREMIER_DIMANCHE_MOIS_11) {
                remplirHeuresDeVisiteUneJournee(numJourDuMois);
            }

        }
    }

    private void remplirHeuresDeVisiteUneJournee(int numJournee) {

        // -1 car le num du jour commence a 1 et non pas 0
        int offset = (numJournee - 1) * NB_SECONDES_JOURNEE;

        // On indique les heures d'ouverture du matin pour cette journee
        Arrays.fill(donnees, offset + DEBUT_VISITE_MATIN, offset
                + FIN_VISITE_MATIN, ETAT_OUVERT_AUX_VISITES);

        // Meme chose pour l'apres-midi
        Arrays.fill(donnees, offset + DEBUT_VISITE_APRES_MIDI, offset
                + FIN_VISITE_APRES_MIDI, ETAT_OUVERT_AUX_VISITES);
    }

    private void calculerToutesLesDonnees() {
        int[] tempsChaqueChocolat = new int[ETAT_CHOCOLAT_LAIT + 1];

        int i;
        int tempsAuMoinsUnChocolat = 0;

        for (i = 0; i < NB_SECONDES_MOIS_11; i++) {

            byte donnee = donnees[i];

            if (contient(donnee, ETAT_OUVERT_AUX_VISITES)) {

                boolean auMoinsUnChocolatIndisponible = false;

                // Extraction des differents chocolats
                for (int j = 0; j < ETATS_CHOCOLAT.length; j++) {

                    byte etatChocolatEnCours = ETATS_CHOCOLAT[j];

                    if (contient(donnee, etatChocolatEnCours)) {
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

        timeLine
                .setTempsRuptureChocolatBlanc(tempsChaqueChocolat[ETAT_CHOCOLAT_BLANC]);
        timeLine
                .setTempsRuptureChocolatNoir(tempsChaqueChocolat[ETAT_CHOCOLAT_NOIR]);
        timeLine
                .setTempsRuptureChocolatLait(tempsChaqueChocolat[ETAT_CHOCOLAT_LAIT]);
        timeLine.setTempsIndisponibiliteGlobale(tempsAuMoinsUnChocolat);

        double qos = (double) (DUREE_FONCTIONNEMENT_THEORIQUE - tempsAuMoinsUnChocolat)
                / DUREE_FONCTIONNEMENT_THEORIQUE;

        timeLine.setQos(qos);
    }

    private void stockerIndisponibiliteChocolat(byte type, int deltaDebut,
            int deltaFin) {

        byte etatChocolat = Chocolat.fromType(type);
        byte aAjouter = etatChocolat;

        int i;
        for (i = deltaDebut; i < deltaFin; i++) {

            byte donnee = donnees[i];

            donnee |= aAjouter;

            donnees[i] = donnee;
        }
    }
}
