package Engine;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class Player extends Sphere{
    public Vector3f minAABB;
    public Vector3f maxAABB;

    private static final float RUN_SPEED = 20;
    private static final float TURN_SPEED = 160;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    public Vector3f rotation = new Vector3f(0,0,0);
    public Player(List<ShaderModuleData> shaderModuleDataList, List<Vector3f> vertices, Vector4f color, List<Float> centerPoint, Float radiusX, Float radiusY, Float radiusZ, String filename, String texture) {
        super(shaderModuleDataList, vertices, color, centerPoint, radiusX, radiusY, radiusZ, filename, texture);
        minAABB = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        maxAABB = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
        for (Vector3f vertex : vertices) {
            Vector4f transformedVertex = new Vector4f(vertex, 1.0f);
            model.transform(transformedVertex);
            Vector3f temp = new Vector3f(transformedVertex.x, transformedVertex.y, transformedVertex.z);

            minAABB.min(temp);
            maxAABB.max(temp);
        }
        System.out.println(minAABB);
        System.out.println(maxAABB);
    }

    public void translateObject(Float offsetX,Float offsetY,Float offsetZ){
        super.translateObject(offsetX, offsetY, offsetZ);
        minAABB = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        maxAABB = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
        for (Vector3f vertex : vertices) {
            Vector4f transformedVertex = new Vector4f(vertex, 1.0f);
            model.transform(transformedVertex);
            Vector3f temp = new Vector3f(transformedVertex.x, transformedVertex.y, transformedVertex.z);

            minAABB.min(temp);
            maxAABB.max(temp);
        }
        System.out.println("Player");
        System.out.println(minAABB);
        System.out.println(maxAABB);
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(float currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public float getCurrentTurnSpeed() {
        return currentTurnSpeed;
    }

    public void setCurrentTurnSpeed(float currentTurnSpeed) {
        this.currentTurnSpeed = currentTurnSpeed;
    }

    public void addRotation(Vector3f rotate){
        rotation.add(rotate);
//        System.out.println(rotation.y);
    }

    public void move(){
        Vector3f tempCenterPoint1 =  updateCenterPoint();
        translateObject(-tempCenterPoint1.x, -tempCenterPoint1.y,-tempCenterPoint1.z);
        rotateObject((float)Math.toRadians(currentTurnSpeed * Window.getFrameTimeSeconds()), 0.0f, 1.0f, 0.0f);
        translateObject(tempCenterPoint1.x,tempCenterPoint1.y,tempCenterPoint1.z);
        Vector3f temp = new Vector3f(0, currentTurnSpeed * Window.getFrameTimeSeconds(),0);
        addRotation(temp);
        float distance = currentSpeed * Window.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(Math.toRadians(rotation.y)));
        float dz = (float) (distance * Math.cos(Math.toRadians(rotation.y)));
        translateObject(dx * 0.01f, 0f, dz * 0.01f);
    }

    public void loadTexture(String filename) throws Exception {
        textures = new ArrayList<>();
        int width, height;
        ByteBuffer buffer;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(filename, w, h, c, 4);
            if (buffer == null)
                throw new Exception("Image File " + filename + " not load " + STBImage.stbi_failure_reason());

            width = w.get();
            height = h.get();
        }

        int id = GL11.glGenTextures();
//        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
        STBImage.stbi_image_free(buffer);
        textID = id;
//        System.out.println(id);
//        return id;
    }

    public void setIbo(){
        ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, Utils.listoInt(index), GL_STATIC_DRAW);
    }

    @Override
    public void draw(Camera camera, Projection projection){
        drawSetup(camera, projection);
        // Bind texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textID);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);

        // Bind IBO dan draw
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,ibo);
        glDrawElements(GL_TRIANGLES, index.size(), GL_UNSIGNED_INT, 0);
        // kalo mau lingkaran tanpa fill ganti ke GL_LINES
    }

    public void createBox(){
        vertices.clear();
        Vector3f temp = new Vector3f();
        ArrayList<Vector3f> tempVertices = new ArrayList<>();
        //Titik 1 kiri atas belakang
        temp.x = centerPoint.get(0) - radiusX / 2;
        temp.y = centerPoint.get(1) + radiusY / 2;
        temp.z = centerPoint.get(2) - radiusZ / 2;
        tempVertices.add(temp);
        temp = new Vector3f();
        //Titik 2 kiri bawah belakang
        temp.x = centerPoint.get(0) - radiusX / 2;
        temp.y = centerPoint.get(1) - radiusY / 2;
        temp.z = centerPoint.get(2) - radiusZ / 2;
        tempVertices.add(temp);
        temp = new Vector3f();
        //Titik 3 kanan bawah belakang
        temp.x = centerPoint.get(0) + radiusX / 2;
        temp.y = centerPoint.get(1) - radiusY / 2;
        temp.z = centerPoint.get(2) - radiusZ / 2;
        tempVertices.add(temp);
        temp = new Vector3f();
        //Titik 4 kanan atas belakang
        temp.x = centerPoint.get(0) + radiusX / 2;
        temp.y = centerPoint.get(1) + radiusY / 2;
        temp.z = centerPoint.get(2) - radiusZ / 2;
        tempVertices.add(temp);
        temp = new Vector3f();
        //Titik 5 kiri atas depan
        temp.x = centerPoint.get(0) - radiusX / 2;
        temp.y = centerPoint.get(1) + radiusY / 2;
        temp.z = centerPoint.get(2) + radiusZ / 2;
        tempVertices.add(temp);
        temp = new Vector3f();
        //Titik 6 kiri bawah depan
        temp.x = centerPoint.get(0) - radiusX / 2;
        temp.y = centerPoint.get(1) - radiusY / 2;
        temp.z = centerPoint.get(2) + radiusZ / 2;
        tempVertices.add(temp);
        temp = new Vector3f();
        //Titik 7 kanan bawah depan
        temp.x = centerPoint.get(0) + radiusX / 2;
        temp.y = centerPoint.get(1) - radiusY / 2;
        temp.z = centerPoint.get(2) + radiusZ / 2;
        tempVertices.add(temp);
        temp = new Vector3f();
        //Titik 8 kanan atas depan
        temp.x = centerPoint.get(0) + radiusX / 2;
        temp.y = centerPoint.get(1) + radiusY / 2;
        temp.z = centerPoint.get(2) + radiusZ / 2;
        tempVertices.add(temp);
        temp = new Vector3f();

        //kotak belakang
        vertices.add(tempVertices.get(0));
        vertices.add(tempVertices.get(1));
        vertices.add(tempVertices.get(2));

        vertices.add(tempVertices.get(2));
        vertices.add(tempVertices.get(3));
        vertices.add(tempVertices.get(0));
        //kotak depan
        vertices.add(tempVertices.get(4));
        vertices.add(tempVertices.get(5));
        vertices.add(tempVertices.get(6));

        vertices.add(tempVertices.get(6));
        vertices.add(tempVertices.get(7));
        vertices.add(tempVertices.get(4));
        //kotak samping kiri
        vertices.add(tempVertices.get(0));
        vertices.add(tempVertices.get(1));
        vertices.add(tempVertices.get(4));

        vertices.add(tempVertices.get(1));
        vertices.add(tempVertices.get(5));
        vertices.add(tempVertices.get(4));
        //kotak samping kanan
        vertices.add(tempVertices.get(7));
        vertices.add(tempVertices.get(6));
        vertices.add(tempVertices.get(2));

        vertices.add(tempVertices.get(2));
        vertices.add(tempVertices.get(3));
        vertices.add(tempVertices.get(7));
        //kotak bawah
        vertices.add(tempVertices.get(1));
        vertices.add(tempVertices.get(5));
        vertices.add(tempVertices.get(6));

        vertices.add(tempVertices.get(6));
        vertices.add(tempVertices.get(2));
        vertices.add(tempVertices.get(1));
        //kotak atas
        vertices.add(tempVertices.get(0));
        vertices.add(tempVertices.get(4));
        vertices.add(tempVertices.get(7));

        vertices.add(tempVertices.get(7));
        vertices.add(tempVertices.get(0));
        vertices.add(tempVertices.get(3));

        normal = new ArrayList<>(Arrays.asList(
                //belakang
                new Vector3f(0.0f,0.0f,-1.0f),
                new Vector3f(0.0f,0.0f,-1.0f),
                new Vector3f(0.0f,0.0f,-1.0f),
                new Vector3f(0.0f,0.0f,-1.0f),
                new Vector3f(0.0f,0.0f,-1.0f),
                new Vector3f(0.0f,0.0f,-1.0f),
                //depan
                new Vector3f(0.0f,0.0f,1.0f),
                new Vector3f(0.0f,0.0f,1.0f),
                new Vector3f(0.0f,0.0f,1.0f),
                new Vector3f(0.0f,0.0f,1.0f),
                new Vector3f(0.0f,0.0f,1.0f),
                new Vector3f(0.0f,0.0f,1.0f),
                //kiri
                new Vector3f(-1.0f,0.0f,0.0f),
                new Vector3f(-1.0f,0.0f,0.0f),
                new Vector3f(-1.0f,0.0f,0.0f),
                new Vector3f(-1.0f,0.0f,0.0f),
                new Vector3f(-1.0f,0.0f,0.0f),
                new Vector3f(-1.0f,0.0f,0.0f),
                //kanan
                new Vector3f(1.0f,0.0f,0.0f),
                new Vector3f(1.0f,0.0f,0.0f),
                new Vector3f(1.0f,0.0f,0.0f),
                new Vector3f(1.0f,0.0f,0.0f),
                new Vector3f(1.0f,0.0f,0.0f),
                new Vector3f(1.0f,0.0f,0.0f),
                //bawah
                new Vector3f(0.0f,-1.0f,0.0f),
                new Vector3f(0.0f,-1.0f,0.0f),
                new Vector3f(0.0f,-1.0f,0.0f),
                new Vector3f(0.0f,-1.0f,0.0f),
                new Vector3f(0.0f,-1.0f,0.0f),
                new Vector3f(0.0f,-1.0f,0.0f),
                //atas
                new Vector3f(0.0f,1.0f,0.0f),
                new Vector3f(0.0f,1.0f,0.0f),
                new Vector3f(0.0f,1.0f,0.0f),
                new Vector3f(0.0f,1.0f,0.0f),
                new Vector3f(0.0f,1.0f,0.0f),
                new Vector3f(0.0f,1.0f,0.0f)
        ));
    }
    public void createSphere(){
        vertices.clear();
        ArrayList<Vector3f> temp = new ArrayList<>();
        int stackCount = 18, sectorCount = 36;
        float x,y,z,xy,nx,ny,nz;
        float sectorStep = (float)(2* Math.PI )/ sectorCount; //sector count
        float stackStep = (float)Math.PI / stackCount; // stack count
        float sectorAngle, stackAngle;

        //titik persegi
        for(int i=0;i<=stackCount;i++){
            stackAngle = (float)Math.PI/2 - i * stackStep;
            xy = (float) (0.5f * Math.cos(stackAngle));
            z = (float) (0.5f * Math.sin(stackAngle));
            for(int j=0;j<sectorCount;j++){
                sectorAngle = j * sectorStep;
                x = (float) (xy * Math.cos(sectorAngle));
                y = (float) (xy * Math.sin(sectorAngle));
                temp.add(new Vector3f(x,y,z));
            }
        }
        vertices = temp;

        int k1, k2;
        ArrayList<Integer> temp_indices = new ArrayList<>();
        for(int i=0;i<stackCount;i++){
            k1 = i * (sectorCount + 1);
            k2 = k1 + sectorCount + 1;

            for(int j=0;j<sectorCount;++j, ++k1, ++k2){
                if(i != 0){
                    temp_indices.add(k1);
                    temp_indices.add(k2);
                    temp_indices.add(k1+1);
                }
                if(i!=(18-1)){
                    temp_indices.add(k1+1);
                    temp_indices.add(k2);
                    temp_indices.add(k2+1);
                }
            }
        }

        this.index = temp_indices;
        ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,
                ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,
                Utils.listoInt(index), GL_STATIC_DRAW);

    }
    public void setupVAOVBO(){
        super.setupVAOVBO();

        //nbo
        nbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, nbo);
        glBufferData(GL_ARRAY_BUFFER,
                Utils.listoFloat(normal),
                GL_STATIC_DRAW);
        tbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, tbo);
        glBufferData(GL_ARRAY_BUFFER,
                Utils.listoFloat2(textures),
                GL_STATIC_DRAW);
