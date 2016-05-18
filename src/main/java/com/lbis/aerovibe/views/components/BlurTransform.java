package com.lbis.aerovibe.views.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import com.squareup.picasso.Transformation;

public class BlurTransform implements Transformation {

	static RenderScript rs;
	static ScriptIntrinsicBlur script;

	public BlurTransform(Context context) {
		super();
		if (rs == null)
			rs = RenderScript.create(context);
		if (script == null)
			script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
	}

	public Bitmap transform(Bitmap bitmap) {
		// Create another bitmap that will hold the results of the filter.
		Bitmap blurredBitmap = Bitmap.createBitmap(bitmap);

		// Allocate memory for Renderscript to work with
		Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SCRIPT);
		Allocation output = Allocation.createTyped(rs, input.getType());

		// Load up an instance of the specific script that we want to use.
		script.setInput(input);

		// Set the blur radius
		script.setRadius(10);

		// Start the ScriptIntrinisicBlur
		script.forEach(output);

		// Copy the output to the blurred bitmap
		output.copyTo(blurredBitmap);
		if (bitmap != blurredBitmap)
			bitmap.recycle();

		return blurredBitmap;
	}

	public String key() {
		return "blur";
	}

}
