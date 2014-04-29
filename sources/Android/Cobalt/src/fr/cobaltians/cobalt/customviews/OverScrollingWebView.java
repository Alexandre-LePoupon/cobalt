/**
 *
 * OverScrollingWebView
 * Cobalt
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Cobaltians
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package fr.cobaltians.cobalt.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class OverScrollingWebView extends WebView {

	/**
	 * Fragment handling scroll events
	 */
	protected HTMLFragment mScrollListener;

	public OverScrollingWebView(Context context) {
		super(context);
	}

	public OverScrollingWebView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}
	
	public OverScrollingWebView(Context context, AttributeSet attributes, int defaultStyle) {
		super(context, attributes, defaultStyle);
	}
	
	public HTMLFragment getScrollListener() {
		return mScrollListener;
	}

	public void setScrollListener(HTMLFragment scrollListener) {
		mScrollListener = scrollListener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			int scrollY = getScrollY();

			// TODO: this is INSANE! Scrolls the webview one pixel down then one pixel up to avoid the freeze feeling...
			scrollTo(0, getScrollY() + 1);
			scrollTo(0, scrollY);
		}
		
		return super.onTouchEvent(event);
	}

	/**
	 * Notifies listener of scrolling
	 */
	@Override
	protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
		super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
		
		if(	mScrollListener != null 
			&& IScrollListener.class.isAssignableFrom(mScrollListener.getClass())) {
			((IScrollListener) mScrollListener).onOverScrolled(scrollX, scrollY, oldScrollX, oldScrollY);
		}
	}	
}