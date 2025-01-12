package ensat.mobile.taceur_parcours;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Parcours.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "parcours";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DISTANCE = "distance";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_POSITIONS = "positions";
    private static final String COLUMN_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DISTANCE + " REAL, "
                + COLUMN_DURATION + " REAL, "
                + COLUMN_POSITIONS + " TEXT, "
                + COLUMN_DATE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean saveParcours(double distance, double duration, String positions, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DISTANCE, distance);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_POSITIONS, positions);
        values.put(COLUMN_DATE, date);
        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }
}
