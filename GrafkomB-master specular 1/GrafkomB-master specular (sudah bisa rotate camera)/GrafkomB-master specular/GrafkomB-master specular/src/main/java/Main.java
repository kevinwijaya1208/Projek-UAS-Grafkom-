import Engine.*;
import Engine.Object;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBImage;
public class Main {
    private Window window =
        new Window(1000,1000,
                "Hello World");
    ArrayList<Object> objects
            = new ArrayList<>();

//    ArrayList<Lantern> objects = new ArrayList<>();

    ArrayList<Vector3f> daftarObjectsMin = new ArrayList<>();
    ArrayList<Vector3f> daftarObjectsMax = new ArrayList<>();

    Skybox skybox;
    ArrayList<Object> objectsRectangle
            = new ArrayList<>();

    ArrayList<Object> objectsPointsControl = new ArrayList<>();

    private static final float RUN_SPEED = 20;
    private static final float TURN_SPEED = 160;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;

    Camera camera = new Camera();
    Camera first = new Camera();
    Camera third = new Camera();
    Projection projection = new Projection(window.getWidth(),window.getHeight());
    private int textureID;
    boolean roleBobTPP = false;
    boolean roleBobFPP = false;
    boolean freeCamera = true;

    Player player;
    public void init(){
        window.init();
        GL.createCapabilities();
        camera.setPosition(0,1.0f,2.0f);
        camera.setRotation((float)Math.toRadians(30.0f),(float)Math.toRadians(0.0f));
        //code

        skybox = new Skybox(
                Arrays.asList(
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/skyShader.vert"
                                , GL_VERTEX_SHADER),
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/skyShader.frag"
                                , GL_FRAGMENT_SHADER)
                ),
                new ArrayList<>(
                        List.of(
                                new Vector3f(-0.5f,0.5f,0.0f),
                                new Vector3f(-0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,0.5f,0.0f)
                        )
                ),
                new Vector4f(1.0f,0.0f,1.0f,1.0f));

        objects.add(new Sphere(
                Arrays.asList(
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.vert"
                                , GL_VERTEX_SHADER),
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.frag"
                                , GL_FRAGMENT_SHADER)
                ),
                new ArrayList<>(
                        List.of(
                                new Vector3f(-0.5f,0.5f,0.0f),
                                new Vector3f(-0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,0.5f,0.0f)
                        )
                ),
                new Vector4f(1.0f,0.0f,1.0f,1.0f),
                Arrays.asList(0.0f,0.0f,0.0f),
                0.125f,
                0.125f,
                0.125f, "/models/grafkom1.obj","D:\\KULIAH\\semester 4\\grafkom\\GrafkomB-master specular\\GrafkomB-master\\src\\main\\resources\\textures\\texture1.png"
        ));
        objects.get(0).scaleObject(0.005f,0.005f,0.005f);

        objects.add(new Sphere(
                Arrays.asList(
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.vert"
                                , GL_VERTEX_SHADER),
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.frag"
                                , GL_FRAGMENT_SHADER)
                ),
                new ArrayList<>(
                        List.of(
                                new Vector3f(-0.5f,0.5f,0.0f),
                                new Vector3f(-0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,0.5f,0.0f)
                        )
                ),
                new Vector4f(1.0f,0.0f,1.0f,1.0f),
                Arrays.asList(0.0f,0.0f,0.0f),
                0.125f,
                0.125f,
                0.125f, "/models/rumah5.obj","D:\\KULIAH\\semester 4\\grafkom\\GrafkomB-master specular\\GrafkomB-master\\src\\main\\resources\\textures\\Farmhouse Texture.png"
        ));
        objects.get(1).rotateObject((float)Math.toRadians(215),0.0f,1.0f,0.0f);
        objects.get(1).scaleObject(0.01f,0.01f,0.01f);
        objects.get(1).translateObject(-0.6f,0.08f,-0.8f);

        objects.add(new Sphere(
                Arrays.asList(
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.vert"
                                , GL_VERTEX_SHADER),
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.frag"
                                , GL_FRAGMENT_SHADER)
                ),
                new ArrayList<>(
                        List.of(
                                new Vector3f(-0.5f,0.5f,0.0f),
                                new Vector3f(-0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,0.5f,0.0f)
                        )
                ),
                new Vector4f(1.0f,0.0f,1.0f,1.0f),
                Arrays.asList(0.0f,0.0f,0.0f),
                0.125f,
                0.125f,
                0.125f, "/models/logs1.obj","D:\\KULIAH\\semester 4\\grafkom\\GrafkomB-master specular 1\\GrafkomB-master specular (sudah bisa rotate camera)\\GrafkomB-master specular\\GrafkomB-master specular\\src\\main\\resources\\textures\\warnalog.png"
        ));
        objects.get(2).scaleObject(0.1f,0.1f,0.1f);
        objects.get(2).translateObject(-0.2f,0.1f,-0.7f);

        objects.add(new Sphere(
                Arrays.asList(
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.vert"
                                , GL_VERTEX_SHADER),
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.frag"
                                , GL_FRAGMENT_SHADER)
                ),
                new ArrayList<>(
                        List.of(
                                new Vector3f(-0.5f,0.5f,0.0f),
                                new Vector3f(-0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,0.5f,0.0f)
                        )
                ),
                new Vector4f(1.0f,0.0f,1.0f,1.0f),
                Arrays.asList(0.0f,0.0f,0.0f),
                0.125f,
                0.125f,
                0.125f, "/models/manusia1.obj","D:\\KULIAH\\semester 4\\grafkom\\GrafkomB-master specular 1\\GrafkomB-master specular (sudah bisa rotate camera)\\GrafkomB-master specular\\GrafkomB-master specular\\src\\main\\resources\\textures\\textureManusia3.PNG"
        ));
        objects.get(3).scaleObject(0.02f,0.02f,0.02f);
        objects.get(3).translateObject(-0.8f,0.08f,-0.7f);
        objects.get(3).rotateObject((float) Math.toRadians(105), 0.0f,1.0f, 0.0f);
        objects.get(3).translateObject(-0.6f, 0.0f, -0.8f);

        objects.add(new Sphere(
                Arrays.asList(
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.vert"
                                , GL_VERTEX_SHADER),
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.frag"
                                , GL_FRAGMENT_SHADER)
                ),
                new ArrayList<>(
                        List.of(
                                new Vector3f(-0.5f,0.5f,0.0f),
                                new Vector3f(-0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,0.5f,0.0f)
                        )
                ),
                new Vector4f(1.0f,0.0f,1.0f,1.0f),
                Arrays.asList(0.0f,0.0f,0.0f),
                0.125f,
                0.125f,
                0.125f, "/models/manusia1.obj","D:\\KULIAH\\semester 4\\grafkom\\GrafkomB-master specular 1\\GrafkomB-master specular (sudah bisa rotate camera)\\GrafkomB-master specular\\GrafkomB-master specular\\src\\main\\resources\\textures\\textureManusia3.PNG"
        ));
        objects.get(4).scaleObject(0.02f,0.02f,0.02f);
        objects.get(4).translateObject(-0.8f,0.08f,-0.7f);

        player = new Player(Arrays.asList(
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.vert"
                                , GL_VERTEX_SHADER),
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.frag"
                                , GL_FRAGMENT_SHADER)
                ),
                new ArrayList<>(
                        List.of(
                                new Vector3f(-0.5f,0.5f,0.0f),
                                new Vector3f(-0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,0.5f,0.0f)
                        )
                ),
                new Vector4f(1.0f,0.0f,1.0f,1.0f),
                Arrays.asList(0.0f,0.0f,0.0f),
                0.125f,
                0.125f,
                0.125f, "/models/bob.obj","D:\\KULIAH\\semester 4\\grafkom\\GrafkomB-master specular 1\\GrafkomB-master specular (sudah bisa rotate camera)\\GrafkomB-master specular\\GrafkomB-master specular\\src\\main\\resources\\textures\\rp_eric_rigged_001_dif.png"
        );
        player.rotateObject((float)Math.toRadians(180),0.0f,1.0f,0.0f);
        player.addRotation(new Vector3f(0, (float)180,0));
        player.scaleObject(0.04f,0.04f,0.04f);
        player.translateObject(0.45f,0.08f,0.9f);

        objects.add(new Sphere(
                Arrays.asList(
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.vert"
                                , GL_VERTEX_SHADER),
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.frag"
                                , GL_FRAGMENT_SHADER)
                ),
                new ArrayList<>(
                        List.of(
                                new Vector3f(-0.5f,0.5f,0.0f),
                                new Vector3f(-0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,0.5f,0.0f)
                        )
                ),
                new Vector4f(1.0f,0.0f,1.0f,1.0f),
                Arrays.asList(0.0f,0.0f,0.0f),
                0.125f,
                0.125f,
                0.125f, "/models/lantern.obj","D:\\KULIAH\\semester 4\\grafkom\\GrafkomB-master specular 1\\GrafkomB-master specular (sudah bisa rotate camera)\\GrafkomB-master specular\\GrafkomB-master specular\\src\\main\\resources\\textures\\lantern.png"

        ));
        objects.get(5).scaleObject(0.1f,0.1f,0.1f);
        objects.get(5).translateObject(-0.3f, 0.0f,1f);

        objects.add(new Sphere(
                Arrays.asList(
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.vert"
                                , GL_VERTEX_SHADER),
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.frag"
                                , GL_FRAGMENT_SHADER)
                ),
                new ArrayList<>(
                        List.of(
                                new Vector3f(-0.5f,0.5f,0.0f),
                                new Vector3f(-0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,0.5f,0.0f)
                        )
                ),
                new Vector4f(1.0f,0.0f,1.0f,1.0f),
                Arrays.asList(0.0f,0.0f,0.0f),
                0.125f,
                0.125f,
                0.125f, "/models/lantern.obj","D:\\KULIAH\\semester 4\\grafkom\\GrafkomB-master specular 1\\GrafkomB-master specular (sudah bisa rotate camera)\\GrafkomB-master specular\\GrafkomB-master specular\\src\\main\\resources\\textures\\lantern.png"));
        objects.get(6).scaleObject(0.1f,0.1f,0.1f);
        objects.get(6).translateObject(-0.4f, 0.0f,0.2f);

        objects.add(new Sphere(
                Arrays.asList(
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.vert"
                                , GL_VERTEX_SHADER),
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.frag"
                                , GL_FRAGMENT_SHADER)
                ),
                new ArrayList<>(
                        List.of(
                                new Vector3f(-0.5f,0.5f,0.0f),
                                new Vector3f(-0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,0.5f,0.0f)
                        )
                ),
                new Vector4f(1.0f,0.0f,1.0f,1.0f),
                Arrays.asList(0.0f,0.0f,0.0f),
                0.125f,
                0.125f,
                0.125f, "/models/lantern.obj","D:\\KULIAH\\semester 4\\grafkom\\GrafkomB-master specular 1\\GrafkomB-master specular (sudah bisa rotate camera)\\GrafkomB-master specular\\GrafkomB-master specular\\src\\main\\resources\\textures\\lantern.png"

        ));
        objects.get(7).scaleObject(0.1f,0.1f,0.1f);
        objects.get(7).translateObject(1f, 0.0f,-0.7f);

        objects.add(new Sphere(
                Arrays.asList(
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.vert"
                                , GL_VERTEX_SHADER),
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.frag"
                                , GL_FRAGMENT_SHADER)
                ),
                new ArrayList<>(
                        List.of(
                                new Vector3f(-0.5f,0.5f,0.0f),
                                new Vector3f(-0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,0.5f,0.0f)
                        )
                ),
                new Vector4f(1.0f,0.0f,1.0f,1.0f),
                Arrays.asList(0.0f,0.0f,0.0f),
                0.125f,
                0.125f,
                0.125f, "/models/lantern.obj","D:\\KULIAH\\semester 4\\grafkom\\GrafkomB-master specular 1\\GrafkomB-master specular (sudah bisa rotate camera)\\GrafkomB-master specular\\GrafkomB-master specular\\src\\main\\resources\\textures\\lantern.png"
        ));
        objects.get(8).scaleObject(0.1f,0.1f,0.1f);
        objects.get(8).translateObject(-0.4f, 0.0f,-0.8f);

        objects.add(new Sphere(
                Arrays.asList(
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.vert"
                                , GL_VERTEX_SHADER),
                        new ShaderProgram.ShaderModuleData(
                                "resources/shaders/scene.frag"
                                , GL_FRAGMENT_SHADER)
                ),
                new ArrayList<>(
                        List.of(
                                new Vector3f(-0.5f,0.5f,0.0f),
                                new Vector3f(-0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,-0.5f,0.0f),
                                new Vector3f(0.5f,0.5f,0.0f)
                        )
                ),
                new Vector4f(1.0f,0.0f,1.0f,1.0f),
                Arrays.asList(0.0f,0.0f,0.0f),
                0.125f,
                0.125f,
                0.125f, "/models/campfirejadi3.obj","D:\\KULIAH\\semester 4\\grafkom\\GrafkomB-master specular 1\\GrafkomB-master specular (sudah bisa rotate camera)\\GrafkomB-master specular\\GrafkomB-master specular\\src\\main\\resources\\textures\\InkedCampfire_MAT_BaseColor_00.png"
        ));
        objects.get(9).scaleObject(0.05f,0.1f,0.05f);
        objects.get(9).translateObject(0.8f, 0.0f,0.6f);

