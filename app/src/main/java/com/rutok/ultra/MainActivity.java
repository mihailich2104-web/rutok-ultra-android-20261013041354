package com.rutok.ultra;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends Activity {
    private ViewPager2 pager;
    private FeedAdapter adapter;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        pager = new ViewPager2(this);
        pager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        pager.setBackgroundColor(Color.BLACK);
        adapter = new FeedAdapter();
        pager.setAdapter(adapter);
        setContentView(pager);

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                adapter.playAt(position);
            }
        });
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String value, float size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }

    private class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.Holder> {
        private final String[] urls = {
            "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"
        };
        private final String[] captions = {
            "Открывай новые места и делись впечатлениями.",
            "Твоя лента коротких видео.",
            "Смотри, сохраняй идеи и делись с друзьями."
        };
        private final SparseArray<VideoView> players = new SparseArray<>();
        private int currentPosition = 0;

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            FrameLayout page = new FrameLayout(MainActivity.this);
            page.setBackgroundColor(Color.BLACK);

            VideoView video = new VideoView(MainActivity.this);
            page.addView(video, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ));

            TextView header = text("RuTok Ultra", 25, Color.WHITE);
            header.setTypeface(null, Typeface.BOLD);
            FrameLayout.LayoutParams hp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.TOP | Gravity.START
            );
            hp.setMargins(dp(20), dp(24), dp(16), 0);
            page.addView(header, hp);

            TextView status = text("Загрузка видео...", 16, Color.WHITE);
            status.setGravity(Gravity.CENTER);
            FrameLayout.LayoutParams sp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            );
            page.addView(status, sp);

            TextView caption = text("", 16, Color.WHITE);
            caption.setMaxWidth(dp(260));
            FrameLayout.LayoutParams cp = new FrameLayout.LayoutParams(
                dp(270), ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.START
            );
            cp.setMargins(dp(18), 0, dp(95), dp(34));
            page.addView(caption, cp);

            LinearLayout actions = new LinearLayout(MainActivity.this);
            actions.setOrientation(LinearLayout.VERTICAL);
            actions.setGravity(Gravity.CENTER_HORIZONTAL);

            Button like = new Button(MainActivity.this);
            like.setText("Лайк");
            like.setTextColor(Color.WHITE);
            like.setBackgroundColor(Color.rgb(45, 45, 45));

            Button share = new Button(MainActivity.this);
            share.setText("Поделиться");
            share.setTextColor(Color.WHITE);
            share.setBackgroundColor(Color.rgb(45, 45, 45));

            actions.addView(like);
            actions.addView(share);

            FrameLayout.LayoutParams ap = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.END | Gravity.BOTTOM
            );
            ap.setMargins(0, 0, dp(10), dp(100));
            page.addView(actions, ap);

            return new Holder(page, video, status, caption, like, share);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder h, int position) {
            h.position = position;
            h.caption.setText(captions[position]);
            h.status.setText("Загрузка видео...");
            h.status.setVisibility(View.VISIBLE);
            h.video.setVideoURI(Uri.parse(urls[position]));
            players.put(position, h.video);

            h.video.setOnPreparedListener((MediaPlayer mp) -> {
                mp.setLooping(true);
                h.status.setVisibility(View.GONE);
                if (h.position == currentPosition) h.video.start();
            });

            h.video.setOnErrorListener((mp, what, extra) -> {
                h.status.setText("Видео недоступно. Проверь интернет.");
                h.status.setVisibility(View.VISIBLE);
                return true;
            });

            h.liked = false;
            h.like.setText("Лайк");
            h.like.setOnClickListener(v -> {
                h.liked = !h.liked;
                h.like.setText(h.liked ? "Нравится: 1" : "Лайк");
            });

            h.share.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,
                    "RuTok Ultra — короткие видео\n" + urls[h.position]);
                startActivity(Intent.createChooser(intent, "Поделиться видео"));
            });
        }

        void playAt(int position) {
            currentPosition = position;
            for (int i = 0; i < players.size(); i++) {
                int key = players.keyAt(i);
                VideoView video = players.valueAt(i);
                if (key == position) {
                    video.start();
                } else if (video.isPlaying()) {
                    video.pause();
                }
            }
        }

        @Override
        public int getItemCount() {
            return urls.length;
        }

        @Override
        public void onViewRecycled(@NonNull Holder h) {
            h.video.stopPlayback();
            players.remove(h.position);
            super.onViewRecycled(h);
        }

        class Holder extends RecyclerView.ViewHolder {
            final VideoView video;
            final TextView status;
            final TextView caption;
            final Button like;
            final Button share;
            int position;
            boolean liked;

            Holder(View item, VideoView video, TextView status,
                   TextView caption, Button like, Button share) {
                super(item);
                this.video = video;
                this.status = status;
                this.caption = caption;
                this.like = like;
                this.share = share;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adapter != null) adapter.playAt(-1);
    }
}
