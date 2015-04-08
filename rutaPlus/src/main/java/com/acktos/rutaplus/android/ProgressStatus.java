package com.acktos.rutaplus.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;

public class ProgressStatus {

	public static void show(Context context,final View progressView, final View contentView, final boolean show){
		int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

		progressView.setVisibility(View.VISIBLE);
		progressView.animate().setDuration(shortAnimTime)
		.alpha(show ? 1 : 0)
		.setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				progressView.setVisibility(show ? View.VISIBLE
						: View.GONE);
			}
		});

		contentView.setVisibility(View.VISIBLE);
		contentView.animate().setDuration(shortAnimTime)
		.alpha(show ? 0 : 1)
		.setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				contentView.setVisibility(show ? View.GONE
						: View.VISIBLE);
			}
		});
	}
}
