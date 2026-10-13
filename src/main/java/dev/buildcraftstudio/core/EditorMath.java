package dev.buildcraftstudio.core;

/** Standalone camera math, tested with javac --release 8 without Forge. */
public final class EditorMath {
    private EditorMath() { }
    public static double clamp(double v, double low, double high) {
        return Math.max(low, Math.min(high, v));
    }
    public static double[] forward(double yawDeg, double pitchDeg) {
        double y = Math.toRadians(yawDeg), p = Math.toRadians(pitchDeg);
        return new double[] {-Math.sin(y) * Math.cos(p), -Math.sin(p), Math.cos(y) * Math.cos(p)};
    }
    public static double[] screenRay(double yawDeg, double pitchDeg, double nx, double ny,
                                     double aspect, double fovDegrees) {
        double[] f = forward(yawDeg, pitchDeg);
        double[] right = {Math.cos(Math.toRadians(yawDeg)), 0, Math.sin(Math.toRadians(yawDeg))};
        double[] up = cross(f, right);
        double t = Math.tan(Math.toRadians(fovDegrees) / 2.0);
        return normalize(new double[]{
            f[0] + right[0]*nx*t*aspect + up[0]*ny*t,
            f[1] + right[1]*nx*t*aspect + up[1]*ny*t,
            f[2] + right[2]*nx*t*aspect + up[2]*ny*t
        });
    }
    private static double[] cross(double[] a, double[] b) {
        return new double[]{a[1]*b[2]-a[2]*b[1], a[2]*b[0]-a[0]*b[2], a[0]*b[1]-a[1]*b[0]};
    }
    private static double[] normalize(double[] a) {
        double size = Math.sqrt(a[0]*a[0]+a[1]*a[1]+a[2]*a[2]);
        return new double[]{a[0]/size, a[1]/size, a[2]/size};
    }
    public static double planeDistance(double cameraY, double rayY, double planeY) {
        if (Math.abs(rayY) < 1e-9) return Double.NaN;
        double d = (planeY-cameraY)/rayY;
        return d >= 0 && d <= 128 ? d : Double.NaN;
    }
}
