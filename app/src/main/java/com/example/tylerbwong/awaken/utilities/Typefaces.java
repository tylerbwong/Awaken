package com.example.tylerbwong.awaken.utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Hashtable;

/**
 * @author Tyler Wong
 */
public class Typefaces {
   private static final String TAG = "Typefaces";

   private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

   @Nullable
   public static Typeface get(Context c, String assetPath) {
      synchronized (cache) {
         if (!cache.containsKey(assetPath)) {
            try {
               Typeface t = Typeface.createFromAsset(c.getAssets(), assetPath);
               cache.put(assetPath, t);
            }
            catch (Exception e) {
               Log.e(TAG, "Could not get typeface '" + assetPath + "' because " + e.getMessage());
               return null;
            }
         }
         return cache.get(assetPath);
      }
   }
}