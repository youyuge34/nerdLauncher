package com.example.yousheng.nerdlauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yousheng on 17/3/23.
 */

public class NerdLauncherFragment extends Fragment {
    private RecyclerView mRecyclerView;

    public static Fragment newIntance() {
        return new NerdLauncherFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nerd_launcer, null);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_nerd_launcher_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();
        return v;
    }

    //startactivity方式启动隐式intent默认自动带了Intent.CATEGORY_DEAFULT类别
    //而不是所有的应用过滤器都带有此类别，所以我们换了种方式，通过packagemanager方式查询符合条件的activity
    private void setupAdapter() {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager packageManager = getActivity().getPackageManager();
        //resolveInfo对象中，可以获取activity标签和其他一些数据
        List<ResolveInfo> activities = packageManager.queryIntentActivities(startupIntent, 0);
        //使用loadlabel方法，对ResloveInfo对象中的activity标签按首字母排序
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                PackageManager pm = getActivity().getPackageManager();
                return a.loadLabel(pm).toString().compareToIgnoreCase(b.loadLabel(pm).toString());
            }
        });
        mRecyclerView.setAdapter(new ActivityAdapter(activities));
        Log.i("size", "setupAdapter: " + activities.size());
    }


    private class ActivityHolder extends RecyclerView.ViewHolder {
        private ResolveInfo mResolveInfo;
        private TextView mTextView;

        public ActivityHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //从resolveinfo中获取信息，通过intent（包名，类名）方法显式启动
                    Intent i=new Intent(Intent.ACTION_MAIN)
                            .setClassName(mResolveInfo.activityInfo.applicationInfo.packageName,mResolveInfo.activityInfo.name);
                    startActivity(i);
                }
            });
        }

        //holder视图层绑定数据层
        public void bindActivity(ResolveInfo info) {
            mResolveInfo = info;
            PackageManager pm = getActivity().getPackageManager();
            String appName = mResolveInfo.loadLabel(pm).toString();
            mTextView.setText(appName);
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
        private List<ResolveInfo> mList;

        public ActivityAdapter(List<ResolveInfo> list) {
            mList = list;
        }

        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,null);
            return  new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(ActivityHolder holder, int position) {
            ResolveInfo info=mList.get(position);
            //holder视图层绑定数据层
            holder.bindActivity(info);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }
}
