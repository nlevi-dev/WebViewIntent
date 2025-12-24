package com.nlevidev.webviewintent;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String[] blacklistAny = {};
    private static final String[] blacklistPrefixes = {"","http://","https://","www.","http://www.","https://www."};
    private static final String[] blacklistStart = {};
    private static final String[] whitelistStart = {"google.com/url?q="};
    private WebView webView;
    private ProgressBar progressBar;
    private TextView titleView;
    private String currentUrl = "";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        Objects.requireNonNull(getSupportActionBar()).hide();

        titleView = findViewById(R.id.actionbar_title);
        titleView.setOnClickListener(v -> copyUrlToClipboard());
        progressBar = findViewById(R.id.actionbar_progress);

        webView = findViewById(R.id.webview);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                updateTitle(url);
                if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                updateTitle(url);
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                checkBlacklistAndQuit(uri.toString());
                String scheme = uri.getScheme();
                if (scheme != null && (scheme.equals("http") || scheme.equals("https"))) {
                    view.loadUrl(uri.toString());
                    return true;
                }
                try {
                    if (uri.toString().startsWith("intent://")) {
                        Intent intent = Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                } catch (Exception ignored) {}
                return true;
            }
        });

        handleIntent(getIntent());
    }

    private void updateTitle(String url) {
        currentUrl = url;
        if (titleView != null) {
            titleView.setText(url);
        }
    }

    private void copyUrlToClipboard() {
        if (currentUrl == null || currentUrl.isEmpty()) return;

        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("URL", currentUrl);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void checkBlacklistAndQuit(String url) {
        url = url.toLowerCase();
        String urlBeforeQuery = url.split("\\?")[0];
        for (String black : blacklistAny) {
            black = black.toLowerCase();
            if (urlBeforeQuery.contains(black)) {
                showBlacklistToastAndQuit();
                return;
            }
        }
        for (String prefix : blacklistPrefixes) {
            for (String blackStart : blacklistStart) {
                boolean whitelisted = false;
                for (String whiteStart : whitelistStart) {
                    String white = (prefix + whiteStart).toLowerCase();
                    if (url.startsWith(white)) {
                        whitelisted = true;
                        break;
                    }
                }
                if (whitelisted) continue;
                String black = (prefix + blackStart).toLowerCase();
                if (url.startsWith(black)) {
                    showBlacklistToastAndQuit();
                    return;
                }
            }
        }
    }

    private void showBlacklistToastAndQuit() {
        Toast.makeText(this, "Blacklisted activity!", Toast.LENGTH_LONG).show();
        System.exit(0);
    }

    private void handleIntent(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            checkBlacklistAndQuit(data.toString());
            webView.loadUrl(data.toString());
        } else {
            webView.loadUrl("https://example.com");
        }
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}