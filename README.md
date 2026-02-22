# 🎋 MyTakenoko - Moteur de Jeu & Intelligence Artificielle

Une implémentation robuste et complète du célèbre jeu de plateau **Takenoko** en **Java**. 
Ce projet académique majeur (réalisé à Polytech) modélise non seulement la logique complexe du jeu de société, mais intègre également un système avancé d'Intelligences Artificielles capables de s'affronter de manière autonome de la modélisation à la simulation massive.

## 🚀 Fonctionnalités Principales

* **Moteur de jeu complet :** Implémentation fidèle des règles officielles (plateau hexagonal, déplacements du Panda et du Jardinier, gestion de la météo, réseau d'irrigation et croissance des bambous).
* **Intelligence Artificielle Modulaire :** Développement de plusieurs profils de Bots via le patron de conception *Strategy* :
  * *Bot Random* : Joue de manière aléatoire (baseline).
  * *Bot Spécialiste* : Focus sur un seul type d'objectif (Panda, Jardinier, Architecte).
  * *Bot Équipe* : Système de vote pondéré où plusieurs "sous-bots" experts proposent des actions.
  * *Bot Expert* : IA avancée utilisant des fonctions heuristiques pour évaluer et scorer chaque action possible.
* **Simulations Massives :** Capacité à lancer des milliers de parties consécutives (`SimulationRunner`) et à générer des statistiques au format CSV pour analyser les taux de victoire des différentes IA.
* **Géométrie Hexagonale :** Gestion avancée des coordonnées spatiales (axiales/cubiques) pour la logique du plateau.

## 🛠️ Technologies & Outils (Stack technique)

Ce projet met l'accent sur les bonnes pratiques de l'ingénierie logicielle et du DevOps :

* **Langage :** Java 21
* **Gestion de projet & Build :** Maven
* **Tests & Mocking :** JUnit 5, Mockito (Tests basés sur l'état et le comportement)
* **Qualité de code :** SonarQube (Analyse statique)
* **DevOps :** Docker, CI/CD (Intégration continue)

## 🏗️ Architecture Logicielle

Le projet est conçu autour de principes de l'**Orienté Objet (POO)** stricts (SOLID, DRY) et exploite plusieurs patrons de conception :
* **Strategy :** Pour isoler et interchanger facilement les comportements des intelligences artificielles.
* **Command / Action :** Encapsulation des actions des joueurs (ex: `DeplacerPanda`, `PoserParcelle`) pour faciliter leur exécution et leur validation.
* **Façade / Délégation :** Pour l'orchestration des bots complexes (BotEquipe) répartissant les rôles à des sous-systèmes spécialisés.

## ⚙️ Installation et Exécution

### Prérequis
* Java 21 ou supérieur
* Maven 3.x

### Compilation et Tests
Pour compiler le projet et lancer la suite de tests unitaires :
```bash
git clone [https://github.com/ton-pseudo/MyTakenoko.git](https://github.com/ton-pseudo/MyTakenoko.git)
cd MyTakenoko
mvn clean package
```
### Lancer une simulation
Pour lancer le jeu (par défaut ou avec des arguments de simulation) :
```bash
# Lancer une partie classique
mvn exec:java

# Lancer une simulation massive de 2000 parties (statistiques)
mvn exec:java "-Dexec.args=--2thousands"
```
## 👥 Auteur

EL HADI Ilias - Élève Ingénieur en Informatique à Polytech Nice Sophia.
BAZON Nathan - Élève Ingénieur en Informatique à Polytech Nice Sophia.
TAMEHMACHT Florian - Élève Ingénieur en Informatique à Polytech Nice Sophia.
CANTO-RINGELSTEIN Timeo- Élève Ingénieur en Informatique à Polytech Nice Sophia.


