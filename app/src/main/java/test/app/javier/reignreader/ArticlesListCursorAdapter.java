package test.app.javier.reignreader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
/**
 * This Adapter show the Database information in the List and allows to hide information with Swipe Left.
 * Here is defined the Animation and the logic for deletion (hide).
 *
 * Based on:https://www.youtube.com/watch?v=YCHNAi9kJI4&list=PLWz5rJ2EKKc_XOgcRukSoKKjewFJZrKV0&index=74
 */
public class ArticlesListCursorAdapter extends CursorAdapter{
    //Used in the Animation logic
    static public boolean isDown=false;
    static public float firstClick;
    static public float nowClick;
    static public boolean isSwipe=false;

    static public ListView listView;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static public ArticlesDataContract articlesData;

    public ArticlesListCursorAdapter(Context context, ArticlesDataContract articlesData,ListView listView) {
        super(context, articlesData.readArticles(), false);
        this.articlesData = articlesData;
        this.listView=listView;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(R.layout.list_row, null);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final String title=cursor.getString(cursor.getColumnIndex(ArticlesDataContract.ArticleColumns.TITLE));
        final TextView titleText = (TextView) view.findViewById(R.id.title_text);
        final TextView detail = (TextView) view.findViewById(R.id.detail_text);
        final TextView dateText = (TextView) view.findViewById(R.id.date_text);

        String author=(cursor.getString(cursor.getColumnIndex(ArticlesDataContract.ArticleColumns.AUTHOR)));
        String date = (cursor.getString(cursor.getColumnIndex(ArticlesDataContract.ArticleColumns.DATE)));
        String time;
        try {
            //This library allows to show "Pretty" formatted time.
            time = new PrettyTime().format(format.parse(date));
        }catch(Exception e){
            time=date;
        }

        titleText.setText(title);
        detail.setText(author);
        dateText.setText(time);

        final String url = cursor.getString(cursor.getColumnIndex(ArticlesDataContract.ArticleColumns.URL));
        final int id = cursor.getInt(cursor.getColumnIndex(ArticlesDataContract.ArticleColumns.INTERNAL_ID));
        final ArticlesListCursorAdapter adapter=this;

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                int action = event.getAction();
                final boolean deletion = Math.abs(v.getTranslationX()) > v.getWidth() / 2;
                if (action == MotionEvent.ACTION_DOWN) {
                    if (!isDown) {
                        isDown = true;
                        firstClick = event.getX();
                    }
                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_POINTER_UP) {
                    float alpha = 1.0f;
                    float translation = 0.0f;
                    if (deletion) {
                        alpha = 0;
                        translation = 0;
                    }
                    if (!isSwipe && action==MotionEvent.ACTION_UP) {
                        //This is a normal CLICK
                        Intent webViewIntent = new Intent(context, WebViewActivity.class);
                        webViewIntent.putExtra(WebViewActivity.URL, url);
                        webViewIntent.putExtra(WebViewActivity.TITLE,title);
                        context.startActivity(webViewIntent);
                    }
                    isDown = false;
                    isSwipe = false;
                    listView.requestDisallowInterceptTouchEvent(false);
                    v.animate().setDuration(100).alpha(alpha).translationX(translation).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (deletion) {
                                articlesData.hide(id);
                                adapter.changeCursor(articlesData.readArticles());
                                adapter.notifyDataSetChanged();
                                v.setAlpha(1);
                                CharSequence text = "Deleted: "+title;
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(v.getContext().getApplicationContext(), text, duration);
                                toast.show();
                            }
                        }
                    });

                } else if (action == MotionEvent.ACTION_MOVE) {
                    if (isDown) {
                        nowClick = event.getX();
                        //This is used to make the difference between a Click and a common UP action.
                        if (!isSwipe && Math.abs(firstClick - nowClick) > v.getWidth() / 20) {
                            isSwipe = true;
                            listView.requestDisallowInterceptTouchEvent(true);

                        }
                        if (isSwipe) {
                                float x = nowClick + v.getTranslationX();
                                float delta = x - firstClick;
                            if (delta < 0) {
                                v.setTranslationX(delta);
                                v.setAlpha(1 - (Math.abs(delta)) / v.getWidth());
                            }
                            if(deletion){
                                titleText.setTextColor(Color.RED);
                                dateText.setTextColor(Color.RED);
                                detail.setTextColor(Color.RED);
                            }
                            else{
                                titleText.setTextColor(Color.BLACK);
                                dateText.setTextColor(Color.BLACK);
                                detail.setTextColor(Color.BLACK);
                            }
                        }
                    }
                }
                return true;
            }
        });
        view.setAlpha(1.0f);
        titleText.setTextColor(Color.BLACK);
        dateText.setTextColor(Color.BLACK);
        detail.setTextColor(Color.BLACK);

    }
}
