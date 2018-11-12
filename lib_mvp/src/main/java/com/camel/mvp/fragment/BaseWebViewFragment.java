package com.camel.mvp.fragment;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.camel.mvp.R;
import com.camel.mvp.activity.BaseWebViewActivity;
import com.camel.mvp.base.MvpBaseInterface;
import com.camel.mvp.base.MvpBasePresenter;
import com.camel.mvp.util.NetworkUtils;
import com.camel.mvp.util.UriUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by CamelLuo on 2018/10/30.
 */
public abstract class BaseWebViewFragment<V extends MvpBaseInterface, P extends MvpBasePresenter<V>>
        extends BaseToolbarFragment {
    private static final String TAG = BaseWebViewFragment.class.getSimpleName();

    /**
     * 图片/照片选择回调值
     */
    private static final int FILE_SELECT_CODE = 101;
    /**
     * 页面超时时长
     */
    private static final int WEB_TIMEOUT = 20000;

    /**
     * WebView的加载进度条
     */
    protected ProgressBar webViewProgress;
    /**
     * WebView的内容布局
     */
    protected FrameLayout webViewContentLayout;
    /**
     * WebView的错误提示布局
     */
    protected RelativeLayout webViewErrorLayout;


    /**
     * 是否出现超时错误
     */
    private boolean isTimeoutError = false;
    /**
     * 是否出现页面连接错误
     */
    private boolean isNetError = false;

    private String startUrl;
    private String shouldloadUrl;

    /**
     * 定义一个String用于存储当前页面所需的cookie
     */
    private String cookie;

    /**
     * WebView对象
     */
    private WebView webView;
    /**
     * Cookie管理器
     */
    private CookieManager cookieManager;

    /**
     * 图片/照片选择回调
     */
    private ValueCallback<Uri> mUploadMessage;//回调图片选择，4.4以下
    private ValueCallback<Uri[]> mUploadCallbackAboveL;//回调图片选择，5.0以上

    private Handler timeoutHandler = new Handler();

    @Override
    protected int setContentViewId() {
        if (isNeedToolbar()) {
            return R.layout.activity_webview_include_tittle;
        } else {
            return R.layout.activity_webview_no_tittle;
        }
    }

    @Override
    protected void getBundleValues(Bundle bundle) {
        if (bundle != null) {
            String url = bundle.getString("webUrl");
            if (!TextUtils.isEmpty(url)) {
                startUrl = url.contains("http") ? url : "http://" + url;
            } else {
                showMsg("请输入正确的url地址！");
            }
        } else {
            showMsg("请传入url地址！");
        }
    }

    @Override
    protected void initViewAndToolbar(View view, Bundle savedInstanceState) {
        if (isNeedToolbar()) {
            initToolBar(true, "测试WebViewFragment");
        }
        //初始化WebView及相关控件
        initWebView(view);
    }

    @Override
    public void onResume() {
        //通知WebView内核恢复所有处理，如动画、播放中的音乐、地理位置等，并且全局停止js处理
        if (webView != null) {
            webView.onResume();
            webView.resumeTimers();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        //通知WebView内核尝试停止所有处理，如动画、播放中的音乐、地理位置等，并且全局停止js处理
        if (webView != null) {
            webView.onPause();
            webView.pauseTimers();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (webView != null) {
            //停止加载
            webView.stopLoading();
            //停止加载html数据，防止WebView退出后仍然播放音乐的问题
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            //清除缓存
            webView.clearCache(true);
            //清除历史
            webView.clearHistory();
            //移除webview上子view
            webView.removeAllViews();
            //把webview从视图中移除
            ((ViewGroup) webView.getParent()).removeView(webView);
            //销毁WebView，防止内存泄漏
            webView.destroy();
            webView = null;
        }
        super.onDestroyView();
    }

    /**
     * 定义一个方法用于初始化WebView及相关控件
     */
    private void initWebView(View view) {
        //绑定WebViewActivity相关的控件
        webViewContentLayout = view.findViewById(R.id.activity_base_webView_content_layout);
        webViewProgress = view.findViewById(R.id.activity_base_webView_progress);
        webViewErrorLayout = view.findViewById(R.id.activity_base_webView_error_layout);

        //创建WebView实例并添加到内容布局中
        webView = new WebView(mActivity);
        webViewContentLayout.addView(webView);

        //设置WebView的参数
        initWebViewSetting();

        //设置Cookie，实例化cookieManager
        if (isUseCookie()) {
            cookieManager = CookieManager.getInstance();
            //允许Cookie跨域写入（不开启会导致H5前端页面在调用跨域接口时无法将cookie写入）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.setAcceptThirdPartyCookies(webView, true);
            }
            //获取Cookie内容，具体实现交由继承子类自行处理
            cookie = getWebCookie();
        }

        //设置WebView客户端
        webView.setWebViewClient(new MeWebViewClient());

        //设置Chrome客户端
        webView.setWebChromeClient(new MyWebViewChromeClient());

        //开始加载传入的网页：
        webView.loadUrl(startUrl);
        debugLog("加载的地址为：" + startUrl);
    }

    /**
     * 定义一个方法用于设置WebView
     */
    private void initWebViewSetting() {
        //声明WebSettings子类
        WebSettings webSettings = webView.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
//        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
//        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
//        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        //在网页加载完成后再加载图片
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        //设置缓存模式，使用默认缓存
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //支持HTML5 的 DOM Storage 机制存储 ***
        webSettings.setDomStorageEnabled(true);
        //设置App缓存路径
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        webSettings.setAppCachePath(mActivity.getDir("appcache", 0).getPath());

        //设置支持本地存储
        webSettings.setDatabaseEnabled(true);
        String dbPath = mActivity.getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setDatabasePath(dbPath);



        //调试WebView，需搭配Google Chrome浏览器的DevTools工具（chrome://inspect/#devices）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(canDebugByChromeDevTools());
        }

        //////////////////////////////////////以下设置为可选配项，可根据实际情况调整//////////////////////////////////////////////////

        //由于Android 5.0以上设备的 Webview 默认不允许加载Http与Https混合内容
        //是否允许Android 5.0以上设备的 Webview 加载Http与Https混合内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //以下两行代码任意两者都可以生效
            webSettings.setMixedContentMode(webSettings.getMixedContentMode());
            /**
             * MIXED_CONTENT_ALWAYS_ALLOW       允许从任何来源加载内容，即使起源是不安全的；
             * MIXED_CONTENT_NEVER_ALLOW        不允许Https加载Http的内容，即不允许从安全的起源去加载一个不安全的资源；
             * MIXED_CONTENT_COMPLTIBILITY_MODE 当涉及到混合式内容时，WebView会尝试去兼容最新Web浏览器的风格
             */
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //////////////////////////////////////以下设置为可选配项，可根据实际情况调整//////////////////////////////////////////////////
    }

    /**
     * 定义一个内部类用于实现 WebViewClient
     */
    private class MeWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //显示加载框
            showLoading();
            //记录当前加载的url
            shouldloadUrl = url;
            //显示进度条
            webViewProgress.setVisibility(View.VISIBLE);

            //网页url进入加载流程，发布网页加载超时任务
            //由于android 6.0 之前的WebView 均不支持网页超时通知事件，因此必须自己实现超时机制
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                //启动超时线程
                timeoutHandler.postDelayed(timeoutRunnable, WEB_TIMEOUT);
            }
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //等待页面加载完成后再加载图片
            if(!webView.getSettings().getLoadsImagesAutomatically()) {
                webView.getSettings().setLoadsImagesAutomatically(true);
            }

            //隐藏加载框
            hideLoading();
            //网页url完成加载流程，撤销网页加载超时任务
            //由于android 6.0 之前的WebView 均不支持网页超时通知事件，因此必须自己实现超时机制
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                timeoutHandler.removeCallbacks(timeoutRunnable);
            }
            //定义一个方法用于在页面加载结束时需要处理的事，交由子类实现
            doOnPageFinished();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //加载网页前先判断当前设备的网络是否连接并且可用
            if (NetworkUtils.isConnectedAndAvailable(mActivity)) {        //网络连接并可用
                return loadUrlByShouldOverrideUrlLoading(view, url);
            } else {
                //根据继承子类的配置 来判断是否使用 本地离线网页进行加载
                //不使用 本地离线网页，则直接显示错误布局提示
                if (!isUseOfflineWebUrl()) {                                                     //网络没有连接或不可用
                    webViewErrorLayout.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                    return false;
                } else {
                    //使用 本地离线网页，尝试加载本地离线网页
                    return loadUrlByShouldOverrideUrlLoading(view, url);
                }
            }
        }


        /**
         * 该方法用于拦截所有该 shouldloadUrl 地址下的 url请求（例如图片，数据接口灯）
         *
         * @param view
         * @param url
         * @return
         */
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            //根据继承子类的配置 来判断是否将cookie 写入到准备加载的url请求中
            if (isUseCookie()) {
                if (!TextUtils.isEmpty(cookie)) {
                    //将cookie设置到url中
                    boolean syncCookie = syncCookie(url, cookie);
                }
            }
            return super.shouldInterceptRequest(view, url);
        }

        // 用于接收加载网页时的错误信息 这个方法在 android 6.0 以下时不回调
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            int statusCode = errorResponse.getStatusCode();
            debugLog("页面加载失败，onReceivedHttpError code = " + statusCode + "request 为：" + request.getUrl());
            if (statusCode == 404 || statusCode == 500) {
                webViewErrorLayout.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
            }
        }

        //////////////////////////////////////以下设置为可选配项，可根据实际情况调整//////////////////////////////////////////////////

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //在某些网页的认证证书不被Android所接受的情况下，默认会停止该网站的访问，如果想要忽略对该证书的检查

            //必须注释 super.onReceivedSslError(view, handler, error);   该代码会执行 handler.cancel(); 来停止页面的访问
            super.onReceivedSslError(view, handler, error);
            //然后打开下列代码的注释 来接受所有网站的证书
