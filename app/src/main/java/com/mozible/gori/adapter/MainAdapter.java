package com.mozible.gori.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.mozible.gori.R;
import com.mozible.gori.models.Content;
import com.mozible.gori.models.MainAdapterObject;
import com.mozible.gori.models.UserProfile;
import com.mozible.gori.utils.GoriConstants;
import com.mozible.gori.view.RoundedImageView;
import com.mozible.gori.view.SquaredFrameLayout;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JunLee on 7/19/16.
 */
public class MainAdapter extends ArrayAdapter<MainAdapterObject> {
    private LayoutInflater mInflater;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private DisplayImageOptions options = null;
    private View.OnClickListener mListInButtonClickListener;
    private View.OnLongClickListener mListInButtonLongClickListener;
    private View.OnClickListener mOtherProfileInButtonClickListener;
    private View.OnClickListener mMyProfileInButtonClickListener;

    public MainAdapter(Context context, List<MainAdapterObject> objects
            , View.OnClickListener listInButtonClickListener
            , View.OnLongClickListener listInButtonLongClickListener
            , View.OnClickListener otherProfileInButtonClickListener
            , View.OnClickListener myProfileInButtonClickListener) {
        super(context, 0, objects);
        mInflater = LayoutInflater.from(context);
        mListInButtonClickListener = listInButtonClickListener;
        mListInButtonLongClickListener = listInButtonLongClickListener;
        mOtherProfileInButtonClickListener = otherProfileInButtonClickListener;
        mMyProfileInButtonClickListener = myProfileInButtonClickListener;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.no_image)
                .showImageForEmptyUri(R.mipmap.no_image)
                .showImageOnFail(R.mipmap.no_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(10)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();

        ImageLoader.getInstance().init(config);

    }

