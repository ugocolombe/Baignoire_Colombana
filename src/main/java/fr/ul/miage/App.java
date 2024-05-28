package fr.ul.miage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * La classe App permet de démarrer l'application JavaFX.
 */

public class App extends Application {

    /**
     * Constructeur par défaut de la classe App.
     */
    public App() {
        // Constructeur par défaut
    }

    /**
     * Méthode pour le démarrage de l'application JavaFX.
     *
     * @param primaryStage Le stage principal pour cette application.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/simulation.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600); // Définir la taille ici
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation de Baignoire");
        primaryStage.show();
    }

    /**
     * Méthode principale pour lancer l'application.
     *
     * @param args Les arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
