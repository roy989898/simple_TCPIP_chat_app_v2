package pom.poly.com.simple_tcpip_chat_app_v2;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

public class BuddyAndChatListContentProvider extends ContentProvider {
    private static final int URI_ROOT = 0, DB_TABLE_BUDDY = 1, DB_TABLE_CHATHISTORY = 2;
    private static final UriMatcher sUriMatcher = new UriMatcher(URI_ROOT);

    static {
        sUriMatcher.addURI(AUTHORITY, BuddyListSQLiteHelper.TABLE_BUDDYS, DB_TABLE_BUDDY);
        sUriMatcher.addURI(AUTHORITY, BuddyListSQLiteHelper.TABLE_CHAT_HISTORY, DB_TABLE_CHATHISTORY);
    }

    private static String AUTHORITY = "pom.poly.com.simple_tcpip_chat_app_v2.BuddyAndChatListContentProvider";
    private static final String CONTENT_UR_S = "content://" + AUTHORITY + "/";
    public static final Uri CONTENT_URI = Uri.parse(CONTENT_UR_S);
    public static final Uri CONTENT_URI_BUDDY = Uri.parse(CONTENT_UR_S + BuddyListSQLiteHelper.TABLE_BUDDYS);
    public static final Uri CONTENT_URI_CHAT = Uri.parse(CONTENT_UR_S + BuddyListSQLiteHelper.TABLE_CHAT_HISTORY);
    private SQLiteDatabase database;

    public BuddyAndChatListContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.

        if (sUriMatcher.match(uri) == DB_TABLE_BUDDY) {
            long rowId = database.delete(BuddyListSQLiteHelper.TABLE_BUDDYS, selection + "=?", selectionArgs);
            //get the uri for the newly inserted item.
            Uri insertedRowUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            //在ContentProvider的insert,update,delete等改变之后调用,通知那些监测databases变化的observer
            getContext().getContentResolver().notifyChange(insertedRowUri, null);
            return 1;

        }
        if (sUriMatcher.match(uri) == DB_TABLE_CHATHISTORY) {
            long rowId = database.delete(BuddyListSQLiteHelper.TABLE_CHAT_HISTORY, selection + "=?", selectionArgs);
            //get the uri for the newly inserted item.
            Uri insertedRowUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            //在ContentProvider的insert,update,delete等改变之后调用,通知那些监测databases变化的observer
            getContext().getContentResolver().notifyChange(insertedRowUri, null);
            return 1;

        }
        return -1;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        if (sUriMatcher.match(uri) == DB_TABLE_BUDDY) {
            long rowId = database.insert(BuddyListSQLiteHelper.TABLE_BUDDYS, null, values);
            //get the uri for the newly inserted item.
            Uri insertedRowUri = ContentUris.withAppendedId(CONTENT_URI_BUDDY, rowId);
            //在ContentProvider的insert,update,delete等改变之后调用,通知那些监测databases变化的observer
            getContext().getContentResolver().notifyChange(insertedRowUri, null);
            return insertedRowUri;

        }
        if (sUriMatcher.match(uri) == DB_TABLE_CHATHISTORY) {
            long rowId = database.insert(BuddyListSQLiteHelper.TABLE_CHAT_HISTORY, null, values);
            //get the uri for the newly inserted item.
            Uri insertedRowUri = ContentUris.withAppendedId(CONTENT_URI_CHAT, rowId);
            //在ContentProvider的insert,update,delete等改变之后调用,通知那些监测databases变化的observer
            getContext().getContentResolver().notifyChange(insertedRowUri, null);
            return insertedRowUri;

        }
        return CONTENT_URI;
    }

    @Override
    public boolean onCreate() {
        BuddyListSQLiteHelper budySQLHelp = new BuddyListSQLiteHelper(getContext());
        database = budySQLHelp.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String[] d_column = {BuddyListSQLiteHelper.COLUMN_PHONENUMNER};
        if (sUriMatcher.match(uri) == DB_TABLE_BUDDY) {
            Cursor cursor = database.query(BuddyListSQLiteHelper.TABLE_BUDDYS, d_column, selection, selectionArgs, null, null, sortOrder);
            return cursor;

        }
        String[] d2_column = {BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MTYPE, BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MESSAGE, BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_PHONENUMNER};
        if (sUriMatcher.match(uri) == DB_TABLE_CHATHISTORY) {
            Cursor cursor = database.query(BuddyListSQLiteHelper.TABLE_BUDDYS, d2_column, selection, selectionArgs, null, null, sortOrder);
            return cursor;

        }
        return new Cursor() {
            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public int getPosition() {
                return 0;
            }

            @Override
            public boolean move(int offset) {
                return false;
            }

            @Override
            public boolean moveToPosition(int position) {
                return false;
            }

            @Override
            public boolean moveToFirst() {
                return false;
            }

            @Override
            public boolean moveToLast() {
                return false;
            }

            @Override
            public boolean moveToNext() {
                return false;
            }

            @Override
            public boolean moveToPrevious() {
                return false;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean isBeforeFirst() {
                return false;
            }

            @Override
            public boolean isAfterLast() {
                return false;
            }

            @Override
            public int getColumnIndex(String columnName) {
                return 0;
            }

            @Override
            public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
                return 0;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return null;
            }

            @Override
            public String[] getColumnNames() {
                return new String[0];
            }

            @Override
            public int getColumnCount() {
                return 0;
            }

            @Override
            public byte[] getBlob(int columnIndex) {
                return new byte[0];
            }

            @Override
            public String getString(int columnIndex) {
                return null;
            }

            @Override
            public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {

            }

            @Override
            public short getShort(int columnIndex) {
                return 0;
            }

            @Override
            public int getInt(int columnIndex) {
                return 0;
            }

            @Override
            public long getLong(int columnIndex) {
                return 0;
            }

            @Override
            public float getFloat(int columnIndex) {
                return 0;
            }

            @Override
            public double getDouble(int columnIndex) {
                return 0;
            }

            @Override
            public int getType(int columnIndex) {
                return 0;
            }

            @Override
            public boolean isNull(int columnIndex) {
                return false;
            }

            @Override
            public void deactivate() {

            }

            @Override
            public boolean requery() {
                return false;
            }

            @Override
            public void close() {

            }

            @Override
            public boolean isClosed() {
                return false;
            }

            @Override
            public void registerContentObserver(ContentObserver observer) {

            }

            @Override
            public void unregisterContentObserver(ContentObserver observer) {

            }

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public void setNotificationUri(ContentResolver cr, Uri uri) {

            }

            @Override
            public Uri getNotificationUri() {
                return null;
            }

            @Override
            public boolean getWantsAllOnMoveCalls() {
                return false;
            }

            @Override
            public Bundle getExtras() {
                return null;
            }

            @Override
            public Bundle respond(Bundle extras) {
                return null;
            }
        };

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
