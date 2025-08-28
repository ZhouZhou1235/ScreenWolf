package com.pinkcandy.screenwolf.utils;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;


/**
 * 截获的图片传输
 * 用于屏幕截图
 */
public class ImageSelection implements Transferable {
    private final BufferedImage image;
    public ImageSelection(BufferedImage image){this.image=image;}
    @Override
    public DataFlavor[] getTransferDataFlavors(){return new DataFlavor[]{DataFlavor.imageFlavor};}
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor){return DataFlavor.imageFlavor.equals(flavor);}
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException,IOException{
        if(!isDataFlavorSupported(flavor)){throw new UnsupportedFlavorException(flavor);}
        return image;
    }
}
