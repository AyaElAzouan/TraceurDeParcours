# **TraceurDeParcours**

Enregistrement du trajet d'un utilisateur en temps réel à l'aide de la géolocalisation, puis l'affichage de la trace du trajet sur une carte; Calcule la distance totale parcourue ainsi que la durée du trajet.

## **Fonctionnalités principales**

- **Capture de la position de l'utilisateur.**
- **Affichage des parcours sur une carte.**
- **Sauvegarde des parcours** avec des informations détaillées telles que : durée, distance, date.

## **Prérequis**

Pour exécuter ou tester cette application mobile, assurez-vous de disposer des éléments suivants :

- **Système d'exploitation** : Windows, macOS ou Linux.

### **Environnement de développement** :

- **Android Studio** (version utilisée dans ce projet : 2024.2.1 Patch 2).

### **Configurations nécessaires dans Android Studio** :

Assurez-vous d'avoir installé et configuré les éléments suivants :

#### **SDK Tools (via le SDK Manager)** :
- **Emulator** : Pour exécuter l'application sur un émulateur Android.
- **Emulator Hypervisor Driver** : Pour optimiser les performances de l'émulateur sur les processeurs compatibles.
- **Android SDK Platform Tools** : Contient les outils essentiels pour le développement.
- **Android SDK Build Tools** : Nécessaire pour compiler et construire l'application.

- **Version SDK utilisé** : Android API Level 34 (Android 14).

#### **Langage de programmation** : Java.

#### **Émulateur ou appareil physique** :
- Un appareil Android ou un émulateur configuré dans Android Studio.

## **Installation et configuration**

### Pour configurer ces outils :

1. Ouvrez **Android Studio**.
2. Allez dans **File > Settings > Appearance & Behavior > System Settings > Android SDK**.
3. Sous l'onglet **SDK Tools**, cochez et installez les éléments suivants :
   - Emulator
   - Emulator Hypervisor Driver for AMD Processors (si applicable)
   - Android SDK Platform Tools
   - Android SDK Build Tools
4. Cliquez sur **Apply**, puis sur **OK** pour enregistrer les modifications.

## **Architecture du projet**

L'application suit une structure standard d'Android, avec une organisation des fichiers source et des ressources. Voici un aperçu de la structure de l'application :


![image](https://github.com/user-attachments/assets/6a8fa2df-e6ba-4568-ac42-ab9e5add3de4)


### **Composants principaux de l'application**

1. **Activités**
   - **MainActivity** : L'activité principale de l'application, servant de point d'entrée. Elle contient la logique pour l'interface utilisateur de base et permet de rediriger l'utilisateur vers les autres activités de l'application.
   - **MapsActivity** : Affiche la carte avec les itinéraires et les parcours de l'utilisateur. Cette activité permet à l'utilisateur de visualiser ses parcours sur une carte et de naviguer dans différentes vues cartographiques.
   - **RealTimeMapActivity** : Affiche une carte en temps réel, potentiellement pour suivre un parcours ou une trace en direct. Cette activité permet de suivre un itinéraire ou de visualiser l'emplacement actuel de l'utilisateur en temps réel.
   - **SavedRoutesActivity** : Permet à l'utilisateur de voir les itinéraires sauvegardés, avec une interface adaptée pour afficher une liste d'éléments sauvegardés. L'utilisateur peut consulter les trajets qu'il a enregistrés pour un usage futur.

2. **Helpers**
   - **DatabaseHelper** : Classe de gestion de la base de données, utilisée pour effectuer des opérations CRUD (Create, Read, Update, Delete) sur les données de l'application, comme les itinéraires sauvegardés.

3. **Ressources**
   - **Layouts** : Les fichiers XML définissent l'interface utilisateur pour chaque activité :
     - `activity_maps.xml` : Interface pour afficher la carte dans l'activité de carte.
     - `activity_realtimemap.xml` : Interface pour afficher la carte en temps réel.
     - `activity_saved_routes.xml` : Interface pour afficher les itinéraires sauvegardés.
     - `activity_traceur_parcours.xml` : Interface pour la page principale.
   - **Drawable** : Contient des images et des icônes utilisées dans l'interface utilisateur de l'application.
   - **Values** : Contient les ressources globales telles que les chaînes de texte (strings.xml), les couleurs (colors.xml), les styles (styles.xml), etc.

## **Installation des dépendances**

Pour utiliser **OSMdroid** dans l'application, vous devez suivre ces étapes pour configurer correctement la bibliothèque.

### 1. Ajouter la dépendance dans le fichier **libs.versions.toml** :

Ajoutez la version d'OSMdroid dans le fichier **libs.versions.toml** de votre projet. Par exemple :

![image](https://github.com/user-attachments/assets/8792df4f-7c7c-4c7d-a67c-e9fefc6f0e14)

![image](https://github.com/user-attachments/assets/c235c32c-f0f7-4391-9af7-b1da2ec1a567)


## **2. Importer la dépendance dans build.gradle**
Ensuite, vous devez ajouter la dépendance dans le fichier build.gradle de votre module :

![image](https://github.com/user-attachments/assets/471aacf2-179f-4a98-9460-e084f6af4076)


## **3. Synchroniser avec Gradle**
Après avoir ajouté la dépendance, n'oubliez pas de synchroniser le projet avec les fichiers Gradle pour que les modifications prennent effet. Vous pouvez le faire en cliquant sur Sync Now dans la barre de notification qui apparaît dans Android Studio ou en sélectionnant File > Sync Project with Gradle Files.


