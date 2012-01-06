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
import static com.dlecan.sqli.wwcc.Utils.toInt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * Classe principale.
 * 
 * @author dlecan
 */
public class QoSChecker {

    private static int DEBUT_VISITE_MATIN;

    private static int FIN_VISITE_MATIN;

    private static int DEBUT_VISITE_APRES_MIDI;

    private static int FIN_VISITE_APRES_MIDI;

    private static int NUM_MOIS_NOVEMBRE;

    /**
     * Inclus le caractere de fin de ligne windows (\r\n)
     */
    private static int NB_BYTES_PAR_LIGNE_NORMALE;

    private static int NB_BYTES_PAR_LIGNE_NORMALE_SS_FIN_LIGNE;

    /**
     * Duree de fonctionnement theorique (novembre), en secondes.
     */
    private static int DUREE_FONCTIONNEMENT_THEORIQUE;

    private static int PREMIER_DIMANCHE_MOIS_11;

    private static int NB_JOURS_SEMAINE;

    private byte[] donnees;

    /**
     * Constructeur.
     */
    public QoSChecker() {
        init();
    }

    private void init() {
        // (5j (S1) + 3 * 6j (S2, S3, S4) + 3 (S5)) * 4h * 60min * 60s
        DUREE_FONCTIONNEMENT_THEORIQUE = (5 + 3 * 6 + 3) * 4 * 60 * 60;
        DEBUT_VISITE_MATIN = 10 * NB_SECONDES_HEURE;
        FIN_VISITE_MATIN = 12 * NB_SECONDES_HEURE;
        DEBUT_VISITE_APRES_MIDI = 14 * NB_SECONDES_HEURE;
        FIN_VISITE_APRES_MIDI = 16 * NB_SECONDES_HEURE;
        NB_BYTES_PAR_LIGNE_NORMALE = 43;
        NB_BYTES_PAR_LIGNE_NORMALE_SS_FIN_LIGNE = NB_BYTES_PAR_LIGNE_NORMALE - 2;
        NUM_MOIS_NOVEMBRE = 11;
        PREMIER_DIMANCHE_MOIS_11 = 6;
        NB_JOURS_SEMAINE = 7;

        donnees = new byte[NB_SECONDES_MOIS_11];
    }

    public Object[] extractQoS(File qualityFile) {
        construireHeuresVisite();
        extraireIntervals(qualityFile);

        Object[] resultats = mesureQoS();
        resultats[0] = DUREE_FONCTIONNEMENT_THEORIQUE;

        return resultats;
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

    private Object[] mesureQoS() {
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

        Object[] resultats = new Object[6];
        resultats[1] = tempsChaqueChocolat[ETAT_CHOCOLAT_BLANC];
        resultats[2] = tempsChaqueChocolat[ETAT_CHOCOLAT_NOIR];
        resultats[3] = tempsChaqueChocolat[ETAT_CHOCOLAT_LAIT];
        resultats[4] = tempsAuMoinsUnChocolat;

        double qos = (double) (DUREE_FONCTIONNEMENT_THEORIQUE - tempsAuMoinsUnChocolat)
                / DUREE_FONCTIONNEMENT_THEORIQUE;

        resultats[5] = qos;

        return resultats;
    }

    private void extraireIntervals(File qualityFile) {
        FileInputStream fileInputStream = null;
        FileChannel channel = null;
        try {
            fileInputStream = new FileInputStream(qualityFile);
            channel = fileInputStream.getChannel();

            ByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY,
                    0, (int) channel.size());

            // Pour stocker chaque "ligne" de donnees
            final byte[] buf = new byte[NB_BYTES_PAR_LIGNE_NORMALE_SS_FIN_LIGNE];

            while (byteBuffer.hasRemaining()) {

                if (byteBuffer.remaining() >= NB_BYTES_PAR_LIGNE_NORMALE) {
                    byteBuffer.get(buf);
                    // Suppression du caractere 'retour ligne'
                    byteBuffer.get();
                    byteBuffer.get();
                } else if (byteBuffer.remaining() == NB_BYTES_PAR_LIGNE_NORMALE_SS_FIN_LIGNE) {
                    byteBuffer.get(buf);
                } else {
                    // Derniere ligne mal foutue
                    break;
                }

                // Le concours porte sur le mois de novembre uniquement
                // On filtre les lignes qui ne nous concerne pas
                // On ne prend pas le risque de parser les autres dates
                // car certaines n'ont pas de sens dans la TZ Paris
                // Exemple : 27/03/2011 02:24:25, car changement d'heure
                // d'ete
                // A 2h, on saute directement a 3h

                int moisDebut = toInt(buf, 3, 4);
                int moisFin = toInt(buf, 23, 24);

                if (moisDebut == NUM_MOIS_NOVEMBRE
                        && moisFin == NUM_MOIS_NOVEMBRE) {

                    int jourDebut = toInt(buf, 0, 1);
                    int heureDebut = toInt(buf, 11, 12);
                    int minutesDebut = toInt(buf, 14, 15);
                    int secondesDebut = toInt(buf, 17, 18);

                    int deltaDebut = Utils.getDelta(jourDebut, heureDebut,
                            minutesDebut, secondesDebut);

                    int jourFin = toInt(buf, 20, 21);
                    int heureFin = toInt(buf, 31, 32);
                    int minutesFin = toInt(buf, 34, 35);
                    int secondesFin = toInt(buf, 37, 38);

                    int deltaFin = Utils.getDelta(jourFin, heureFin,
                            minutesFin, secondesFin);

                    if (deltaFin >= deltaDebut) {

                        // Extraction du type de chocolat
                        byte type = buf[40];

                        stockerIndisponibiliteChocolat(type, deltaDebut,
                                deltaFin);
                    }
                    // else
                    // on saute la ligne car incoherente (date de fin AVANT date
                    // de debut) Ignoree donc

                } else if (moisDebut == NUM_MOIS_NOVEMBRE
                        || moisFin == NUM_MOIS_NOVEMBRE) {
                    System.out.println(">>>>>> " + new String(buf));
                    // Incoherence dans la ligne
                    // On saute pour le moment
                    // TODO
                } else {
                    // ne concerne pas le mois de novembre
                }
            }

        } catch (IOException e) {
            throw new QoSCheckerException(e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                }
            }
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                }
            }
        }
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

    private boolean contient(int ensemble, int aVerifier) {
        return (ensemble & aVerifier) == aVerifier;
    }
}
