package examples.base64;

import io.simplefuzzing.coverage.Executable;

import java.util.Arrays;

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;

public class Base64Test implements Executable<byte[]> {
    @Override
    public void execute(byte[] input) throws Throwable {
        if(!Arrays.equals(input, decodeBase64(encodeBase64String(input)))) {
            throw new AssertionError("Should be equal");
        }
    }
}
