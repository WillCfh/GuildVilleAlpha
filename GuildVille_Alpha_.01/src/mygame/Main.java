package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.util.SkyFactory;
import com.jme3.water.SimpleWaterProcessor;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    protected Spatial player;
    Boolean isRunning = true;
    public CameraNode camNode;

    @Override
    public void simpleInitApp() {



        Spatial tutorial_Sequence = assetManager.loadModel("/Scenes/Tutorial_Sequence.j3o");
        tutorial_Sequence.setLocalTranslation(0, 0, 0);
        //rootNode.attachChild(tutorial_Sequence);

        player = assetManager.loadModel("Models/Tutorial_Sequence/chartest.j3o");
        player.setLocalTranslation(0f, 2.8f, 0.0f);

        rootNode.attachChild(player);

        // Disable the default flyby cam
        flyCam.setEnabled(false);
//create the camera Node
        camNode = new CameraNode("Camera Node", cam);
//This mode means that camera copies the movements of the target:
        camNode.setControlDir(ControlDirection.SpatialToCamera);
//Move camNode, e.g. behind and above the target:
        camNode.setLocalTranslation(new Vector3f(-15, 5, 0));
//Rotate the camNode to look at the target:
        camNode.lookAt(player.getLocalTranslation(), Vector3f.UNIT_Y);

//Attach the camNode to the target:
        rootNode.attachChild(camNode);
        guiNode.detachAllChildren();


        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText helloText = new BitmapText(guiFont, false);
        helloText.setSize(guiFont.getCharSet().getRenderedSize());
        helloText.setText("RPG Test Grounds");
        helloText.setLocalTranslation(300, helloText.getLineHeight(), 0);
        guiNode.attachChild(helloText);


        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/sky.jpg", true));

        // we create a water processor
        SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(assetManager);
        waterProcessor.setReflectionScene(rootNode);

        //s we set the water plane
        Vector3f waterLocation = new Vector3f(-350, -3, 900);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
        viewPort.addProcessor(waterProcessor);

        // we set wave properties
        waterProcessor.setWaterDepth(40);         // transparency of water
        waterProcessor.setDistortionScale(0.05f); // strength of waves
        waterProcessor.setWaveSpeed(0.05f);       // speed of waves

        // we define the wave size by setting the size of the texture coordinates
        Quad quad = new Quad(912, 1000);
        quad.scaleTextureCoordinates(new Vector2f(6f, 6f));

        // we create the water geometry from the quad
        Geometry water = new Geometry("water", quad);
        water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        water.setLocalTranslation(-400, -3, 512);
        water.setShadowMode(ShadowMode.Receive);
        water.setMaterial(waterProcessor.getMaterial());

        rootNode.attachChild(water);


        DirectionalLight sun2 = new DirectionalLight();
        sun2.setDirection(new Vector3f(0.1f, 2.7f, 3.0f));
        rootNode.addLight(sun2);
        initKeys();
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    /**
     * Custom Keybinding: Map named actions to inputs.
     */
    private void initKeys() {
        // You can map one or several inputs to one named action
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("JUMP", new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addMapping("Descend", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("Rotate", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        // Add the names to the action listener.
        inputManager.addListener(actionListener, "Pause");
        inputManager.addListener(analogListener, "Descend", "Rotate", "Left", "Right", "JUMP", "Forward", "Backward");

    }
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Pause") && !keyPressed) {
                isRunning = !isRunning;
            }
        }
    };
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            if (isRunning) {
                if (name.equals("JUMP")) {
                    Vector3f v = player.getLocalTranslation();
                    player.setLocalTranslation(v.x, v.y + value * speed * 25, v.z);
                    v = camNode.getLocalTranslation();
                    camNode.setLocalTranslation(v.x, v.y + value * speed * 25, v.z);
                }
                if (name.equals("Descend")) {
                    Vector3f v = player.getLocalTranslation();
                    player.setLocalTranslation(v.x, v.y - value * speed * 25, v.z);
                    v = camNode.getLocalTranslation();
                    camNode.setLocalTranslation(v.x, v.y - value * speed * 25, v.z);
                }
                if (name.equals("Rotate")) {
                    player.rotate(0, value * speed, 0);
                }
                if (name.equals("Forward")) {
                    Vector3f v = player.getLocalTranslation();
                    player.setLocalTranslation(v.x + value * speed * 25, v.y, v.z);
                    v = camNode.getLocalTranslation();
                    camNode.setLocalTranslation(v.x + value * speed * 25, v.y, v.z);
                }
                if (name.equals("Backward")) {
                    Vector3f v = player.getLocalTranslation();
                    player.setLocalTranslation(v.x - value * speed * 25, v.y, v.z);
                    v = camNode.getLocalTranslation();
                    camNode.setLocalTranslation(v.x - value * speed * 25, v.y, v.z);
                }
                if (name.equals("Right")) {
                    Vector3f v = player.getLocalTranslation();
                    player.setLocalTranslation(v.x, v.y, v.z + value * speed * 25);
                    v = camNode.getLocalTranslation();
                    camNode.setLocalTranslation(v.x, v.y, v.z + value * speed * 25);
                }
                if (name.equals("Left")) {
                    Vector3f v = player.getLocalTranslation();
                    player.setLocalTranslation(v.x, v.y, v.z - value * speed * 25);
                    v = camNode.getLocalTranslation();
                    camNode.setLocalTranslation(v.x, v.y, v.z - value * speed * 25);
                }
            } else {
                System.out.println("Press P to unpause.");
            }
        }
    };
}
