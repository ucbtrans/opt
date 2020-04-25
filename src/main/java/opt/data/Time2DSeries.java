package opt.data;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Time2DSeries {
    public List<Float> time = new ArrayList<>();
    public List<List<Double>> values = new ArrayList<>();

    public List<Double> aggregate_cells(){
        return values.stream()
                .map( x -> x.stream().mapToDouble(z->z).sum() )
                .collect(toList());
    }

}