//            handler.proceed();
        }


        //////////////////////////////////////以下设置为可选配项，可根据实际情况调整//////////////////////////////////////////////////


        /**
         * 定义一个方法用于在 WebViewClient类中的 shouldOverrideUrlLoading() 方法中进行url地址加载
         * 加载前会 根据继承子类的配置 来决定是否写入Cookie，并根据Url的Scheme来进行拦截跳转指定功能
         *
         * @param view webView 对象
         * @param url  待加载的url地址
         * @return
         */
        private boolean loadUrlByShouldOverrideUrlLoading(WebView view, String url) {
            //根据继承子类的配置 来判断是否将cookie 写入到准备加载的url地址中
            if (isUseCookie()) {
                if (!TextUtils.isEmpty(cookie)) {
                    //将cookie设置到url中
                    boolean syncCookie = syncCookie(url, cookie);
                }
            }

            //特殊Url地址的Scheme进行跳转拦截：拨号Scheme -> 跳转到系统拨号界面
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                return true;
            }

            //加载url地址
            view.loadUrl(url);
            return true;
        }

        /**
         * 定义一个方法用于将cookie同步到WebView
         *
         * @param url    WebView要加载的url
         * @param cookie 要同步的cookie
         * @return true 同步cookie成功，false同步cookie失败
         */
        public boolean syncCookie(String url, String cookie) {
            CookieSyncManager.createInstance(mActivity);

            cookieManager.setAcceptCookie(true);
            cookieManager.removeSessionCookie();// 移除以前的cookie
            cookieManager.removeAllCookie();
            StringBuilder sbCookie = new StringBuilder();//创建一个拼接cookie的容器

            sbCookie.append(cookie);//拼接sessionId
            sbCookie.append(String.format(";domain=%s", ""));
            sbCookie.append(String.format(";path=%s", ""));
            String cookieValue = sbCookie.toString();
            //设置cookie
            cookieManager.setCookie(url, cookieValue);//为url设置cookie
            //获取设置的cookie
            String newCookie = cookieManager.getCookie(url);
//        Logger.t(TAG).e("同步后获取到url的cookie为：  " + newCookie);
            CookieSyncManager.getInstance().sync();
            return TextUtils.isEmpty(newCookie) ? false : true;
        }
    }

    /**
     * 定义一个内部类用于继承WebChromeClient
     */
    private class MyWebViewChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                // 网页加载完成,隐藏进度条
                webViewProgress.setVisibility(View.GONE);
                //当页面没有出现超时错误和网络连接错误时允许reload的网页进行显示
                if (!isTimeoutError && !isNetError) {
                    webView.setVisibility(View.VISIBLE);
                    webViewErrorLayout.setVisibility(View.GONE);
                }
            } else {
                // 加载中，更新进度条进度
                webViewProgress.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            // android 6.0 以下 只能通过title 来获取网页错误信息
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (title.contains("404") || title.contains("500") || title.contains("Error") || title.contains("找不到网页") || title.contains("网页无法打开")) {
                    debugLog("加载页面失败，加载到的页面标题为：  " + title);
                    //记录网页连接错误标志
                    isNetError = true;
                    webViewErrorLayout.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                } else {
                    //为防止部分网页请求内部图片元素时间过长失败导致页面超时，因此在读取到页面标题时认为该页面能够显示，撤销网页加载超时任务
                    //由于android 6.0 之前的WebView 均不支持网页超时通知事件，因此必须自己实现超时机制
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                }
            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            //定义一个方法用于处理网页发送过来的js交互,交由继承子类实现
            boolean dealResult = dealJsPromptValue(message);
            if (dealResult) {
                //如果继承子类处理了js交互，应返回true以拦截JsPrompt框的弹出
                return true;
            } else {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        }

        // For Android < 3.0 时触发该方法回调，打开选择照片相关的应用选择器
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
//            Logger.t(TAG).e("openFileChooser 小于3.0 方法被回调了！");
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_SELECT_CODE);
        }

        // For Android > 3.0 < 4.1 时触发该方法回调，打开选择照片相关的应用选择器
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
//            Logger.t(TAG).e("openFileChooser 3.0 ~ 4.1 方法被回调了！");
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, "File Browser"), FILE_SELECT_CODE);
        }

        // For Android > 4.1 < 5.0 时触发该方法回调，打开选择照片相关的应用选择器
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
//            Logger.t(TAG).e("openFileChooser 4.1~ 5.0 方法被回调了！");
            mUploadMessage = uploadMsg;
            PictureSelector.create(BaseWebViewFragment.this)
                    .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                    .previewImage(true)// 是否可预览图片 true or false
                    .isCamera(true)// 是否显示拍照按钮 true or false
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

        }

        // Android  > 5.0 时触发该方法回调，打开选择照片相关的应用选择器
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
            mUploadCallbackAboveL = valueCallback;
            PictureSelector.create(BaseWebViewFragment.this)
                    .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                    .previewImage(true)// 是否可预览图片 true or false
                    .isCamera(true)// 是否显示拍照按钮 true or false
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        debugLog("收到的resultCode ：" + resultCode + "   requestCode:  " + requestCode);
        //上传图片结果回调
        if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            if (resultCode == RESULT_OK) {
                // 图片选择结果回调
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList != null && selectList.size() > 0){
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    //将图片绝对路径转换为媒体Uri路径
                    Uri sourceUri = UriUtils.getMediaUriFromPath(mActivity, selectList.get(0).getPath());
                    if (sourceUri != null) {
                        debugLog("获取的图片地址的Uri为：" + sourceUri.toString());
                        if (Build.VERSION.SDK_INT >= 21) {//5.0以上版本处理
                            Uri[] uris = new Uri[]{sourceUri};
                            //将图片Uri地址传递给H5页面
                            mUploadCallbackAboveL.onReceiveValue(uris);//回调给js
                        } else {//4.4以下处理
                            //将图片Uri地址传递给H5页面
                            mUploadMessage.onReceiveValue(sourceUri);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= 21) {//5.0以上版本处理
                            Uri[] uris = new Uri[]{Uri.parse("")};
                            mUploadCallbackAboveL.onReceiveValue(uris);//回调给js
                            mUploadCallbackAboveL = null;
                        } else { //4.4以下处理
                            mUploadMessage.onReceiveValue(Uri.parse(""));
                            mUploadMessage = null;
                        }
                    }
                }else {
                    if (Build.VERSION.SDK_INT >= 21) {//5.0以上版本处理
                        Uri[] uris = new Uri[]{Uri.parse("")};
                        mUploadCallbackAboveL.onReceiveValue(uris);//回调给js
                        mUploadCallbackAboveL = null;
                    } else { //4.4以下处理
                        mUploadMessage.onReceiveValue(Uri.parse(""));
                        mUploadMessage = null;
                    }
                }
            } else {
                //如果弹出图片选择器后用户取消使用该功能，也应该发送空的Uri给H5页面，否则再次点击上传图片时会无法响应
                if (Build.VERSION.SDK_INT >= 21) {//5.0以上版本处理
                    Uri[] uris = new Uri[]{Uri.parse("")};
                    mUploadCallbackAboveL.onReceiveValue(uris);//回调给js
                    mUploadCallbackAboveL = null;
                } else { //4.4以下处理
                    mUploadMessage.onReceiveValue(Uri.parse(""));
                    mUploadMessage = null;
                }
                return;
            }
        }
    }

    /**
     * 定义一个线程用于执行页面超时操作
     */
    private final Runnable timeoutRunnable = new Runnable() {

        @Override
        public void run() {
            webView.stopLoading();
            //记录网页加载失败标志
            isTimeoutError = true;
            webViewErrorLayout.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        }
    };


    /**
     * 定义一个方法用于实现WebViewFragment在使用时用户按下返回键回退网页
     * 需在宿主Activity的onKeyDown() 方法中调用该方法
     * @return 可以回退时 返回false ，不可以回退时 返回 true，建议宿主Activity在返回true时调用 super.onKeyDown(keyCode, event)
     */
    public boolean onKeyDownForWebViewFragment(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                webViewErrorLayout.setVisibility(View.GONE);
            } else {

            }
            return false;
        } else {
            return true;
        }
    }

    ////////////////////////////////////////////////////抽象方法及空方法重载相关//////////////////////////////////////////////////////////

    /**
     * 定义一个抽象方法用于是否使用Cookie
     * Cookie机制只对原生WebView启用，当使用腾讯X5内核时无论设置为true 或者 false 均不生效
     *
     * @return
     */
    protected abstract boolean isUseCookie();

    /**
     * 定义一个方法用于设置是否允许进行WebView调试，需搭配Google Chrome浏览器的DevTools工具（chrome://inspect/#devices）
     * 设备android系统SDK版本必须大于19（android 4.4）
     *
     * @return
     */
    protected abstract boolean canDebugByChromeDevTools();

    /**
     * 定义一个抽象方法用于是否使用本地Url网页，以便于进行离线网页加载功能
     *
     * @return
     */
    protected abstract boolean isUseOfflineWebUrl();

    /**
     * 定义一个抽象方法用于在页面加载结束时需要处理的工作
     */
    protected abstract void doOnPageFinished();

    /**
     * 定义一个方法用于让继承子类处理与网页的js交互，建议与前端沟通好采用JsPrompt的形式来传递js事件
     *
     * @param message js事件
     * @return 如继承子类不需要处理与网页的js交互，请不要复写该方法，该方法默认会返回false，如果需要处理，处理完毕后请返回true。
     * 以拦截jsPromt的显示
     */
    protected boolean dealJsPromptValue(String message) {
        return false;
    }

    /**
     * 定义一个方法用于获取Cookie内容，具体如何获取交由继承子类实现
     *
     * @return cookie内容
     */
    protected String getWebCookie() {
        return null;
    }

    ////////////////////////////////////////////////////抽象方法及空方法重载相关//////////////////////////////////////////////////////////
}
