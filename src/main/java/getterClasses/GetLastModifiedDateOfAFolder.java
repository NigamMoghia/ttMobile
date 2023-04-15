package getterClasses;

import java.awt.*;
import java.io.File;

/**
 * Created by RGupta on 2/7/2018.
 */
public class GetLastModifiedDateOfAFolder {

    public static long getLatestModifiedDate(File dir) {
        File[] files = dir.listFiles();
        long latestDate = 0;
      try {
          for (File file : files) {
              long fileModifiedDate = file.isDirectory()
                      ? getLatestModifiedDate(file) : file.lastModified();
              if (fileModifiedDate > latestDate) {
                  latestDate = fileModifiedDate;

              }
          }
      }catch (NullPointerException e){
          System.out.println("Received null pointer exception for file ");
          return Math.max(latestDate, dir.lastModified());
      }
        return Math.max(latestDate, dir.lastModified());
    }
}
