package com.oriana.moroccan.bdarija.helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class ArabTv extends TextView {

	public ArabTv(Context context) {
		super(context);

		applyCustomFont(context);
	}

	public ArabTv(Context context, AttributeSet attrs) {
		super(context, attrs);

		applyCustomFont(context);
	}

	public ArabTv(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		applyCustomFont(context);
	}

	private void applyCustomFont(Context context) {
		Typeface customFont = Typeface.createFromAsset(context.getAssets(), "DroidKufi-Regular.ttf");
		
		setTypeface(customFont);
	}
}
