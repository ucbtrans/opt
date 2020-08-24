package opt.tests;

import opt.Benchmarker;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestBenchmarkSpeed {

    float duration = 36000f;
    String description = "no output request";

    @Test
    public void run(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        LocalDateTime now = LocalDateTime.now();
        String logfile = String.format("/home/gomes/Desktop/test/benchmark/log_%s.txt",dtf.format(now));
        Benchmarker bmk = new Benchmarker(logfile,description,duration);
        bmk.run();
    }

}
