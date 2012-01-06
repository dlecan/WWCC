package com.dlecan.sqli.wwcc;

import static com.dlecan.sqli.wwcc.Utils.toInt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Classe principale.
 * 
 * @author dlecan
 */
public class QoSChecker {

    private static int NUM_MOIS_NOVEMBRE = 11;

    /**
     * Inclus le caractere de fin de ligne windows (\r\n)
     */
    private static int NB_BYTES_PAR_LIGNE_NORMALE = 43;

    private static int NB_BYTES_PAR_LIGNE_NORMALE_SS_FIN_LIGNE = NB_BYTES_PAR_LIGNE_NORMALE - 2;

    /**
     * Constructeur.
     */
    public QoSChecker() {
        // Rien
    }

    public TimeLine extractQoS(File qualityFile) {
        TimeLineBuilder builder = new TimeLineBuilder();
        
        builder.forMonth(NUM_MOIS_NOVEMBRE);
        
        // Ajout des visites d'enfants
        // Pour le moment, ne fait rien (donnees en "dur" dans le buidler), mais
        // c'est pour l'exemple d'usage de l'API.
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

                        builder
                                .withIntervalIndispoDepuisDebutDuMoisPourChocolatDonne(
                                        type, deltaDebut, deltaFin);
                    }
                    // else
                    // on saute la ligne car incoherente (date de fin AVANT date
                    // de debut) Ignoree donc

                } else if (moisDebut == NUM_MOIS_NOVEMBRE
                        || moisFin == NUM_MOIS_NOVEMBRE) {
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
}
