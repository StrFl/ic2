/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.client.resources.IResource
 *  net.minecraft.client.resources.IResourceManager
 *  net.minecraft.client.resources.data.AnimationMetadataSection
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.util.LogCategory;
import ic2.core.util.ReflectionUtil;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

@SideOnly(value=Side.CLIENT)
public class BlockTextureStitched
extends TextureAtlasSprite {
    private final int subIndex;
    private AnimationMetadataSection animationMeta;
    private BufferedImage comparisonImage;
    private TextureAtlasSprite mappedTexture;
    private int mipmapLevels;
    private static Map<String, CacheEntry> cachedImages = new HashMap<String, CacheEntry>();
    private static Map<Integer, List<BlockTextureStitched>> existingTextures = new HashMap<Integer, List<BlockTextureStitched>>();
    private static Field fieldMipmapLevels;
    private static Field fieldAnisotropicFiltering;
    private static Field fieldParentAnimationMeta;
    private static Method methodFixTransparentPixels;
    private static Method methodPrepareAnisotropicFiltering;

    public BlockTextureStitched(String name, int subIndex1) {
        super(name);
        BlockTextureStitched.initReflection();
        this.subIndex = subIndex1;
    }

    public void copyFrom(TextureAtlasSprite textureStitched) {
        if (textureStitched.getIconName().equals("missingno") && this.mappedTexture != null) {
            super.copyFrom(this.mappedTexture);
        } else {
            super.copyFrom(textureStitched);
        }
    }

    public void updateAnimation() {
    }

    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean load(IResourceManager manager, ResourceLocation location) {
        String name = location.getResourcePath();
        int index = name.indexOf(58);
        if (index != -1) {
            location = new ResourceLocation(location.getResourceDomain(), name.substring(0, index));
        }
        location = new ResourceLocation(location.getResourceDomain(), "textures/blocks/" + location.getResourcePath() + ".png");
        try {
            this.mipmapLevels = fieldMipmapLevels.getInt(Minecraft.getMinecraft().getTextureMapBlocks());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        IResource resource = null;
        try {
            resource = manager.getResource(location);
            boolean bl = this.loadSubImage(resource);
            return bl;
        }
        catch (IOException e) {
            IC2.log.debug(LogCategory.Resource, e, "Texture sub image load failed.");
            boolean bl = true;
            return bl;
        }
        finally {
            if (resource != null) {
                try {
                    resource.getInputStream().close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    public boolean loadSubImage(IResource res) throws IOException {
        BufferedImage bufferedImage;
        String name = this.getIconName();
        CacheEntry cacheEntry = cachedImages.get(name);
        if (cacheEntry != null) {
            bufferedImage = cacheEntry.image;
            this.animationMeta = cacheEntry.animationMeta;
        } else {
            bufferedImage = ImageIO.read(res.getInputStream());
            this.animationMeta = (AnimationMetadataSection)res.getMetadata("animation");
            cachedImages.put(name, new CacheEntry(bufferedImage, this.animationMeta));
        }
        int animationLength = 1;
        if (this.animationMeta != null && this.animationMeta.getFrameHeight() > 0) {
            animationLength = this.animationMeta.getFrameHeight();
            try {
                fieldParentAnimationMeta.set((Object)this, this.animationMeta);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            this.animationMeta = null;
        }
        int size = bufferedImage.getHeight() / animationLength;
        int count = bufferedImage.getWidth() / size;
        int index = this.subIndex;
        if (count == 1 || count == 6 || count == 12) {
            index %= count;
        } else if (count == 2) {
            index /= 6;
        } else {
            IC2.log.warn(LogCategory.Resource, "Texture %s is not properly sized.", name);
            throw new IOException();
        }
        this.width = size;
        this.height = size;
        return this.loadFrames(bufferedImage, index, animationLength);
    }

    public IIcon getRealTexture() {
        return this.mappedTexture == null ? this : this.mappedTexture;
    }

    private boolean loadFrames(BufferedImage image, int index, int animationLength) {
        assert (animationLength > 0);
        int totalHeight = this.height * animationLength;
        this.comparisonImage = image.getSubimage(index * this.width, 0, this.width, totalHeight);
        int[] rgbaData = new int[this.width * totalHeight];
        this.comparisonImage.getRGB(0, 0, this.width, totalHeight, rgbaData, 0, this.width);
        int hash = Arrays.hashCode(rgbaData);
        List<BlockTextureStitched> matchingTextures = existingTextures.get(hash);
        if (matchingTextures != null) {
            int[] rgbaData2 = new int[this.width * totalHeight];
            for (BlockTextureStitched matchingTexture : matchingTextures) {
                if (matchingTexture.width != this.width || matchingTexture.comparisonImage.getHeight() != totalHeight) continue;
                matchingTexture.comparisonImage.getRGB(0, 0, this.width, totalHeight, rgbaData2, 0, this.width);
                if (!Arrays.equals(rgbaData, rgbaData2)) continue;
                this.mappedTexture = matchingTexture;
                this.comparisonImage = null;
                return true;
            }
            matchingTextures.add(this);
        } else {
            matchingTextures = new ArrayList<BlockTextureStitched>();
            matchingTextures.add(this);
            existingTextures.put(hash, matchingTextures);
        }
        int pixelsPerFrame = this.width * this.height;
        if (this.animationMeta != null && this.animationMeta.getFrameCount() > 0) {
            for (Integer frameIndex : this.animationMeta.getFrameIndexSet()) {
                if (frameIndex >= animationLength) {
                    throw new RuntimeException("invalid frame index: " + frameIndex + " (" + this.getIconName() + ")");
                }
                while (this.framesTextureData.size() <= frameIndex) {
                    this.framesTextureData.add(null);
                }
                int[][] data = new int[1 + this.mipmapLevels][];
                data[0] = Arrays.copyOfRange(rgbaData, frameIndex * pixelsPerFrame, (frameIndex + 1) * pixelsPerFrame);
                try {
                    methodFixTransparentPixels.invoke((Object)this, new Object[]{data});
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
                this.framesTextureData.set(frameIndex, data);
            }
        } else {
            for (int i = 0; i < animationLength; ++i) {
                int[][] data = new int[1 + this.mipmapLevels][];
                data[0] = Arrays.copyOfRange(rgbaData, i * pixelsPerFrame, (i + 1) * pixelsPerFrame);
                try {
                    methodFixTransparentPixels.invoke((Object)this, new Object[]{data});
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
                this.framesTextureData.add(data);
            }
        }
        return false;
    }

    public static void onPostStitch() {
        for (List<BlockTextureStitched> textures : existingTextures.values()) {
            for (BlockTextureStitched texture : textures) {
                texture.comparisonImage = null;
            }
        }
        cachedImages.clear();
        existingTextures.clear();
    }

    private static void initReflection() {
        if (fieldMipmapLevels == null) {
            fieldMipmapLevels = ReflectionUtil.getField(TextureMap.class, "field_147636_j", "mipmapLevels");
            if (fieldMipmapLevels == null) {
                throw new RuntimeException("Can't find mipmapLevel field.");
            }
            fieldAnisotropicFiltering = ReflectionUtil.getField(TextureMap.class, "field_147637_k", "anisotropicFiltering");
            if (fieldAnisotropicFiltering == null) {
                throw new RuntimeException("Can't find anisotropicFiltering field.");
            }
            fieldParentAnimationMeta = ReflectionUtil.getField(TextureAtlasSprite.class, "field_110982_k", "animationMetadata");
            if (fieldParentAnimationMeta == null) {
                throw new RuntimeException("Can't find animationMetadata field.");
            }
            methodFixTransparentPixels = ReflectionUtil.getMethod(TextureAtlasSprite.class, new String[]{"func_147961_a", "fixTransparentPixels"}, int[][].class);
            if (methodFixTransparentPixels == null) {
                throw new RuntimeException("Can't find fixTransparentPixels method.");
            }
            methodPrepareAnisotropicFiltering = ReflectionUtil.getMethod(TextureAtlasSprite.class, new String[]{"func_147960_a", "prepareAnisotropicFiltering"}, int[][].class, Integer.TYPE, Integer.TYPE);
            if (methodPrepareAnisotropicFiltering == null) {
                throw new RuntimeException("Can't find prepareAnisotropicFiltering method.");
            }
        }
    }

    static class CacheEntry {
        final BufferedImage image;
        final AnimationMetadataSection animationMeta;

        CacheEntry(BufferedImage image1, AnimationMetadataSection animationMeta1) {
            this.image = image1;
            this.animationMeta = animationMeta1;
        }
    }
}

