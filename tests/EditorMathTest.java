import dev.buildcraftstudio.core.EditorMath;

public final class EditorMathTest {
    private static int passed;
    static void near(double got,double expected) {
        if(!Double.isFinite(got) || Math.abs(got-expected)>1e-7) throw new AssertionError(got+" != "+expected);
        passed++;
    }
    static void test(boolean success) { if(!success) throw new AssertionError("test "+(passed+1));passed++; }
    public static void main(String[] args) {
        double[] f=EditorMath.forward(0,0);near(f[0],0);near(f[1],0);near(f[2],1);
        f=EditorMath.forward(90,0);near(f[0],-1);near(f[1],0);near(f[2],0);
        f=EditorMath.forward(0,90);near(f[0],0);near(f[1],-1);near(f[2],0);
        double[] r=EditorMath.screenRay(0,0,0,0,16.0/9.0,70);
        near(r[0],0);near(r[1],0);near(r[2],1);
        r=EditorMath.screenRay(0,0,1,0,16.0/9.0,70);test(r[0]>0);test(r[2]>0);
        r=EditorMath.screenRay(0,0,-1,0,16.0/9.0,70);test(r[0]<0);
        r=EditorMath.screenRay(0,0,0,1,16.0/9.0,70);test(r[1]>0);
        near(EditorMath.planeDistance(10,-.5,0),20);
        test(Double.isNaN(EditorMath.planeDistance(10,0,0)));
        test(Double.isNaN(EditorMath.planeDistance(10,1,0)));
        near(EditorMath.clamp(100,0,30),30);
        near(EditorMath.clamp(-5,0,30),0);
        near(EditorMath.clamp(7,0,30),7);
        System.out.println("PASS: "+passed+" EditorMath assertions");
    }
}
