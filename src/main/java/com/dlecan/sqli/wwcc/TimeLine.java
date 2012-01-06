package com.dlecan.sqli.wwcc;

/**
 * La timeline, qui stocke les donnees calculees.
 * 
 * @author dlecan
 */
public class TimeLine {

    private int tempsFonctionnementTheorique;

    private int tempsRuptureChocolatBlanc;

    private int tempsRuptureChocolatNoir;

    private int tempsRuptureChocolatLait;

    private int tempsIndisponibiliteGlobale;

    private double qos;

    public double getQos() {
        return qos;
    }

    public void setQos(double qos) {
        this.qos = qos;
    }

    public int getTempsFonctionnementTheorique() {
        return tempsFonctionnementTheorique;
    }

    public void setTempsFonctionnementTheorique(int tempsFonctionnementTheorique) {
        this.tempsFonctionnementTheorique = tempsFonctionnementTheorique;
    }

    public int getTempsIndisponibiliteGlobale() {
        return tempsIndisponibiliteGlobale;
    }

    public void setTempsIndisponibiliteGlobale(int tempsIndisponibiliteGlobale) {
        this.tempsIndisponibiliteGlobale = tempsIndisponibiliteGlobale;
    }

    public int getTempsRuptureChocolatBlanc() {
        return tempsRuptureChocolatBlanc;
    }

    public void setTempsRuptureChocolatBlanc(int tempsRuptureChocolatBlanc) {
        this.tempsRuptureChocolatBlanc = tempsRuptureChocolatBlanc;
    }

    public int getTempsRuptureChocolatLait() {
        return tempsRuptureChocolatLait;
    }

    public void setTempsRuptureChocolatLait(int tempsRuptureChocolatLait) {
        this.tempsRuptureChocolatLait = tempsRuptureChocolatLait;
    }

    public int getTempsRuptureChocolatNoir() {
        return tempsRuptureChocolatNoir;
    }

    public void setTempsRuptureChocolatNoir(int tempsRuptureChocolatNoir) {
        this.tempsRuptureChocolatNoir = tempsRuptureChocolatNoir;
    }

    public Object[] toArray() {
        return new Object[] { tempsFonctionnementTheorique,
                tempsRuptureChocolatBlanc, tempsRuptureChocolatNoir,
                tempsRuptureChocolatLait, tempsIndisponibiliteGlobale, qos };
    }
}
