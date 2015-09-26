package test.app.javier.reignreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {
    final static String URL="url";
    final static String TITLE="title";
    WebViewActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent intent=getIntent();
        String url=intent.getStringExtra(WebViewActivity.URL);
        final String title=intent.getStringExtra(WebViewActivity.TITLE);
        WebView webView = (WebView) findViewById(R.id.webView);
        activity=this;
        //Based on: http://stackoverflow.com/questions/9464361/how-to-show-a-loading-bar-when-rendering-with-webview-loadurl-in-android
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                activity.setTitle("Loading... (" + progress + "%)");
                activity.setProgress(progress);
                if(progress == 100)
                    activity.setTitle(title);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        webView.loadUrl(url);
    }
}
