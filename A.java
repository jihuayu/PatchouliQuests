class A{

    boolean a(){
        if(B.b(this,1,2))return true;
        return false;
    }
}

class B{
    public static boolean  b(A o,int a, int b){
        return false;
    }
}