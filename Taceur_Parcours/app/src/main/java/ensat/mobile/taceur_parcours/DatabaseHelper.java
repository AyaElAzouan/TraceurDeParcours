package ensat.mobile.taceur_parcours;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Cette classe gère les interactions avec la base de données SQLite pour l'application Traceur de Parcours.
 * Elle permet de créer, mettre à jour et gérer les données des parcours enregistrés.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Nom de la base de données
    private static final String DATABASE_NAME = "Parcours.db";
    // Version de la base de données (à incrémenter si la structure change)
    private static final int DATABASE_VERSION = 1;

    // Nom de la table et colonnes
    private static final String TABLE_NAME = "parcours";
    private static final String COLUMN_ID = "id"; // Identifiant unique pour chaque enregistrement
    private static final String COLUMN_DISTANCE = "distance"; // Distance parcourue (en mètres ou kilomètres)
    private static final String COLUMN_DURATION = "duration"; // Durée du parcours (en secondes)
    private static final String COLUMN_POSITIONS = "positions"; // Positions GPS sous forme de chaîne (JSON ou autre format)
    private static final String COLUMN_DATE = "date"; // Date du parcours (format texte, ex. "YYYY-MM-DD")

    /**
     * Constructeur de la classe DatabaseHelper.
     *
     * @param context Le contexte de l'application
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Méthode appelée lors de la création initiale de la base de données.
     * Elle est utilisée pour définir la structure de la base de données, notamment les tables.
     *
     * @param db L'instance de la base de données SQLite
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Requête SQL pour créer la table "parcours"
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // ID unique auto-incrémenté
                + COLUMN_DISTANCE + " REAL, " // Distance stockée sous forme de nombre réel
                + COLUMN_DURATION + " REAL, " // Durée stockée sous forme de nombre réel
                + COLUMN_POSITIONS + " TEXT, " // Positions GPS stockées sous forme de texte
                + COLUMN_DATE + " TEXT)"; // Date stockée sous forme de texte
        db.execSQL(createTable); // Exécution de la requête SQL
    }

    /**
     * Méthode appelée lors de la mise à jour de la base de données.
     * Utilisée pour gérer les changements de structure de la base de données.
     *
     * @param db         L'instance de la base de données SQLite
     * @param oldVersion L'ancienne version de la base de données
     * @param newVersion La nouvelle version de la base de données
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Supprime la table existante si elle existe
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Recrée la table avec la nouvelle structure
        onCreate(db);
    }

    /**
     * Méthode pour sauvegarder un nouveau parcours dans la base de données.
     *
     * @param distance  La distance parcourue
     * @param duration  La durée du parcours
     * @param positions Les positions GPS (sous forme de chaîne)
     * @param date      La date du parcours
     * @return true si l'insertion est réussie, false sinon
     */
    public boolean saveParcours(double distance, double duration, String positions, String date) {
        // Ouvre la base de données en mode écriture
        SQLiteDatabase db = this.getWritableDatabase();
        // Prépare les valeurs à insérer dans la table
        ContentValues values = new ContentValues();
        values.put(COLUMN_DISTANCE, distance); // Distance parcourue
        values.put(COLUMN_DURATION, duration); // Durée du parcours
        values.put(COLUMN_POSITIONS, positions); // Positions GPS
        values.put(COLUMN_DATE, date); // Date du parcours
        // Insère les valeurs dans la table et récupère le résultat
        long result = db.insert(TABLE_NAME, null, values);
        // Retourne true si l'insertion a réussi (result != -1), sinon false
        return result != -1;
    }
}