//        uniformsMap.createUniform("lightColor");
//        uniformsMap.createUniform("lightPos");
    }
    public void drawSetup(Camera camera, Projection projection){
        super.drawSetup(camera,projection);

        // Bind NBO
        glEnableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, nbo);
        glVertexAttribPointer(1,
                3, GL_FLOAT,
                false,
                0, 0);

        // Bind Texture
        glEnableVertexAttribArray(2);
        glBindBuffer(GL_ARRAY_BUFFER, tbo);
        glVertexAttribPointer(2,
                2, GL_FLOAT,
                false,
                0, 0);

//        uniformsMap.setUniform("lightColor",
//                new Vector3f(1.0f,1.0f,0.0f));
//        uniformsMap.setUniform("lightPos",
//                new Vector3f(1.0f,30.0f,4.0f));
//        uniformsMap.setUniform("viewPos",camera.getPosition());

//        Vector3f ambientObject = new Vector3f(Player.ambientObj);
//        Vector3f diffuseObject = new Vector3f(Player.diffuseObj);
//        Vector3f specularObject = new Vector3f(Player.specularObj);

        // directional light (matahari paling besarnya kek sumber cahaya utama)
        uniformsMap.setUniform("dirLight.direction", new Vector3f(-0.2f,-1.0f,-0.3f));
