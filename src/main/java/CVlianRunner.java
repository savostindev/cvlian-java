
import com.cvlian.CVlian;
import org.opencv.core.Core;

public class CVlianRunner {
    static {
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        CVlian app = new CVlian("src/main/resources/haarcascades/haarcascade_frontalface_default.xml");
        app.run("<source images>");
    }
}
