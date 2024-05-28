package fr.ul.miage;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * La classe Contrôleur pour gérer l'interface graphique de la simulation de remplissage de la baignoire.
 */
public class BaignoireController {
    @FXML
    private TextField capaciteBaignoireField;
    @FXML
    private TextField nombreRobinetsField;
    @FXML
    private TextField nombreFuitesField;
    @FXML
    private VBox robinetsBox;
    @FXML
    private VBox fuitesBox;
    @FXML
    private VBox fuitesRepairBox;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Rectangle rect;
    @FXML
    private Rectangle baignoireBackground;
    @FXML
    private Rectangle waterLevelTop;
    @FXML
    private Label remplissageLabel;
    @FXML
    private Label resultLabel;
    @FXML
    private HBox robinetsGraphics;
    @FXML
    private HBox fuitesGraphics;
    @FXML
    private LineChart<Number, Number> lineChart;

    private Baignoire baignoire;
    private List<Robinet> robinets;
    private List<Fuite> fuites;
    private ScheduledService<Void> svc;
    private Instant startTime;
    private Instant endTime;
    private boolean isRunning;
    private final Pattern pattern = Pattern.compile("\\d*");

    private XYChart.Series<Number, Number> series;
    private int elapsedTime;

    private double totalWaterUsed; // Variable to keep track of the total water used

    /**
     * Constructeur par défaut de la classe BaignoireController.
     */
    public BaignoireController() {
        // Constructeur par défaut
    }