//        uniformsMap.setUniform("dirLight.ambient", new Vector3f(0.05f,0.05f,0.05f));
//        uniformsMap.setUniform("dirLight.diffuse", new Vector3f(0.4f,0.4f,0.4f));
//        uniformsMap.setUniform("dirLight.specular", new Vector3f(0.5f,0.5f,0.5f));
        uniformsMap.setUniform("dirLight.ambient",new Vector3f(0.005f,0.005f,0.005f));
        uniformsMap.setUniform("dirLight.diffuse",new Vector3f(0.05f,0.05f,0.05f));
        uniformsMap.setUniform("dirLight.specular",new Vector3f(0.05f,0.05f,0.05f));

        // light position ini posisi cahaya yang kecilnya yang diatur sumber cahayanya mau di objek mana
//        Vector3f[] _pointLightPositions = {
//                new Vector3f(0.7f,0.2f,2.0f),
//                new Vector3f(2.3f,-3.3f,-4.0f),
//                new Vector3f(-4.0f,2.0f,-12.0f),
//                new Vector3f(0.0f,0.0f,-3.0f)
//        }
        Vector3f[] _pointLightPositions = {
                new Vector3f(-0.3f, 0.08f,1.3f),
                new Vector3f(-0.4f, 0.08f,0.33f),
                new Vector3f(1f, 0.08f,-0.566f),
                new Vector3f(-0.4f, 0.08f,-0.665f),
                new Vector3f(0.8f, 0.08f,0.6f)
        };
