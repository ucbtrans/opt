package opt.tests;

import opt.data.LinkParameters;
import org.junit.Test;

import java.util.HashSet;

public class TestSnippet {

    @Test
    public void test_snippet(){

        // create two hash sets
        HashSet <LinkParameters> newset = new HashSet<>();

        // populate hash set
        LinkParameters a = new LinkParameters(100,200,300);
        newset.add(a);

        // clone the hash set
        HashSet <LinkParameters>  cloneset = (HashSet)newset.clone();

        System.out.println("Hash set values: "+ newset);
        System.out.println("Clone Hash set values: "+ cloneset);

    }
}
