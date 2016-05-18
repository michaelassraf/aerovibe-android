package com.lbis.mobile.utils;

import com.lbis.aerovibe_android.R;

public class Enums {

	public enum Picture {
		Leaf(R.drawable.leaf), HomeSun(R.drawable.home_sun), Facroty(R.drawable.factory), Nature(R.drawable.nature);

		int drawableInt;

		private Picture(int drawableInt) {
			this.drawableInt = drawableInt;
		}

		public int getDrawableInt() {
			return drawableInt;
		}
	}
}
