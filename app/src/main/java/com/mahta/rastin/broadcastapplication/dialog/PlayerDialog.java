package com.mahta.rastin.broadcastapplication.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.adapter.FavoriteMediaAdapter;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.helper.RealmController;
import com.mahta.rastin.broadcastapplication.model.FavoriteMedia;
import com.mahta.rastin.broadcastapplication.model.Media;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class PlayerDialog extends Dialog {
    public Activity activity;
    public Dialog dialog;

    private SeekBar seekBar;
    private TextView txtTitle;
    private TextView txtPastTime;
    private TextView txtDuration;
    private TextView txtDescription;
    private ImageView imgExit;
    private ImageView imgPlayAndPause;
    private AVLoadingIndicatorView prgWait;
    private LikeButton btnFavorite;
    private FavoriteMediaAdapter favoriteAdapter;
    public TextView txtNoMedia;
    private int position;

    private Media media;
    private boolean playPause;
    private boolean initialStage = true;
    private MediaPlayer mediaPlayer;

    private Thread seeker;
    private Runnable seekRunnable;
    private Handler mHandler = new Handler();

    public PlayerDialog(Activity activity,
                        TextView txtNoMedia,
                        Media media,
                        FavoriteMediaAdapter favoriteAdapter,
                        int position) {
        super(activity);
        this.activity = activity;
        this.media = media;
        this.favoriteAdapter = favoriteAdapter;
        this.position = position;
        this.txtNoMedia = txtNoMedia;
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                doWhenDismiss();
            }
        });
    }

    public PlayerDialog(Activity activity,
                        Media media) {
        super(activity);
        this.activity = activity;
        this.media = media;
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                doWhenDismiss();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_player_dialog);

        txtTitle = findViewById(R.id.txtTitle);
        txtPastTime = findViewById(R.id.txtPastTime);
        txtDuration = findViewById(R.id.txtDuration);
        txtDescription = findViewById(R.id.txtDescription);
        prgWait = findViewById(R.id.prgWait);

        txtTitle.setText(media.getTitle());

        if (!media.getDescription().isEmpty()){
            txtDescription.setVisibility(View.VISIBLE);
            txtDescription.setText(media.getDescription());
        }


        imgExit = findViewById(R.id.imgExit);
        imgPlayAndPause = findViewById(R.id.imgPlayAndPause);

        mediaPlayer = new MediaPlayer();
        seekBar = findViewById(R.id.seekBar);

        btnFavorite =  findViewById(R.id.btnFavorite);
        if (G.realmController.hasFavoriteMedia(media.getId()))
            btnFavorite.setLiked(true);

        btnFavorite.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                G.i("adding " +  (media.getId()));

                FavoriteMedia favoriteMedia = new FavoriteMedia(media);
                G.realmController.addFavoriteMedia(favoriteMedia);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                G.realmController.removeFavoriteMedia(
                        (media.getId())
                );
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }
        });

        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doWhenDismiss();
                dismiss();
            }
        });
        imgPlayAndPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAndPause();
            }
        });

        playAndPause();

    }

    private void playAndPause() {
        if (!playPause) {
            changePlayAndPauseResource(1);

            if (initialStage) {
                new Player().execute(G.FILE_URL + media.getPath());
            } else {
                if (!mediaPlayer.isPlaying())
                    mediaPlayer.start();

                if (seekRunnable != null && seeker != null) {
                    seeker = new Thread(seekRunnable);
                    seeker.start();
                }
            }

            playPause = true;

        } else {
            changePlayAndPauseResource(0);

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();

                if (seeker != null && seeker.isAlive())
                    seeker.interrupt();
            }

            playPause = false;
        }
    }


    class Player extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean prepared = false;

            try {
                G.i("path: " + strings[0]);

                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        initialStage = true;
                        playPause = false;
                        changePlayAndPauseResource(0);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });

                mediaPlayer.prepare();

                prepared = true;

            } catch (Exception e) {
                G.e(e.getMessage());
                e.printStackTrace();
                prepared = false;
            }

            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (mediaPlayer == null){
                doWhenDismiss();
                dismiss();
            }

            if (prgWait.getVisibility() == View.VISIBLE) {
                prgWait.setVisibility(View.GONE);
            }


            seekBar.setMax((int)mediaPlayer.getDuration());

            mediaPlayer.start();

            initialStage = false;

            String duration = String.format(Locale.US, "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()),
                    TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()))
            );
            txtDuration.setText(duration);

            seekRunnable = new Runnable() {
                @Override
                public void run() {
                    while (true){
                        if(mediaPlayer != null){
                            G.i("seeking");
                            try {
                                int mCurrentPosition = 0;
                                String mTime = "0:00";

                                if (mediaPlayer != null){
                                    mCurrentPosition = (int)mediaPlayer.getCurrentPosition();
                                    mTime = String.format(Locale.US, "%02d:%02d",
                                            TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition),
                                            TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition))
                                    );
                                }

                                final int current = mCurrentPosition;
                                final String time = mTime;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        txtPastTime.setText(time);
                                        seekBar.setProgress(current);
                                    }
                                });
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                break;
                            }
                        }else
                            break;
                    }
                }
            };
            seeker = new Thread(seekRunnable);
            seeker.start();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgWait.setVisibility(View.VISIBLE);
        }
    }

    private void doWhenDismiss(){
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            if (seeker != null && seeker.isAlive())
                seeker.interrupt();
        }
        if (favoriteAdapter != null && !btnFavorite.isLiked()){
            favoriteAdapter.notifyItemRemoved(position);
            if (favoriteAdapter.getItemCount() <= 0)
                txtNoMedia.setVisibility(View.VISIBLE);        }
    }


    private void changePlayAndPauseResource(int status){
        if (status == 0)
            imgPlayAndPause.setImageDrawable(getContext()
                    .getResources()
                    .getDrawable(R.drawable.img_play)
            );
        else
            imgPlayAndPause.setImageDrawable(getContext()
                    .getResources()
                    .getDrawable(R.drawable.img_pause)
            );
    }

}
