package opt.tests;

import opt.data.AbstractParameters;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;

public class TestSnippet {

    @Ignore
    @Test
    public void test_snippet(){

        // create two hash sets
        HashSet <AbstractParameters> newset = new HashSet<>();

        // populate hash set
        AbstractParameters a = new AbstractParameters(100f,200f,300f);
        newset.add(a);

        // clone the hash set
        HashSet <AbstractParameters>  cloneset = new HashSet<>();
        newset.forEach(x->cloneset.add(x.clone()));
        AbstractParameters b = cloneset.iterator().next();

        System.out.println(newset==cloneset);
        System.out.println(newset.equals(cloneset));
        System.out.println(a==b);
        System.out.println(a.equals(b));

    }
}
