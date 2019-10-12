package com.mumu.jsrecyclerview8;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.HashMap;
import java.util.List;

import static com.mumu.jsrecyclerview8.App.getContext;

/**
 * @author : zlf
 * date    : 2019/5/26
 * github  : https://github.com/mamumu
 * blog    : https://www.jianshu.com/u/281e9668a5a6
 * desc    :
 */
public class MainAdapter extends BaseQuickAdapter<MainEntity.ResultsBean, BaseViewHolder> {

    private HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
    private int mWidth;
    private int mHeight;

    public MainAdapter(@Nullable List<MainEntity.ResultsBean> data) {
        super(R.layout.item_main, data);
    }

    public MainAdapter() {
        super(R.layout.item_main);
    }

    @Override
    protected void convert(BaseViewHolder helper, MainEntity.ResultsBean data) {
        //将每一个需要赋值的id和对应的数据绑定
        ImageView imageView = helper.getView(R.id.item_iv_main);
        helper.setText(R.id.item_tv_main_name, "type:"+data.getType()+"+id:"+data.get_id());//名字
        if (TextUtils.isEmpty(data.getType()) || TextUtils.isEmpty(data.getUrl())) {
            return;
        }
        if (hashMap.get(helper.getAdapterPosition()) != null) {
            //屏幕的宽度(px值）
            int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
            //图片的宽度
            mWidth = screenWidth / 2;
            //图片的高度
            mHeight=hashMap.get(helper.getAdapterPosition());
            //如果图片存储的高度不为空，则使用图片的存储高度作为imageView的高度
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.width=mWidth;
            layoutParams.height=mHeight;
            imageView.setLayoutParams(layoutParams);
            Log.d("mmmm", "S_height" + helper.getAdapterPosition() + "=" + hashMap.get(helper.getAdapterPosition()));
        }

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.icon_no_shop)
                .error(R.mipmap.icon_no_shop);
        Glide.with(mContext)
                .asBitmap()
                .apply(options)
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap bitmap, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        //存储图片的高度，以便在向上滚动的时候使用
                        int height = bitmap.getHeight();
                        if (hashMap.get(helper.getAdapterPosition()) == null) {
                            hashMap.put(helper.getAdapterPosition(), height);
                        }
//                        Log.d("mmm", "S_width" + helper.getAdapterPosition() + "=" + width); //400px
//                        Log.d("mmm", "S_height" + helper.getAdapterPosition() + "=" + height); //400px
                        return false;
                    }
                })
                .load(data.getUrl())
                .into(imageView);
        //对两个按钮进行监听

    }
}
