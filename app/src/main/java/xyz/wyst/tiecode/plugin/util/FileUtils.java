package xyz.wyst.tiecode.plugin.util;

import xyz.wyst.tiecode.plugin.App;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import xyz.wyst.tiecode.plugin.util.LogUtils;
import com.tiecode.develop.util.firstparty.file.StreamUtils;
import java.nio.channels.FileChannel;

public class FileUtils {

    public static byte[] readStream(InputStream is) {
        try {
            return StreamUtils.readStreamBytes(is);
        } catch (IOException e) {
            LogUtils.printfException(App.mApp, e);
            return new byte[0];
        }
    }

    public static boolean renameFile(String file_old, String file_new) {
        File oldfile = new File(file_old);
        File newfile = new File(file_new);
        if (!oldfile.exists()) {
            LogUtils.writeMainLog("备份源文件不存在！");
            return false;
        }

        if (newfile.exists()) {
            LogUtils.writeMainLog("移动文件目标目录已有文件");
        }

        try {
            moveTo(oldfile, newfile);
            return true;
        } catch (Exception e) {
            LogUtils.printfException(App.mApp, e);
            return false;
        }

        /*
        if (file_new.equals(file_old)) {
            return true;
        }
        File oldfile = new File(file_old);
        if (!oldfile.exists()) {
            return false;
        }
        File newfile = new File(file_new);
        if (newfile.exists()) {
            return false;
        }
        if (oldfile.renameTo(newfile)) {
            return true;
        }
        return false;
        */
    }

    /**
     * 获取文件前缀名
     **/
    public static String getPrefixName(File file) {
        if (file == null) {
            return null;
        }
        String name = file.getName();
        int i = name.lastIndexOf('.');
        if (i == -1) {
            return name;
        } else {
            if (i == 0) {
                return name;
            } else {
                return name.substring(0, i);
            }
        }
    }

    /**
     * 读取文件
     **/
    public static String readString(File file) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            boolean first = true;
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    content.append(line);
                } else {
                    content.append('\n').append(line);
                }
            }
            br.close();
            return content.toString();
        } catch (Exception e) {
            LogUtils.printfException(App.mApp, e);
            return "";
        }
    }

    /**
     * 写出文件
     **/
    public static void writeString(File file, String content) {
        try {
            if (!file.exists()) {
                createFile(file);
            }
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            LogUtils.printfException(App.mApp, e);
            return;
        }
    }

    public static void addString(File file, String content) throws IOException {
        FileWriter fw = new FileWriter(file, true);
        fw.write(content);
        fw.flush();
        fw.close();
    }

    /**
     * 创建文件
     **/
    public static boolean createFile(File file) {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.printfException(App.mApp, e);
            return false;
        }
    }

    /**
     * 读入资源文件
     **/
    public static String readAssets(String name) {
        try {
            InputStream inputstream = App.mApp.getAssets().open(name);
            if (inputstream == null) {
                return "";
            }
            int length = inputstream.available();
            byte[] buffer = new byte[length];
            inputstream.read(buffer);
            String res2 = new String(buffer, 0, length, "utf-8");
            inputstream.close();
            return res2;
        } catch (IOException e) {
            LogUtils.printfException(App.mApp, e);
            return "";
            //throw new RuntimeException(e);
        }
    }

    public static void moveTo(File from, File to) throws IOException {
        if (from.isDirectory()) {
            moveDir(from, to);
        } else {
            moveFile(from, to);
        }
    }

    private static void moveDir(File oldFile, File newFile) throws IOException {

        copyFolder(oldFile, newFile);
        FileUtils.deleteFile(oldFile);

    }

    private static void moveFile(File oldFile, File newFile) throws IOException {

        copy(oldFile, newFile);
        FileUtils.deleteFile(oldFile);

    }

    /**
     * Delete the given file
     * @param file The file to delete
     * @return Whether we succeeded
     */
    public static boolean deleteFile(File file) {
        boolean success = true;
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File subFile : file.listFiles()) {
                    if (!success) {
                        return false;
                    }
                    success = success && deleteFile(subFile);
                }
            }
            if (success)
                success = success && file.delete();
        }
        return success;
    }

    /**
     * 复制文件
     **/
    public static boolean copy(File from, File to) {
        try {
            if (!from.exists()) {
                return false;
            }
            if (!to.exists()) {
                to.getParentFile().mkdirs();
                if (from.isFile())
                    to.createNewFile();
            }
            FileInputStream fileIns = null;
            FileOutputStream fileOuts = null;
            FileChannel source = null;
            FileChannel destination = null;
            try {
                fileIns = new FileInputStream(from);
                fileOuts = new FileOutputStream(to);
                source = fileIns.getChannel();
                destination = fileOuts.getChannel();
                destination.transferFrom(source, 0, source.size());
            } catch (IOException e) {
                LogUtils.printfException(App.mApp, e);
                throw e;
            } finally {
                if (fileIns != null)
                    fileIns.close();
                if (fileOuts != null)
                    fileOuts.close();
                if (source != null)
                    source.close();
                if (destination != null)
                    destination.close();
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtils.printfException(App.mApp, e);
            return false;
        }
    }

    //复制文件夹
    public static boolean copyFolder(File srcFile, File destFile) {
        //判断数据源File对象是否是目录
        if (srcFile.isDirectory()) {
            String srcFileName = srcFile.getName();
            //在目的地下创建和数据源File名称一样的目录
            File newFolder = new File(destFile, srcFileName);//F:\\english
            if (!newFolder.exists()) {
                newFolder.mkdir();
            }

            //获取数据源File下所有文件或者目录的File数组
            File[] fileArray = srcFile.listFiles();//返回一个抽象路径名数组，这些路径名表示此抽象路径名表示的目录中的文件
            if (fileArray != null && fileArray.length != 0) {
                //遍历该File数组，得到每一个File对象
                for (File file : fileArray) {
                    //把该File作为数据源File对象，递归调用复制文件夹的方法
                    copyFolder(file, newFolder);
                }
                return true;
            } else {
                return false;
            }
        } else {
            //不是，说明是文件，直接复制，采用字节流复制文件
            File newFile = new File(destFile, srcFile.getName());
            if (copy(srcFile, newFile)) {
                return true;
            } else {
                return false;
            }

        }
    }

    //重命名文件夹
    public static boolean updataDirname(String file_name_yuan, String file_name_after) {
        File oldfile = new File(file_name_yuan);
        if (!oldfile.exists()) {
            return false;
        }
        File newfile = new File(file_name_after);
        if (newfile.exists()) {
            return false;
        }
        if (oldfile.renameTo(newfile)) {
            return true;
        }
        return false;
    }

    //删除文件夹
    public static boolean deleteDir(File file) {
        boolean success = true;
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File subFile : file.listFiles()) {
                    if (!success) {
                        return false;
                    }
                    success = success && deleteDir(subFile);
                }
            }
            if (success)
                success = success && file.delete();
        }
        return success;
    }
}