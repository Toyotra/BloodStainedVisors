/*
Jad Menkara
Clip.java
used to constrain a value between 2 other values and return the value



 */


package projectUtils;

//the Clip class
public class Clip {

    //static method used to return a clipped value
    public static float clipVal(float max, float min, float val){
        if(val > max){return max;}
        if(val < min){return min;}
        return val;
    }
}