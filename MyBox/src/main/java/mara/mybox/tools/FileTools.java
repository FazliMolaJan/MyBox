/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mara.mybox.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import static mara.mybox.value.AppVaribles.logger;
import mara.mybox.value.CommonValues;
import mara.mybox.data.FileSynchronizeAttributes;
import mara.mybox.data.FileInformation;
import static mara.mybox.value.CommonValues.AppTempPath;
import static mara.mybox.value.CommonValues.AppDataRoot;

/**
 * @Author mara
 * @CreateDate 2018-6-2 11:01:45
 * @Description
 */
public class FileTools {

    public static long getFileCreateTime(final String filename) {
        try {
            FileTime t = Files.readAttributes(Paths.get(filename), BasicFileAttributes.class).creationTime();
            return t.toMillis();
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getFilePath(final String filename) {
        if (filename == null) {
            return null;
        }
        int pos = filename.lastIndexOf("/");
        if (pos < 0) {
            return "";
        }
        return filename.substring(0, pos);
    }

    public static String getFileName(final String filename) {
        if (filename == null) {
            return null;
        }
        String fname = filename;
        int pos = filename.lastIndexOf("/");
        if (pos >= 0) {
            fname = fname.substring(pos + 1);
        }
        return fname;
    }

    public static String getFilePrefix(final String filename) {
        String fname = getFileName(filename);
        if (fname == null) {
            return null;
        }
        int pos = fname.lastIndexOf(".");
        if (pos >= 0) {
            fname = fname.substring(0, pos);
        }
        return fname;
    }

    public static String getFileSuffix(final String filename) {
        if (filename == null) {
            return null;
        }
        String suffix;
        int pos = filename.lastIndexOf(".");
        if (pos >= 0 && filename.length() > pos) {
            suffix = filename.substring(pos + 1);
        } else {
            suffix = "";
        }
        return suffix;
    }

    public static String replaceFileSuffix(String filename, String newSuffix) {
        if (filename == null) {
            return null;
        }
        String fname = filename;
        int pos = filename.lastIndexOf(".");
        if (pos >= 0) {
            fname = fname.substring(0, pos) + "." + newSuffix;
        } else {
            fname += "." + newSuffix;
        }
        return fname;
    }

    public static String getTempFile(String filename) {
        if (filename == null) {
            return null;
        }
        String fname = filename;
        int pos = filename.lastIndexOf(".");
        if (pos >= 0) {
            fname = fname.substring(0, pos) + new Date().getTime() + "." + fname.substring(pos + 1);
        }
        return fname;
    }

    public static File getTempFile() {
        File file = new File(AppTempPath + File.separator + new Date().getTime() + ValueTools.getRandomInt(100));
        while (file.exists()) {
            file = new File(AppTempPath + File.separator + new Date().getTime() + ValueTools.getRandomInt(100));
        }
        return file;
    }

    public static boolean isPDF(String filename) {
        String suffix = getFileSuffix(filename);
        if (suffix == null) {
            return false;
        }
        return "PDF".equals(suffix.toUpperCase());
    }

    public static String insertFileName(String filename, String inStr) {
        if (filename == null) {
            return null;
        }
        if (inStr == null) {
            return filename;
        }
        int pos = filename.lastIndexOf(".");
        if (pos < 0) {
            return filename + inStr;
        }
        return filename.substring(0, pos) + inStr + "." + filename.substring(pos + 1);
    }

    public static File getHelpFile(String helpFile) {
        File file = new File(AppDataRoot + "/" + helpFile);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    public static String showFileSizeKB(long size) {
        long kb = (long) (size / 1024f + 0.5);
        if (kb == 0) {
            kb = 1;
        }
        String s = kb + "";
        String t = "";
        int count = 0;
        for (int i = s.length() - 1; i >= 0; i--, count++) {
            if (count > 0 && (count % 3 == 0)) {
                t = "," + t;
            }
            t = s.charAt(i) + t;
        }
        return t + " KB";
    }

    public static String showFileSize(long size) {
        double kb = size * 1.0f / 1024;
        if (kb < 1024) {
            return ValueTools.roundDouble3(kb) + " KB";
        } else {
            double mb = size * 1.0f / (1024 * 1024);
            if (mb < 1024) {
                return ValueTools.roundDouble3(mb) + " MB";
            } else {
                double gb = size * 1.0f / (1024 * 1024 * 1024);
                return ValueTools.roundDouble3(gb) + " GB";
            }
        }
    }

    public static String showFileSize2(long size) {
        double kb = size * 1.0f / 1024;
        String s = ValueTools.roundDouble3(kb) + " KB";
        if (kb < 1024) {
            return s;
        }

        double mb = size * 1.0f / (1024 * 1024);
        if (mb < 1024) {
            return s + " (" + ValueTools.roundDouble3(mb) + " MB)";
        } else {
            double gb = size * 1.0f / (1024 * 1024 * 1024);
            return s + " (" + ValueTools.roundDouble3(gb) + " GB)";
        }

    }

    public static class FileSortType {

        public static final int FileName = 0;
        public static final int ModifyTime = 1;
        public static final int CreateTime = 2;
        public static final int Size = 3;

    }

    public static void sortFiles(List<File> files, int sortTpye) {
        switch (sortTpye) {
            case FileSortType.FileName:
                Collections.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        return f1.getAbsolutePath().compareTo(f1.getAbsolutePath());
                    }
                });
                break;

            case FileSortType.ModifyTime:
                Collections.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        long t1 = f1.lastModified();
                        long t2 = f2.lastModified();
                        if (t1 == t2) {
                            return 0;
                        }
                        if (t1 > t2) {
                            return 1;
                        }
                        return -1;
                    }
                });
                break;

            case FileSortType.CreateTime:
                Collections.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        long t1 = FileTools.getFileCreateTime(f1.getAbsolutePath());
                        long t2 = FileTools.getFileCreateTime(f2.getAbsolutePath());
                        if (t1 == t2) {
                            return 0;
                        }
                        if (t1 > t2) {
                            return 1;
                        }
                        return -1;
                    }
                });
                break;

            case FileSortType.Size:
                Collections.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        long t1 = f1.length();
                        long t2 = f2.length();
                        if (t1 == t2) {
                            return 0;
                        }
                        if (t1 > t2) {
                            return 1;
                        }
                        return -1;
                    }
                });
                break;

        }
    }

    public static void sortFileInformations(List<FileInformation> files, int sortTpye) {
        switch (sortTpye) {
            case FileSortType.FileName:
                Collections.sort(files, new Comparator<FileInformation>() {
                    @Override
                    public int compare(FileInformation f1, FileInformation f2) {
                        return f1.getFile().getAbsolutePath().compareTo(f1.getFile().getAbsolutePath());
                    }
                });
                break;

            case FileSortType.ModifyTime:
                Collections.sort(files, new Comparator<FileInformation>() {
                    @Override
                    public int compare(FileInformation f1, FileInformation f2) {
                        long t1 = f1.getFile().lastModified();
                        long t2 = f2.getFile().lastModified();
                        if (t1 == t2) {
                            return 0;
                        }
                        if (t1 > t2) {
                            return 1;
                        }
                        return -1;
                    }
                });
                break;

            case FileSortType.CreateTime:
                Collections.sort(files, new Comparator<FileInformation>() {
                    @Override
                    public int compare(FileInformation f1, FileInformation f2) {
                        long t1 = FileTools.getFileCreateTime(f1.getFile().getAbsolutePath());
                        long t2 = FileTools.getFileCreateTime(f2.getFile().getAbsolutePath());
                        if (t1 == t2) {
                            return 0;
                        }
                        if (t1 > t2) {
                            return 1;
                        }
                        return -1;
                    }
                });
                break;

            case FileSortType.Size:
                Collections.sort(files, new Comparator<FileInformation>() {
                    @Override
                    public int compare(FileInformation f1, FileInformation f2) {
                        long t1 = f1.getFile().length();
                        long t2 = f2.getFile().length();
                        if (t1 == t2) {
                            return 0;
                        }
                        if (t1 > t2) {
                            return 1;
                        }
                        return -1;
                    }
                });
                break;

        }
    }

    public static boolean isSupportedImage(File file) {
        if (file == null || !file.isFile()) {
            return false;
        }
        String suffix = getFileSuffix(file.getName()).toLowerCase();
        return CommonValues.SupportedImages.contains(suffix);
    }

    public static boolean copyFile(File sourceFile, File targetFile,
            boolean isCanReplace, boolean isCopyAttrinutes) {
        try {
            if (sourceFile == null || !sourceFile.exists() || !sourceFile.isFile()) {
                return false;
            }
            if (!targetFile.exists()) {
                if (isCopyAttrinutes) {
                    Files.copy(Paths.get(sourceFile.getAbsolutePath()), Paths.get(targetFile.getAbsolutePath()),
                            StandardCopyOption.COPY_ATTRIBUTES);
                } else {
                    Files.copy(Paths.get(sourceFile.getAbsolutePath()), Paths.get(targetFile.getAbsolutePath()));
                }
            } else if (!isCanReplace || targetFile.isDirectory()) {
                return false;
            } else if (isCopyAttrinutes) {
                Files.copy(Paths.get(sourceFile.getAbsolutePath()), Paths.get(targetFile.getAbsolutePath()),
                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            } else {
                Files.copy(Paths.get(sourceFile.getAbsolutePath()), Paths.get(targetFile.getAbsolutePath()),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (Exception e) {
            logger.error(e.toString());
            return false;
        }
    }

    public static FileSynchronizeAttributes copyWholeDirectory(File sourcePath, File targetPath) {
        FileSynchronizeAttributes attr = new FileSynchronizeAttributes();
        copyWholeDirectory(sourcePath, targetPath, attr);
        return attr;
    }

    public static boolean copyWholeDirectory(File sourcePath, File targetPath, FileSynchronizeAttributes attr) {
        try {
            if (sourcePath == null || !sourcePath.exists() || !sourcePath.isDirectory()) {
                return false;
            }
            if (attr == null) {
                attr = new FileSynchronizeAttributes();
            }
            if (targetPath.exists()) {
                if (!deleteDir(targetPath)) {
                    return false;
                }
            }
            targetPath.mkdirs();
            File[] files = sourcePath.listFiles();
            for (File file : files) {
                File targetFile = new File(targetPath + File.separator + file.getName());
                if (file.isFile()) {
                    if (copyFile(file, targetFile, attr)) {
                        attr.setCopiedFilesNumber(attr.getCopiedFilesNumber() + 1);
                    } else if (!attr.isContinueWhenError()) {
                        return false;
                    }
                } else {
                    if (copyWholeDirectory(file, targetFile, attr)) {
                        attr.setCopiedDirectoriesNumber(attr.getCopiedDirectoriesNumber() + 1);
                    } else if (!attr.isContinueWhenError()) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.error(e.toString());
            return false;
        }
    }

    public static boolean copyFile(File sourceFile, File targetFile, FileSynchronizeAttributes attr) {
        if (attr == null) {
            attr = new FileSynchronizeAttributes();
        }
        return copyFile(sourceFile, targetFile, attr.isCanReplace(), attr.isCopyAttrinutes());
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                boolean success = deleteDir(file);
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static String getFontFile(String fontName) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("linux")) {
            return getFontFile("/usr/share/fonts/", fontName);

        } else if (os.contains("windows")) {
            return getFontFile("C:/Windows/Fonts/", fontName);

        } else if (os.contains("mac")) {
            String f = getFontFile("/Library/Fonts/", fontName);
            if (f != null) {
                return f;
            } else {
                return getFontFile("/System/Library/Fonts/", fontName);
            }
        }
        return null;
    }

    public static String getFontFile(String path, String fontName) {
        if (new File(path + fontName + ".ttf ").exists()) {
            return path + fontName + ".ttf ";
        } else if (new File(path + fontName.toLowerCase() + ".ttf ").exists()) {
            return path + fontName + ".ttf ";
        } else if (new File(path + fontName.toUpperCase() + ".ttf ").exists()) {
            return path + fontName + ".ttf ";
        } else {
            return null;
        }
    }

    public static List<File> splitFileByFilesNumber(File file,
            String filename, long filesNumber) {
        try {
            if (file == null || filesNumber <= 0) {
                return null;
            }
            long bytesNumber = file.length() / filesNumber;
            List<File> splittedFiles = new ArrayList<>();
            try (FileInputStream inputStream = new FileInputStream(file)) {
                String newFilename;
                int digit = (filesNumber + "").length();
                byte[] buf = new byte[(int) bytesNumber];
                int bufLen, fileIndex = 1, startIndex = 0, endIndex = 0;
                while ((fileIndex < filesNumber)
                        && (bufLen = inputStream.read(buf)) != -1) {
                    endIndex += bufLen;
                    newFilename = filename + "-cut-f" + ValueTools.fillLeftZero(fileIndex, digit)
                            + "-b" + (startIndex + 1) + "-b" + endIndex;
                    try (FileOutputStream outputStream = new FileOutputStream(newFilename)) {
                        if (bytesNumber > bufLen) {
                            buf = ByteTools.subBytes(buf, 0, bufLen);
                        }
                        outputStream.write(buf);
                    }
                    splittedFiles.add(new File(newFilename));
                    fileIndex++;
                    startIndex = endIndex;
                }
                buf = new byte[(int) (file.length() - endIndex)];
                bufLen = inputStream.read(buf);
                if (bufLen > 0) {
                    endIndex += bufLen;
                    newFilename = filename + "-cut-f" + ValueTools.fillLeftZero(fileIndex, digit)
                            + "-b" + (startIndex + 1) + "-b" + endIndex;
                    try (FileOutputStream outputStream = new FileOutputStream(newFilename)) {
                        outputStream.write(buf);
                    }
                    splittedFiles.add(new File(newFilename));
                }
            }
            return splittedFiles;
        } catch (Exception e) {
            logger.debug(e.toString());
            return null;
        }
    }

    public static List<File> splitFileByBytesNumber(File file,
            String filename, long bytesNumber) {
        try {
            if (file == null || bytesNumber <= 0) {
                return null;
            }
            List<File> splittedFiles = new ArrayList<>();
            try (FileInputStream inputStream = new FileInputStream(file)) {
                String newFilename;
                long fnumber = file.length() / bytesNumber;
                if (file.length() % bytesNumber > 0) {
                    fnumber++;
                }
                int digit = (fnumber + "").length();
                byte[] buf = new byte[(int) bytesNumber];
                int bufLen, fileIndex = 1, startIndex = 0, endIndex = 0;
                while ((bufLen = inputStream.read(buf)) != -1) {
                    endIndex += bufLen;
                    newFilename = filename + "-cut-f" + ValueTools.fillLeftZero(fileIndex, digit)
                            + "-b" + (startIndex + 1) + "-b" + endIndex;
                    try (FileOutputStream outputStream = new FileOutputStream(newFilename)) {
                        if (bytesNumber > bufLen) {
                            buf = ByteTools.subBytes(buf, 0, bufLen);
                        }
                        outputStream.write(buf);
                    }
                    splittedFiles.add(new File(newFilename));
                    fileIndex++;
                    startIndex = endIndex;
                }
            }
            return splittedFiles;
        } catch (Exception e) {
            logger.debug(e.toString());
            return null;
        }
    }

    public static List<File> splitFileByStartEndList(File file,
            String filename, List<Integer> startEndList) {
        try {
            if (file == null || startEndList == null
                    || startEndList.isEmpty() || startEndList.size() % 2 > 0) {
                return null;
            }
            List<File> splittedFiles = new ArrayList<>();
            for (int i = 0; i < startEndList.size(); i += 2) {
                File f = cutFile(file, filename, startEndList.get(i), startEndList.get(i + 1));
                if (f != null) {
                    splittedFiles.add(f);
                }
            }
            return splittedFiles;
        } catch (Exception e) {
            logger.debug(e.toString());
            return null;
        }
    }

    // 1-based start, that is: from (start - 1) to ( end - 1) actually
    public static File cutFile(File file,
            String filename, long startIndex, long endIndex) {
        try {
            if (file == null || startIndex < 1 || startIndex > endIndex) {
                return null;
            }
            File targetFile = FileTools.getTempFile();
            String newFilename = filename + "-cut-b" + startIndex + "-b" + endIndex;
            try (FileInputStream inputStream = new FileInputStream(file)) {
                if (startIndex > 1) {
                    inputStream.skip(startIndex - 1);
                }
                int cutLength = (int) (endIndex - startIndex + 1);
                byte[] buf = new byte[cutLength];
                int bufLen;
                bufLen = inputStream.read(buf);
                if (bufLen == -1) {
                    return null;
                }
                try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
                    if (cutLength > bufLen) {
                        buf = ByteTools.subBytes(buf, 0, bufLen);
                        newFilename = filename + "-cut-b" + startIndex + "-b" + bufLen;
                    }
                    outputStream.write(buf);
                }
            }
            targetFile.renameTo(new File(newFilename));
            return targetFile;
        } catch (Exception e) {
            logger.debug(e.toString());
            return null;
        }
    }

    public static boolean mergeFiles(List<File> files, File targetFile) {
        try {
            if (files == null || files.isEmpty() || targetFile == null) {
                return false;
            }
            int bufSize = 4096;
            try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
                byte[] buf = new byte[bufSize];
                int bufLen;
                for (File file : files) {
                    try (FileInputStream inputStream = new FileInputStream(file)) {
                        while ((bufLen = inputStream.read(buf)) != -1) {
                            if (bufSize > bufLen) {
                                buf = ByteTools.subBytes(buf, 0, bufLen);
                            }
                            outputStream.write(buf);
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.debug(e.toString());
            return false;
        }
    }

}
