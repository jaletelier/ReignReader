package test.app.javier.reignreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * This Class have all the information of the SQLite database.
 * Define the Schema, and the most important actions.
 */
public class ArticlesDataContract {
    public static final String ARTICLES_TABLE_NAME="Articles";

    public static class ArticleColumns{
        public static final String INTERNAL_ID = BaseColumns._ID;
        public static final String ARTICLE_ID = "id";
        public static final String TITLE = "title";
        public static final String AUTHOR = "author";
        public static final String DATE = "date";
        public static final String HIDE = "hide";
        public static final String URL = "url";
    }
    public static final String CREATE_ARTICLE_TABLE="create table "+ARTICLES_TABLE_NAME+"( " +
            ArticleColumns.INTERNAL_ID + " integer "+"primary key autoincrement,"+
            ArticleColumns.ARTICLE_ID + " string "+"unique not null,"+
            ArticleColumns.TITLE + " string " + "not null,"+
            ArticleColumns.AUTHOR + " string "+"not null,"+
            ArticleColumns.DATE + " string "+"not null,"+
            ArticleColumns.URL + " string "+"not null,"+
            ArticleColumns.HIDE + " integer "+"not null"+
            " )";

    private ArticlesDBHelper dbHelper;
    private SQLiteDatabase database;

    public ArticlesDataContract(Context context){
        dbHelper = new ArticlesDBHelper(context);
        database = dbHelper.getWritableDatabase();
    }
    public boolean insert(String ArticleId, String Title, String Author, String Date, String url){
        ContentValues values=new ContentValues();
        values.put(ArticleColumns.ARTICLE_ID,ArticleId);
        values.put(ArticleColumns.TITLE,Title);
        values.put(ArticleColumns.AUTHOR,Author);
        values.put(ArticleColumns.DATE, Date);
        values.put(ArticleColumns.HIDE, 0); //0 is NOT HIDE.
        values.put(ArticleColumns.URL, url);
        try {
            database.insertOrThrow(ARTICLES_TABLE_NAME, null, values);
        }
        catch(SQLiteConstraintException e)
        {
            return false;
        }
        return true;
    }
    public void delete(int id){
        String selection = ArticleColumns.INTERNAL_ID + " = ?";
        String[] selectionArgs = { ""+id };
        database.delete(ARTICLES_TABLE_NAME, selection, selectionArgs);
    }
    public void hide(int id){
        //This hide a Article, we do not remove it from the Database because we want to remember that was deleted in the future.
        ContentValues values = new ContentValues();
        values.put(ArticleColumns.HIDE,1);
        String selection = ArticleColumns.INTERNAL_ID+ " = ?";
        String[] selectionArgs = { ""+id };
        database.update(ARTICLES_TABLE_NAME, values, selection, selectionArgs);
    }
    public Cursor readArticles(){
        String orderBy="dateTime("+ArticleColumns.DATE+") DESC";
        String[] columns=new String[]{ArticleColumns.INTERNAL_ID,ArticleColumns.TITLE,ArticleColumns.AUTHOR,ArticleColumns.DATE,ArticleColumns.URL};
        String selection=ArticleColumns.HIDE+"=0";
        return database.query(ARTICLES_TABLE_NAME,columns,selection,null,null,null,orderBy);
    }
}
