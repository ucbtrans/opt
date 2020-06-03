package opt.data;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Time2DSeries {
    public List<Float> time = new ArrayList<>();
    public List<List<Double>> values = new ArrayList<>();  // (xxx,time)

    public List<Double> sum1(){
        if(values.isEmpty())
            return null;
        int numtime = values.iterator().next().size();
        List<Double> X = new ArrayList<>();
        for(int k=0;k<numtime;k++) {
            final int finalK = k;
            X.add(values.stream().mapToDouble(z -> z.get(finalK)).sum());
        }
        return X;
    }

    public List<Double> sum2(){
        return values.stream()
                .map( x -> x.stream().mapToDouble(z->z).sum() )
                .collect(toList());
    }

    public Double sumsum(){
        return values.stream().mapToDouble(z->z.stream().mapToDouble(x->x).sum()).sum();
    }

}