    @Override
    public int getViewTypeCount() {
        return Content.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContentViewHolder contentViewHolder = null;
        MyProfileViewHolder myProfileViewHolder =null;
        OtherProfileViewHolder otherProfileViewHolder = null;

        View view = convertView;
        if( view == null) {
            if ( getItemViewType(position) == MainAdapterObject.MAIN_ADAPTER_TYPE.TYPE_CONTENT.ordinal()) {
                contentViewHolder = new ContentViewHolder();
                view = mInflater.inflate(R.layout.list_item_content, parent, false);
                contentViewHolder = bindContentView(contentViewHolder, view);
                view.setTag(R.layout.list_item_content, contentViewHolder);
            } else if(getItemViewType(position) == MainAdapterObject.MAIN_ADAPTER_TYPE.TYPE_OTHER_PROFILE.ordinal()) {
                otherProfileViewHolder = new OtherProfileViewHolder();
                view = mInflater.inflate(R.layout.list_item_profile_other, parent, false);
                otherProfileViewHolder = bindOtherProfileView(otherProfileViewHolder, view);
                view.setTag(R.layout.list_item_profile_other, otherProfileViewHolder);
            } else if(getItemViewType(position) == MainAdapterObject.MAIN_ADAPTER_TYPE.TYPE_MY_PROFILE.ordinal()) {
                myProfileViewHolder = new MyProfileViewHolder();
                view = mInflater.inflate(R.layout.list_item_profile_my, parent, false);
                myProfileViewHolder = bindMyProfileView(myProfileViewHolder, view);
                view.setTag(R.layout.list_item_profile_my, myProfileViewHolder);
            }
        } else {
            if ( getItemViewType(position) == MainAdapterObject.MAIN_ADAPTER_TYPE.TYPE_CONTENT.ordinal()) {
                contentViewHolder = (ContentViewHolder) view.getTag(R.layout.list_item_content);
            } else if(getItemViewType(position) == MainAdapterObject.MAIN_ADAPTER_TYPE.TYPE_OTHER_PROFILE.ordinal()) {
                otherProfileViewHolder = (OtherProfileViewHolder) view.getTag(R.layout.list_item_profile_other);
            } else if(getItemViewType(position) == MainAdapterObject.MAIN_ADAPTER_TYPE.TYPE_MY_PROFILE.ordinal()) {
                myProfileViewHolder = (MyProfileViewHolder) view.getTag(R.layout.list_item_profile_my);
            }

        }

        if ( getItemViewType(position) == MainAdapterObject.MAIN_ADAPTER_TYPE.TYPE_CONTENT.ordinal()) {
            Content content = (Content)getItem(position);
            ImageLoader.getInstance().displayImage(GoriConstants.makeImageURL(content.image_file),
                    contentViewHolder.list_item_main_photo, options, animateFirstListener);
            ImageLoader.getInstance().displayImage(GoriConstants.makeImageURL(
                    content.user_profile.profile_image), contentViewHolder.list_item_user_profile_photo);
            contentViewHolder.list_item_user_profile_username.setText(content.user_profile.user.username);
            contentViewHolder.description.setText(content.description);

            contentViewHolder.relative_layout_profile.setTag(position);
            contentViewHolder.list_item_square_frame_layout.setTag(position);
            contentViewHolder.btnLike.setTag(position);
            contentViewHolder.btnComments.setTag(position);
            contentViewHolder.tsLikesCounter.setTag(position);
        } else if( getItemViewType(position) == MainAdapterObject.MAIN_ADAPTER_TYPE.TYPE_OTHER_PROFILE.ordinal()) {
            UserProfile userProfile = (UserProfile)getItem(position);
            ImageLoader.getInstance().displayImage(GoriConstants.makeImageURL(userProfile.profile_image), otherProfileViewHolder.list_item_user_profile_photo);
            otherProfileViewHolder.list_item_user_profile_username.setText(userProfile.user.username + userProfile.getJobInAdapter());
            if(userProfile.isFollow) {
                otherProfileViewHolder.follow_user.setText("DIS FOLLOW");
                otherProfileViewHolder.follow_user.setBackgroundResource(R.color.btn_context_menu_text_red);
            } else {
                otherProfileViewHolder.follow_user.setText("FOLLOW");
                otherProfileViewHolder.follow_user.setBackgroundResource(R.color.btn_send_pressed);
            }
            if(userProfile.user_content_count == -1) {
                otherProfileViewHolder.textview_content_count.setText("...");
            } else {
                otherProfileViewHolder.textview_content_count.setText(String.valueOf(userProfile.user_content_count));
            }
            if(userProfile.user_follower_count == -1) {
                otherProfileViewHolder.textview_follower_count.setText("...");
            } else {
                otherProfileViewHolder.textview_follower_count.setText(String.valueOf(userProfile.user_follower_count));
            }
            if(userProfile.user_following_count == -1) {
                otherProfileViewHolder.textview_following_count.setText("...");
            } else {
                otherProfileViewHolder.textview_following_count.setText(String.valueOf(userProfile.user_following_count));
            }

            otherProfileViewHolder.follow_user.setTag(position);
            otherProfileViewHolder.linearlayout_content_count_wrapper.setTag(position);
            otherProfileViewHolder.linearlayout_follower_count_wrapper.setTag(position);
            otherProfileViewHolder.linearlayout_following_count_wrapper.setTag(position);

        } else if( getItemViewType(position) == MainAdapterObject.MAIN_ADAPTER_TYPE.TYPE_MY_PROFILE.ordinal()) {
            UserProfile userProfile = (UserProfile)getItem(position);
            ImageLoader.getInstance().displayImage(GoriConstants.makeImageURL(userProfile.profile_image), myProfileViewHolder.list_item_user_profile_photo);
            myProfileViewHolder.list_item_user_profile_username.setText(userProfile.user.username + userProfile.getJobInAdapter());
            if(userProfile.user_content_count == -1) {
                myProfileViewHolder.textview_content_count.setText("...");
            } else {
                myProfileViewHolder.textview_content_count.setText(String.valueOf(userProfile.user_content_count));
            }
            if(userProfile.user_follower_count == -1) {
                myProfileViewHolder.textview_follower_count.setText("...");
            } else {
                myProfileViewHolder.textview_follower_count.setText(String.valueOf(userProfile.user_follower_count));
            }
            if(userProfile.user_following_count == -1) {
                myProfileViewHolder.textview_following_count.setText("...");
            } else {
                myProfileViewHolder.textview_following_count.setText(String.valueOf(userProfile.user_following_count));
            }

            myProfileViewHolder.list_item_user_profile_photo.setTag(position);
            myProfileViewHolder.profile_modify.setTag(position);
            myProfileViewHolder.linearlayout_content_count_wrapper.setTag(position);
            myProfileViewHolder.linearlayout_follower_count_wrapper.setTag(position);
            myProfileViewHolder.linearlayout_following_count_wrapper.setTag(position);
        }

        return view;
    }

