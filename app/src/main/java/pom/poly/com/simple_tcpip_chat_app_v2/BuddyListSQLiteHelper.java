package pom.poly.com.simple_tcpip_chat_app_v2;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by User on 11/3/2015.
 */
public class BuddyListSQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_BUDDYS = "buddys";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PHONENUMNER = "phonenumber";
    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_BUDDYS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_PHONENUMNER
            + " text not null);";
    public static final String TABLE_CHAT_HISTORY = "chats";
    public static final String COLUMN_CHAT_HISTORY_ID = "_id";
    public static final String COLUMN_CHAT_HISTORY_PHONENUMNER = "phonenumber";
    public static final String COLUMN_CHAT_HISTORY_MESSAGE = "message";
    public static final String COLUMN_CHAT_HISTORY_MTYPE = "type";
    private static final String DATABASE_CHAT_HISTORY_CREATE = "create table "
            + TABLE_CHAT_HISTORY + "(" + COLUMN_CHAT_HISTORY_ID
            + " integer primary key autoincrement, " + COLUMN_CHAT_HISTORY_MESSAGE + "  text not null, " + COLUMN_CHAT_HISTORY_MTYPE + "  text not null, " + COLUMN_CHAT_HISTORY_PHONENUMNER
            + " text not null);";
    public static final String MESSAGE_TYPE_RECEIVE = "receive";
    public static final String MESSAGE_TYPE_SEND = "send";
    public static final String DATABASE_NAME = "buddys.db";
    private static final int DATABASE_VERSION = 1;

    public BuddyListSQLiteHelper(Context context){
        super(context, DATABASE_NAME, null,DATABASE_VERSION);

    }
    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
       db.execSQL(DATABASE_CHAT_HISTORY_CREATE);

    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(BuddyListSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDDYS);
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_HISTORY);
        onCreate(db);

    }
}
