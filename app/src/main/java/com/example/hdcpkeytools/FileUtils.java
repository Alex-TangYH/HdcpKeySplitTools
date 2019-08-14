package com.example.hdcpkeytools;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileUtils {
    private static final String FOLDER_SEPARATOR = "/";
    public final static String HDCP_KEY_DIR_PATH = "HDCP_OR_KEY";
    private Context mContext;

    public FileUtils(Context context){
        mContext = context;
    }

    /**
     * 从预设的产测文件夹路径下查找指定文件
     *
     * @param fileName 查找的目标文件名
     * @return 查找的文件是否存在
     */
    public String searchFileInFactoryTestPath(String fileName) {
        GetMountPoint getMountPoint = new GetMountPoint(mContext);
        List<GetMountPoint.MountPoint> arrayList = getMountPoint.getMountedPoint();
        for (GetMountPoint.MountPoint mountPoint :
                arrayList) {
            if (mountPoint.isRemovable()) {
                StringBuilder sb = new StringBuilder(mountPoint.getFile().getPath());
                sb.append(FOLDER_SEPARATOR).append(HDCP_KEY_DIR_PATH).append(FOLDER_SEPARATOR).append(fileName);
                File targetFile = new File(String.valueOf(sb));
                if (targetFile.exists()) {
                    return targetFile.getPath();
                }
            }
        }
        return null;
    }


    /**
     * 用于获取挂载点的工具类
     */
    public static class GetMountPoint {
        private Context context;

        public GetMountPoint(Context context) {
            this.context = context;
        }

        /**
         * 核心操作-获取所有挂载点信息。
         */
        List<GetMountPoint.MountPoint> getMountPoint() {
            try {
                Class<StorageManager> class_StorageManager = StorageManager.class;
                Method method_getVolumeList = class_StorageManager.getMethod("getVolumeList");
                Method method_getVolumeState = class_StorageManager
                        .getMethod("getVolumeState", String.class);
                StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
                Class class_StorageVolume = Class.forName("android.os.storage.StorageVolume");
                Method method_isRemovable = class_StorageVolume.getMethod("isRemovable");
                Method method_getPath = class_StorageVolume.getMethod("getPath");
                Method method_getPathFile = class_StorageVolume.getMethod("getPathFile");
                Object[] objArray = (Object[]) method_getVolumeList.invoke(sm);

                // 所有挂载点File---附带是内置存储还是外置存储的标志
                List<MountPoint> result = new ArrayList<>();
                for (Object value : objArray) {
                    String path = (String) method_getPath.invoke(value);
                    File file = (File) method_getPathFile.invoke(value);
                    boolean isRemovable = (boolean) method_isRemovable.invoke(value);
                    boolean isMounted = (method_getVolumeState.invoke(sm, path))
                            .equals(Environment.MEDIA_MOUNTED);//获取挂载状态。
                    result.add(new MountPoint(file, isRemovable, isMounted));
                }
                return result;
            } catch (NoSuchMethodException | InvocationTargetException
                    | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 获取处于挂载状态的挂载点的信息
         */
        public List<MountPoint> getMountedPoint() {
            List<MountPoint> result = this.getMountPoint();
            Iterator<MountPoint> iterator = result.iterator();
            while(iterator.hasNext()){
                MountPoint value = iterator.next();
                if (!value.isMounted) {
                    iterator.remove();
                }
            }
            return result;
        }

        /**
         * 挂载点实体类
         */
        public class MountPoint {
            private File file;
            /**
             * 用于判断是否为内置存储卡，如果为true就是代表本挂载点可以移除，就是外置存储卡，否则反之
             */
            private boolean isRemovable;
            /**
             * 用于标示，这段代码执行的时候这个出处卡是否处于挂载状态，如果是为true，否则反之
             */
            private boolean isMounted;

            MountPoint(File file, boolean isRemovable, boolean isMounted) {
                this.file = file;
                this.isMounted = isMounted;
                this.isRemovable = isRemovable;
            }

            public File getFile() {
                return file;
            }

            public boolean isRemovable() {
                return isRemovable;
            }

            public boolean isMounted() {
                return isMounted;
            }

            @Override
            public String toString() {
                return "MountPoint{" +
                        "file=" + file +
                        ", isRemovable=" + isRemovable +
                        ", isMounted=" + isMounted +
                        '}';
            }
        }
    }

}