    public ContentViewHolder bindContentView(ContentViewHolder contentViewHolder, View view) {
        contentViewHolder.card_view = (CardView) view.findViewById(R.id.card_view);
        contentViewHolder.relative_layout_profile = (RelativeLayout) view.findViewById(R.id.relative_layout_profile);
        contentViewHolder.list_item_user_profile_photo = (ImageView) view.findViewById(R.id.list_item_user_profile_photo);
        contentViewHolder.list_item_user_profile_username = (TextView) view.findViewById(R.id.list_item_user_profile_username);
        contentViewHolder.list_item_square_frame_layout = (SquaredFrameLayout) view.findViewById(R.id.list_item_square_frame_layout);
        contentViewHolder.list_item_main_photo = (ImageView) view.findViewById(R.id.list_item_main_photo);
        contentViewHolder.ivFeedBottom = (ImageView) view.findViewById(R.id.ivFeedBottom);
        contentViewHolder.btnLike = (ImageButton) view.findViewById(R.id.btnLike);
        contentViewHolder.btnComments = (ImageButton) view.findViewById(R.id.btnComments);
        contentViewHolder.tsLikesCounter = (TextSwitcher) view.findViewById(R.id.tsLikesCounter);
        contentViewHolder.description = (TextView) view.findViewById(R.id.description);

        contentViewHolder.relative_layout_profile.setOnClickListener(mListInButtonClickListener);
        contentViewHolder.list_item_square_frame_layout.setOnLongClickListener(mListInButtonLongClickListener);
        contentViewHolder.btnLike.setOnClickListener(mListInButtonClickListener);
        contentViewHolder.btnComments.setOnClickListener(mListInButtonClickListener);
        contentViewHolder.tsLikesCounter.setOnClickListener(mListInButtonClickListener);

        return contentViewHolder;
    }

