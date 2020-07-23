package opt.tests;

import opt.data.*;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class TestSnippet {

    @Ignore
    @Test
    public void A(){
        try {
            Project project = ProjectFactory.load_project("/home/gomes/Desktop/testrc/aaa.opt",false);

            Scenario scenario = project.get_scenarios().iterator().next().get_scenario();

            // read road parameters and geometries from the links ...........
            Map<Long, FDparamsAndRoadGeoms> link2paramsandgeoms = new HashMap<>();
            Set<FDparams> unique_params = new HashSet<>();
            Set<RoadGeom> unique_geoms = new HashSet<>();
            for(AbstractLink link : scenario.links.values()) {

                FDparamsAndRoadGeoms x = link.get_fdparams_and_roadgeoms();
                link2paramsandgeoms.put(link.id,x);

                // store gp road parameters
                unique_params.add( x.gpparams ) ;
                if(x.roadGeom.mng_fdparams!=null)
                    unique_params.add( x.roadGeom.mng_fdparams );
                if(x.roadGeom.aux_fdparams!=null)
                    unique_params.add( x.roadGeom.aux_fdparams );

                if(x.roadGeom.notEmpty())
                    unique_geoms.add( x.roadGeom );
            }


            // map from params to its id ...................
            Map<FDparams,Long> param2id = new HashMap<>();
            long c = 0;
            for(FDparams p : unique_params)
                param2id.put(p,c++);

            // map from roadgeom to its id ...................
            Map<RoadGeom,Long> geom2id = new HashMap<>();
            c = 0;
            for(RoadGeom rg : unique_geoms)
                geom2id.put(rg,c++);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Ignore
    @Test
    public void B() {
        try {
            Project project = ProjectFactory.load_project("/home/gomes/Desktop/testrc/rctest.opt",false);
            ProjectFactory.save_project(project,"/home/gomes/Desktop/testrc/aaa.opt");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }



}
