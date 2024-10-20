package engine;

public class Vector3f {
    public float x, y, z;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public Vector3f add(Vector3f other) {
        return new Vector3f(this.x + other.x, this.y + other.y, this.z + other.z);
    }
    public float length() {
        return (float)Math.sqrt(x*x + y*y +z*z);
    }
}
