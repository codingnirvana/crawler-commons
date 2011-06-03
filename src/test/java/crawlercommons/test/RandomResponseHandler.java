package crawlercommons.test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.http.handler.AbstractHttpHandler;

@SuppressWarnings("serial")
public class RandomResponseHandler extends AbstractHttpHandler {
    private static final long DEFAULT_DURATION = 1000L;
    
    private int _length;
    private long _duration;
    private Random _rand;
    
    /**
     * Create an HTTP response handler that sends random data back at a particular rate.
     * 
     * @param length - number of bytes to return
     * @param duration - duration for response, in milliseconds.
     */
    public RandomResponseHandler(int length, long duration) {
        _length = length;
        _duration = duration;
        _rand = new Random();
    }
    
    /**
     * Send back <length> bytes of random data in one second.
     * 
     * @param length - number of bytes to return
     */
    public RandomResponseHandler(int length) {
        this(length, DEFAULT_DURATION);
    }
    
    @Override
    public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
        response.setContentLength(_length);
        response.setContentType("text/html");
        response.setStatus(200);
        
        OutputStream os = response.getOutputStream();
        long startTime = System.currentTimeMillis();
        byte[] bytes = new byte[1];
        
        try {
            for (long i = 0; i < _length; i++) {
                _rand.nextBytes(bytes);
                os.write(bytes[0]);
                
                // Given i/_length as % of data written, we know that
                // this * duration is the target elapsed time. Figure out
                // how much to delay to make it so.
                long curTime = System.currentTimeMillis();
                long elapsedTime = curTime - startTime;
                long targetTime = (i * _duration) / _length;
                if (elapsedTime < targetTime) {
                    Thread.sleep(targetTime - elapsedTime);
                }
            }
        } catch (InterruptedException e) {
            throw new HttpException(500, "Response handler interrupted");
        }
    }
}
