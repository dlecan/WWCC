package com.dlecan.sqli.wwcc;

import static com.dlecan.sqli.wwcc.Etat.ETATS_CHOCOLAT;
import static com.dlecan.sqli.wwcc.Etat.ETAT_CHOCOLAT_BLANC;
import static com.dlecan.sqli.wwcc.Etat.ETAT_CHOCOLAT_LAIT;
import static com.dlecan.sqli.wwcc.Etat.ETAT_CHOCOLAT_NOIR;
import static com.dlecan.sqli.wwcc.Etat.ETAT_OUVERT_AUX_VISITES;
import static com.dlecan.sqli.wwcc.Utils.NB_SECONDES_HEURE;
import static com.dlecan.sqli.wwcc.Utils.NB_SECONDES_JOURNEE;
import static com.dlecan.sqli.wwcc.Utils.contient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Le constructeur de {@link TimeLine}.
 * <p>
 * Le builder stocke un gros tableau (<code>donnees</code>), dont chaque cellule
 * represente 1 seconde du mois dont on veut analyser la QoS.
 * </p>
 * <p>
 * Chaque cellule (1 sconde donc) contient plusieurs informations, stockee sous
 * forme d'octet :
 * </p>
 * <ul>
 * <li>0 : valeur par defaut : ni indispo chocolat, ni visite d'enfant,</li>
 * <li>n : combine sous forme d'octet visite ou indispo d'un ou plusieurs
 * chocolat.</li>
 * </ul>
 * 
 * @author dlecan
 */
public final class TimeLineBuilder {

    private static final int NB_JOURS_SEMAINE = 7;

    private int premierDimancheDuMois;

    /**
     * Duree de fonctionnement theorique en secondes.
     */
    private int dureeFonctionnementTheorique;

    private byte[] donnees;

    private final TimeLine tl;

    private final Calendar premierJourDuMois;

    private int mois;

    private int nbSecondesMois;

    private int nbJoursMois;

    private int annee;

    private final List<int[]> visitesEnfants;

    /**
     * Constructeur.
     */
    public TimeLineBuilder() {
        premierJourDuMois = Calendar.getInstance();

        visitesEnfants = new ArrayList<int[]>();

        tl = new TimeLine();
    }

    /**
     * Indique pour quel mois la time line doit etre construite.
     * 
     * @param mois
     *            Numero standard du mois (1 = janvier, 12 = decembre).
     * @return Le builder courant.
     */
    public TimeLineBuilder forMonth(int mois) {
        this.mois = mois;
        return this;
    }

    /**
     * Indique pour quelle annee la time line doit etre construite.
     * 
     * @param annee
     *            Annee sur 4 chiffres.
     * @return Le builder courant.
     */
    public TimeLineBuilder forYear(int annee) {
        this.annee = annee;
        return this;
    }

    /**
     * Ajoute une visite d'enfant : heure de debut et heure de fin.
     * <p>
     * Valable tous les jours sauf le dimanche.
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

        visitesEnfants.add(new int[] { heureDebut, minuteDebut, heureFin,
                minuteFin });

        return this;
    }

    /**
     * Indique que le parametrage statique est termine.
     */
    public void finParametrageStatique() {

        premierJourDuMois.set(annee, mois - 1, 1);

        nbJoursMois = premierJourDuMois.getActualMaximum(Calendar.DAY_OF_MONTH);
        nbSecondesMois = nbJoursMois * NB_SECONDES_JOURNEE;

        premierDimancheDuMois = trouverPremierDimancheDuMois();

        donnees = new byte[nbSecondesMois];

        construireHeuresVisiteCalculerEtDureeFonctionnementTheorique();
    }

    /**
     * Ajoute une indisponibilite.
     * 
     * @param typeChocolat
     *            Type de chocolat concerne.
     * @param deltaDebut
     *            Delta de debut depuis le 01/11.
     * @param deltaFin
     *            Delta de fin depuis le 01/11.
     * @return Le builder courant.
     */
    public TimeLineBuilder withIndispoDepuisDebutDuMoisPourChocolatDonne(
            byte typeChocolat, int deltaDebut, int deltaFin) {

        stockerIndisponibiliteChocolatPourUnIntervalle(typeChocolat,
                deltaDebut, deltaFin);

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

        return tl;
    }

