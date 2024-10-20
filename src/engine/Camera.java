package engine;

public class Camera {
    public static float focalLength;
    public static Vector3f pos = new Vector3f(0, 0, -300);
    public static final float FOV = (float) Math.toRadians(60);
    public static Vector3f rot = new Vector3f(0, 0,0);



    public static void updateFocalLength(int screenHeight) {
        focalLength = (screenHeight / 2.0f) / (float) Math.tan(FOV / 2.0f);

    }

    public static void forward(float speed){
        pos.z += Math.cos(rot.y)*speed;
        pos.x += Math.sin(rot.y)*speed;
    }
}
