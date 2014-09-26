/*
 * This is the source code of Telegram for Android v. 1.3.2.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013.
 */

package org.telegram.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.telegram.messenger.FileLog;
import org.telegram.messenger.TLRPC;
import org.telegram.messenger.Utilities;

import java.util.ArrayList;

public class PhotoObject {
    public TLRPC.PhotoSize photoOwner;
    public Bitmap image;

    public PhotoObject(TLRPC.PhotoSize photo, int preview) {
        photoOwner = photo;

        if (preview != 0 && photo instanceof TLRPC.TL_photoCachedSize) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            opts.inDither = false;
            opts.outWidth = photo.w;
            opts.outHeight = photo.h;
            try {
                image = BitmapFactory.decodeByteArray(photoOwner.bytes, 0, photoOwner.bytes.length, opts);
                if (image != null) {
                    if (preview == 2) {
                        Utilities.blurBitmap(image);
                    }
                    if (ImageLoader.getInstance().runtimeHack != null) {
                        ImageLoader.getInstance().runtimeHack.trackFree(image.getRowBytes() * image.getHeight());
                    }
                }
            } catch (Throwable throwable) {
                FileLog.e("tmessages", throwable);
            }
        }
    }

    public static PhotoObject getClosestImageWithSize(ArrayList<PhotoObject> arr, int width, int height) {
        if (arr == null) {
            return null;
        }
        int closestWidth = 9999;
        int closestHeight = 9999;
        PhotoObject closestObject = null;
        for (PhotoObject obj : arr) {
            if (obj == null || obj.photoOwner == null) {
                continue;
            }
            int diffW = Math.abs(obj.photoOwner.w - width);
            int diffH = Math.abs(obj.photoOwner.h - height);
            if (closestObject == null || closestWidth > diffW || closestHeight > diffH || closestObject.photoOwner instanceof TLRPC.TL_photoCachedSize) {
                closestObject = obj;
                closestWidth = diffW;
                closestHeight = diffH;
            }
        }
        return closestObject;
    }
}
