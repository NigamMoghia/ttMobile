package util;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by RGupta on 1/2/2018.
 */
public  class SyncPipe_CMDLine implements  Runnable {

    private final OutputStream ostrm_;
    private final InputStream istrm_;

    public SyncPipe_CMDLine(InputStream istrm, OutputStream ostrm) {
        istrm_ = istrm;
        ostrm_ = ostrm;
    }

    public void run() {
        try {
            final byte[] buffer = new byte[1024];
            for (int length = 0; (length = istrm_.read(buffer)) != -1;) {
                ostrm_.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
