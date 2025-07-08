package com.pinkcandy.screenwolf;

import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;
import com.pinkcandy.screenwolf.utils.GUtil;

public class AnimationSprite extends JLabel {
    private final Dimension size;
    private final Timer playTimer;
    private final ImageIcon[] frames;
    private int frameIndex;
    private int frameLength;
    private final Map<String, String> animations;
    public boolean flip_h = false;
    public String currentAnimation;
    private final ClassLoader classLoader;

    public AnimationSprite(Dimension size, Map<String, String> animations, ClassLoader classLoader) {
        this.size = size;
        this.animations = animations;
        this.classLoader = classLoader;
        this.frames = new ImageIcon[GUtil.GAME_maxFrameLength];
        
        // 初始化定时器
        this.playTimer = new Timer(GUtil.DEFAULT_animationPlaySpeed, e -> {
            if (frameLength > 0) {
                updateDisplay();
                frameIndex = (frameIndex + 1) % frameLength;
            }
        });
        
        setOpaque(false);
        
        // 加载第一个动画
        if (animations != null && !animations.isEmpty()) {
            setAnimation(animations.keySet().iterator().next());
        }

        playAnimation();

    }

    private void updateDisplay() {
        ImageIcon currentFrame = frames[frameIndex];
        if (flip_h) {
            currentFrame = new ImageIcon(GUtil.flipImage(currentFrame.getImage()));
        }
        setIcon(currentFrame);
    }

    public void setAnimation(String animationName) {
        if (animationName.equals(currentAnimation)) {
            return; // 已经是当前动画，无需切换
        }
        
        String path = animations.get(animationName);
        if (path == null) {
            return;
        }
        
        this.currentAnimation = animationName;
        this.frameIndex = 0;
        this.frameLength = 0;
        
        try {
            // 确保路径以"/"结尾
            if (!path.endsWith("/")) {
                path += "/";
            }
            
            List<String> frameFiles = loadFrameFiles(path);
            loadFrames(path, frameFiles);
            
            // 如果有帧，显示第一帧
            if (frameLength > 0) {
                updateDisplay();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> loadFrameFiles(String path) throws IOException {
        List<String> frameFiles = new ArrayList<>();
        Enumeration<URL> resources = classLoader.getResources(path);
        
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if ("jar".equals(resource.getProtocol())) {
                JarURLConnection jarConn = (JarURLConnection) resource.openConnection();
                try (JarFile jarFile = jarConn.getJarFile()) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.startsWith(path) && !name.equals(path)) {
                            frameFiles.add(name.substring(path.length()));
                        }
                    }
                }
            }
        }
        
        // 按帧号排序
        frameFiles.sort(this::compareFrameNames);
        return frameFiles;
    }

    private void loadFrames(String path, List<String> frameFiles) throws IOException {
        for (int i = 0; i < frameFiles.size() && i < frames.length; i++) {
            String imagePath = path + frameFiles.get(i);
            try (InputStream is = classLoader.getResourceAsStream(imagePath)) {
                if (is != null) {
                    Image image = ImageIO.read(is);
                    frames[i] = GUtil.scaleImageIcon(new ImageIcon(image), size.width);
                    frameLength++;
                }
            }
        }
    }

    private int compareFrameNames(String a, String b) {
        return Integer.compare(extractFrameNumber(a), extractFrameNumber(b));
    }

    private int extractFrameNumber(String filename) {
        String numStr = filename.replaceAll("[^0-9]", "");
        return numStr.isEmpty() ? 0 : Integer.parseInt(numStr);
    }

    public void playAnimation() {
        if (!playTimer.isRunning() && frameLength > 0) {
            playTimer.start();
        }
    }

    public void stopAnimation() {
        playTimer.stop();
    }

    public void setFlipHorizontal(boolean flip) {
        this.flip_h = flip;
        if (frameLength > 0) {
            updateDisplay();
        }
    }
}