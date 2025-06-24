package com.yjsoft.led.util;

import android.app.Activity;
import android.content.Context;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtils {
    /**
     *  
     *      * the traditional io way  
     *      * @param filename 
     *      * @return 
     *      * @throws IOException 
     *      
     */
    public static byte[] toByteArray0(String filename) throws IOException {

        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }

    /**
     * NIO way
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray2(String filename) throws IOException {

        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }

        FileChannel channel = null;
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(f);
            channel = fs.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
            while ((channel.read(byteBuffer)) > 0) {
                // do nothing
//                MyTrace.e("reading");
            }
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Mapped File  way
     * MappedByteBuffer 可以在处理大文件时，提升性能
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(String filename) throws IOException {

        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(filename, "r").getChannel();
            MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size()).load();
            System.out.println(byteBuffer.isLoaded());
            byte[] result = new byte[(int) fc.size()];
            if (byteBuffer.remaining() > 0) {
//                MyTrace.e("remain");
                byteBuffer.get(result, 0, byteBuffer.remaining());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 复制assets目录下所有文件及文件夹到指定路径
     *
     * @param mActivity   上下文
     * @param mAssetsPath Assets目录的相对路径
     * @param mSavePath   复制文件的保存路径
     * @return void
     */
    public static void copyAssetsFiles(Activity mActivity, String mAssetsPath, String mSavePath) {
        try {
            // 获取assets目录下的所有文件及目录名
           String[] fileNames = mActivity.getResources().getAssets().list(mAssetsPath);
            if (fileNames.length > 0) {
                // 若是目录
                for (String fileName : fileNames) {
                    String newAssetsPath;
                    // 确保Assets路径后面没有斜杠分隔符，否则将获取不到值
                    if ((mAssetsPath == null) || "".equals(mAssetsPath) || "/".equals(mAssetsPath)) {
                        newAssetsPath = fileName;
                    } else {
                        if (mAssetsPath.endsWith("/")) {
                            newAssetsPath = mAssetsPath + fileName;
                        } else {
                            newAssetsPath = mAssetsPath + "/" + fileName;
                        }
                    }
                    // 递归调用
                    copyAssetsFiles(mActivity, newAssetsPath, mSavePath +"/"+ fileName);
                }
            } else {
//                LogUtils.e(mSavePath);
                // 若是文件
                File file = new File(mSavePath);
                // 若文件夹不存在，则递归创建父目录
                file.getParentFile().mkdirs();
                InputStream is = mActivity.getResources().getAssets().open(mAssetsPath);
                FileOutputStream fos = new FileOutputStream(new File(mSavePath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                // 循环从输入流读取字节
                while ((byteCount = is.read(buffer)) != -1) {
                    // 将读取的输入流写入到输出流
                    fos.write(buffer, 0, byteCount);
                }
                // 刷新缓冲区
                fos.flush();
                fos.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getSize(Activity mActivity, String mAssetsPath){
        try {
            String[] fileNames = mActivity.getResources().getAssets().list(mAssetsPath);
//            assert fileNames != null;
//            LogUtils.e("java Size:"+fileNames.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getJson(String fileName, Context context){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // 创建字符流对象
            FileReader reader = new FileReader(fileName);
            // 创建字符串拼接
            StringBuilder builder = new StringBuilder();
            // 读取一个字符
            int read = reader.read();
            // 能读取到字符
            while(read != -1) {
                // 拼接字符串
                builder.append((char) read);
                // 读取下一个字符
                read = reader.read();
            }
            // 关闭字符流
            reader.close();
            return builder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
