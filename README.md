#  Bomberman – Projet SAE 2.01

Développement d’un jeu **Bomberman** avec JavaFX dans le cadre de la **SAÉ 2.01 – Développement d’une application** au BUT Informatique d’Aix-en-Provence.

##  Durée de développement
**1 semaine**

##  Équipe de développement

- [**Kensufox**](https://github.com/Kensufox) (QUILLIEC Matisse)  
  - Génération et chargement de la map  
  - Gestion des bombes et des explosions  
  - Création et effets des Power-Up  
  - Intégration de musique et effets sonores  
  - Création des textures et sprites  
  - Implémentation du mode **Capture the Flag**  
  - Intégration d’un éditeur de niveaux  
  - Javadoc et tests unitaires partiels  

- [**D-WADE**](https://github.com/LoanALLARD) (ALLARD Loan)  
  - Création de l’interface utilisateur et des menus  
  - Gestion des inputs et des keybinds personnalisés  
  - Gestion du score et navigation entre menus  
  - Réalisation du diagramme UML  

- [**RADJOU Dinesh**](https://github.com/RADJOU-Dinesh-24003262)  
  - Création de l'IA du bot dans le mode **Contre l’ordinateur**  
  - Implémentation d'un gestionnaire de profils  
  - Améliorations UX liées au comportement du bot  
  - Javadoc et tests unitaires partiels  

- **NEIRA Leandro**  
  - Implémentation du système de cooldown des bombes  

---

##  Sommaire
1. [Introduction](#1-introduction)  
2. [Fonctionnalités implémentées](#2-fonctionnalités-implémentées)  
3. [Analyse et conception](#3-analyse-et-conception)  
4. [Répartition des tâches](#4-répartition-des-tâches)  
5. [Annexes](#5-annexes)

---

## 1. Introduction

### 1.1 Présentation générale
Ce projet consiste à développer une version personnalisée du célèbre jeu **Super Bomberman**, en Java avec **JavaFX** pour l’interface graphique.

### 1.2 Objectifs fonctionnels et techniques

#### Objectifs fonctionnels
- Génération ou chargement de la map
- Déplacement des joueurs
- Gestion des collisions
- Pose de bombes et explosions
- Apparition et application des Power-Ups

#### Fonctionnalités additionnelles
- Mode **Contre l’ordinateur** avec IA
- Lecteur de musique + effets sonores
- Mode **Capture the Flag**
- **Éditeur de niveaux**
- Gestionnaire de **profils joueurs**

#### Objectifs techniques
- Architecture **MVC**
- Documentation via **Javadoc**
- Tests unitaires avec **JUnit**
- Versionnement avec **Git & GitHub**
- Interface fluide et ergonomique avec **JavaFX**

### 1.3 Contexte pédagogique
Ce projet met en œuvre des compétences telles que :
- Programmation orientée objet
- Interfaces graphiques
- Qualité logicielle et bonnes pratiques
- Expérience utilisateur (UX)
- Communication technique

### 1.4 Technologies utilisées
| Technologie         | Usage |
|---------------------|-----------------------------|
| **Java**            | Langage principal |
| **JavaFX**          | Interface graphique, son |
| **CSS**             | Stylisation des interfaces |
| **Aseprite**        | Création des sprites |
| **PlantUML**        | Diagrammes UML |
| **JUnit**           | Tests unitaires |
| **Git & GitHub**    | Versionnement, collaboration |
| **Javadoc**         | Documentation du code |

---

## 2. Fonctionnalités implémentées

###  Contrôles joueurs
- **Player 1** : Z (haut), S (bas), Q (gauche), D (droite), Espace (bombe)
- **Player 2** : I (haut), K (bas), J (gauche), L (droite), P (bombe)
- **Keybinds** personnalisables via le menu Options

###  Bombes
- Délai d’explosion : 3s
- Cooldown de pose : 2s
- Portée par défaut : 3 (dans les 4 directions cardinales)
- Explosions détruisent les blocs cassables et les joueurs

###  Power-Ups
- Apparaissent avec 30% de chance lors de la destruction d’un bloc
- Effets :
  - **Speed** : vitesse augmentée
  - **Bomb-Range** : portée des bombes augmentée
  - **Bomb-Cooldown** : délai entre poses réduit

###  Modes de jeu
- **Classique** : 1v1, le dernier survivant gagne
- **Contre l’ordinateur** : 1 joueur vs un bot (IA)
- **Capture the Flag** : Récupérer le drapeau adverse et le ramener à son spawn
- **Éditeur de niveau** : Création et sauvegarde de maps personnalisées

###  Musique et sons
- **Modes de lecture** : Normal, Boucle, Aléatoire
- **Transitions** : fadeIn/fadeOut pour une lecture fluide

###  Map
- Structure chargée depuis un `.txt`  
- Génération automatique de blocs cassables dans zones libres (hors spawn)
- Taux de génération : 70%

###  IA du bot
- Comportement stratégique : évite explosions, cherche joueur, pose bombes
- Plusieurs niveaux de difficulté
- S’adapte en fonction du comportement joueur

---

## 3. Analyse et conception

### 3.1 Modélisation UML
Diagramme de classes UML disponible en **annexe**.

### Classes principales :
| Classe | Rôle |
|--------|------|
| `GameApplication` | Lancement de l’application |
| `GameMap` | Chargement et génération de la carte |
| `Player` | Gestion des actions du joueur |
| `InputHandler` | Gestion des entrées clavier |
| `BotPlayer` | IA du bot via `PathFinder`, `BombAnalyzer`… |
| `GameMapController` | Boucle de jeu (mode classique) |
| `GameMapControllerBot` | Boucle de jeu contre IA |
| `GameMapControllerFlag` | Boucle Capture the Flag |
| `Power` | Gestion des power-ups et effets |

### 3.2 Architecture logicielle
Application structurée selon le **modèle MVC**, garantissant :
- Une séparation claire des responsabilités
- Une meilleure maintenabilité
- Une plus grande évolutivité

---