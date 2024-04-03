package demo;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

/**
 * @author ShiYou
 * @date 2024年03月04日 13:45
 * Description:
 */
public class RarTeat {

    public static void main(String[] args) {
        try {
            // 读取源文件
            File rarFile = new File("F:/2.rar");
            Archive archive = new Archive(new FileInputStream(rarFile));
            List<FileHeader> fileHeaders = archive.getFileHeaders();

            for (FileHeader fileHeader : fileHeaders) {
                // 源文件头部信息
                String fileName = fileHeader.getFileName();
                File file = new File("F:/1" + File.separator + fileName);

                if (fileHeader.isDirectory()) {
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                } else {
                    try {
                        if (!file.exists()) {
                            if (!file.getParentFile().exists()) {
                                file.getParentFile().mkdirs();
                            }
                            file.createNewFile();
                        }
                        FileOutputStream os = new FileOutputStream(file);
                        archive.extractFile(fileHeader, os);
                        os.close();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            archive.close();
        } catch (Exception e) {
            throw new RuntimeException("解压RAR文件失败（不支持RAR5类型）");
        }
    }
}
