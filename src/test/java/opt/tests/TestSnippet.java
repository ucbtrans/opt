package opt.tests;

import opt.data.LinkParameters;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;

public class TestSnippet {

    @Ignore
    @Test
    public void test_snippet(){

        // create two hash sets
        HashSet <LinkParameters> newset = new HashSet<>();

        // populate hash set
        LinkParameters a = new LinkParameters(100f,200f,300f);
        newset.add(a);

        // clone the hash set
        HashSet <LinkParameters>  cloneset = new HashSet<>();
        newset.forEach(x->cloneset.add(x.clone()));
        LinkParameters b = cloneset.iterator().next();

        System.out.println(newset==cloneset);
        System.out.println(newset.equals(cloneset));
        System.out.println(a==b);
        System.out.println(a.equals(b));

    }
}
