package com.example.yousheng.nerdlauncher;

import android.support.v4.app.Fragment;

public class NerdLauncerActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return NerdLauncherFragment.newIntance();
    }


}
