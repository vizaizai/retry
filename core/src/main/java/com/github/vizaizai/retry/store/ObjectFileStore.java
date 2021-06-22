package com.github.vizaizai.retry.store;

import com.github.vizaizai.logging.LoggerFactory;
import com.github.vizaizai.retry.exception.StoreException;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 对象文件存储
 * @author liaochongwei
 * @date 2021/1/6 15:46
 */
public class ObjectFileStore implements ObjectStore {
    private String storePath;
    private static final String DEFAULT = "/data/retry";
    private static final Logger log = LoggerFactory.getLogger(ObjectFileStore.class);
    public ObjectFileStore() {
        this.storePath = DEFAULT;
    }

    @Override
    public void save(Object source, String key) {
        try {
            File fileDir = new File(storePath);
            if (!fileDir.exists() && fileDir.mkdirs()) {
                log.info("'{}' created", storePath);
            }

            String fileName = fileDir.getPath() + File.separator + key;
            try (FileOutputStream fos = new FileOutputStream(fileName);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)){
                 oos.writeObject(source);
            }
        }catch (Exception e) {
            log.error("Failed to save object");
            throw new StoreException(e);
        }
    }

    @Override
    public List<Object> load() {
        List<File> files = this.listFiles();
        List<Object> objects = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            try( FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                 objects.add(ois.readObject());
            }catch (Exception e) {
                log.error("Failed to read object.", e);
                try {
                    Files.delete(file.toPath());
                } catch (IOException ioeE) {
                    log.error("Delete file error.", ioeE);
                }
            }
        }
        return objects;
    }

    @Override
    public void delete(String key) {
        Path path = Paths.get(storePath + File.separator + key);
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.error("Delete file error.", e);
        }
    }

    public List<String> getFileNames() {
        List<File> files = this.listFiles();
        List<String> names = new ArrayList<>();
        for (File file : files) {
            names.add(file.getName());
        }
        return names;
    }

    private List<File> listFiles() {
        File fileDir = new File(storePath);
        if (!fileDir.exists()) {
            return Collections.emptyList();
        }
        File[] files = fileDir.listFiles();
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(files);
    }

    public  void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    public String getStorePath() {
        return storePath;
    }
}
