package com.example.unimate;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unimate.databinding.ActivityDictionaryBinding;

public class DictionaryActivity extends AppCompatActivity {

    private ActivityDictionaryBinding binding;
    private static final String DICTIONARY_URL = "https://dictionary.cambridge.org/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDictionaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up custom toolbar
        setupToolbar();
        
        // Set up WebView
        setupWebView();
    }
    
    private void setupToolbar() {
        // Hide the default action bar since we're using a custom toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        // Set up back button click listener
        ImageButton backButton = binding.btnBack;
        backButton.setOnClickListener(v -> finish());
    }

    private void setupWebView() {
        // Configure WebView
        WebView webView = binding.webView;
        WebSettings webSettings = webView.getSettings();
        
        // Enable JavaScript for full functionality
        webSettings.setJavaScriptEnabled(true);
        
        // Enable zoom controls
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        
        // Improve performance
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);
        
        // Show progress bar while loading
        final ProgressBar progressBar = binding.progressBar;
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        
        // Handle page navigation within the WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        
        // Load the dictionary website
        webView.loadUrl(DICTIONARY_URL);
    }
    
    @Override
    public void onBackPressed() {
        WebView webView = binding.webView;
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
} 