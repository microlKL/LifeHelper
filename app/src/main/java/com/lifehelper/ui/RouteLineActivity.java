package com.lifehelper.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.PlanNode;
import com.lifehelper.R;
import com.lifehelper.app.MyConstance;
import com.lifehelper.entity.RoutLinePlanots;
import com.lifehelper.entity.RoutLineTabEntity;
import com.lifehelper.presenter.impl.RouteLinePresenterImpl;
import com.lifehelper.tools.T;
import com.lifehelper.tools.ViewUtils;
import com.lifehelper.ui.fragment.ResultLineBusFragment;
import com.lifehelper.ui.fragment.RouteLineLocationFragment;
import com.lifehelper.view.RouteLineTabView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jsion on 16/3/16.
 */
public class RouteLineActivity extends BaseActivity implements RouteLineTabView, RouteLineLocationFragment.OnGetFragmentValueListener {
    @Bind(R.id.toolbar_route_line)
    Toolbar mToolbar;
    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.tv_route_line_search)
    TextView mRouteLineSearch;

    private RouteLinePresenterImpl mPresenter;
    private ResultLineBusFragment mResultLineBusFragment;
    //    private ResultLineBusFragment mResultLineWalkFragment;
//    private ResultLineBusFragment mResultLineCarFragment;
    private RouteLineLocationFragment mRouteLineLocationFragment;
    private int mCurrentTabType;
    private BDLocation mCurrentBDLoation;
    private PlanNode mStartNode;
    private PlanNode mTargetNote;
    private RoutLinePlanots mRoutLinePlanots;
    private String mStartAddress;
    private String mTargetAddress;

    @OnClick(R.id.tv_route_line_search)
    void routeLineSearch() {
        if (TextUtils.isEmpty(mStartAddress)) {
            T.show(this, getResources().getString(R.string.start_add_empty), 0);
        } else if (TextUtils.isEmpty(mTargetAddress)) {
            T.show(this, getResources().getString(R.string.target_add_empty), 0);
        } else {
//            replaceResultFragment(mCurrentTabType);
            mRouteLineSearch.setVisibility(View.INVISIBLE);
            // set fragment data
            Bundle args = new Bundle();
            args.putParcelable(MyConstance.ROUTELINE_PLANNOTES, mRoutLinePlanots);
            switch (mCurrentTabType) {
                case TAB_TYPE._BUS:
                    mRoutLinePlanots.setTabType(TAB_TYPE._BUS);
                    mResultLineBusFragment.setArguments(args);
                    break;
                case TAB_TYPE._CAR:
                    mRoutLinePlanots.setTabType(TAB_TYPE._CAR);
                    mResultLineBusFragment.setArguments(args);
//                    mResultLineCarFragment.setArguments(args);
                    break;
                case TAB_TYPE._WALK:
                    mRoutLinePlanots.setTabType(TAB_TYPE._WALK);
                    mResultLineBusFragment.setArguments(args);
//                    mResultLineWalkFragment.setArguments(args);
                    break;
            }

            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fl_fragment_container, mResultLineBusFragment);
            ft.commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_line);
        init();
    }

    @Override
    protected void initData() {
        mRoutLinePlanots = new RoutLinePlanots();

        getIntentData();
        mCurrentTabType = TAB_TYPE._BUS;
        mPresenter = new RouteLinePresenterImpl(this);
        mStartAddress = getResources().getString(R.string.my_address);
    }

    /**
     * get intent data
     */
    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mCurrentBDLoation = bundle.getParcelable(MyConstance.CURRENT_LOCATION);
                if (mCurrentBDLoation != null) {
                    mStartNode = PlanNode.withLocation(new LatLng(mCurrentBDLoation.getLatitude(), mCurrentBDLoation.getLongitude()));
                    mRoutLinePlanots.setStartPlanNode(mStartNode);
                }
            }
        }
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        mRouteLineLocationFragment = new RouteLineLocationFragment();
        mResultLineBusFragment = new ResultLineBusFragment();