//        System.out.println(POS1);
        for (int i=0;i<_pointLightPositions.length;i++){
            float distance = camera.getPosition().distance(_pointLightPositions[0]);
            uniformsMap.setUniform("pointLights["+ i +"].position", _pointLightPositions[i]);
            uniformsMap.setUniform("pointLights["+ i +"].ambient", new Vector3f(0.05f,0.05f,0.05f));
            uniformsMap.setUniform("pointLights["+ i +"].diffuse", new Vector3f(0.8f,0.8f,0.8f));
            uniformsMap.setUniform("pointLights["+ i +"].specular", new Vector3f(1.0f,1.0f,1.0f));
            uniformsMap.setUniform("pointLights["+ i +"].constant",1.0f );
            uniformsMap.setUniform("pointLights["+ i +"].linear", 0.09f);
            uniformsMap.setUniform("pointLights["+ i +"].quadratic", 0.032f);
        }

        // spotlight (ini kayak senter)
//        uniformsMap.setUniform("spotLight.position", camera.getPosition());
//        uniformsMap.setUniform("spotLight.direction", camera.getDirection());
//        uniformsMap.setUniform("spotLight.ambient", new Vector3f(0.0f,0.0f,0.0f));
//        uniformsMap.setUniform("spotLight.diffuse", new Vector3f(1.0f,1.0f,1.0f));
//        uniformsMap.setUniform("spotLight.specular", new Vector3f(1.0f,1.0f,1.0f));
//        uniformsMap.setUniform("spotLight.constant", 1.0f);
//        uniformsMap.setUniform("spotLight.linear", 0.09f);
//        uniformsMap.setUniform("spotLight.quadratic", 0.032f);
//        uniformsMap.setUniform("spotLight.cutOff", (float)Math.cos(Math.toRadians(12.5f)));
//        uniformsMap.setUniform("spotLight.outerCutOff", (float)Math.cos(Math.toRadians(12.5f)));

        uniformsMap.setUniform("viewPos",camera.getPosition());

        uniformsMap.setUniform("textureSampler", 0);

    }

    public void loadFbxFiles(){
        index = new ArrayList<>();
        normal = new ArrayList<>();
        int numMeshes = scene.mNumMeshes();
        for (int x = 0; x <numMeshes; x++) { //kalo ada beberapa model
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(x));

            // vertices
            AIVector3D.Buffer verticesBuffer = mesh.mVertices();
            int numVertices = mesh.mNumVertices();


            for (int i = 0; i < numVertices; i++) {
                AIVector3D vertex = verticesBuffer.get(i);
                Vector3f verticesVec = new Vector3f(vertex.x(), vertex.y(), vertex.z());
                vertices.add(verticesVec);
            }

            //  normal
            AIVector3D.Buffer normalsBuffer = mesh.mNormals();
            int numNormals = mesh.mNumVertices();

            for (int i = 0; i < numNormals; i++) {
                AIVector3D vertex = normalsBuffer.get(i);
                Vector3f verticesVec = new Vector3f(vertex.x(), vertex.y(), vertex.z());
                normal.add(verticesVec);
            }

            //indices
            AIFace.Buffer facesBuffer = mesh.mFaces();
            int numFaces = mesh.mNumFaces();

            for (int i = 0; i < numFaces; i++) {
                AIFace face = facesBuffer.get(i);
                int numIndices = face.mNumIndices();
                for (int j = 0; j < numIndices; j++) {
                    int index = face.mIndices().get(j);
                    this.index.add(index);
                }
                System.out.println();
            }
        }

    }

    public void loadObjModel(String fileName){
        List<String> lines = Utils.readAllLines(fileName);

//        List<Vector3f> vertices = new ArrayList<>();
//        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();
        vertices.clear();
        normal = new ArrayList<>();
        index = new ArrayList<>();
        for (String line: lines){
            String[] tokens = line.split("\\s+");
            switch (tokens[0]){
                case "v":
                    // vertices
                    Vector3f verticesVec = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    vertices.add(verticesVec);
                    break;
                case "vt":
                    Vector2f textureVec = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    );
                    textures.add(textureVec);
                    break;
                case "vn":
                    Vector3f normalsVec = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    normal.add(normalsVec);
                    break;
                case "f":
                    processFace(tokens[1],  faces);
                    processFace(tokens[2],  faces);
                    processFace(tokens[3],  faces);
                    break;
                default:
                    break;
            }
//            System.out.println(vertices);
//            System.out.println(normals);
//            System.out.println(textures);
//            System.out.println(faces);
        }
        List<Integer> indices = new ArrayList<>();
        float[] verticesArr = new float[vertices.size() * 3];
        int i = 0;
        for (Vector3f pos : vertices){
            verticesArr[i *3] = pos.x;
            verticesArr[i *3 + 1] = pos.y;
            verticesArr[i *3 + 2] = pos.z;
            i++;
        }
        float[] texCoordArr = new float[vertices.size() * 2];
        float[] normalArr = new float[vertices.size() * 3];

        for (Vector3i face: faces){
            processVertex(face.x, face.y,face.z, textures, normal, indices,texCoordArr, normalArr);
        }

        int[] indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();
        this.vertices = Utils.floatToList(verticesArr);
        this.normal = Utils.floatToList(normalArr);
        this.index = Utils.intToList(indicesArr);
        this.textures = Utils.floatToList2(texCoordArr);
        // Load the texture
    }

    private static void processVertex(int pos,int texCoord, int normal, List<Vector2f> texCoordList, List<Vector3f> normalList,
                                      List<Integer> indicesList, float[] texCoordArr, float[] normalArr){
        indicesList.add(pos);
        if (texCoord >= 0){
            Vector2f texCoordVec = texCoordList.get(texCoord);
            texCoordArr[pos * 2]= texCoordVec.x;
            texCoordArr[pos * 2 + 1]= 1 - texCoordVec.y;

        }

        if (normal >= 0){
            Vector3f normalVec = normalList.get(normal);
            normalArr[pos * 3] = normalVec.x;
            normalArr[pos * 3 + 1] = normalVec.y;
            normalArr[pos * 3 + 2] = normalVec.z;
        }
    }

    private static void processFace(String token, List<Vector3i> faces){
        String[] lineToken = token.split("/");
        int length = lineToken.length;
        int pos = -1, coords= - 1, normal = -1;
        pos = Integer.parseInt(lineToken[0])-1;
        if (length > 1){
            String textCoord = lineToken[1];
            coords = textCoord.length() > 0 ? Integer.parseInt(textCoord)- 1: -1;
            if (length > 2){
                normal = Integer.parseInt(lineToken[2])-1;
            }
        }
        Vector3i facesVec = new Vector3i(pos, coords, normal);
        faces.add(facesVec);
    }
}
