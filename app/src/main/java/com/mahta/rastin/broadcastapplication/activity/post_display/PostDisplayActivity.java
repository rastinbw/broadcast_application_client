package com.mahta.rastin.broadcastapplication.activity.post_display;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.model.Post;

public class PostDisplayActivity extends AppCompatActivity {

    private Post currentPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_display);

        currentPost = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);
        getFragmentManager().beginTransaction()
                .replace(R.id.frmPostDisplayContainer,new PostContentFragment())
                .commit();

    }


    public String getContent() {
        return currentPost.getContent();
    }

    public Post getCurrentPost() {
        return currentPost;
    }


}
