package com.maeultalk.gongneungtalk.contents.setting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.maeultalk.gongneungtalk.R;
import com.maeultalk.gongneungtalk.activity.CommentsActivity;
import com.maeultalk.gongneungtalk.activity.ImageActivity;
import com.maeultalk.gongneungtalk.activity.SearchSpotActivity;
import com.maeultalk.gongneungtalk.activity.SpotActivity;
import com.maeultalk.gongneungtalk.activity.SpotActivity2;
import com.maeultalk.gongneungtalk.contents.model.ContentModel;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

//todo 글라이드에서 따로 따로 이미지 불러올 때, 각각 모든 이미지 불러오기 완료하면 제일 마지막에 완료됐을 때 풀투리프레쉬 끝내주기.
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private ArrayList<ContentModel> contentModelsInAdapter = new ArrayList<ContentModel>();
    private ArrayList<ContentModel> contentModelsAdded = new ArrayList<ContentModel>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private Boolean init;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private final int VIEW_FOOT = 2;
    private final int VIEW_HEAD = 3;

    // 이미지 로딩 완료 카운트
    int chileCount = 0;
    int glideCount = 0;

    // 어떤 액티비티에서 부르는지(메인액티비티 or 스팟액티비티)
    boolean callFromSpotActivity;

    DecimalFormat dc = new DecimalFormat("###,###,###,###");

    private OnLoadMoreListener onLoadMoreListener;
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    SetFooter setFooter;
    public interface SetFooter {
        void last();
        void next();
        //void start();
    }

    public RecyclerViewAdapter(Activity activity, ArrayList<ContentModel> contentModels, SwipeRefreshLayout mSwipeRefreshLayout, boolean init, RecyclerView recyclerView, boolean callFromSpotActivity) {
        this.activity = activity;
        this.contentModelsInAdapter = contentModels;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
        this.recyclerView = recyclerView;
        this.init = init;
        this.callFromSpotActivity = callFromSpotActivity;

        glideCount = 0;
    }

    // 로딩 완료됐을 때를 위한 리스너 구현
    private OnLoadFinishListner onLoadFinishListner;
    public interface OnLoadFinishListner{
        void onLoadFinish();
    }
    public void setOnLoadFinishListner(OnLoadFinishListner onLoadFinishListner) {
        this.onLoadFinishListner = onLoadFinishListner;
    }
    //todo 로드 완료됐을 때
    //onLoadFinishListner.onLoadFinish();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        /*if(viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_content,parent,false);
            return new ViewHolder(view);
        } else {
            return new ProgressViewHolder(LayoutInflater.from(context).inflate(R.layout.item_progress, parent, false));
        }*/

        if(viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_content,parent,false);
            return new ViewHolder(view);
        } else if(viewType == VIEW_FOOT) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_footer,parent,false);
            return new FooterViewHolder(view);
        } else if(viewType == VIEW_PROG) {
            return new ProgressViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_content, parent, false));
        } /*else if(viewType == VIEW_HEAD) {
            return new ProgressViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_header, parent, false));
        }*/

        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //int testPos = position;
        //int cc1 = recyclerView.getChildCount();
        if (holder instanceof ViewHolder) {
            final ViewHolder itemholder = (ViewHolder) holder;
            chileCount++;
            //if(position==0) {
            final ContentModel contentModel = contentModelsInAdapter.get(position);
            itemholder.spot.setText(contentModel.getSpot());
            itemholder.goToSpot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context, contentModel.getSpot(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, SpotActivity2.class);
                    intent.putExtra("spot_name", contentModel.getSpot());
                    intent.putExtra("spot_code", contentModel.getSpot_code());
                    activity.startActivity(intent);
                    //activity.overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out); // 액티비티 전환 애니메이션
                    //activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_in_left); // 액티비티 전환 애니메이션
                    //activity.overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_out_right); // 액티비티 전환 애니메이션
                    //activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right); // 액티비티 전환 애니메이션
                    //activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // 액티비티 전환 애니메이션
                    //activity.overridePendingTransition() // 액티비티 전환 애니메이션
                }
            });

            //itemholder.nick.setText( TextUtils.isEmpty(contentModel.getEmail()) ? "마을톡" : contentModel.getEmail() );
            itemholder.nick.setText(contentModel.getNick());

            switch (contentModel.getIdentity()) {
                case "resident":
                    itemholder.identity.setText("주민");
                    break;
                case "merchant":
                    itemholder.identity.setText("상인");
                    break;
                case "student":
                    itemholder.identity.setText("대학생");
                    break;
                case "worker":
                    itemholder.identity.setText("직장인");
                    break;
                case "visitor":
                    itemholder.identity.setText("방문자");
                    break;
            }
            //itemholder.identity.setText(contentModel.getIdentity());

            String date = contentModel.getTime();
            String new_date = "호랑이 담배 피던 시절";
            // SimpleDateFormat의 형식을 선언한다.
            SimpleDateFormat original_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

            SimpleDateFormat new_format = new SimpleDateFormat("M월 d일 ah:mm", Locale.KOREA);
            // 날짜 형식 변환시 파싱 오류를 try.. catch..로 체크한다.
            try {
                // 문자열 타입을 날짜 타입으로 변환한다.
                Date original_date = original_format.parse(date);
                // 날짜 형식을 원하는 타입으로 변경한다.
                new_date = new_format.format(original_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            itemholder.time.setText(new_date); //SimpleDateFormat http://blog.acronym.co.kr/350
            itemholder.content.setText(contentModel.getContent());
            itemholder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, ImageActivity.class);
                    intent.putExtra("individual_image", contentModel.getImage());
                    activity.startActivity(intent);
                }
            });
            if(contentModel.getImage().equals("null") || TextUtils.isEmpty(contentModel.getImage())) {
                ((ViewHolder) holder).imageLayout.setVisibility(View.GONE);
            } else {
                ((ViewHolder) holder).imageLayout.setVisibility(View.VISIBLE);
                Glide.with(activity).load("http://gongneungtalk.cafe24.com/images/" + contentModel.getImage())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                itemholder.progressBar.setVisibility(View.INVISIBLE);
                                itemholder.image_load_fail.setVisibility(View.VISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                                //itemholder.progressBar.setVisibility(View.INVISIBLE);

                                //Toast.makeText(context, "onLoadCleared", Toast.LENGTH_SHORT).show();
                                //if(init==false) {
                                //mSwipeRefreshLayout.setRefreshing(false);

                                //todo 이미지 로드가 모두 완료되었을 때
                                /*boolean test = isFromMemoryCache;
                                int test2 = recyclerView.getChildCount();
                                boolean test3 = isFirstResource;*/

                                //int cc = recyclerView.getChildCount();
                                if(!isFirstResource) {
                                    glideCount++;
                                    if(glideCount == chileCount) {
                                        onLoadFinishListner.onLoadFinish();
                                    }
                                } else {
                                    onLoadFinishListner.onLoadFinish();
                                }


                                /*if(isFromMemoryCache == true) {
                                    onLoadFinishListner.onLoadFinish();
                                } else {
                                    glideCount++;
                                    if(glideCount == recyclerView.getChildCount()) {
                                        onLoadFinishListner.onLoadFinish();
                                    }
                                }*/


                                //}

                                return false;
                            }
                        })
                        /*.diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)*/
                        //.placeholder(R.mipmap.ic_launcher)
                        //.override(4, 3)
                        .centerCrop()
                        //.fitCenter()
                        //.thumbnail(0.1f)
                        .into(((ViewHolder) holder).imageView);
                /*.into(new ImageViewTarget<GlideDrawable>(holder.imageView) {
                    @Override
                    protected void setResource(GlideDrawable resource) {

                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        Toast.makeText(context, "onLoadCleared", Toast.LENGTH_SHORT).show();
                    }
                });*/
                //}
            }

            itemholder.textView_comments.setText("댓글 " + contentModel.getComments() + "개");
            itemholder.textView_comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(activity, "준비중입니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, CommentsActivity.class);
                    intent.putExtra("no", contentModel.getNo());
                    intent.putExtra("content", contentModel.getContent());
                    activity.startActivity(intent);
                }
            });
            itemholder.layout_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, "₦10(노원)을 \"고마워요\" 하였습니다.", Toast.LENGTH_SHORT).show();
                    itemholder.imageView_like.setImageResource(R.drawable.recommended);
                    itemholder.textView_like.setText(String.valueOf(Integer.valueOf(itemholder.textView_like.getText().toString())+10));
                    itemholder.textView_likeAdd.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                    itemholder.textView_like.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                    itemholder.textView_likeTotal.setText(dc.format(Integer.valueOf(itemholder.textView_likeTotal.getText().toString().replaceAll(",", ""))+10));
                }
            });
            itemholder.imageView_like.setAdjustViewBounds(true);
