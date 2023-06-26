package Engine;

import de.matthiasmann.twl.utils.PNGDecoder;
import jdk.jshell.execution.Util;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.*;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Skybox extends Object {
    int textID;
    int nightTextID;

    private static final float ROTATE_SPEED = 1f;
    private float rotation = 0;

    private static final float SIZE = 500f;

    private static final float[] VERTICES = {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE,  SIZE
    };
    private float time = 0;

    private static String[] TEXTURE_FILES = {"right", "left", "top", "bottom", "back", "front"};
    private static String[] TEXTURE_FILES2 = {"rightDoom", "leftDoom", "topDoom", "bottomDoom", "backDoom", "frontDoom"};
    private static String[] TEXTURE_FILES3 = {"kanan", "kiri", "atas", "bawah", "belakang", "depan"};
    private static String[] TEXTURE_FILES4 = {"kananHutan", "kiriHutan", "atasHutan", "bawahHutan", "belakangHutan", "depanHutan"};
    private static String[] TEXTURE_FILES5 = {"kanan_hutan_mlm", "kiri_hutan_mlm", "atas_hutan_mlm", "bawah_hutan_mlm", "belakang_hutan_mlm", "depan_hutan_mlM"};

    private static String[] NIGHT_TEXTURE_FILES = {"nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront"};

    public Skybox(List<ShaderModuleData> shaderModuleDataList, List<Vector3f> vertices, Vector4f color) {
        super(shaderModuleDataList, vertices, color);
        this.vertices = Utils.floatToList(VERTICES);
        setupVAOVBO();
        this.textID = loadCubeMap(TEXTURE_FILES4);
        this.nightTextID = loadCubeMap(TEXTURE_FILES5);
    }

    public Skybox(List<ShaderModuleData> shaderModuleDataList, List<Vector3f> vertices, List<Vector3f> verticesColor) {
        super(shaderModuleDataList, vertices, verticesColor);
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
                VERTICES,
                GL_STATIC_DRAW);
    }

    public void drawSetup(Camera camera, Projection projection){
        bind();

        // cahaya sm warna itu frag
        // pergerakan atau draw vert
        Matrix4f temp = new Matrix4f(camera.getViewMatrix());
        temp.m30(0);
        temp.m31(0);
        temp.m32(0);
        rotation += ROTATE_SPEED * 0.005f;
        temp.rotate((float) Math.toRadians(rotation), new Vector3f(0,1,0), temp);
        uniformsMap.setUniform(
                "viewMatrix", temp);
        uniformsMap.setUniform(
                "projectionMatrix", projection.getProjMatrix());
        uniformsMap.setUniform("cubeMap", 0);
        uniformsMap.setUniform("cubeMap2", 1);
        // Bind VBO
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer(0,
                3, GL_FLOAT,
                false,
                0, 0);
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
        bindTextures();
        glDrawArrays(GL_TRIANGLES, 0,
                VERTICES.length);
        for(Object child:childObject){
            child.draw(camera,projection);
        }
    }

    private void bindTextures(){
        time += 0.005f * 1000;
        time %= 24000;
        int texture1;
        int texture2;
        float blendFactor;
        if(time >= 0 && time < 5000){
            texture1 = nightTextID;
            texture2 = nightTextID;
            blendFactor = (time - 0)/(5000 - 0);
        }else if(time >= 5000 && time < 8000){
            texture1 = nightTextID;
            texture2 = textID;
            blendFactor = (time - 5000)/(8000 - 5000);
        }else if(time >= 8000 && time < 21000){
            texture1 = textID;
            texture2 = textID;
            blendFactor = (time - 8000)/(21000 - 8000);
        }else{
            texture1 = textID;
            texture2 = nightTextID;
            blendFactor = (time - 21000)/(24000 - 21000);
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
        uniformsMap.setUniform("blendFactor", blendFactor);
    }

    private TextureData decodeTextureFile(String fileName) {
        int width = 0;
        int height = 0;
        ByteBuffer buffer = null;
        try {
            FileInputStream in = new FileInputStream(fileName);
            PNGDecoder decoder = new PNGDecoder(in);
            width = decoder.getWidth();
            height = decoder.getHeight();
            buffer = ByteBuffer.allocateDirect(4 * width * height);
            decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Tried to load texture " + fileName + ", didn't work");
            System.exit(-1);
        }
        return new TextureData(buffer, width, height);
    }

    public int loadCubeMap(String[] textureFiles){
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

        for (int i=0;i<textureFiles.length;i++){
            TextureData data = decodeTextureFile("D:\\KULIAH\\semester 4\\grafkom\\GrafkomB-master specular 1\\GrafkomB-master specular (sudah bisa rotate camera)\\GrafkomB-master specular\\GrafkomB-master specular\\src\\main\\resources\\textures\\" + textureFiles[i] + ".png");
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X+i,0,GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0 ,GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
        }
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP,GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP,GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        return texID;
    }


}