    /**
     * Initialise le contrôleur et configure les éléments de l'interface graphique.
     */
    @FXML
    public void initialize() {
        rect.setHeight(0.0);
        rect.setFill(Color.BLUE);
        waterLevelTop.setHeight(0.0);
        waterLevelTop.setFill(Color.BLACK);
        isRunning = false;

        series = new XYChart.Series<>();
        lineChart.getData().add(series);

        lineChart.setLegendVisible(false);  // Hide legend

        capaciteBaignoireField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!pattern.matcher(newValue).matches()) {
                capaciteBaignoireField.setText(oldValue);
            }
        });

        nombreRobinetsField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!pattern.matcher(newValue).matches()) {
                nombreRobinetsField.setText(oldValue);
            } else {
                updateRobinets();
            }
        });

        nombreFuitesField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!pattern.matcher(newValue).matches()) {
                nombreFuitesField.setText(oldValue);
            } else if (!isRunning) {
                updateFuites();
            }
        });
    }

    /**
     * Met à jour les robinets affichés dans l'interface graphique en fonction du nombre spécifié.
     */
    @FXML
    public void updateRobinets() {
        robinetsBox.getChildren().clear();
        robinetsBox.getChildren().add(new Label("Débits des robinets:"));
        robinetsGraphics.getChildren().clear();
        int nbRobinets;
        try {
            nbRobinets = Integer.parseInt(nombreRobinetsField.getText());
            if (nbRobinets > 5) {
                showAlert("Limite dépassée", "Le nombre de robinets ne peut pas dépasser 5.");
                return;
            }
        } catch (NumberFormatException e) {
            return;
        }
        for (int i = 0; i < nbRobinets; i++) {
            TextField robinetField = new TextField();
            robinetField.setPromptText("Débit du robinet " + (i + 1));
            robinetsBox.getChildren().add(robinetField);

            Circle robinetCircle = new Circle(5, Color.GREEN);
            robinetsGraphics.getChildren().add(robinetCircle);

            final int index = i;
            robinetField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!pattern.matcher(newValue).matches()) {
                    robinetField.setText(oldValue);
                } else if (isRunning) {
                    try {
                        double debit = Double.parseDouble(newValue);
                        robinets.get(index).setDebit(debit);
                        Platform.runLater(this::updateRemplissageUI);
                    } catch (NumberFormatException ignored) {
                    }
                }
            });
        }
    }

    /**
     * Met à jour les fuites affichées dans l'interface graphique en fonction du nombre spécifié.
     */
    @FXML
    public void updateFuites() {
        fuitesBox.getChildren().clear();
        fuitesBox.getChildren().add(new Label("Débits des fuites:"));
        fuitesRepairBox.getChildren().clear();
        fuitesGraphics.getChildren().clear();
        int nbFuites;
        try {
            nbFuites = Integer.parseInt(nombreFuitesField.getText());
            if (nbFuites > 5) {
                showAlert("Limite dépassée", "Le nombre de fuites ne peut pas dépasser 5.");
                return;
            }
        } catch (NumberFormatException e) {
            return;
        }

        for (int i = 0; i < nbFuites; i++) {
            TextField fuiteField = new TextField();
            fuiteField.setPromptText("Débit de la fuite " + (i + 1));
            fuitesBox.getChildren().add(fuiteField);

            Circle fuiteCircle = new Circle(5, Color.RED);
            fuitesGraphics.getChildren().add(fuiteCircle);

            Button reparerButton = new Button("Réparer fuite " + (i + 1));
            reparerButton.setDisable(true);
            final int index = i;
            reparerButton.setOnAction(e -> reparerFuite(index));
            fuitesRepairBox.getChildren().add(reparerButton);

            fuiteField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!pattern.matcher(newValue).matches()) {
                    fuiteField.setText(oldValue);
                }
            });
        }
    }

    /**
     * Affiche un message d'alerte.
     *
     * @param title   Le titre de l'alerte.
     * @param message Le message de l'alerte.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Vérifie si tous les champs de saisie sont valides.
     *
     * @return true si tous les champs sont valides, sinon false.
     */
    private boolean areFieldsValid() {
        if (capaciteBaignoireField.getText().isEmpty() ||
                nombreRobinetsField.getText().isEmpty() ||
                nombreFuitesField.getText().isEmpty()) {
            showAlert("Champs manquants", "Tous les champs doivent être remplis.");
            return false;
        }

        // Check that all text fields for robinet and fuite debits are filled
        for (Node node : robinetsBox.getChildren()) {
            if (node instanceof TextField && ((TextField) node).getText().isEmpty()) {
                showAlert("Champs manquants", "Tous les champs doivent être remplis.");
                return false;
            }
        }
        for (Node node : fuitesBox.getChildren()) {
            if (node instanceof TextField && ((TextField) node).getText().isEmpty()) {
                showAlert("Champs manquants", "Tous les champs doivent être remplis.");
                return false;
            }
        }

        return true;
    }

    /**
     * Démarre la simulation de remplissage de la baignoire.
     */
    @FXML
    public void demarre() {
        if (isRunning || !areFieldsValid()) return;

        double capacite = Double.parseDouble(capaciteBaignoireField.getText());
        baignoire = new Baignoire(capacite);

        robinets = new ArrayList<>();
        if (robinetsBox.getChildren().size() > 1) {
            List<TextField> robinetFields = (List<TextField>) (List<?>) robinetsBox.getChildren().subList(1, robinetsBox.getChildren().size());
            for (TextField robinetField : robinetFields) {
                double debit = Double.parseDouble(robinetField.getText());
                Robinet robinet = new Robinet(debit, baignoire);
                robinets.add(robinet);
                robinet.start();
            }
        }

        fuites = new ArrayList<>();
        if (fuitesBox.getChildren().size() > 1) {
            List<TextField> fuiteFields = (List<TextField>) (List<?>) fuitesBox.getChildren().subList(1, fuitesBox.getChildren().size());
            for (TextField fuiteField : fuiteFields) {
                double debit = Double.parseDouble(fuiteField.getText());
                Fuite fuite = new Fuite(debit, baignoire);
                fuites.add(fuite);
                fuite.start();
            }
        }

        startButton.setDisable(true);
        stopButton.setDisable(false);
        capaciteBaignoireField.setDisable(true);
        nombreRobinetsField.setDisable(true);
        nombreFuitesField.setDisable(true);
        fuitesBox.getChildren().forEach(node -> {
            if (node instanceof TextField) {
                node.setDisable(true); // Prevent modification of fuite debits during simulation
            }
        });

        // Enable repair buttons only after the simulation starts
        fuitesRepairBox.getChildren().forEach(node -> {
            if (node instanceof Button) {
                node.setDisable(false);
            }
        });

        startTime = Instant.now();
        totalWaterUsed = 0; // Reset total water used
        elapsedTime = 0; // Reset elapsed time
        series.getData().clear(); // Clear previous data
        isRunning = true;

        svc = new ScheduledService<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        Platform.runLater(() -> updateRemplissageUI());
                        return null;
                    }
                };
            }
        };
        svc.setPeriod(javafx.util.Duration.seconds(1));
        svc.start();
    }

    /**
     * Met à jour l'interface utilisateur pour afficher le niveau de remplissage de la baignoire.
     */
    private void updateRemplissageUI() {
        double newHeight = (baignoire.getVolume() / baignoire.getCapacite()) * baignoireBackground.getHeight();
        rect.setHeight(newHeight);
        rect.setY(baignoireBackground.getY() + baignoireBackground.getHeight() - newHeight);
        waterLevelTop.setHeight(4);
        waterLevelTop.setY(rect.getY() - 4); // Adjust the top black line position
        double tauxRemplissage = (baignoire.getVolume() / baignoire.getCapacite()) * 100;
        remplissageLabel.setText(String.format("%.2f%%", tauxRemplissage));

        // Add data to the chart
        elapsedTime++;
        series.getData().add(new XYChart.Data<>(elapsedTime, tauxRemplissage));

        // Calculate total water used
        double waterInflow = robinets.stream().mapToDouble(Robinet::getDebit).sum();
        double waterOutflow = fuites.stream().mapToDouble(Fuite::getDebit).sum();
        if (tauxRemplissage < 100) {
            totalWaterUsed += waterInflow - waterOutflow;
        } else {
            totalWaterUsed = robinets.stream().mapToDouble(Robinet::getDebit).sum() * elapsedTime - waterOutflow * elapsedTime;
            arreter();
        }
    }

    /**
     * Arrête la simulation de remplissage de la baignoire.
     */
    @FXML
    public void arreter() {
        if (!isRunning) return;

        isRunning = false;
        if (svc != null) svc.cancel();
        for (Robinet robinet : robinets) {
            robinet.stopRunning();
        }
        for (Fuite fuite : fuites) {
            fuite.stopRunning();
        }

        startButton.setDisable(false);
        stopButton.setDisable(true);

        capaciteBaignoireField.setDisable(false);
        nombreRobinetsField.setDisable(false);
        nombreFuitesField.setDisable(false);
        robinetsBox.getChildren().forEach(node -> {
            if (node instanceof TextField) {
                node.setDisable(false);
            }
        });
        fuitesBox.getChildren().forEach(node -> {
            if (node instanceof TextField) {
                node.setDisable(false);
            }
        });

        // Disable repair buttons after simulation stops
        fuitesRepairBox.getChildren().forEach(node -> {
            if (node instanceof Button) {
                node.setDisable(true);
            }
        });

        // Display total water used and simulation time in milliseconds
        endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        resultLabel.setText(String.format("Simulation terminée !\nEau totale utilisée: %.2f litres\nTemps de simulation: %d ms",
                totalWaterUsed, duration.toMillis()));
    }

    /**
     * Répare une fuite spécifiée par son index.
     *
     * @param index L'index de la fuite à réparer.
     */
    private void reparerFuite(int index) {
        if (index >= 0 && index < fuites.size()) {
            fuites.get(index).stopRunning();
            Node node = fuitesGraphics.getChildren().get(index);
            if (node instanceof Circle) {
                ((Circle) node).setFill(Color.GRAY); // Change the circle color to indicate repair
            }
            Button button = (Button) fuitesRepairBox.getChildren().get(index);
            button.setDisable(true); // Disable the button
            Platform.runLater(this::updateRemplissageUI); // Update the bathtub filling dynamically
        }
    }
}