//            itemholder.textView_likeTotal.setText(String.valueOf(dc.format((int)(Math.random()*10000))));
            itemholder.textView_likeTotal.setText(dc.format((int)(Math.random()*10000)));


        } else if(holder instanceof FooterViewHolder) {
            final FooterViewHolder footerHolder = (FooterViewHolder) holder;
            footerHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerView.smoothScrollToPosition(0);
                }
            });


            setFooter = new SetFooter() {
                @Override
                public void last() {
                    footerHolder.button.setVisibility(View.VISIBLE);
                    footerHolder.progressBar_footer.setVisibility(View.INVISIBLE);
                    //Toast.makeText(activity, "last();", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void next() {
                    footerHolder.button.setVisibility(View.INVISIBLE);
                    footerHolder.progressBar_footer.setVisibility(View.VISIBLE);
                    //Toast.makeText(activity, "next();", Toast.LENGTH_SHORT).show();
                }
                /*@Override
                public void start() {
                    footerHolder.button.setVisibility(View.INVISIBLE);
                    footerHolder.progressBar_footer.setVisibility(View.INVISIBLE);
                }*/
            };
        } /*else if(holder instanceof HeaderViewHolder) {
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            headerViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_searchSpot = new Intent(activity, SearchSpotActivity.class);
                    activity.startActivity(intent_searchSpot);
                }
            });

            headerViewHolder.nick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_searchSpot = new Intent(activity, SearchSpotActivity.class);
                    activity.startActivity(intent_searchSpot);
                }
            });

            SharedPreferences pref = activity.getSharedPreferences("GongneungTalk_UserInfo", MODE_PRIVATE);
            headerViewHolder.nick.setText(pref.getString("nick", "null"));
            switch (pref.getString("identity", "null")) {
                case "resident":
                    headerViewHolder.identity.setText("주민");
                    break;
                case "merchant":
                    headerViewHolder.identity.setText("상인");
                    break;
                case "student":
                    headerViewHolder.identity.setText("대학생");
                    break;
                case "worker":
                    headerViewHolder.identity.setText("직장인");
                    break;
                case "visitor":
                    headerViewHolder.identity.setText("방문자");
                    break;
            }

        }*/

    }

    public void setLast() {
        setFooter.last();
    }
    public void setNext() {
        setFooter.next();
    }
    /*public void setStart() {
        setFooter.start();
    }*/

    @Override
    public int getItemCount() {
        return contentModelsInAdapter.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position)) {
            return VIEW_FOOT;
        } /*else if (isPositionHeader(position)) {
            return VIEW_HEAD;
        }*/
        return VIEW_ITEM;
    }
    /*@Override
    public int getItemViewType(int position) {
        return contentModelsInAdapter.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }*/

    private boolean isPositionFooter(int position) {
        return position == contentModelsInAdapter.size();
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public void setProgressMore(final boolean isProgress) {
        if (isProgress) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    contentModelsInAdapter.add(null);
                    notifyItemInserted(contentModelsInAdapter.size() - 1);
                }
            });
        } else {
            contentModelsInAdapter.remove(contentModelsInAdapter.size() - 1);
            notifyItemRemoved(contentModelsInAdapter.size());
        }
    }

    public void addItem(ArrayList<ContentModel> contentModelsAdded) {
        //this.contentModelsAdded = contentModelsAdded;
        Log.d("contentModel.size() - ", "InAdapter1 : " + contentModelsAdded.size());
        Log.d("contentModel2.size() - ", "InAdapter1 : " + contentModelsInAdapter.size());
        int previousItemSize = contentModelsInAdapter.size() + 1;
        Log.d("contentModel.size() - ", "InAdapter2 : " + contentModelsAdded.size());
        Log.d("contentModel2.size() - ", "InAdapter2 : " + contentModelsInAdapter.size());
        contentModelsInAdapter.addAll(contentModelsAdded);
        Log.d("contentModel.size() - ", "InAdapter3 : " + contentModelsAdded.size());
        Log.d("contentModel2.size() - ", "InAdapter3 : " + contentModelsInAdapter.size());
        notifyItemRangeInserted(previousItemSize, contentModelsAdded.size());
        Log.d("contentModel.size() - ", "InAdapter4 : " + contentModelsAdded.size());
        Log.d("contentModel2.size() - ", "InAdapter4 : " + contentModelsInAdapter.size());

    }

    public void setItem(ArrayList<ContentModel> contentModels) {
        chileCount = 0;
        glideCount = 0;
        contentModelsInAdapter.clear();
        //contentModelsInAdapter.addAll(contentModels);
        contentModelsInAdapter = contentModels;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView spot;
        TextView time;
        TextView nick;
        TextView identity;
        TextView content;
        RelativeLayout imageLayout;
        TextView image_load_fail;
        ImageView imageView;
        LinearLayout goToSpot;
        //View border_below_spotName;
        ProgressBar progressBar;
        TextView textView_comments;
        LinearLayout layout_like;
        ImageView imageView_like;
        TextView textView_like;
        TextView textView_likeAdd;
        TextView textView_likeTotal;
        public ViewHolder(View itemView) {
            super(itemView);
            spot = itemView.findViewById(R.id.textView_spot);
            time = itemView.findViewById(R.id.textView_time);
            nick = itemView.findViewById(R.id.textView_nick);
            identity = itemView.findViewById(R.id.textView_identity);
            content = itemView.findViewById(R.id.textView6);
            imageLayout = (RelativeLayout) itemView.findViewById(R.id.imageLayout);
            image_load_fail = itemView.findViewById(R.id.textView_image_load_fail);
            imageView = itemView.findViewById(R.id.imageView3);
            //border_below_spotName = itemView.findViewById(R.id.border_below_spotName);
            goToSpot = itemView.findViewById(R.id.goToSpot);
            if(callFromSpotActivity == true) {
                goToSpot.setVisibility(View.GONE);
                //border_below_spotName.setVisibility(View.GONE);
            }
            progressBar = itemView.findViewById(R.id.progressBar);
            textView_comments = (TextView) itemView.findViewById(R.id.textView_comments);
            layout_like = (LinearLayout) itemView.findViewById(R.id.layout_like);
            imageView_like = (ImageView) itemView.findViewById(R.id.imageView_like);
            imageView_like.setAdjustViewBounds(true);
            textView_likeAdd = (TextView) itemView.findViewById(R.id.textView_likeAdd);
            textView_like = (TextView) itemView.findViewById(R.id.textView_like);
            textView_likeTotal = (TextView) itemView.findViewById(R.id.textView_likeTotal);
        }
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar pBar;
        public ProgressViewHolder(View v) {
            super(v);
            pBar = (ProgressBar) v.findViewById(R.id.pBar);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        Button button;
        ProgressBar progressBar_footer;
        public FooterViewHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button);
            progressBar_footer = itemView.findViewById(R.id.progressBar_footer);
        }

    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        TextView nick;
        TextView identity;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.content_layout);
            nick = itemView.findViewById(R.id.textView_nick);
            identity = itemView.findViewById(R.id.textView_identity);

        }

    }

}