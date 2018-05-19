package com.example.abhishekbharti.highlighttext;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String content = getResources().getString(R.string.lorem_ipsum);
        final TextView textView = (TextView) findViewById(R.id.textview);
        reset = (Button)findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(content);
                setSpannableText(textView, content);
            }
        });
        enableDisableButton(false, reset);

        textView.setText(content);
        setSpannableText(textView, content);
    }

    private void setSpannableText(TextView textView, String content){
        if(!TextUtils.isEmpty(content)){
            String definition = content.trim();
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(definition, TextView.BufferType.SPANNABLE);

            Spannable spans = (Spannable) textView.getText();
            BreakIterator iterator = BreakIterator.getWordInstance(Locale.US);
            iterator.setText(definition);
            int start = iterator.first();

            for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
                String clickedWord = definition.substring(start, end);
                if (Character.isLetterOrDigit(clickedWord.charAt(0))) {
                    ClickableSpan clickSpan = getClickableSpan(textView, clickedWord, content);
                    spans.setSpan(clickSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    private ClickableSpan getClickableSpan(final TextView textView, final String word, final String content) {
        return new ClickableSpan() {
            final String clickedWord;
            {
                clickedWord = word;
            }

            @Override
            public void onClick(View widget) {
                highlightSearchedTerm(textView, clickedWord, content);
                enableDisableButton(true, reset);
                Log.d("Tapped on:", clickedWord);
                Toast.makeText(widget.getContext(), clickedWord, Toast.LENGTH_SHORT).show();
            }

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.black_363636));
                ds.setUnderlineText(false);
            }
        };
    }

    private void highlightSearchedTerm(TextView textView, String clickedWord, String content) {
        Spannable spannable = new SpannableString(content);

        for (int i = -1; (i = content.toLowerCase(Locale.US).indexOf(clickedWord.toLowerCase(Locale.US), i + 1)) != -1; ) {
            int startPos = i;
            int endPos = startPos + clickedWord.length();

            if (startPos != -1) {
                spannable.setSpan(new RoundedBackgroundSpan(this), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        textView.setText(spannable);
    }

    private void enableDisableButton(boolean shouldEnable, Button button){
        if(shouldEnable){
            button.setClickable(true);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        } else {
            button.setClickable(false);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        }
    }
}
