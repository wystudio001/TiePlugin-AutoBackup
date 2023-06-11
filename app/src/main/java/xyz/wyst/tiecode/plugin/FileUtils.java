package xyz.wyst.tiecode.plugin;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

public class FileUtils {

    private static String[] UNITS = new String[] { "B", "K", "M", "G", "T", "P", "E", "Z", "Y" };

    /**
     * Copy or move file/directory
     */

    public static void moveTo(File from, File to) throws IOException {
        if (from.isDirectory()) {
            moveDir(from, to);
        } else {
            moveFile(from, to);
        }
    }

    public static void copyTo(File from, File to) throws IOException {
        if (from.isDirectory()) {
            copyDir(from, to);
        } else {
            copyFile(from, to);
        }
    }

    private static void moveDir(File oldFile, File newFile) throws IOException {
        copyDir(oldFile, newFile);
        FileUtils.deleteFile(oldFile);
    }

    private static void moveFile(File oldFile, File newFile) throws IOException {
        copyFile(oldFile, newFile);
        FileUtils.deleteFile(oldFile);
    }

    private static void copyDir(File srcFile, File dstFile) throws IOException {
        if (!dstFile.exists())
            dstFile.mkdirs();
        for (File file : dstFile.listFiles()) {
            if (file.isDirectory()) {
                copyDir(file, new File(dstFile.getAbsolutePath() + File.separator + file.getName()));
            } else {
                copyFile(file, new File(dstFile.getAbsolutePath() + File.separator + file.getName()));
            }
        }
    }

    private static void copyFile(File srcFile, File dstFile) throws IOException {
        if (!srcFile.exists()) {
            return;
        }
        if (!dstFile.exists()) {
            dstFile.getParentFile().mkdirs();
            if (srcFile.isFile())
                dstFile.createNewFile();
        }
        FileInputStream fileIns = null;
        FileOutputStream fileOuts = null;
        FileChannel source = null;
        FileChannel destination = null;
        try {
            fileIns = new FileInputStream(srcFile);
            fileOuts = new FileOutputStream(dstFile);
            source = fileIns.getChannel();
            destination = fileOuts.getChannel();
            destination.transferFrom(source, 0, source.size());
        } catch (IOException e) {
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
        }
    }

    /**
     * Get file prefix or suffix
     */

    public static String getFileSuffix(File f) {
        if (f == null) {
            return null;
        }
        String name = f.getName();
        int i = name.lastIndexOf('.');
        if (i == -1) {
            return null;
        } else {
            return name.substring(i + 1);
        }
    }

    public static String getFilePrefix(File f) {
        if (f == null) {
            return null;
        }
        String name = f.getName();
        int i = name.lastIndexOf('.');
        if (i == -1) {
            return name;
        } else {
            return name.substring(0, i);
        }
    }

    /**
     * Get the size of file in string with a unit
     *
     * @param file The file
     */
    public static String getFileSize(File file) {
        long size = file.length();
        int unit = 0;
        while (size > 1024) {
            size >>= 10;//2^10 = 1024
            unit++;
        }
        BigDecimal bd = new BigDecimal(file.length());
        BigDecimal divider = new BigDecimal(1024);
        for (int i = 0; i < unit; i++) {
            bd = bd.divide(divider, 2, BigDecimal.ROUND_HALF_UP);
        }
        return bd.toPlainString() + UNITS[unit];
    }

    /**
     * Get file digests...
     */

    public static String check(File file, String code) throws IOException {
        if (code.length() == 8) {
            if (code.equalsIgnoreCase(getCRC32(file))) {
                return "CRC32";
            }
        } else if (code.length() == 32) {
            if (code.equalsIgnoreCase(getMD5(file))) {
                return "MD5";
            }
        } else if (code.length() == 40) {
            if (code.equalsIgnoreCase(getSHA1(file))) {
                return "SHA1";
            }
        }
        return null;
    }

    public static String getSHA1(File file) throws IOException {
        return getDigest(file, "SHA1");
    }

    public static String getMD5(File file) throws IOException {
        return getDigest(file, "MD5");
    }

