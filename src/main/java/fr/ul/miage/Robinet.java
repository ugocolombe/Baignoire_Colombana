package fr.ul.miage;

/**
 * La classe Robinet représente un robinet qui ajoute de l'eau à une baignoire.
 */
public class Robinet extends Thread {
    private double debit;
    private final Baignoire baignoire;
    private boolean running;

    /**
     * Constructeur pour créer un robinet en spécifiant la baignoire et le débit de celui-ci.
     *
     * @param debit    Le débit d'eau du robinet.
     * @param baignoire La baignoire associée au robinet.
     */
    public Robinet(double debit, Baignoire baignoire) {
        this.debit = debit;
        this.baignoire = baignoire;
        this.running = true;
    }

    /**
     * Obtient le débit du robinet.
     *
     * @return Le débit du robinet.
     */
    public double getDebit() {
        return debit;
    }

    /**
     * Définit le débit du robinet.
     *
     * @param debit Le nouveau débit du robinet.
     */
    public void setDebit(double debit) {
        this.debit = debit;
    }

    /**
     * Exécute le robinet, ajoutant de l'eau à la baignoire à intervalles réguliers(chaque seconde).
     */
    @Override
    public void run() {
        while (running) {
            baignoire.ajouterEau(debit);
            try {
                Thread.sleep(1000); // Simulate filling the bathtub every second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Arrête l'exécution du robinet.
     */
    public void stopRunning() {
        this.running = false;
    }
}
