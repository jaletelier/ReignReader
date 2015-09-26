package test.app.javier.reignreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This Class help the maintainability, the creation and the upgrade of the database.
 * based on:http://www.hermosaprogramacion.com/2014/10/android-sqlite-bases-de-datos/
 */
public class ArticlesDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Articles.db";
    public static final int DATABASE_VERSION = 7;

    public ArticlesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ArticlesDataContract.CREATE_ARTICLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ArticlesDataContract.ARTICLES_TABLE_NAME);
        db.execSQL(ArticlesDataContract.CREATE_ARTICLE_TABLE);
    }
}
