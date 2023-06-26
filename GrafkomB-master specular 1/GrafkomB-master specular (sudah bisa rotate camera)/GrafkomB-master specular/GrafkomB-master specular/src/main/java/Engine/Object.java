package Engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBImage;


public class Object extends ShaderProgram{
    List<Vector3f> vertices;
    int vao;
    int vbo;
    Vector4f color;
    UniformsMap uniformsMap;

    List<Vector3f> verticesColor;
    int vboColor;

    public Matrix4f model;

    List<Object> childObject;

    public Vector3f minAABB;
    public Vector3f maxAABB;

    public List<Object> getChildObject() {
        return childObject;
    }

    public void setChildObject(List<Object> childObject) {
        this.childObject = childObject;
    }

    public Vector3f updateCenterPoint(){
        Vector3f centerTemp = new Vector3f();
        model.transformPosition(0.0f,0.0f,0.0f,centerTemp);
        return centerTemp;
    }
    public Object(List<ShaderModuleData> shaderModuleDataList,
                  List<Vector3f> vertices,
                  Vector4f color) {
        super(shaderModuleDataList);
        this.vertices = vertices;
//        setupVAOVBO();
        this.color = color;
        minAABB = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        maxAABB = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
        uniformsMap = new UniformsMap(getProgramId());
//        uniformsMap.createUniform(
//                "uni_color");
//        uniformsMap.createUniform(
//                "model");
//        uniformsMap.createUniform(
//                "view");
//        uniformsMap.createUniform(
//                "projection");
        model = new Matrix4f();
        childObject = new ArrayList<>();
    }
    public Object(List<ShaderModuleData> shaderModuleDataList,
                  List<Vector3f> vertices,
                  List<Vector3f> verticesColor) {
        super(shaderModuleDataList);
        this.vertices = vertices;
        this.verticesColor = verticesColor;
        setupVAOVBOWithVerticesColor();
    }
    public void setupVAOVBO(){
        //set vao
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        //set vbo
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        //mengirim vertices ke shader
        glBufferData(GL_ARRAY_BUFFER,
                Utils.listoFloat(vertices),
                GL_STATIC_DRAW);
    }
    public void setupVAOVBOWithVerticesColor(){
        //set vao
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        //set vbo
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        //mengirim vertices ke shader
        glBufferData(GL_ARRAY_BUFFER,
                Utils.listoFloat(vertices),
                GL_STATIC_DRAW);
        //set vboColor
        vboColor = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboColor);
        //mengirim vbocolor ke shader
        glBufferData(GL_ARRAY_BUFFER,
                Utils.listoFloat(verticesColor),
                GL_STATIC_DRAW);
    }
    public void drawSetup(Camera camera, Projection projection){
        bind();
//        uniformsMap.setUniform(
//                "uni_color", color);
        uniformsMap.setUniform(
                "model", model);
        uniformsMap.setUniform(
                "view", camera.getViewMatrix());
        uniformsMap.setUniform(
                "projection", projection.getProjMatrix());
//        uniformsMap.setUniform("textureSampler", 0);
        // Bind VBO
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer(0,
                3, GL_FLOAT,
                false,
                0, 0);
    }
    public void drawSetupWithVerticesColor(){
        bind();
        // Bind VBO
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer(0,
                3, GL_FLOAT,
                false,
                0, 0);
        // Bind VBOColor
        glEnableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, vboColor);
        glVertexAttribPointer(1,
                3, GL_FLOAT,
                false,
                0, 0);
    }

    private int loadTexture(String path) {
        int textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        // Set texture parameters
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        // Load texture from file
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer imageBuffer = STBImage.stbi_load(path, width, height, channels, 4);
        if (imageBuffer != null) {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width.get(0), height.get(0), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageBuffer);
            STBImage.stbi_image_free(imageBuffer);
        } else {
            System.err.println("Failed to load texture: " + path);
        }

        return textureID;
    }

    public void draw(Camera camera,Projection projection){
        drawSetup(camera,projection);
        // Draw the vertices
        glLineWidth(10);
        glPointSize(10);
        //GL_TRIANGLES
        //GL_LINE_LOOP
        //GL_LINE_STRIP
        //GL_LINES
        //GL_POINTS
        //GL_TRIANGLE_FAN
        int textureID = loadTexture("resources/FA_Pattern (4).png");
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        glDrawArrays(GL_TRIANGLES, 0,
                vertices.size());
        for(Object child:childObject){
            child.draw(camera,projection);
        }
    }
    public void drawWithVerticesColor(){
        drawSetupWithVerticesColor();
        // Draw the vertices
        glLineWidth(10);
        glPointSize(10);
        //GL_TRIANGLES
        //GL_LINE_LOOP
        //GL_LINE_STRIP
        //GL_LINES
        //GL_POINTS
        //GL_TRIANGLE_FAN
        glDrawArrays(GL_TRIANGLES, 0,
                vertices.size());
    }
    public void drawLine(){
//        drawSetup();
        // Draw the vertices
        glLineWidth(1);
        glPointSize(1);
        glDrawArrays(GL_LINE_STRIP, 0,
                vertices.size());
    }
    public void addVertices(Vector3f newVector){
        vertices.add(newVector);
        setupVAOVBO();
    }
    public void translateObject(Float offsetX,Float offsetY,Float offsetZ){
        model = new Matrix4f().translate(offsetX,offsetY,offsetZ).mul(new Matrix4f(model));
        for (Object child:childObject){
            child.translateObject(offsetX,offsetY,offsetZ);
        }
        minAABB = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        maxAABB = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
        for (Vector3f vertex : vertices) {
            Vector4f transformedVertex = new Vector4f(vertex, 1.0f);
            model.transform(transformedVertex);
            Vector3f temp = new Vector3f(transformedVertex.x, transformedVertex.y, transformedVertex.z);

            minAABB.min(temp);
            maxAABB.max(temp);
        }
    }
    public void rotateObject(Float degree, Float offsetX,Float offsetY,Float offsetZ){
        model = new Matrix4f().rotate(degree,offsetX,offsetY,offsetZ).mul(new Matrix4f(model));
        for (Object child:childObject){
            child.rotateObject(degree,offsetX,offsetY,offsetZ);
        }
        minAABB = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        maxAABB = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
        for (Vector3f vertex : vertices) {
            Vector4f transformedVertex = new Vector4f(vertex, 1.0f);
            model.transform(transformedVertex);
            Vector3f temp = new Vector3f(transformedVertex.x, transformedVertex.y, transformedVertex.z);

            minAABB.min(temp);
            maxAABB.max(temp);
        }
        System.out.println("Player Rotatae");
        System.out.println(minAABB);
        System.out.println(maxAABB);
    }
    public void scaleObject(Float x,Float y,Float z){
        model = new Matrix4f().scale(x,y,z).mul(new Matrix4f(model));
        for (Object child:childObject){
            child.scaleObject(x,y,z);
        }
        minAABB = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        maxAABB = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
        for (Vector3f vertex : vertices) {
            Vector4f transformedVertex = new Vector4f(vertex, 1.0f);
            model.transform(transformedVertex);
            Vector3f temp = new Vector3f(transformedVertex.x, transformedVertex.y, transformedVertex.z);

            minAABB.min(temp);
            maxAABB.max(temp);
        }
    }

    public boolean checkCollision(Object otherSphere) {
        if (maxAABB.x < otherSphere.minAABB.x || minAABB.x > otherSphere.maxAABB.x) {
            return false;
        }
        if (maxAABB.y < otherSphere.minAABB.y || minAABB.y > otherSphere.maxAABB.y) {
            return false;
        }
        if (maxAABB.z < otherSphere.minAABB.z || minAABB.z > otherSphere.maxAABB.z) {
            return false;
        }
        return true;

    }

