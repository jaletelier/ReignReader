package test.app.javier.reignreader;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An object of this class can read information from the given URL if the json has the expected format.
 * It show messages, insert articles to the database and set the Refresh state.
 */
public class SimpleJsonRequest {
    String url;
    public SimpleJsonRequest(String url){
        this.url=url;
    }
    public void loadNews(final SwipeRefreshLayout swipe, final ArticlesListCursorAdapter adapter,final ArticlesDataContract articlesData) {
        swipe.setRefreshing(true); //Show the "loading image"
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JSONObject response = jsonObject;
                boolean newData=false;
                try {
                    JSONArray  hits = (JSONArray) response.get("hits");
                for(int i=0;i<hits.length();i++){
                    //Read the JSON data and load it on the database.
                    JSONObject article=(JSONObject)hits.get(i);

                    String title=article.getString("title");
                    if(title==null || title.equals("null")) title=article.getString("story_title");
                    if(title==null || title.equals("null")) title="Unknown title";

                    String url=article.getString("url");
                    if(url==null || url.equals("null")) url=article.getString("story_url");
                    if(url==null || url.equals("null")) url="http://www.google.com/search?q="+title;

                    String author=article.getString("author");
                    String date = article.getString("created_at");

                    //I Use the ID to know if a article is duplicated or hidden.
                    String article_id=article.getString("story_id");
                    if(article_id==null || article_id.equals("null")) article_id=title;

                    boolean newArticle=articlesData.insert(article_id, title, author, date,url);
                    newData=newData||newArticle; //If no insertions are made then there is no newData.
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CharSequence text = "Reader up to date";
                int duration = Toast.LENGTH_SHORT;
                if(newData) {
                    text = "New Articles loaded";
                    adapter.changeCursor(articlesData.readArticles());
                    adapter.notifyDataSetChanged();
                }
                Toast toast = Toast.makeText(swipe.getContext().getApplicationContext(), text, duration);
                toast.show();
                swipe.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CharSequence text = "Connection error: Check your internet availability";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(swipe.getContext().getApplicationContext(), text, duration);
                toast.show();
                swipe.setRefreshing(false);
            }
        });
        RequestQueueSingleton.getInstance().addToRequestQueue(jor);
    }
}
