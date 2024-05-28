package fr.ul.miage;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

/**
 * La classe Remplir permet de remplir la baignoire d'eau.
 */
public class Remplir extends ScheduledService<Baignoire> {
    private final Baignoire baignoire;

    /**
     * Constructeur pour créer un service de remplissage avec une baignoire associée.
     *
     * @param baignoire La baignoire à remplir.
     */
    public Remplir(Baignoire baignoire) {
        this.baignoire = baignoire;
    }

    /**
     * Crée une tâche pour le service planifié.
     *
     * @return La tâche de remplissage.
     */
    @Override
    protected Task<Baignoire> createTask() {
        return new Task<Baignoire>() {
            @Override
            protected Baignoire call() throws Exception {
                return baignoire;
            }
        };
    }
}
