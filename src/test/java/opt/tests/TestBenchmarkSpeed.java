package opt.tests;

import opt.Benchmarker;
import org.junit.Test;
import utils.OTMUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TestBenchmarkSpeed {

    float duration = 36000f;

    @Test
    public void run_test_suite(){

        ArrayList<Double> none = new ArrayList<>();
        ArrayList<Double> cell = new ArrayList<>();
        ArrayList<Double> lg = new ArrayList<>();

        int numreps = 10;

        for(int i=0;i<numreps;i++)
            none.add( run_once("none",false,false) );
        for(int i=0;i<numreps;i++)
            cell.add( run_once("cell",true,false) );
        for(int i=0;i<numreps;i++)
            lg.add( run_once("lg",false,true) );

        System.out.println(String.format("No output request: %f",OTMUtils.sum(none)/numreps));
        System.out.println(String.format("Cell output request: %f",OTMUtils.sum(cell)/numreps));
        System.out.println(String.format("Lg output request: %f",OTMUtils.sum(lg)/numreps));
    }

    private double run_once(String description,boolean celldata,boolean lgdata){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        LocalDateTime now = LocalDateTime.now();
        String logfile = String.format("/home/gomes/Desktop/test/benchmark/log_%s.txt",dtf.format(now));
        Benchmarker bmk = new Benchmarker(logfile,description,duration);
        bmk.run(celldata,lgdata);
        return bmk.runtime;
    }

}
