package com.mumu.jsrecyclerview8;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mumu.jsrecyclerview8.api.ApiUrl;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import per.goweii.actionbarex.common.ActionBarCommon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.abc_main_return)
    ActionBarCommon abcMainReturn;
    @BindView(R.id.srl_main)
    SmartRefreshLayout srlMain;
    @BindView(R.id.fab_main)
    FloatingActionButton fabMain;

    private RecyclerView rvMain;
    private int distance;
    private boolean visible = true;
    private MainAdapter mMainAdapter;
    private View top;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ViewHolder viewHolder;
    private boolean mCanLoop = true;
    private List<MainEntity.ResultsBean> mList = new ArrayList<>();
    private int mPage = 1;//页数
    private boolean isSrl = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        rvMain = findViewById(R.id.rv_main);

        initView();
    }

    private void initView() {
        refreshView();
        initBanner();
        smartRefreshView();
        getPicCmd();
    }

    /**
     * 刷新消息列表
     */
    private void refreshView() {
        // 创建StaggeredGridLayoutManager实例
        MMStaggeredGridLayoutManager layoutManager =
                new MMStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rvMain.setLayoutManager(layoutManager);
        //RecyclerView的滚动监听，是否展示FloatingActionButton
        rvMain.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //向下滚动
                if (distance < -ViewConfiguration.getTouchSlop() && !visible) {
                    //显示fab
                    showFABAnimation(fabMain);
                    distance = 0;
                    visible = true;
                } else if (distance > ViewConfiguration.getTouchSlop() && visible) {
                    //隐藏
                    hideFABAnimation(fabMain);
                    distance = 0;
                    visible = false;
                }
                //向下滑并且可见  或者  向上滑并且不可见
                if ((dy > 0 && visible) || (dy < 0 && !visible)) {
                    distance += dy;
                }
                //滑动到顶部的时候隐藏按钮
                if (!recyclerView.canScrollVertically(-1)) {
                    //隐藏
                    hideFABAnimation(fabMain);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int[] first = new int[2];
                layoutManager.findFirstCompletelyVisibleItemPositions(first);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (first[0] == 1 || first[1] == 1)) {
                    layoutManager.invalidateSpanAssignments();
                }
            }
        });
        mMainAdapter = new MainAdapter();
        rvMain.setAdapter(mMainAdapter);
        top = getLayoutInflater().inflate(R.layout.item_main_header, rvMain, false);
        mMainAdapter.addHeaderView(top);
        //因为要加载recyclerview中的header的资源，所以吧绑定放在这
        ButterKnife.bind(this);
        mMainAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });
    }


    private void initBanner() {
        arrayList.clear();
        arrayList.add("http://img2.imgtn.bdimg.com/it/u=1447362014,2103397884&fm=200&gp=0.jpg");
        arrayList.add("http://img1.imgtn.bdimg.com/it/u=111342610,3492888501&fm=26&gp=0.jpg");
        arrayList.add("http://imgsrc.baidu.com/imgad/pic/item/77094b36acaf2eddc8c37dc7861001e9390193e9.jpg");
        viewHolder = new ViewHolder(top);
        if (arrayList.size() <= 1) {
            mCanLoop = false;
        } else {
            mCanLoop = true;
        }
        viewHolder.cbMain.setPages(new CBViewHolderCreator() {
            @Override
            public Holder createHolder(View itemView) {
                return new NetImageHolderView(itemView);
            }

            @Override
            public int getLayoutId() {
                return R.layout.item_main_banner;
            }
        }, arrayList)
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setPointViewVisible(mCanLoop)
                .setCanLoop(mCanLoop)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Toast.makeText(MainActivity.this, "你点击了第" + position + "张图片", Toast.LENGTH_SHORT).show();
                    }
                });
        if (arrayList.size() > 0) {
            viewHolder.cbMain.startTurning(3000);
        }

        initReListener(viewHolder.tvMain1);
        initReListener(viewHolder.tvMain2);
        initReListener(viewHolder.tvMain3);
        initReListener(viewHolder.tvMain4);
        initReListener(viewHolder.tvMain5);
        initReListener(viewHolder.tvMain6);
        initReListener(viewHolder.tvMain7);
        initReListener(viewHolder.tvMain8);
    }

    private void initReListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "暂未开放！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 轮播图对应的holder
     */
    public class NetImageHolderView extends Holder<String> {
        private ImageView mImageView;

        //构造器
        public NetImageHolderView(View itemView) {
            super(itemView);
        }

        @Override
        protected void initView(View itemView) {
            //找到对应展示图片的imageview
            mImageView = itemView.findViewById(R.id.iv_banner);
            //设置图片加载模式为铺满，具体请搜索 ImageView.ScaleType.FIT_XY
            mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        @Override
        public void updateUI(String data) {
            //使用glide加载更新图片
            Glide.with(MainActivity.this).load(data).into(mImageView);
        }
    }

    /**
     * by moos on 2017.8.21
     * func:显示fab动画
     */
    public void showFABAnimation(View view) {
        view.setVisibility(View.VISIBLE);
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(400).start();

    }

    /**
     * by moos on 2017.8.21
     * func:隐藏fab的动画
     */

    public void hideFABAnimation(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 0f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 0f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 0f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(400).start();
        view.setVisibility(View.GONE);
    }

    @OnClick(R.id.fab_main)
    public void onViewClicked() {
        //缓慢滑动到顶部
        rvMain.smoothScrollToPosition(0);
    }

    static
    class ViewHolder {
        @BindView(R.id.cb_main)
        ConvenientBanner cbMain;
        @BindView(R.id.tv_main1)
        TextView tvMain1;
        @BindView(R.id.tv_main2)
        TextView tvMain2;
        @BindView(R.id.tv_main3)
        TextView tvMain3;
        @BindView(R.id.tv_main4)
        TextView tvMain4;
        @BindView(R.id.tv_main5)
        TextView tvMain5;
        @BindView(R.id.tv_main6)
        TextView tvMain6;
        @BindView(R.id.tv_main7)
        TextView tvMain7;
        @BindView(R.id.tv_main8)
        TextView tvMain8;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * MainActivity中增加下拉刷新和上拉加载的监听方法
     */
    private void smartRefreshView() {
        srlMain.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //下拉刷新,一般添加调用接口获取数据的方法
                mPage = 1;
                isSrl = true;
                getPicCmd();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                //上拉加载，一般添加调用接口获取更多数据的方法
                mPage++;
                isSrl = true;
                getPicCmd();
            }
        });
    }

    /**
     * 获取图片的请求
     */
    private void getPicCmd(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gank.io/api/")
                //设置数据解析器
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiUrl apiUrl=retrofit.create(ApiUrl.class);
        Call<BaseResponse<MainEntity.ResultsBean>> call = apiUrl.getPic(10,mPage);
        call.enqueue(new Callback<BaseResponse<MainEntity.ResultsBean>>() {
                         @Override
                         public void onResponse(Call<BaseResponse<MainEntity.ResultsBean>> call, Response<BaseResponse<MainEntity.ResultsBean>> response) {
                             if (mPage == 1) {
                                 mList.clear();
                             }
                             if (response.body() != null&& response.body().getResults().size() > 0) {
                                 mList.addAll(response.body().getResults());
                                 if (isSrl && mPage != 1) {
                                     int start = mList.size();
                                     mMainAdapter.notifyItemRangeInserted(start, 10);
                                 } else {
                                     mMainAdapter.setNewData(mList);
                                 }
                                 isSrl = false;

                                 srlMain.finishRefresh();
                                 if (response.body() != null && response.body().getResults().size() >= 10) {
                                     srlMain.finishLoadMore();
                                 } else {
                                     srlMain.finishLoadMoreWithNoMoreData();
                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<BaseResponse<MainEntity.ResultsBean>> call, Throwable t) {
                             Log.e("mmm","errow "+t.getMessage());
                         }
                     }
        );
    }
}
