package opt.utils;

import java.util.HashMap;
import java.util.Set;

public class BijectiveMap<A,B> {

    private HashMap<A,B> AtoB = new HashMap<>();
    private HashMap<B,A> BtoA = new HashMap<>();

    public void put(A a,B b){
        if( !AtoB.containsKey(a) & !BtoA.containsKey(b) ){
            AtoB.put(a,b);
            BtoA.put(b,a);
        }
    }

    public B AtoB(A a){
        return AtoB.get(a);
    }

    public A BtoA(B b){
        return BtoA.get(b);
    }

    public void removeForFirst(A a){
        if(!AtoB.containsKey(a))
            return;
        B b = AtoB.get(a);
        AtoB.remove(a);
        BtoA.remove(b);
    }

    public void removeForSecond(B b){
        if(!BtoA.containsKey(b))
            return;
        A a = BtoA.get(b);
        AtoB.remove(a);
        BtoA.remove(b);
    }

    public Set<B> getBs(){
        return BtoA.keySet();
    }

    public Set<A> getAs(){
        return AtoB.keySet();
    }
}