    private static String getDigest(File file, String algo) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance(algo);
            byte[] buffer = new byte[8192];
            FileInputStream fis = new FileInputStream(file);
            while (true) {
                int r = fis.read(buffer);
                if (r == -1) {
                    break;
                }
                md.update(buffer, 0, r);
            }
            fis.close();
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {

        }
        return null;
    }

    public static String getCRC32(File file) throws IOException {
        byte[] buffer = new byte[8192];
        CRC32 crc = new CRC32();
        FileInputStream fis = new FileInputStream(file);
        while (true) {
            int r = fis.read(buffer);
            if (r == -1) {
                break;
            }
            crc.update(buffer, 0, r);
        }
        fis.close();
        return Long.toHexString(crc.getValue());
    }

    /**
     * Read file...
     */

    public static String readString(File file) throws IOException {
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
    }

    public static String readString(File file, String encoding) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
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
    }

    public static byte[] readBytes(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[fis.available()];
        fis.read(bytes);
        fis.close();
        return bytes;
    }

    /**
     * Write file...
     */

    public static void write(File file, String content) throws IOException {
        if (!file.exists()) {
            createFile(file);
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        writer.write(content);
        writer.flush();
        writer.close();
    }

    public static void write(File file, byte[] bytes) throws IOException {
        write(file, bytes, 0, bytes.length);
    }

    public static void write(File file, byte[] bytes, int offset, int len) throws IOException {
        if (!file.exists()) {
            createFile(file);
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes, offset, len);
        fos.flush();
        fos.close();
    }

    /**
     * Append to the end of file
     */

    public static void append(File file, byte[] appendix) throws IOException {
        append(file, appendix, 0, appendix.length);
    }

    public static void append(File file, byte[] appendix, int off, int len) throws IOException {
        if (!file.exists()) {
            createFile(file);
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(raf.length());
        raf.write(appendix, off, len);
        raf.close();
    }

    public static void append(File file, String appendix) throws IOException {
        if (!file.exists()) {
            createFile(file);
        }
        FileWriter fw = new FileWriter(file, true);
        fw.write(appendix);
        fw.flush();
        fw.close();
    }

    /**
     * Search files by name
     * 
     * @param file The <strong>directory</strong> you want to search
     * @param match The name or regular expression.This is decided by useRegex
     * @param useRegex Whether the parameter 'match' is a regex
     * @param ignoreCase Whether we should ignore the case
     * @param searchSub Whether we should search sub directories
     * @return The unodered result of searching
     */
    public static List<File> searchByName(File file, String match, boolean useRegex, boolean ignoreCase,
            boolean searchSub) {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("not a directory");
        }
        ArrayList<File> result = new ArrayList<>();
        Pattern pattern = null;
        if (useRegex) {
            if (ignoreCase) {
                pattern = Pattern.compile(match, Pattern.CASE_INSENSITIVE);
            } else {
                pattern = Pattern.compile(match);
            }
        } else {
            if (ignoreCase) {
                match = match.toLowerCase();
            }
        }
        for (File f : file.listFiles()) {
            searchByNameInternal(result, f, match, ignoreCase, useRegex, pattern, searchSub);
        }
        return result;
    }

    /**
     * Thia is a helper method of searchByName() to search fies actually
     */
    private static void searchByNameInternal(List<File> result, File file, String match, boolean ignoreCase,
            boolean useRegex, Pattern pattern, boolean searchSub) {
        if (useRegex) {
            Matcher m = pattern.matcher(file.getName());
            if (m.find()) {
                result.add(file);
            }
        } else {
            if (ignoreCase) {
                if (file.getName().toLowerCase().contains(match)) {
                    result.add(file);
                }
            } else {
                if (file.getName().contains(match)) {
                    result.add(file);
                }
            }
        }
        if (file.isDirectory() && searchSub) {
            for (File sub : file.listFiles()) {
                searchByNameInternal(result, sub, match, ignoreCase, useRegex, pattern, searchSub);
            }
        }
    }

    /**
     * This method is to delete the given file (with its sub files)
     * And all the changes and actions depend on the given DeleteFileMonitor
     * We also send status to the monitor.
     *
     * @param file The file you want to delete
     * @param monitor The monitor you want to apply
     */
    public static void deleteFile(File file, DeleteFileMonitor monitor) {
        if (monitor == null) {
            boolean success = deleteFile(file);
            if (!success) {
                throw new RuntimeException("unable to delete");
            }
            return;
        }
        final List<File> files = new ArrayList<>();
        final List<File> canceledFiles = new ArrayList<>();
        monitor.onStartDetecting();
        if (monitor.onFileDetected(file)) {
            files.add(file);
            monitor.onFileCountUpdated(files.size());
        } else {
            monitor.onFinishDetecting(0);
        }
        int target = 0;
        int lastSendedCount = files.size();
        boolean newFileAdded = false;
        boolean haveFileCanceled = false;
        while (target < files.size()) {
            newFileAdded = false;
            haveFileCanceled = false;
            File thisFile = files.get(target);
            if (thisFile.isDirectory()) {
                for (File subFile : thisFile.listFiles()) {
                    if (monitor.onFileDetected(subFile)) {
                        files.add(subFile);
                        newFileAdded = true;
                    } else if (!haveFileCanceled) {
                        canceledFiles.add(subFile);
                        haveFileCanceled = true;
                    }
                }
            }
            if (newFileAdded && files.size() - lastSendedCount >= monitor.getMinUpdateSize()) {
                lastSendedCount = files.size();
                monitor.onFileCountUpdated(files.size());
            }
            target++;
        }
        monitor.onFileCountUpdated(files.size());
        target = 0;
        while (target < canceledFiles.size()) {
            File cancelFile = canceledFiles.get(target).getParentFile();
            label: while (true) {
                for (int i = 0; i < files.size(); i++) {
                    if (files.get(i).getAbsolutePath().equals(cancelFile.getAbsolutePath())) {
                        files.remove(i);
                        cancelFile = cancelFile.getParentFile();
                        continue label;
                    }
                }
                break;
            }
            target++;
        }
        canceledFiles.clear();
        monitor.onFinishDetecting(files.size());
        final int all = files.size();
        int successCount = 0;
        lastSendedCount = 0;
        while (files.size() > 0) {
            File fileToDelete = files.get(files.size() - 1);
            boolean result = fileToDelete.delete();
            if (!result) {
                if (!monitor.onDeleteFailed(fileToDelete)) {
                    break;
                }
            } else {
                successCount++;
                if (successCount - lastSendedCount > monitor.getMinUpdateSize()) {
                    monitor.onDeletedCountUpdated(successCount);
                    lastSendedCount = successCount;
                }
            }
            files.remove(files.size() - 1);
        }
        monitor.onDeletedCountUpdated(successCount);
        monitor.onDeleteFinished(successCount == all, successCount, all);
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
     * Create the given file
     * @param file The file to create
     * @return Whether we succeeded
     */
    public static boolean createFile(File file) {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create the given file as directory
     * @param file The file to create as a directory
     * @return Whether we succeeded
     */
    public static boolean createDirectory(File file) {
        return file.mkdirs();
    }

    /**
     * Get the name of file by string path
     * @param path Path of a file
     * @return file name
     */
    public static String getFileName(String path) {
        int pos = path.lastIndexOf(File.pathSeparator);
        if (pos == -1) {
            return path;
        } else {
            return path.substring(pos + 1, path.length());
        }
    }

    /**
     * Get the parent path of file by string path
     * @param path Path of a file parent
     * @return parent path
     */
    public static String getFilePath(String path) {
        int pos = path.lastIndexOf(File.pathSeparator);
        if (pos == -1) {
            return path;
        } else {
            return path.substring(0, pos + 1);
        }
    }

    public interface DeleteFileMonitor {

        /**
         * This method is to notify you that the process is going to start finding files
         * to be deleted.
         */
        void onStartDetecting();

        /**
         * This method can let you select whether the file detected should be deleted
         * Note that if you have returned 'false' to one of the files,it will affect its parent
         * directory's deleting process.So when a file is canceled,its parent directory will be canceled as well.
         * 
         * @param file The file detected by the process
         * @return Whether the given file should be deleted soon
         */
        boolean onFileDetected(File file);

        /**
         * When we are finding files,we will try to update 
         * the file count to be deleted and notify you.
         * If the changed count is under the size you return,it will not call onFileCountUpdated()
         *
         * @return the min changed size you want to receive
         */
        int getMinUpdateSize();

        /**
         * This method is to notify you that the count of files
         * to be deleted is updated greatly since last call to this method.
         *
         * @param fileCount The new count of files to be deleted
         */
        void onFileCountUpdated(int fileCount);

        /**
         * This method is to notify you that we have finished the process
         * of finding files to be deleted and we try to send the count of 
         * files to you.
         * The size of getMinUpdateSize() is ignored.
         * It will may be under the size last sended by onFileCountUpdated()
         * because the parent files of the canceled files are removed this time.
         *
         * @param fileCount The count of files to be deleted
         */
        void onFinishDetecting(int fileCount);

        /**
         * This method is alike onFileCountUpdated().
         * This method is to notify you that the count of deleted files has updated
         *
         * @param deletedCount The count of deleted files
         */
        void onDeletedCountUpdated(int deletedCount);

        /**
         * This method is to notify you that we are failed to.delete the given file
         * Return true to continue and return false to stop
         *
         * @return Whether to continue
         */
        boolean onDeleteFailed(File file);

        /**
         * This method is to notify you that we have finished all the work of this process
         *
         * @param allDeleted Whether we are successful
         * @param deletedCount the count of files we actually deleted
         * @param supposedCount the count of files we should delete
         */
        void onDeleteFinished(boolean allDeleted, int deletedCount, int supposedCount);

    }

}