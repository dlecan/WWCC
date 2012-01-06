package com.dlecan.sqli.wwcc;

/**
 * Le constructeur de {@link TimeLine}.
 * 
 * @author dlecan
 */
public final class TimeLineBuilder {

    /**
     * Constructeur.
     */
    public TimeLineBuilder() {

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
     * 
     * @param typeChocolat
     * @param deltaDebut
     * @param deltaFin
     * @return Le builder courant.
     */
    public TimeLineBuilder withIntervalIndispoDepuisDebutDuMoisPourChocolatDonne(
            byte typeChocolat, int deltaDebut, int deltaFin) {

        return this;
    }

    public TimeLine build() {
        return null;
    }

}
