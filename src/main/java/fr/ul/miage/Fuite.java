package fr.ul.miage;

/**
 * La classe Fuite représente une fuite dans la baignoire.
 */
public class Fuite extends Thread {
    private double debit;
    private final Baignoire baignoire;
    private boolean running;

    /**
     * Constructeur pour créer une fuite en spécifiant la baignoire et le débit de celle-ci.
     *
     * @param debit    Le débit de la fuite.
     * @param baignoire La baignoire associée à la fuite.
     */
    public Fuite(double debit, Baignoire baignoire) {
        this.debit = debit;
        this.baignoire = baignoire;
        this.running = true;
    }

    /**
     * Obtient le débit de la fuite.
     *
     * @return Le débit de la fuite.
     */
    public double getDebit() {
        return debit;
    }

    /**
     * Définit le débit de la fuite.
     *
     * @param debit Le nouveau débit de la fuite.
     */
    public void setDebit(double debit) {
        this.debit = debit;
    }

    /**
     * Exécute la fuite, retirant de l'eau de la baignoire à intervalles réguliers (chaque seconde).
     */
    @Override
    public void run() {
        while (running) {
            baignoire.retirerEau(debit);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Arrête l'exécution de la fuite.
     */
    public void stopRunning() {
        this.running = false;
    }
}
