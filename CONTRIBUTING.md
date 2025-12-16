## 🌳 1. Stratégie de Branche : Le Git Flow Simplifié

Nous utilisons un modèle simple pour gérer notre historique de code : le **GitHub Flow**. Il garantit que le code principal est toujours stable.

### A. Les Branches Clés

| Branche | Rôle | Règle |
| :--- | :--- | :--- |
| **`main`** | La seule branche de **production stable**. Elle représente le code prêt à fonctionner. | **Interdiction de pousser (push) directement.** La fusion se fait uniquement via Pull Request. |
| **`feature/xxx`** | Les branches de travail, créées pour chaque tâche (Issue). | Sont temporaires et doivent être supprimées après la fusion. |

### B. Le Cycle de Vie d'une Tâche (Issue)

Pour chaque nouvelle fonctionnalité, correction de bug ou amélioration que vous souhaitez implémenter :

#### Étape 1 : Préparation et Création de la Branche

1.  Assurez-vous d'être sur la dernière version de `main` :
    ```bash
    git switch main
    git pull
    ```
2.  Créez une nouvelle branche pour votre travail. Utilisez un nom clair, souvent lié à l'Issue ou à la fonctionnalité :
    ```bash
    git switch -c feature/nom-de-votre-tache
    # Exemple : git checkout -b feature/impl-moteur-objectifs
    ```

#### Étape 2 : Développement et Commit

1.  Travaillez sur votre branche.
2.  Utilisez la **Convention de Commit** (voir Section 2) pour chaque sauvegarde de progrès.

#### Étape 3 : Demande de Fusion (Pull Request - PR)

1.  Une fois la tâche terminée et les tests passés, poussez votre branche sur GitHub :
    ```bash
    git push -u origin feature/nom-de-votre-tache
    ```
2.  Sur GitHub, ouvrez une **Pull Request (PR)** pour fusionner votre branche vers `main`.

#### Étape 4 : Revue et Suppression

1.  La PR sera révisée par un pair (Code Review).
2.  Après validation, la PR est fusionnée.
3.  **Action finale :** Supprimez la branche de travail sur GitHub et en local :
    ```bash
    git branch -d feature/nom-de-votre-tache
    ```
