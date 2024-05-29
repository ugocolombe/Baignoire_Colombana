# Installation

Ce guide vous aidera à installer et configurer le projet de simulation de baignoire sur votre machine.

## Prérequis

- Java Development Kit (JDK) 17 ou supérieur.
- Maven 3.6.3 ou supérieur.
- Un environnement de développement intégré (IDE) tel que IntelliJ IDEA ou Eclipse.

## Étapes d'installation

### 1. Cloner le dépôt GitHub

Ouvrez un terminal ou une invite de commande et exécutez la commande suivante pour cloner le dépôt :

```sh
git clone https://github.com/ugocolombe/Baignoire_Colombana
cd Baignoire_Colombana
```

### 2. Compiler le projet et télécharger les dépendances

Exécutez la commande suivante pour compiler le projet et télécharger toutes les dépendances nécessaires :

```sh
mvn clean
mvn install
```

Cette commande exécutera les étapes suivantes :

- Nettoyage des fichiers de construction précédents (clean). 
- Téléchargement des dépendances définies dans le pom.xml. 
- Compilation du code source. 
- Création du fichier JAR exécutable.

### 3. Exécuter l'application

Une fois la construction terminée, exécutez l'application en utilisant Maven :

mvn javafx:run



### Problèmes potentiels

Problème : Maven ne trouve pas JavaFX

Si Maven ne parvient pas à trouver les dépendances JavaFX, assurez-vous d'avoir ajouté les bonnes dépendances dans votre pom.xml :

```sh
<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>17</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>17</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-graphics</artifactId>
        <version>17</version>
    </dependency>
</dependencies>
```


En suivant ces étapes, vous devriez être en mesure d'installer et d'exécuter le projet "Baignoire_Colombana" sur votre machine.