//        for (int i = 1; i < objects.size(); i++) {
//            daftarObjectsMin.add(new Vector3f(objects.get(i).minAABB.x, objects.get(i).minAABB.y, objects.get(i).minAABB.z));
//            daftarObjectsMax.add(new Vector3f(objects.get(i).maxAABB.x, objects.get(i).maxAABB.y, objects.get(i).maxAABB.z));
//            daftarCollision.add(new Collision(objects.get(i).minAABB, objects.get(i).maxAABB));
//        }
//        System.out.println(daftarObjectsMin);
//        System.out.println(daftarObjectsMax);
//        for (int i = 0;i<daftarCollision.size();i++){
//            System.out.println(daftarCollision.get(i).minAABB);
//            System.out.println(daftarCollision.get(i).maxAABB);
//        }
    }

    public void input(){
        if (window.isKeyPressed(GLFW_KEY_P)){
            for (int i = 1; i < objects.size(); i++) {
                System.out.println(i);
                System.out.println(objects.get(i).minAABB);
                System.out.println(objects.get(i).maxAABB);
                System.out.println(player.checkCollision(objects.get(i)));
            }
        }

        if (window.isKeyPressed(GLFW_KEY_2)){
            camera.setPosition(0,1.0f,2.0f);
            camera.setRotation((float)Math.toRadians(30.0f),(float)Math.toRadians(0.0f));
            freeCamera = true;
            roleBobFPP = false;
            roleBobTPP = false;
        }
        if (freeCamera) {
            if (window.isKeyPressed(GLFW_KEY_W)){
                float distance = 1.5f * 0.01f;
                float dx = (float) (distance * Math.sin(Math.toRadians(player.rotation.y)));
                float dz = (float) (distance * Math.cos(Math.toRadians(player.rotation.y)));
                player.translateObject(dx * 0.1f, 0f, dz * 0.1f);
                third.setPosition(third.getPosition().x + dx * 0.1f, third.getPosition().y +0f, third.getPosition().z + dz * 0.1f);
            }
            if (window.isKeyPressed(GLFW_KEY_S)){
                float distance = -1.5f * 0.01f;
                float dx = (float) (distance * Math.sin(Math.toRadians(player.rotation.y)));
                float dz = (float) (distance * Math.cos(Math.toRadians(player.rotation.y)));
                player.translateObject(dx * 0.1f, 0f, dz * 0.1f);
                third.setPosition(third.getPosition().x + dx * 0.1f, third.getPosition().y +0f, third.getPosition().z + dz * 0.1f);
            }
            if(window.isKeyPressed(GLFW_KEY_A)) {
                Vector3f tempCenterPoint1 =  player.updateCenterPoint();
                player.translateObject(-tempCenterPoint1.x, -tempCenterPoint1.y,-tempCenterPoint1.z);
                player.rotateObject((float)Math.toRadians(1), 0.0f, 1.0f, 0.0f);
                player.translateObject(tempCenterPoint1.x,tempCenterPoint1.y,tempCenterPoint1.z);
                Vector3f temp = new Vector3f(0, 1f,0);
                player.addRotation(temp);
                Vector3f target = player.updateCenterPoint();
                Vector3f sub = new Vector3f(third.getPosition().x - target.x, third.getPosition().y - target.y,
                        third.getPosition().z - target.z);

                third.addRotation(0f, (float) Math.toRadians(-1f));
                float xnow = (float) ((sub.x * Math.cos(Math.toRadians(1f))) + (sub.z * Math.sin(Math.toRadians(1f))));
                float ynow = sub.y;
                float znow = (float) (( -sub.x * Math.sin(Math.toRadians(1f))) + (sub.z * Math.cos(Math.toRadians(1f))));
                third.setPosition(xnow + target.x, ynow + target.y, znow + target.z);

                first.addRotation(0f, (float) Math.toRadians(-1f));
                float xnow2 = (float) ((sub.x * Math.cos(Math.toRadians(1f))) + (sub.z * Math.sin(Math.toRadians(1f))));
                float ynow2 = sub.y;
                float znow2 = (float) (( -sub.x * Math.sin(Math.toRadians(1f))) + (sub.z * Math.cos(Math.toRadians(1f))));
                first.setPosition(xnow2 + target.x, ynow2 + target.y, znow2 + target.z);
            }

            if(window.isKeyPressed(GLFW_KEY_D)) {
                Vector3f tempCenterPoint1 =  player.updateCenterPoint();
                player.translateObject(-tempCenterPoint1.x, -tempCenterPoint1.y,-tempCenterPoint1.z);
                player.rotateObject((float)Math.toRadians(1), 0.0f, -1.0f, 0.0f);
                player.translateObject(tempCenterPoint1.x,tempCenterPoint1.y,tempCenterPoint1.z);
                Vector3f temp = new Vector3f(0, -1f,0);
                player.addRotation(temp);
                Vector3f target = player.updateCenterPoint();
                Vector3f sub = new Vector3f(third.getPosition().x - target.x, third.getPosition().y - target.y,
                        third.getPosition().z - target.z);

                third.addRotation(0f, (float) Math.toRadians(1f));
                float xnow = (float) ((sub.x * Math.cos(Math.toRadians(-1f))) + (sub.z * Math.sin(Math.toRadians(-1f))));
                float ynow = sub.y;
                float znow = (float) (( -sub.x * Math.sin(Math.toRadians(-1f))) + (sub.z * Math.cos(Math.toRadians(-1f))));
                third.setPosition(xnow + target.x, ynow + target.y, znow + target.z);

                first.addRotation(0f, (float) Math.toRadians(1f));
                float xnow2 = (float) ((sub.x * Math.cos(Math.toRadians(-1f))) + (sub.z * Math.sin(Math.toRadians(-1f))));
                float ynow2 = sub.y;
                float znow2 = (float) (( -sub.x * Math.sin(Math.toRadians(-1f))) + (sub.z * Math.cos(Math.toRadians(-1f))));
                first.setPosition(xnow2 + target.x, ynow2 + target.y, znow2 + target.z);
            }
            float move = 0.004f;
            if (window.isKeyPressed(GLFW_KEY_I)) {
                camera.moveForward(move);
            }
            if (window.isKeyPressed(GLFW_KEY_K)) {
                camera.moveBackwards(move);
            }
            if (window.isKeyPressed(GLFW_KEY_J)) {
                camera.moveLeft(move);
            }
            if (window.isKeyPressed(GLFW_KEY_L)) {
                camera.moveRight(move);
            }
            if (window.getMouseInput().isLeftButtonPressed()) {
                Vector2f displayVector = window.getMouseInput().getDisplVec();
                camera.addRotation((float) Math.toRadians(displayVector.x * 0.1f), (float) Math.toRadians(displayVector.y * 0.1f));
            }
            if(window.getMouseInput().getScroll().y != 0){
                projection.setFOV(projection.getFOV() - (window.getMouseInput().getScroll().y * 0.01f));
                window.getMouseInput().setScroll(new Vector2f());
            }
            if (window.isKeyPressed(GLFW_KEY_UP)) {
                camera.moveUp(move);
            }
            if (window.isKeyPressed(GLFW_KEY_DOWN)) {
                camera.moveDown(move);
            }
        }
        if(window.isKeyPressed(GLFW_KEY_1)){
            Vector3f objek = player.updateCenterPoint();
            first.setPosition(objek.x, objek.y, objek.z);
            first.moveUp(0.07f);
            roleBobFPP = true;
            freeCamera = false;
            roleBobTPP = false;
        }
        if (roleBobFPP){
            if (window.isKeyPressed(GLFW_KEY_W)){
                boolean tabrakan = false;
                float distance = 1.5f * 0.03f;
                float dx = (float) (distance * Math.sin(Math.toRadians(player.rotation.y)));
                float dz = (float) (distance * Math.cos(Math.toRadians(player.rotation.y)));
//                for (int i = 1; i < objects.size(); i++) {
//                    System.out.println(i);
//                    System.out.println(objects.get(i).minAABB);
//                    System.out.println(objects.get(i).maxAABB);
//                    if (player.checkCollision(objects.get(i))){
//                        tabrakan = true;
//                    }
//                }
//                if (!tabrakan) {
                    player.translateObject(dx * 0.03f, 0f, dz * 0.03f);
                    first.setPosition(first.getPosition().x + dx * 0.03f, first.getPosition().y + 0f, first.getPosition().z + dz * 0.03f);
//                }
            }
            if (window.isKeyPressed(GLFW_KEY_S)){
                float distance = -1.5f * 0.03f;
                float dx = (float) (distance * Math.sin(Math.toRadians(player.rotation.y)));
                float dz = (float) (distance * Math.cos(Math.toRadians(player.rotation.y)));
                player.translateObject(dx * 0.03f, 0f, dz * 0.03f);
                first.setPosition(first.getPosition().x + dx * 0.03f, first.getPosition().y +0f, first.getPosition().z + dz * 0.03f);
            }
            if(window.isKeyPressed(GLFW_KEY_A)) {
                Vector3f tempCenterPoint1 =  player.updateCenterPoint();
                player.translateObject(-tempCenterPoint1.x, -tempCenterPoint1.y,-tempCenterPoint1.z);
                player.rotateObject((float)Math.toRadians(1), 0.0f, 1.0f, 0.0f);
                player.translateObject(tempCenterPoint1.x,tempCenterPoint1.y,tempCenterPoint1.z);
                Vector3f temp = new Vector3f(0, 1f,0);
                player.addRotation(temp);
                Vector3f target = player.updateCenterPoint();
                Vector3f sub = new Vector3f(first.getPosition().x - target.x, first.getPosition().y - target.y,
                        first.getPosition().z - target.z);

                first.addRotation(0f, (float) Math.toRadians(-1f));
                float xnow = (float) ((sub.x * Math.cos(Math.toRadians(1f))) + (sub.z * Math.sin(Math.toRadians(1f))));
                float ynow = sub.y;
                float znow = (float) (( -sub.x * Math.sin(Math.toRadians(1f))) + (sub.z * Math.cos(Math.toRadians(1f))));
                first.setPosition(xnow + target.x, ynow + target.y, znow + target.z);

                third.addRotation(0f, (float) Math.toRadians(-1f));
                float xnow2 = (float) ((sub.x * Math.cos(Math.toRadians(1f))) + (sub.z * Math.sin(Math.toRadians(1f))));
                float ynow2 = sub.y;
                float znow2 = (float) (( -sub.x * Math.sin(Math.toRadians(1f))) + (sub.z * Math.cos(Math.toRadians(1f))));
                third.setPosition(xnow2 + target.x, ynow2 + target.y, znow2 + target.z);
            }

            if(window.isKeyPressed(GLFW_KEY_D)) {
                Vector3f tempCenterPoint1 =  player.updateCenterPoint();
                player.translateObject(-tempCenterPoint1.x, -tempCenterPoint1.y,-tempCenterPoint1.z);
                player.rotateObject((float)Math.toRadians(1), 0.0f, -1.0f, 0.0f);
                player.translateObject(tempCenterPoint1.x,tempCenterPoint1.y,tempCenterPoint1.z);
                Vector3f temp = new Vector3f(0, -1f,0);
                player.addRotation(temp);
                Vector3f target = player.updateCenterPoint();
                Vector3f sub = new Vector3f(first.getPosition().x - target.x, first.getPosition().y - target.y,
                        first.getPosition().z - target.z);

                first.addRotation(0f, (float) Math.toRadians(1f));
                float xnow = (float) ((sub.x * Math.cos(Math.toRadians(-1f))) + (sub.z * Math.sin(Math.toRadians(-1f))));
                float ynow = sub.y;
                float znow = (float) (( -sub.x * Math.sin(Math.toRadians(-1f))) + (sub.z * Math.cos(Math.toRadians(-1f))));
                first.setPosition(xnow + target.x, ynow + target.y, znow + target.z);

                third.addRotation(0f, (float) Math.toRadians(1f));
                float xnow2 = (float) ((sub.x * Math.cos(Math.toRadians(-1f))) + (sub.z * Math.sin(Math.toRadians(-1f))));
                float ynow2 = sub.y;
                float znow2 = (float) (( -sub.x * Math.sin(Math.toRadians(-1f))) + (sub.z * Math.cos(Math.toRadians(-1f))));
                third.setPosition(xnow2 + target.x, ynow2 + target.y, znow2 + target.z);
            }
        }

        if(window.isKeyPressed(GLFW_KEY_3)){
            Vector3f objek = player.updateCenterPoint();
            third.setPosition(objek.x, objek.y, objek.z);
            third.moveUp(0.1f);
            third.moveBackwards(0.1f);
            roleBobTPP = true;
            freeCamera = false;
            roleBobFPP = false;
        }
        if (roleBobTPP) {
//            System.out.println(player.updateCenterPoint().x);
//            System.out.println(player.updateCenterPoint().y);
//            System.out.println(player.updateCenterPoint().z);
//            System.out.println(player.rotation.y);
            if (window.isKeyPressed(GLFW_KEY_W)){
                boolean tabrakan = false;
                float distance = 1.5f * 0.01f;
                float dx = (float) (distance * Math.sin(Math.toRadians(player.rotation.y)));
                float dz = (float) (distance * Math.cos(Math.toRadians(player.rotation.y)));
//                for (int i = 1; i < objects.size(); i++) {
//                    System.out.println(i);
//                    System.out.println(objects.get(i).minAABB);
//                    System.out.println(objects.get(i).maxAABB);
//                    if (player.checkCollision(objects.get(i))){
//                        tabrakan = true;
//                    }
//                }
//                if (!tabrakan){
                    player.translateObject(dx * 0.1f, 0f, dz * 0.1f);
                    third.setPosition(third.getPosition().x + dx * 0.1f, third.getPosition().y +0f, third.getPosition().z + dz * 0.1f);
//                }

            }
            if (window.isKeyPressed(GLFW_KEY_S)){
                boolean tabrakan = false;
                float distance = -1.5f * 0.01f;
                float dx = (float) (distance * Math.sin(Math.toRadians(player.rotation.y)));
                float dz = (float) (distance * Math.cos(Math.toRadians(player.rotation.y)));
                player.translateObject(dx * 0.1f, 0f, dz * 0.1f);
                third.setPosition(third.getPosition().x + dx * 0.1f, third.getPosition().y +0f, third.getPosition().z + dz * 0.1f);
            }
            if(window.isKeyPressed(GLFW_KEY_A)) {
                Vector3f tempCenterPoint1 =  player.updateCenterPoint();
                player.translateObject(-tempCenterPoint1.x, -tempCenterPoint1.y,-tempCenterPoint1.z);
                player.rotateObject((float)Math.toRadians(1), 0.0f, 1.0f, 0.0f);
                player.translateObject(tempCenterPoint1.x,tempCenterPoint1.y,tempCenterPoint1.z);
                Vector3f temp = new Vector3f(0, 1f,0);
                player.addRotation(temp);
                Vector3f target = player.updateCenterPoint();
                Vector3f sub = new Vector3f(third.getPosition().x - target.x, third.getPosition().y - target.y,
                        third.getPosition().z - target.z);

                third.addRotation(0f, (float) Math.toRadians(-1f));
                float xnow = (float) ((sub.x * Math.cos(Math.toRadians(1f))) + (sub.z * Math.sin(Math.toRadians(1f))));
                float ynow = sub.y;
                float znow = (float) (( -sub.x * Math.sin(Math.toRadians(1f))) + (sub.z * Math.cos(Math.toRadians(1f))));
                third.setPosition(xnow + target.x, ynow + target.y, znow + target.z);

                first.addRotation(0f, (float) Math.toRadians(-1f));
                float xnow2 = (float) ((sub.x * Math.cos(Math.toRadians(1f))) + (sub.z * Math.sin(Math.toRadians(1f))));
                float ynow2 = sub.y;
                float znow2 = (float) (( -sub.x * Math.sin(Math.toRadians(1f))) + (sub.z * Math.cos(Math.toRadians(1f))));
                first.setPosition(xnow2 + target.x, ynow2 + target.y, znow2 + target.z);
            }

            if(window.isKeyPressed(GLFW_KEY_D)) {
                Vector3f tempCenterPoint1 =  player.updateCenterPoint();
                player.translateObject(-tempCenterPoint1.x, -tempCenterPoint1.y,-tempCenterPoint1.z);
                player.rotateObject((float)Math.toRadians(1), 0.0f, -1.0f, 0.0f);
                player.translateObject(tempCenterPoint1.x,tempCenterPoint1.y,tempCenterPoint1.z);
                Vector3f temp = new Vector3f(0, -1f,0);
                player.addRotation(temp);
                Vector3f target = player.updateCenterPoint();
                Vector3f sub = new Vector3f(third.getPosition().x - target.x, third.getPosition().y - target.y,
                        third.getPosition().z - target.z);

                third.addRotation(0f, (float) Math.toRadians(1f));
                float xnow = (float) ((sub.x * Math.cos(Math.toRadians(-1f))) + (sub.z * Math.sin(Math.toRadians(-1f))));
                float ynow = sub.y;
                float znow = (float) (( -sub.x * Math.sin(Math.toRadians(-1f))) + (sub.z * Math.cos(Math.toRadians(-1f))));
                third.setPosition(xnow + target.x, ynow + target.y, znow + target.z);

                first.addRotation(0f, (float) Math.toRadians(1f));
                float xnow2 = (float) ((sub.x * Math.cos(Math.toRadians(-1f))) + (sub.z * Math.sin(Math.toRadians(-1f))));
                float ynow2 = sub.y;
                float znow2 = (float) (( -sub.x * Math.sin(Math.toRadians(-1f))) + (sub.z * Math.cos(Math.toRadians(-1f))));
                first.setPosition(xnow2 + target.x, ynow2 + target.y, znow2 + target.z);
            }
        }
    }


    public void loop(){
        while (window.isOpen()) {
            window.update();
            glClearColor(0.0f,
                    0.0f, 0.0f,
                    0.0f);
            GL.createCapabilities();
            input();
            //code
            //..
            for(Object object:objects){
                if (freeCamera) {
                    object.draw(camera, projection);
                } else if (roleBobFPP){
                    object.draw(first, projection);
                } else if (roleBobTPP){
                    object.draw(third,projection);
                }
            }

            skybox.draw(camera, projection);

            if (freeCamera) {
                player.draw(camera, projection);
            } else if (roleBobFPP){
                player.draw(first, projection);
            } else if (roleBobTPP){
                player.draw(third,projection);
            }
//            player.draw(camera, projection);

//            for (Lantern lantern:objects){
//                if (freeCamera) {
//                    lantern.draw(camera, projection);
//                } else if (roleBobFPP){
//                    lantern.draw(first, projection);
//                } else if (roleBobTPP){
//                    lantern.draw(third,projection);
//                }
//            }

            // Restore state
            glDisableVertexAttribArray(0);
            // Poll for window events.
            // The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }
    public void run() {

        init();
        loop();

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    public static void main(String[] args) {
        new Main().run();
    }
}