//        mResultLineWalkFragment = new ResultLineBusFragment();
//        mResultLineCarFragment = new ResultLineBusFragment();
//        mResultLineWalkFragment = new ResultLineWalkFragment();
//        mResultLineCarFragment = new ResultLineCarFragment();

        FragmentManager mFM = getFragmentManager();
        FragmentTransaction mFT = mFM.beginTransaction();
        mFT.replace(R.id.fl_fragment_container, mRouteLineLocationFragment);
        mFT.commit();

    }

    @Override
    protected void initEvent() {
        mPresenter.getRouteLineEntitys();
        mToolbar.setTitle(getResources().getString(R.string.route_line));
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.abc_ic_ab_back_mtrl_am_alpha));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mRouteLineSearch.getVisibility() == View.VISIBLE) {
                onBackPressed();
            } else {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fl_fragment_container, mRouteLineLocationFragment);
                ft.commit();
                mRouteLineSearch.setVisibility(View.VISIBLE);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mRouteLineSearch.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        } else {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fl_fragment_container, mRouteLineLocationFragment);
            ft.commit();
            mRouteLineSearch.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void bindRouteLineTabs(List<RoutLineTabEntity> routLineTabEntities) {
        for (RoutLineTabEntity tabEntity : routLineTabEntities) {
            TabLayout.Tab tab = mTabLayout.newTab();
            tab.setText(tabEntity.getTabName());
            tab.setTag(tabEntity.getTabType());
            mTabLayout.addTab(tab);
            mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mCurrentTabType = (int) tab.getTag();
                    if (mRouteLineSearch.getVisibility() != View.VISIBLE) {
                        replaceResultFragment(mCurrentTabType);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }

    }

    /**
     * based on tab_type replace current result fragment
     *
     * @param tag
     */
    private void replaceResultFragment(int tag) {
//        FragmentManager fm = getFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        Bundle args = new Bundle();
//        args.putParcelable(MyConstance.ROUTELINE_PLANNOTES, mRoutLinePlanots);
        switch (tag) {
            case TAB_TYPE._BUS:
                mResultLineBusFragment.differentRoutePlan(tag);
//                mRoutLinePlanots.setTabType(tag);
//                mResultLineBusFragment.setArguments(args);
//                ft.replace(R.id.fl_fragment_container, mResultLineBusFragment);
                break;
            case TAB_TYPE._WALK:
                mResultLineBusFragment.differentRoutePlan(tag);

//                mRoutLinePlanots.setTabType(tag);
//                mResultLineBusFragment.setArguments(args);
//                mResultLineWalkFragment.setArguments(args);
//                ResultLineWalkFragment temp = new ResultLineWalkFragment();
//                ft.replace(R.id.fl_fragment_container, temp);
//                ft.replace(R.id.fl_fragment_container, mResultLineWalkFragment);
                break;
            case TAB_TYPE._CAR:
                mResultLineBusFragment.differentRoutePlan(tag);

//                mRoutLinePlanots.setTabType(tag);
//                mResultLineBusFragment.setArguments(args);
//                mResultLineCarFragment.setArguments(args);
//                ft.replace(R.id.fl_fragment_container, mResultLineCarFragment);
                break;
        }
//        ft.commit();
    }

    @Override
    public void startAddChanged(String startAdd) {
        mStartAddress = startAdd;
        if (startAdd.equals(getResources().getString(R.string.my_address))) {
            mStartNode = PlanNode.withLocation(new LatLng(mCurrentBDLoation.getLatitude(), mCurrentBDLoation.getLongitude()));
        } else {
            mStartNode = PlanNode.withCityNameAndPlaceName(mCurrentBDLoation.getCity(), startAdd);
        }
        mRoutLinePlanots.setStartPlanNode(mStartNode);
    }

    @Override
    public void targetAddChanged(String targetAdd) {
        mTargetAddress = targetAdd;
        mTargetNote = PlanNode.withCityNameAndPlaceName(mCurrentBDLoation.getCity(), targetAdd);
        mRoutLinePlanots.setTargetPlanNode(mTargetNote);
    }

    public static class TAB_TYPE {
        public static final int _BUS = 32;
        public static final int _WALK = 33;
        public static final int _CAR = 34;

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN && isShouldHideInput(getCurrentFocus(), ev)) {
            ViewUtils.hideSolftInput(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
