package com.example.stylemigration.slice;

import com.example.stylemigration.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.agp.components.Component;
import ohos.agp.window.dialog.ToastDialog;
import ohos.agp.window.service.WindowManager;
import ohos.app.Context;
import ohos.data.rdb.ValuesBucket;
import ohos.global.resource.Resource;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.agp.components.*;
import ohos.media.image.common.Size;
import ohos.media.photokit.metadata.AVStorage;
import ohos.utils.net.Uri;
import ohos.media.image.ImagePacker;
import ohos.media.image.ImageSource;

//我的头文件
import com.example.stylemigration.utils.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
//线程相关
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.media.image.PixelMap;
import ohos.net.NetHandle;
import ohos.net.NetManager;
import ohos.net.NetStatusCallback;



public class LandscapeAbilitySlice extends AbilitySlice {
    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00201, "Landscape");
    //    private String pngCachePath = "resources/rawfile/image/test.png";
    private FileInputStream fileInputStream = null;
    private byte[] imgBuffer;
    private String urlStrUpload = "http://101.200.209.222:5050/upload/landscape";
    private String urlStrDld = "http://101.200.209.222:5050/LandscapeWeights/test_fake.png";

    private Button buttonGallery;
    private Button buttonCamara;
    private Button buttonMigration;
    private Button newToMainButton;

    private Image image;

    private MyEventHandler handler;


    //图片相关
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x0001, "选择图片测试");
    private final int imgRequestCode=1123;
    Image showChooseImg=null;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_landscape);
        // 隐藏状态栏、设置状态栏和导航栏透明
        getWindow().addFlags(WindowManager.LayoutConfig.MARK_TRANSLUCENT_STATUS);

        showChooseImg=(Image)findComponentById(ResourceTable.Id_real_image);


        buttonGallery = (Button)findComponentById(ResourceTable.Id_but_gallery);
        buttonCamara = (Button)findComponentById(ResourceTable.Id_but_camara);
        buttonMigration = (Button)findComponentById(ResourceTable.Id_but_migration);
        newToMainButton = (Button)findComponentById(ResourceTable.Id_but_back_to_main);
        image = (Image)findComponentById(ResourceTable.Id_fake_image);

        newToMainButton.setClickedListener(component -> terminate());
        //初始化线程类
        initHandler();
        //按钮响应响应事件
        buttonCamara.setClickedListener(
                listener -> presentForResult(new TakePhotoAbilitySlice(), new Intent(), 1124));
        buttonGallery.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        selectPic();
                    }

                }).start();
            }

        });

        buttonMigration.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                image.setPixelMap(ResourceTable.Media_uploading);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doRequest(fileInputStream);
                    }
                }).start();
            }
        });

    }

    //处理PageAbility返回的结果
    @Override
    protected void onResult(int requestCode,Intent intent){
        //处理相机返回的图像
        if (requestCode == 1124){
            String path = intent.getStringParam("path");
            imgBuffer = intent.getByteArrayParam("fileInputStream");
            ImageSource imageSource = null;
            try {
                imageSource = ImageSource.create(imgBuffer,null);
                ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
//                decodingOptions.desiredRegion = new Rect(0,0,1024,1024);
                decodingOptions.desiredSize = new Size(512,512);//长宽分别有512个像素，每个像素4Byte，共1048576Bytes
                //顺时针旋转
                decodingOptions.rotateDegrees = 90;
                PixelMap pixelMap = imageSource.createPixelmap(decodingOptions);
                long b = pixelMap.getPixelBytesCapacity();

                //储存位图
                ImagePacker imagePacker = ImagePacker.create();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImagePacker.PackingOptions packingOptions = new ImagePacker.PackingOptions();
                packingOptions.quality = 100;
                imagePacker.initializePacking(os, packingOptions);
                imagePacker.addImage(pixelMap);
                imagePacker.finalizePacking();
                imgBuffer = os.toByteArray();

                //设置图片控件对应的位图
                showChooseImg.setPixelMap(pixelMap);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (imageSource != null) {
                    imageSource.release();
                }
            }
        }
    }

    /**
     * 新建线程
     */
    private void initHandler() {
        handler = new MyEventHandler(EventRunner.current());
    }

    private class MyEventHandler extends EventHandler{
        public MyEventHandler(EventRunner runner) throws IllegalArgumentException {
            super(runner);
        }

        @Override
        protected void processEvent(InnerEvent event) {
            super.processEvent(event);
            if(event==null){
                return;
            }
            //更新图片
            if(event.eventId==1002){
                byte[] data = (byte[])event.object;
                ImageSource imageSource = ImageSource.create(data,new ImageSource.SourceOptions());
                PixelMap pixelMap = imageSource.createPixelmap(null);
                saveImage(MyFun.createFileName("test"), pixelMap);
                image.setPixelMap(pixelMap);
                pixelMap.release();
                String msg = "文件已储存至相册";
                showTips(getContext(),msg);
            }
        }
        private void showTips(Context context, String msg) {
            getUITaskDispatcher().asyncDispatch(() -> new ToastDialog(context).setText(msg).show());
        }
    }


    /**
     * 从图库选择图片
     */

    public void selectPic(){
        Intent intent = new Intent();
        Operation opt=new Intent.OperationBuilder().withAction("android.intent.action.GET_CONTENT").build();
        intent.setOperation(opt);
        intent.addFlags(Intent.FLAG_NOT_OHOS_COMPONENT);
        intent.setType("image/*");
        startAbilityForResult(intent, imgRequestCode);

    }
    //onAbilityResult的重载，在执行startAbilityForResult后紧接着执行onAbilityResult，
    @Override
    protected void onAbilityResult(int requestCode, int resultCode, Intent resultData){
        if(requestCode==imgRequestCode)
        {
            if (resultData==null){
                return;
            }

            HiLog.info(label,"选择图片getUriString:"+resultData.getUriString());
            //选择的Img对应的Uri
            String chooseImgUri=resultData.getUriString();
            HiLog.info(label,"选择图片getScheme:"+chooseImgUri.substring(chooseImgUri.lastIndexOf('/')));

            //定义数据能力帮助对象
            DataAbilityHelper helper = DataAbilityHelper.creator(getContext());
            //定义图片来源对象
            ImageSource imageSource = null;
            //获取选择的Img对应的Id
            String chooseImgId = null;
            //如果是选择文件则getUriString结果为content://com.android.providers.media.documents/document/image%3A30，其中%3A是":"的URL编码结果，后面的数字就是image对应的Id
            //如果选择的是图库则getUriString结果为content://media/external/images/media/30，最后就是image对应的Id
            //这里需要判断是选择了文件还是图库
            if(chooseImgUri.lastIndexOf("%3A")!=-1){
                chooseImgId = chooseImgUri.substring(chooseImgUri.lastIndexOf("%3A")+3);
            }
            else {
                chooseImgId = chooseImgUri.substring(chooseImgUri.lastIndexOf('/')+1);
            }
            //获取图片对应的uri，由于获取到的前缀是content，我们替换成对应的dataability前缀
            Uri uri=Uri.appendEncodedPathToUri(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI,chooseImgId);
            HiLog.info(label,"选择图片dataability路径:"+uri.toString());
            try {
                //读取图片
                FileDescriptor fd = helper.openFile(uri, "r");
                imageSource = ImageSource.create(fd, null);
                //创建位图
                ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
//                decodingOptions.desiredRegion = new Rect(0,0,1024,1024);
                decodingOptions.desiredSize = new Size(512,512);//长宽分别有512个像素，每个像素4Byte，共1048576Bytes
                PixelMap pixelMap = imageSource.createPixelmap(decodingOptions);
                long b = pixelMap.getPixelBytesCapacity();

                //储存位图
                ImagePacker imagePacker = ImagePacker.create();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImagePacker.PackingOptions packingOptions = new ImagePacker.PackingOptions();
                packingOptions.quality = 100;
                imagePacker.initializePacking(os, packingOptions);
                imagePacker.addImage(pixelMap);
                imagePacker.finalizePacking();
                imgBuffer = os.toByteArray();

                //设置图片控件对应的位图
                showChooseImg.setPixelMap(pixelMap);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (imageSource != null) {
                    imageSource.release();
                }
            }
        }
    }


    /**
     * 执行POST和GET请求
     */
    public void doRequest(FileInputStream fileInputStream){
        NetManager manager = NetManager.getInstance(this);
        if(!manager.hasDefaultNet()){
            return;
        }
        //实现网络操作功能
        NetHandle netHandler = manager.getDefaultNet();
        manager.addDefaultNetStatusCallback(new NetStatusCallback(){
            //网络正常
            @Override
            public void onAvailable(NetHandle handle) {
                super.onAvailable(handle);
                HiLog.info(LABEL,"网络状况正常");

            }
            //网络阻塞
            @Override
            public void onBlockedStatusChanged(NetHandle handle, boolean blocked) {
                super.onBlockedStatusChanged(handle, blocked);
                HiLog.info(LABEL,"网络状况阻塞");
            }
        });

        //提交POST请求
        HttpURLConnection postConnection = null;
        URL postUrl;

//        ResourceManager resourceManager = this.getApplicationContext().getResourceManager();
//        RawFileEntry rawFileEntry = resourceManager.getRawFileEntry(filepath);

        try{
            postUrl = new URL(urlStrUpload);
            postConnection = (HttpURLConnection)postUrl.openConnection();
            postConnection.setConnectTimeout(40000);
            postConnection.setReadTimeout(30000);
            postConnection.setDoOutput(true);
            postConnection.setDoInput(true);
            postConnection.setUseCaches(false);
            postConnection.setRequestMethod("POST");
            OutputStream out = new DataOutputStream((postConnection.getOutputStream()));
            //fileInputStream为null时，通过imgBuffer传入从图库获取的byte数组
            if(fileInputStream == null){
                int length = imgBuffer.length;
                out.write(imgBuffer,0,length);
            }
            //fileInputStream不为空时，通过fileInputStream传入从相机获取的InputStream输入流
            else{
                DataInputStream in = new DataInputStream(fileInputStream);
                int bytes = 0;
                byte[] bufferOut = new byte[1024];
                while ((bytes = in.read(bufferOut)) != -1) {
                    out.write(bufferOut, 0, bytes);
                }
                in.close();
            }
            out.flush();
            out.close();
            //访问rawfile内文件的写法
//            Resource resource = rawFileEntry.openRawFile();
//            int length = resource.available();
//            byte[] bufferOut = new byte[length];
//            resource.read(bufferOut,0,length);
//            out.write(bufferOut,0,length);


            int responseCode = postConnection.getResponseCode(); // 获取返回码
            String s = Integer.toString(responseCode); //转换为string
            HiLog.info(LABEL, "HTTP Response Status: " + s);
            // 读取返回数据
            StringBuffer strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    postConnection.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //提交GET请求
        HttpURLConnection connection = null;
        URL url;
        InputStream inputStream = null;
        ByteArrayOutputStream todo = new ByteArrayOutputStream();
        try{
            url = new URL(urlStrDld);
            connection = (HttpURLConnection)netHandler.openConnection(url);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
//            connection.setDoOutput(true);
            connection.setConnectTimeout(40000);
            connection.setReadTimeout(30000);
            if(connection.getResponseCode()==200){
                inputStream = connection.getInputStream();
                long file_length = connection.getContentLength();
                int len = 0;
                byte[] data = new byte[1024];
                int total_length = 0;
                while((len = inputStream.read(data))!=-1){
                    total_length+=len;
                    int value = (int)((total_length/(float)file_length)*100);
                    todo.write(data,0,len);
                }
                //最终更新UI
                handler.sendEvent(InnerEvent.get(1002,todo.toByteArray()));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null!=inputStream){
                try{
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(null!=connection){
                connection.disconnect();
            }
        }
    }

    /**
     * 把图片写入相册
     * @param fileName 文件名
     * @param pixelMap 待储存的位图文件
     */
    private void saveImage(String fileName, PixelMap pixelMap) {

        try {
            ValuesBucket valuesBucket = new ValuesBucket();
            valuesBucket.putString(AVStorage.Images.Media.DISPLAY_NAME, fileName);
            valuesBucket.putString("relative_path", "DCIM/");
            valuesBucket.putString(AVStorage.Images.Media.MIME_TYPE, "image/JPEG");
            //应用独占
            valuesBucket.putInteger("is_pending", 1);
            DataAbilityHelper helper = DataAbilityHelper.creator(this);
            int id = helper.insert(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, valuesBucket);
            Uri uri = Uri.appendEncodedPathToUri(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, String.valueOf(id));
            //这里需要"w"写权限
            FileDescriptor fd = helper.openFile(uri, "w");
            ImagePacker imagePacker = ImagePacker.create();
            ImagePacker.PackingOptions packingOptions = new ImagePacker.PackingOptions();
            OutputStream outputStream = new FileOutputStream(fd);
            packingOptions.format = "image/jpeg";
            packingOptions.quality = 100;
            boolean result = imagePacker.initializePacking(outputStream, packingOptions);
            if (result) {
                result = imagePacker.addImage(pixelMap);
                if (result) {
                    long dataSize = imagePacker.finalizePacking();
                }
            }
            outputStream.flush();
            outputStream.close();
            valuesBucket.clear();
            //解除独占
            valuesBucket.putInteger("is_pending", 0);
            helper.update(uri, valuesBucket, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