    public OtherProfileViewHolder bindOtherProfileView(OtherProfileViewHolder otherProfileViewHolder, View view) {
        otherProfileViewHolder.card_view = (CardView) view.findViewById(R.id.card_view);
        otherProfileViewHolder.relative_layout_profile = (RelativeLayout) view.findViewById(R.id.relative_layout_profile);
        otherProfileViewHolder.list_item_user_profile_photo = (RoundedImageView) view.findViewById(R.id.list_item_user_profile_photo);
        otherProfileViewHolder.list_item_user_profile_username = (TextView) view.findViewById(R.id.list_item_user_profile_username);
        otherProfileViewHolder.follow_user = (Button) view.findViewById(R.id.follow_user);
        otherProfileViewHolder.textview_content_count = (TextView) view.findViewById(R.id.textview_content_count);
        otherProfileViewHolder.textview_follower_count = (TextView) view.findViewById(R.id.textview_follower_count);
        otherProfileViewHolder.textview_following_count = (TextView) view.findViewById(R.id.textview_following_count);
        otherProfileViewHolder.linearlayout_content_count_wrapper = (LinearLayout) view.findViewById(R.id.linearlayout_content_count_wrapper);
        otherProfileViewHolder.linearlayout_follower_count_wrapper = (LinearLayout) view.findViewById(R.id.linearlayout_follower_count_wrapper);
        otherProfileViewHolder.linearlayout_following_count_wrapper = (LinearLayout) view.findViewById(R.id.linearlayout_following_count_wrapper);

        otherProfileViewHolder.follow_user.setOnClickListener(mOtherProfileInButtonClickListener);
        otherProfileViewHolder.linearlayout_content_count_wrapper.setOnClickListener(mOtherProfileInButtonClickListener);
        otherProfileViewHolder.linearlayout_follower_count_wrapper.setOnClickListener(mOtherProfileInButtonClickListener);
        otherProfileViewHolder.linearlayout_following_count_wrapper.setOnClickListener(mOtherProfileInButtonClickListener);
        return otherProfileViewHolder;
    }
    public MyProfileViewHolder bindMyProfileView(MyProfileViewHolder myProfileViewHolder, View view) {
        myProfileViewHolder.card_view = (CardView) view.findViewById(R.id.card_view);
        myProfileViewHolder.relative_layout_profile = (RelativeLayout) view.findViewById(R.id.relative_layout_profile);
        myProfileViewHolder.list_item_user_profile_photo = (RoundedImageView) view.findViewById(R.id.list_item_user_profile_photo);
        myProfileViewHolder.list_item_user_profile_username = (TextView) view.findViewById(R.id.list_item_user_profile_username);
        myProfileViewHolder.profile_modify = (Button) view.findViewById(R.id.profile_modify);
        myProfileViewHolder.textview_content_count = (TextView) view.findViewById(R.id.textview_content_count);
        myProfileViewHolder.textview_follower_count = (TextView) view.findViewById(R.id.textview_follower_count);
        myProfileViewHolder.textview_following_count = (TextView) view.findViewById(R.id.textview_following_count);
        myProfileViewHolder.linearlayout_content_count_wrapper = (LinearLayout) view.findViewById(R.id.linearlayout_content_count_wrapper);
        myProfileViewHolder.linearlayout_follower_count_wrapper = (LinearLayout) view.findViewById(R.id.linearlayout_follower_count_wrapper);
        myProfileViewHolder.linearlayout_following_count_wrapper = (LinearLayout) view.findViewById(R.id.linearlayout_following_count_wrapper);
        myProfileViewHolder.logout = (Button) view.findViewById(R.id.logout);

        myProfileViewHolder.list_item_user_profile_photo.setOnClickListener(mMyProfileInButtonClickListener);
        myProfileViewHolder.profile_modify.setOnClickListener(mMyProfileInButtonClickListener);
        myProfileViewHolder.linearlayout_content_count_wrapper.setOnClickListener(mMyProfileInButtonClickListener);
        myProfileViewHolder.linearlayout_follower_count_wrapper.setOnClickListener(mMyProfileInButtonClickListener);
        myProfileViewHolder.linearlayout_following_count_wrapper.setOnClickListener(mMyProfileInButtonClickListener);
        myProfileViewHolder.logout.setOnClickListener(mMyProfileInButtonClickListener);
        return myProfileViewHolder;
    }

    public class ContentViewHolder {

        CardView card_view;
        RelativeLayout relative_layout_profile;
        ImageView list_item_user_profile_photo;
        TextView list_item_user_profile_username;
        SquaredFrameLayout list_item_square_frame_layout;
        ImageView list_item_main_photo;
        ImageView ivFeedBottom;
        ImageButton btnLike;
        ImageButton btnComments;
        TextSwitcher tsLikesCounter;
        TextView description;
    }

    public class OtherProfileViewHolder {
        CardView card_view;
        RelativeLayout relative_layout_profile;
        RoundedImageView list_item_user_profile_photo;
        TextView list_item_user_profile_username;
        Button follow_user;
        TextView textview_content_count;
        TextView textview_follower_count;
        TextView textview_following_count;
        LinearLayout linearlayout_content_count_wrapper;
        LinearLayout linearlayout_follower_count_wrapper;
        LinearLayout linearlayout_following_count_wrapper;
    }

    public class MyProfileViewHolder {
        CardView card_view;
        RelativeLayout relative_layout_profile;
        RoundedImageView list_item_user_profile_photo;
        TextView list_item_user_profile_username;
        Button profile_modify;
        TextView textview_content_count;
        TextView textview_follower_count;
        TextView textview_following_count;
        LinearLayout linearlayout_content_count_wrapper;
        LinearLayout linearlayout_follower_count_wrapper;
        LinearLayout linearlayout_following_count_wrapper;
        Button logout;
    }

    private static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
