import nio.NioVsIo;
import nio.TestWriteByFileChannel;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws Exception {
        NioVsIo tester = new NioVsIo();

        if (args.length == 0) {
            tester.chooseMethod(args);
        } else {
            tester.chooseMethod(args);
        }

    }
}
