package opt.data;

import java.util.Map;
import java.util.Objects;

public class RoadGeom {

    public AddLanes mng_addlanes;
    public FDparams mng_fdparams;
    public AddLanes aux_addlanes;
    public FDparams aux_fdparams;

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        RoadGeom that = (RoadGeom) o;
//        return Objects.equals(mng_addlanes, that.mng_addlanes) &&
//                Objects.equals(mng_fdparams, that.mng_fdparams) &&
//                Objects.equals(aux_addlanes, that.aux_addlanes) &&
//                Objects.equals(aux_fdparams, that.aux_fdparams);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(mng_addlanes, mng_fdparams, aux_addlanes, aux_fdparams);
//    }

    public boolean notEmpty(){
        return mng_addlanes!=null || aux_addlanes!=null;
    }

    public jaxb.Roadgeom to_jaxb(Map<FDparams,Long> param2id){
        jaxb.Roadgeom x = new jaxb.Roadgeom();

        if(mng_addlanes!=null){
            jaxb.AddLanes addlane = new jaxb.AddLanes();
            addlane.setSide("in");
            addlane.setIsopen(mng_addlanes.isopen);
            addlane.setLanes(mng_addlanes.lanes);
            addlane.setRoadparam( param2id.get(mng_fdparams) );
            x.getAddLanes().add(addlane);
        }

        if(aux_addlanes!=null){
            jaxb.AddLanes addlane = new jaxb.AddLanes();
            addlane.setSide("out");
            addlane.setLanes(aux_addlanes.lanes);
            addlane.setRoadparam( param2id.get(aux_fdparams) );
            x.getAddLanes().add(addlane);
        }
        return x;
    }

}
