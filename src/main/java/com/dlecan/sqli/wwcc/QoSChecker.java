package com.dlecan.sqli.wwcc;

import static com.dlecan.sqli.wwcc.Utils.toInt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Classe principale.
 * <p>
 * Ne fonctionne qu'avec des fichiers avec des retours ligne "Windows"
 * <code>\r\n</code>.
 * </p>
 * <p>
 * A faire donc pour le retour ligne Unix (<code>\n</code> uniquement).
 * </p>
 * 
 * @author dlecan
 */
public class QoSChecker {

    /**
     * Inclus le caractere de fin de ligne windows (\r\n)
     */
    private static int NB_BYTES_PAR_LIGNE_NORMALE = 43;

    private static int NB_BYTES_PAR_LIGNE_NORMALE_SS_FIN_LIGNE = NB_BYTES_PAR_LIGNE_NORMALE - 2;

    private int mois;

    /**
     * Constructeur.
     */
    public QoSChecker() {
        // Rien
    }

    public TimeLine extractQoS(File qualityFile, int annee, int mois) {
        this.mois = mois;
        TimeLineBuilder builder = new TimeLineBuilder();

        builder.forYear(annee);
        builder.forMonth(mois);

        // Ajout des visites d'enfants
        builder.withVisiteEnfant(10, 00, 12, 00);
        builder.withVisiteEnfant(14, 00, 16, 00);

        builder.finParametrageStatique();

        ajouterIndisponibilites(qualityFile, builder);

        return builder.build();
    }

    private void ajouterIndisponibilites(File qualityFile,
            TimeLineBuilder builder) {
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

                // Les 3 tests qui suivent correspondent aux cas suivants :
                // 1/ ligne normale, avec caracteres de fin de ligne
                // 2/ ligne de fin de fichier, sans caracteres de ligne
                // 3/ ligne qui ne contient, a priori, rien d'interessant
                if (byteBuffer.remaining() >= NB_BYTES_PAR_LIGNE_NORMALE) {
                    byteBuffer.get(buf);
                    // Suppression du caractere 'retour ligne'
                    byteBuffer.get();
                    byteBuffer.get();
                } else if (byteBuffer.remaining() == NB_BYTES_PAR_LIGNE_NORMALE_SS_FIN_LIGNE) {
                    byteBuffer.get(buf);
                } else {
                    // Derniere ligne, mal foutue, on quitte.
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

                if (moisDebut == mois && moisFin == mois) {

                    int deltaDebut = getDeltaDebut(buf);
                    int deltaFin = getDeltaFin(buf);

                    if (deltaFin >= deltaDebut) {

                        // Extraction du type de chocolat
                        byte type = buf[40];

                        // Ajout de l'intervalle d'indisponbilite
                        builder.withIndispoDepuisDebutDuMoisPourChocolatDonne(
                                type, deltaDebut, deltaFin);
                    }
                    // else
                    // on saute la ligne car incoherente (date de fin AVANT date
                    // de debut)
                    // Ignoree donc

                } else if (moisDebut == mois || moisFin == mois) {
                    // Ligne sur 2 mois.
                    // On saute ? La spec ne dit rien sur le sujet.
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
                    // Rien
                }
            }
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
//                  Rien
                }
            }
        }
    }

    /**
     * Calcul du delta de fin d'intervalle depuis le 1er jour du mois.
     * 
     * @param buf
     *            Buffer de données.
     * @return Delta en secondes.
     */
    private int getDeltaFin(final byte[] buf) {
        int jourFin = toInt(buf, 20, 21);
        int heureFin = toInt(buf, 31, 32);
        int minutesFin = toInt(buf, 34, 35);
        int secondesFin = toInt(buf, 37, 38);

        return Utils.getDelta(jourFin, heureFin, minutesFin, secondesFin);
    }

    /**
     * Calcul du delta de debut d'intervalle depuis le 1er jour du mois.
     * 
     * @param buf
     *            Buffer de données.
     * @return Delta en secondes.
     */
    private int getDeltaDebut(final byte[] buf) {
        int jourDebut = toInt(buf, 0, 1);
        int heureDebut = toInt(buf, 11, 12);
        int minutesDebut = toInt(buf, 14, 15);
        int secondesDebut = toInt(buf, 17, 18);

        return Utils.getDelta(jourDebut, heureDebut, minutesDebut,
                secondesDebut);
    }
}
