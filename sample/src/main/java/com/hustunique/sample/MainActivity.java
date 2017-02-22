

package com.hustunique.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hustunique.parsingplayer.parser.extractor.QQExtractor;
import com.hustunique.parsingplayer.player.view.ParsingVideoView;

public class MainActivity extends AppCompatActivity {
    private ParsingVideoView mVideoView;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = (ParsingVideoView) findViewById(R.id.videoView);
        mVideoView.play(QQExtractor.TEST_URL);
    }

    // turn black when resume to this activity
    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.onDestroy();
    }
}
