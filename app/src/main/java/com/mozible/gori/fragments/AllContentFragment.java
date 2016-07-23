package com.mozible.gori.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mozible.gori.GoriApplication;
import com.mozible.gori.MainActivity;
import com.mozible.gori.R;
import com.mozible.gori.UserProfileActivity;
import com.mozible.gori.adapter.MainAdapter;
import com.mozible.gori.models.Content;
import com.mozible.gori.models.MainAdapterObject;
import com.mozible.gori.models.PostResult;
import com.mozible.gori.models.UserProfile;
import com.mozible.gori.tasks.MainInfoTask;
import com.mozible.gori.utils.GoriConstants;
import com.mozible.gori.utils.GoriPreferenceManager;
import com.mozible.gori.utils.ServerInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by JunLee on 7/21/16.
 */
public class AllContentFragment extends Fragment {
    private MainActivity mMainActivity;
    private PullToRefreshListView mListView;
    private MainInfoTask mMainInfoTask;
    private MainAdapter mMainAdapter;
    private List<MainAdapterObject> mMainContentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_newsfeed, container, false);
        initView(rootView);
        mMainActivity = (MainActivity)getActivity();
        if(mMainContentList.size() == 0) {
            startMainInfoTask();
        }

        return rootView;
    }

    private void initView(View rootView) {
        mListView = (PullToRefreshListView) rootView.findViewById(R.id.listview);
        initAdapter();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void initAdapter() {
        if(mMainContentList == null) {
            mMainContentList = new ArrayList<>();
        }
        if(mMainAdapter == null) {
            mMainAdapter = new MainAdapter(getActivity(), mMainContentList, listInButtonClickListener,
                    listInButtonLongClickListener, null, null);
        }
        mListView.setAdapter(mMainAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getId() == mListView.getId()) {

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshAll();
            }
        });
    }

    public void refreshAll() {
        startMainInfoTask();
    }

    private void startMainInfoTask() {
        String session = GoriPreferenceManager.getInstance(getActivity()).getSession();
        ServerInterface api = GoriApplication.getInstance().getServerInterface();
        api.getAllContents(session, new Callback<ArrayList<Content>>() {

            @Override
            public void success(ArrayList<Content> s, Response response) {
                if(s != null && s.size() > 0) {
                    mMainContentList.clear();
                    mMainContentList.addAll(s);
                    mMainAdapter.notifyDataSetChanged();
                }
                mListView.onRefreshComplete();

            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private View.OnClickListener listInButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.relative_layout_profile:
                    Intent i = new Intent(getActivity(), UserProfileActivity.class);
                    int position = (Integer)v.getTag();
                    Content content = (Content) mMainAdapter.getItem(position);
                    i.putExtra("USER_PROFILE", UserProfile.makeJSon(content.user_profile));
                    startActivity(i);
                    break;
                case R.id.btnLike:
                    break;
                case R.id.btnComments:
                    break;
                case R.id.tsLikesCounter:
                    break;
            }
        }
    };

    private View.OnLongClickListener listInButtonLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            switch(v.getId()) {
                case R.id.list_item_square_frame_layout:
                    mMainActivity.showSnackbar("image : " + v.getTag());
                    break;
            }
            return true;
        }
    };
}
