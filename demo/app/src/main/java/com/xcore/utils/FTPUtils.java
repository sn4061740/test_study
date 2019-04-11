package com.xcore.utils;

/**
 * 用于Android和FTP服务器进行交互的工具类
 * 下载导入 commons-net-3.6.jar 包
 * @author
 *
 */
public class FTPUtils {
//    private FTPClient ftpClient = null;
//    private static FTPUtils ftpUtilsInstance = null;
//    public String FTPUrl;
//    public int FTPPort;
//    public String UserName;
//    public String UserPassword;
//
//    private FTPUtils()
//    {
//        ftpClient = new FTPClient();
//    }
//    /*
//     * 得到类对象实例（因为只能有一个这样的类对象，所以用单例模式）
//     */
//    public static FTPUtils getInstance() {
//        if (ftpUtilsInstance == null)
//        {
//            ftpUtilsInstance = new FTPUtils();
//        }
//        return ftpUtilsInstance;
//    }
//
//    /**
//     * 设置FTP服务器
//     * @param FTPUrl1   FTP服务器ip地址
//     * @param FTPPort1   FTP服务器端口号
//     * @param UserName1    登陆FTP服务器的账号
//     * @param UserPassword1    登陆FTP服务器的密码
//     * @return
//     */
//    public boolean initFTPSetting(String FTPUrl1, int FTPPort1, String UserName1, String UserPassword1)
//    {
//        this.FTPUrl = FTPUrl1;
//        this.FTPPort = FTPPort1;
//        this.UserName = UserName1;
//        this.UserPassword = UserPassword1;
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int reply;
//
//                try {
//                    //1.要连接的FTP服务器Url,Port
//                    ftpClient.connect(FTPUrl, FTPPort);
//
//                    //2.登陆FTP服务器
//                    ftpClient.login(UserName, UserPassword);
//
//                    //3.看返回的值是不是230，如果是，表示登陆成功
//                    reply = ftpClient.getReplyCode();
//
//                    if (!FTPReply.isPositiveCompletion(reply))
//                    {
//                        //断开
//                        ftpClient.disconnect();
//                    }
//                } catch (SocketException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        return true;
//    }
//
//
//    public Handler handler;
//
//
//    /**
//     * 上传文件
////     * @param FilePath    要上传文件所在SDCard的路径
//     * @param FileName    要上传的文件的文件名(如：Sim唯一标识码)
//     * @return    true为成功，false为失败
//     */
//    public boolean uploadFile(final String FileName) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int reply;
//                boolean boo=false;
//                if (!ftpClient.isConnected()) {
//                    try {
//                        ftpClient.enterLocalPassiveMode();
//                        //1.要连接的FTP服务器Url,Port
//                        ftpClient.connect(FTPUrl, FTPPort);
//
//                        //2.登陆FTP服务器
//                        ftpClient.login(UserName, UserPassword);
//
//                        //3.看返回的值是不是230，如果是，表示登陆成功
//                        reply = ftpClient.getReplyCode();
//
//                        if (!FTPReply.isPositiveCompletion(reply)) {
//                            //断开
//                            ftpClient.disconnect();
//                            boo=false;
//                            Log.e("TAG","连接断开");
//                        }
//                        boo=true;
//                    } catch (SocketException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//                if(boo==false){
//                    Log.e("TAG","没有连接上..");
//                    return;
//                }
//                FileInputStream fileInputStream =null;
//                try {
//                    String fingStr=SystemUtils.getFingerprint();
//
//                    //设置存储路径
//                    ftpClient.makeDirectory("/"+fingStr);
//                    ftpClient.changeWorkingDirectory("/"+fingStr);
//
//                    //设置上传文件需要的一些基本信息
//                    ftpClient.setBufferSize(1024);
//                    ftpClient.setControlEncoding("UTF-8");
////                    ftpClient.enterLocalActiveMode();//  . enterLocalPassiveMode();
//                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//
//                    //文件上传吧～
//                    fileInputStream = new FileInputStream(new File(SDPATH+""+FileName));
//                    ftpClient.storeFile(FileName, fileInputStream);
//                    Log.e("TAG","上传文件"+FileName+" 成功");
//
//                    //关闭文件流
//                    if(fileInputStream!=null) {
//                        fileInputStream.close();
//                    }
//                    if(paths!=null&&paths.size()>0){
//                        String path1=paths.remove(0);
//                        uploadFile(path1);
//                        return;
//                    }else{
//                        if(handler!=null){
//                            handler.sendEmptyMessage(0);
//                        }
//                    }
//                    if (ftpClient != null) {
//                        //退出登陆FTP，关闭ftpCLient的连接
//                        ftpClient.logout();
//                        ftpClient.disconnect();
//                    }
//
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    Log.e("TAG","上传错误");
//                }
//                finally {
//                }
//            }
//        }).start();
//
//
//        return true;
//    }
//
//    private List<String> paths=new ArrayList<>();
//    public String SDPATH;
//
//    public void startUpload(String sPath,List<String> paths1){
//        paths=paths1;
//        if(paths!=null&&paths.size()>0){
//            String path=paths.remove(0);
//            uploadFile(path);
//        }
//
//    }
//
//    /**
//     * 获取内置SD卡路径
//     *
//     * @return
//     */
//    public static String getInnerSDCardPath() {
//        return Environment.getExternalStorageDirectory().getPath();
//    }
//
//    /**
//     * 下载文件
//     * @param FilePath  要存放的文件的路径
//     * @param FileName   远程FTP服务器上的那个文件的名字
//     * @return   true为成功，false为失败
//     */
//    public boolean downLoadFile(String FilePath, String FileName) {
//
//        if (!ftpClient.isConnected())
//        {
//            if (!initFTPSetting(FTPUrl,  FTPPort,  UserName,  UserPassword))
//            {
//                return false;
//            }
//        }
//
//        try {
//            // 转到指定下载目录
//            ftpClient.changeWorkingDirectory("/data");
//
//            // 列出该目录下所有文件
//            FTPFile[] files = ftpClient.listFiles();
//
//            // 遍历所有文件，找到指定的文件
//            for (FTPFile file : files) {
//                if (file.getName().equals(FileName)) {
//                    //根据绝对路径初始化文件
//                    File localFile = new File(FilePath);
//
//                    // 输出流
//                    OutputStream outputStream = new FileOutputStream(localFile);
//
//                    // 下载文件
//                    ftpClient.retrieveFile(file.getName(), outputStream);
//
//                    //关闭流
//                    outputStream.close();
//                }
//            }
//
//            //退出登陆FTP，关闭ftpCLient的连接
//            ftpClient.logout();
//            ftpClient.disconnect();
//
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        return true;
//    }

}