    private int trouverPremierDimancheDuMois() {
        int premierDimancheDuMois = 0;

        while (premierDimancheDuMois == 0) {
            int jourDuMois = premierJourDuMois.get(Calendar.DAY_OF_MONTH);
            int jourDeLaSemaine = premierJourDuMois.get(Calendar.DAY_OF_WEEK);
            if (jourDeLaSemaine == Calendar.SUNDAY) {
                premierDimancheDuMois = jourDuMois;
            }
            premierJourDuMois.set(Calendar.DAY_OF_MONTH, jourDuMois + 1);
        }
        return premierDimancheDuMois;
    }

    private void construireHeuresVisiteCalculerEtDureeFonctionnementTheorique() {
        for (int numJourDuMois = 1; numJourDuMois <= nbJoursMois; numJourDuMois++) {

            // Pour savoir si un numero de jour donne est un dimanche,
            // on ramene ce numero en base 7 (nb jours de la semaine) via modulo
            // Ensuite, il suffit de comparer le resultat avec le numero du
            // premier jour du mois qui porte le nom du jour que l'on cherche
            // numJourDuMois est un dimanche sur novembre 2011 si :
            // numJourDuMois % 7 == 6
            // car 6 => numero du premier dimanche de novembre 2011

            // Pas de visite le dimanche
            if (numJourDuMois % NB_JOURS_SEMAINE != premierDimancheDuMois) {
                int dureeTotaleVisiteUneJournee = remplirHeuresDeVisiteUneJournee(numJourDuMois);

                dureeFonctionnementTheorique += dureeTotaleVisiteUneJournee;
            }
        }

        tl.setTempsFonctionnementTheorique(dureeFonctionnementTheorique);
    }

    private int remplirHeuresDeVisiteUneJournee(int numJournee) {

        // -1 car le num du jour commence a 1 et dans notre tableau de "donnees"
        // tout est indexe depuis 0
        int offset = (numJournee - 1) * NB_SECONDES_JOURNEE;

        int dureeTotaleVisite = 0;

        // On indique les heures de visite des endants pour cette journee
        for (int[] visite : visitesEnfants) {

            int debutVisite = visite[0] * NB_SECONDES_HEURE + visite[1];
            int finVisite = visite[2] * NB_SECONDES_HEURE + visite[3];

            dureeTotaleVisite += finVisite - debutVisite;

            Arrays.fill(donnees, offset + debutVisite, offset + finVisite,
                    ETAT_OUVERT_AUX_VISITES);
        }
        return dureeTotaleVisite;
    }

    private void calculerToutesLesDonnees() {
        int[] tempsChaqueChocolat = new int[ETAT_CHOCOLAT_LAIT + 1];

        int i;
        int tempsAuMoinsUnChocolat = 0;

        // On parcourt tout le tableau
        for (i = 0; i < nbSecondesMois; i++) {

            byte donnee = donnees[i];

            // On ne retient que les donnees qui sont ouvertes aux visites
            if (contient(donnee, ETAT_OUVERT_AUX_VISITES)) {

                boolean auMoinsUnChocolatIndisponible = false;

                // On cherche quel chocolat est present dans une donnee
                // Pour rappel, une donnee == 1 seconde
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

        tl.setTempsRuptureChocolatBlanc(tempsChaqueChocolat[ETAT_CHOCOLAT_BLANC]);
        tl.setTempsRuptureChocolatNoir(tempsChaqueChocolat[ETAT_CHOCOLAT_NOIR]);
        tl.setTempsRuptureChocolatLait(tempsChaqueChocolat[ETAT_CHOCOLAT_LAIT]);
        tl.setTempsIndisponibiliteGlobale(tempsAuMoinsUnChocolat);

        double qos = (double) (dureeFonctionnementTheorique - tempsAuMoinsUnChocolat)
                / dureeFonctionnementTheorique;

        tl.setQos(qos);
    }

    private void stockerIndisponibiliteChocolatPourUnIntervalle(byte type,
            int deltaDebut, int deltaFin) {

        byte etatChocolat = Chocolat.fromType(type);
        byte aAjouter = etatChocolat;

        for (int i = deltaDebut; i < deltaFin; i++) {

            byte donnee = donnees[i];

            // Ici on "ajoute" la donnee si elle n'y est pas, ne fait rien si
            // elle y est deja (geree par le |).
            donnee |= aAjouter;

            donnees[i] = donnee;
        }
    }
}