//    public boolean checkCollision(ArrayList<Collision> otherSphere) {
//        boolean tabrakan = false;
//        for (int i = 0;i<otherSphere.size();i++) {
//            if (maxAABB.x < otherSphere.get(i).minAABB.x || minAABB.x > otherSphere.get(i).maxAABB.x) {
//                tabrakan = false;
//            }
//            if (maxAABB.y < otherSphere.get(i).minAABB.y || minAABB.y > otherSphere.get(i).maxAABB.y) {
//                tabrakan = false;
//            }
//            if (maxAABB.z < otherSphere.get(i).minAABB.z || minAABB.z > otherSphere.get(i).maxAABB.z) {
//                tabrakan = false;
//            }
//            tabrakan = true;
//
//        }
//        return tabrakan;
//    }

//    public boolean checkCollision(ArrayList<Vector3f> min, ArrayList<Vector3f> max) {
//        boolean collision = true;
//        for (int i = 0;i<min.size();i++){
//            if (maxAABB.x < min.get(i).x || minAABB.x > max.get(i).x) {
//                return false;
//            }
//            if (maxAABB.y < min.get(i).y || minAABB.y > max.get(i).y) {
//                return false;
//            }
//            if (maxAABB.z < min.get(i).z || minAABB.z > max.get(i).z) {
//                return false;
//            }
//            return true;
//        }
//        return collision;
//    }
}
