import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static java.nio.charset.Charset.forName;
import static java.nio.file.Files.readAllBytes;

/**
 * @author <a href="mailto:410486047@qq.com">Grey</a>
 * @date 2021/3/2
 * @since
 */
public class FileUtil {

    /**
     * 判断两个文件内容是否一样，注意：传入的是文件路径，而非文件内容！
     *
     * @param fileAPath 文件A的路径
     * @param fileBPath 文件B的路径
     * @param encoding  编码  如果不指定，默认UTF-8
     * @return 内容一致则返回true 不一致则返回false
     */
    public static boolean isSameContent(String fileAPath, String fileBPath, String encoding) throws IOException {
        if (null == encoding || "".equals(encoding)) {
            encoding = "UTF-8";
        }
        return content(fileAPath, encoding).equals(content(fileBPath, encoding));
    }

    /**
     * 以指定编码读取文件内容, 注意文件大小要限制一下，否则内存容易爆掉
     * 注意，文件大小不能超过2G
     *
     * @param filePath 文件路径
     * @param encoding 编码 如果不指定，默认UTF-8
     * @return
     */
    public static String content(String filePath, String encoding) throws IOException {
        return new String(readAllBytes(new File(filePath).toPath()), forName(encoding));
    }

    /**
     * 在rootFolder目录下，创建targetFolder名称的文件夹
     * eg：
     * rootFolder : c:/git
     * targetFolder: 12345
     * 执行后，则在git目录下创建了12345这个文件夹
     *
     * @param rootFolder   targetFolder所在的文件夹
     * @param targetFolder 需要创建的文件夹
     * @return 创建成功与否，成功为true，不成功为false
     */
    public static boolean createFolder(String rootFolder, String targetFolder) {
        File folder = new File(rootFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return new File(folder + File.separator + targetFolder).mkdir();
    }

    /**
     * 删除一个文件或者文件夹，如果文件夹里面有内容，则里面的内容也要递归删除掉
     *
     * @param dir
     * @return
     */
    public static boolean deleteFile(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteFile(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }


    /**
     * 将内容写入文件，返回文件路径
     *
     * @param content      写入内容
     * @param fileLocation 文件路径
     * @return
     */
    public static void contentToFile(String content, String fileLocation) {
        try (FileWriter fw = new FileWriter(fileLocation)) {
            //将str里面的内容读取到fw所指定的文件中
            fw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成一个空文件
     *
     * @return String[0] 表示文件夹绝对路径，String[1]表示空文件绝对路径
     */
    public static String[] generateEmptyFile() throws IOException {
        String folder = String.valueOf(System.nanoTime());
        File file = new File(folder);
        file.mkdir();
        File txtFile = new File(file.getAbsoluteFile() + File.separator + System.nanoTime() + ".txt");
        txtFile.createNewFile();
        System.out.println("generate an empty txt file  " + txtFile.getAbsolutePath());
        return new String[]{file.getAbsolutePath(), txtFile.getAbsolutePath()};
    }

    /**
     * 获取文件所在的文件夹
     *
     * @param filePath 文件的全路径
     * @return
     */
    public static String getFolder(String filePath) {
        return filePath.substring(0,filePath.lastIndexOf(File.separator));
    }


}
