package fr.ul.miage;

/**
 * La classe Baignoire représente une baignoire avec une capacité.
 */
public class Baignoire {
    private double capacite;
    private double volume;

    /**
     * Constructeur pour créer une baignoire avec une capacité donnée.
     *
     * @param capacite La capacité de la baignoire.
     */
    public Baignoire(double capacite) {
        this.capacite = capacite;
        this.volume = 0;
    }

    /**
     * Ajoute une quantité d'eau à la baignoire.
     *
     * @param quantite La quantité d'eau à ajouter.
     */
    public synchronized void ajouterEau(double quantite) {
        volume = Math.min(volume + quantite, capacite);
    }

    /**
     * Retire une quantité d'eau de la baignoire.
     *
     * @param quantite La quantité d'eau à retirer.
     */
    public synchronized void retirerEau(double quantite) {
        volume = Math.max(volume - quantite, 0);
    }

    /**
     * Obtient le volume actuel de la baignoire.
     *
     * @return Le volume actuel de la baignoire.
     */
    public double getVolume() {
        return volume;
    }

    /**
     * Obtient la capacité de la baignoire.
     *
     * @return La capacité de la baignoire.
     */
    public double getCapacite() {
        return capacite;
    }
}
