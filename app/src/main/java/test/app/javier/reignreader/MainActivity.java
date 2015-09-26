package test.app.javier.reignreader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ArticlesListCursorAdapter adapter; //This adapter manage the List content and the actions on it.
    SwipeRefreshLayout swipeLayout;
    SimpleJsonRequest hackerNews; //With this Object I loaded the requested data in the database.
    ArticlesDataContract ArticlesData; //This Object mannage all the articles loaded.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestQueueSingleton.setInstance(this);

        ArticlesData=new ArticlesDataContract(this);
        hackerNews = new SimpleJsonRequest("http://hn.algolia.com/api/v1/search_by_date?query=android");

        ListView listView = (ListView) findViewById(R.id.listView);

        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setRefreshing(true);

        adapter =new ArticlesListCursorAdapter(this,ArticlesData,listView);

        listView.setAdapter(adapter);

        this.onRefresh(); //Load the information

    }


    @Override
    public void onRefresh() {
        //This is required by the OnRefreshListener and Refresh the data when swype down.
        hackerNews.loadNews(swipeLayout, adapter, ArticlesData);
    }
}
