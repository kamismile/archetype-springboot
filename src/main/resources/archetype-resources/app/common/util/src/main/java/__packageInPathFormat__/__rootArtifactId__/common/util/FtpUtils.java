#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.util;

/**
 * ftp工具类
 * <p/>
 * Created by haiyang.song on 15/12/24.
 */

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.TimeZone;

public class FtpUtils {
    private static Logger logger = LoggerFactory.getLogger(FtpUtils.class);

    private FTPClient ftpClient;
    private String strIp;
    private int intPort;
    private String user;
    private String password;

    public FtpUtils(String strIp, int intPort, String user, String Password) {
        this.strIp = strIp;
        this.intPort = intPort;
        this.user = user;
        this.password = Password;
        this.ftpClient = new FTPClient();
    }

    public boolean ftpLogin() {
        boolean isLogin = false;
        FTPClientConfig ftpClientConfig = new FTPClientConfig();
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
        this.ftpClient.setControlEncoding("GBK");
        this.ftpClient.configure(ftpClientConfig);

        try {
            if (this.intPort > 0) {
                this.ftpClient.connect(this.strIp, this.intPort);
            } else {
                this.ftpClient.connect(this.strIp);
            }

            int e = this.ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(e)) {
                this.ftpClient.disconnect();
                logger.error("[FTP]" + "登录FTP服务失败！");
                return false;
            }

            this.ftpClient.login(this.user, this.password);
            this.ftpClient.enterLocalPassiveMode();
            this.ftpClient.setFileType(2);
            logger.info("[FTP]" + "恭喜" + this.user + "成功登录FTP服务器");
            isLogin = true;
        } catch (Exception var4) {
            var4.printStackTrace();
            logger.error("[FTP]" + this.user + "登录FTP服务失败！" + var4.getMessage());
        }

        this.ftpClient.setBufferSize(2048);
        this.ftpClient.setDataTimeout(30000);
        return isLogin;
    }

    public void ftpLogOut() {
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                boolean e = this.ftpClient.logout();
                if (e) {
                    logger.info("[FTP]" + "成功退出服务器");
                }
            } catch (IOException var10) {
                var10.printStackTrace();
                logger.warn("[FTP]" + "退出FTP服务器异常！" + var10.getMessage());
            } finally {
                try {
                    this.ftpClient.disconnect();
                } catch (IOException var9) {
                    var9.printStackTrace();
                    logger.warn("[FTP]" + "关闭FTP服务器的连接异常！");
                }

            }
        }

    }

    public boolean uploadFile(File localFile, String romotUpLoadePath) {
        BufferedInputStream inStream = null;
        boolean success;
        try {
            this.ftpClient.changeWorkingDirectory(romotUpLoadePath);
            inStream = new BufferedInputStream(new FileInputStream(localFile));
            logger.info("[FTP]" + localFile.getName() + "开始上传.....");
            success = this.ftpClient.storeFile(localFile.getName(), inStream);
            if (!success) {
                return false;
            }

            logger.info("[FTP]" + localFile.getName() + "上传成功");
        } catch (FileNotFoundException var18) {
            var18.printStackTrace();
            logger.error("[FTP]" + localFile + "未找到");
            return false;
        } catch (IOException var19) {
            var19.printStackTrace();
            return false;
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException var17) {
                    var17.printStackTrace();
                }
            }

        }
        return true;
    }

    public boolean downloadFile(String remoteFileName, String localDires, String remoteDownLoadPath) {
        String strFilePath = localDires + remoteFileName;
        BufferedOutputStream outStream = null;
        boolean success = false;

        try {
            this.ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            outStream = new BufferedOutputStream(new FileOutputStream(strFilePath));
            logger.info("[FTP]" + remoteFileName + "开始下载....");
            success = this.ftpClient.retrieveFile(remoteFileName, outStream);
            if (success) {
                logger.info("[FTP]" + remoteFileName + "成功下载到" + strFilePath);
                return true;
            }
        } catch (Exception var18) {
            var18.printStackTrace();
            logger.error("[FTP]" + remoteFileName + "下载失败");
        } finally {
            if (null != outStream) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException var17) {
                    var17.printStackTrace();
                }
            }
        }

        if (!success) {
            logger.error("[FTP]" + remoteFileName + "下载失败!!!");
        }

        return success;
    }

    public boolean uploadDirectory(String localDirectory, String remoteDirectoryPath) {
        File src = new File(localDirectory);

        try {
            remoteDirectoryPath = remoteDirectoryPath + src.getName() + "/";
            this.ftpClient.makeDirectory(remoteDirectoryPath);
        } catch (IOException var7) {
            var7.printStackTrace();
            logger.info("[FTP]" + remoteDirectoryPath + "目录创建失败");
        }

        File[] allFile = src.listFiles();

        int currentFile;
        for (currentFile = 0; currentFile < allFile.length; ++currentFile) {
            if (!allFile[currentFile].isDirectory()) {
                String srcName = allFile[currentFile].getPath();
                this.uploadFile(new File(srcName), remoteDirectoryPath);
            }
        }

        for (currentFile = 0; currentFile < allFile.length; ++currentFile) {
            if (allFile[currentFile].isDirectory()) {
                this.uploadDirectory(allFile[currentFile].getPath(), remoteDirectoryPath);
            }
        }

        return true;
    }

    public boolean downLoadDirectory(String localDirectoryPath, String remoteDirectory) {
        try {
            String e = (new File(remoteDirectory)).getName();
            localDirectoryPath = localDirectoryPath + e + "//";
            (new File(localDirectoryPath)).mkdirs();
            FTPFile[] allFile = this.ftpClient.listFiles(remoteDirectory);

            int currentFile;
            for (currentFile = 0; currentFile < allFile.length; ++currentFile) {
                if (!allFile[currentFile].isDirectory()) {
                    this.downloadFile(allFile[currentFile].getName(), localDirectoryPath, remoteDirectory);
                }
            }

            for (currentFile = 0; currentFile < allFile.length; ++currentFile) {
                if (allFile[currentFile].isDirectory()) {
                    String strRemoteDirectoryPath = remoteDirectory + "/" + allFile[currentFile].getName();
                    this.downLoadDirectory(localDirectoryPath, strRemoteDirectoryPath);
                }
            }

            return true;
        } catch (IOException var7) {
            var7.printStackTrace();
            logger.info("[FTP]" + "下载文件夹失败");
            return false;
        }
    }

    public FTPClient getFtpClient() {
        return this.ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
