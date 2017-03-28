package cn.poco.jane;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Locale;
import java.util.Map;

import cn.poco.jane.social.BaseSharedActivity;
import cn.poco.jane.social.TpShareHelper;
import cn.poco.pie.Pie;
import cn.poco.pie.PieContent;
import cn.poco.pie.SocialNetwork;
import cn.poco.pie.listener.PieAuthListener;
import cn.poco.pie.media.PieAudio;
import cn.poco.pie.media.PieImage;
import cn.poco.pie.media.PieMedia;
import cn.poco.pie.media.PieVideo;
import cn.poco.pie.media.PieWebpage;

public class MainActivity extends BaseSharedActivity {

    private static final String TITLE = "Jane";
    private static final String CONTENT = "遇见美好";
    private static final String TARGET_URL = "http://www.pooc.cn";
    private static final String IMAGE_URL = "http://www.adnonstop.com/viewcode/images/app-icon/jane-icon-90x90.png";

    private RadioButton mTextRB, mImageRB, mWebPageRB, mAudioRB, mVideoRB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpViews();
    }

    private void setUpViews() {
        mTextRB = (RadioButton) findViewById(R.id.text);
        mImageRB = (RadioButton) findViewById(R.id.image);
        mWebPageRB = (RadioButton) findViewById(R.id.webpage);
        mAudioRB = (RadioButton) findViewById(R.id.audio);
        mVideoRB = (RadioButton) findViewById(R.id.video);
    }

    @Override
    public PieContent provideShareContent(TpShareHelper helper, SocialNetwork target) {
        PieContent content = new PieContent(TITLE, CONTENT, TARGET_URL);
        PieMedia media;
        if (mImageRB.isChecked()) {
            media = generateImage();
        } else if (mWebPageRB.isChecked()) {
            media = new PieWebpage(generateImage());
        } else if (mAudioRB.isChecked()) {
            media = new PieAudio(generateImage(), TARGET_URL, TITLE);
        } else if (mVideoRB.isChecked()) {
            media = new PieVideo(generateImage(), TARGET_URL, TITLE);
        } else {
            media = null;
        }
        content.setMedia(media);

        if (target == SocialNetwork.WEIBO) {
            content.setText(String.format(Locale.CHINA, "#Jane-简拼# %s", CONTENT));
        } else if (target == SocialNetwork.MORE || target == SocialNetwork.COPY) {
            content.setText(CONTENT + " " + TARGET_URL);
        }

        return  content;
    }

    private PieImage generateImage() {
//        PieImage image = new PieImage(file);
//        PieImage image = new PieImage(bitmap);
//        PieImage image = new PieImage(resId);
        PieImage image = new PieImage(IMAGE_URL);
        return image;
    }

    public void dialog(View v) {
        startShare(null);
    }

    public void pop(View v) {
        startShare(v, false);
    }

    public void fsPop(View v) {
        startShare(v, true);
    }

    //-----------------------------------------------------------------------

    private PieAuthListener mAuthListener = new PieAuthListener() {

        @Override
        public void onSuccess(SocialNetwork socialNetwork, int action, Map<String, String> data) {
            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SocialNetwork socialNetwork, int action) {
            Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SocialNetwork socialNetwork, int action, int errCode, Throwable tr) {
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    };

    public void wechat(View v) {
        Pie.get(this).auth(this, SocialNetwork.WECHAT, mAuthListener);
    }

    public void weibo(View v) {
        Pie.get(this).auth(this, SocialNetwork.WEIBO, mAuthListener);
    }

    public void qq(View v) {
        Pie.get(this).auth(this, SocialNetwork.QQ, mAuthListener);
    }
}
