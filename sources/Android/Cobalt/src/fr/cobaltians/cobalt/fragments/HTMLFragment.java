/**
 *
 * HTMLFragment
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

package fr.cobaltians.cobalt.fragments;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.LoadingLayoutProxy;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;
import fr.cobaltians.cobalt.activities.HTMLActivity;
import fr.cobaltians.cobalt.customviews.IScrollListener;
import fr.cobaltians.cobalt.customviews.OverScrollingWebView;
import fr.cobaltians.cobalt.customviews.PullToRefreshOverScrollWebview;
import fr.cobaltians.cobalt.database.LocalStorage;
import fr.cobaltians.cobalt.webViewClients.ScaleWebViewClient;
import fr.cobaltians.cobalt.Cobalt;
import fr.cobaltians.cobalt.R;

/**
 * {@link Fragment} allowing interactions between native and Web
 * 
 * @author Diane Moebs
 */
public abstract class HTMLFragment extends Fragment implements IScrollListener {
	
	// TAG
	private final static String TAG = "HTMLFragment";
	// RESOURCES
	private final static String ASSETS_PATH = "file:///android_asset/";
		
	// CONFIGURATION FILE
	private final static String CONF_FILE = "cobalt.conf";
	private final static String kAndroidController = "androidController";
	public final static String kExtras = "extras";
	private final static String kPage = "page";
	protected final static String kActivity = "activity";
	protected final static String kPullToRefresh = "pullToRefresh";
	protected final static String kInfiniteScroll = "infiniteScroll";
	protected final static String kSwipe = "swipe";
	
	/*********************************************************************************
	 * JS MESSAGES
	 ********************************************************************************/
	
	// GENERAL
	protected final static String kJSAction = "action";
	protected final static String kJSCallback = "callback";
	protected final static String kJSData = "data";
	protected final static String kJSPage = "page";
	protected final static String kJSType = "type";
	protected final static String kJSValue = "value";
	
	// CALLBACKS
	protected final static String JSTypeCallBack = "callback";
	
	// COBALT IS READY
	protected final static String JSTypeCobaltIsReady = "cobaltIsReady";
	
	// EVENTS
	protected final static String JSTypeEvent = "event";
	protected final static String kJSEvent = "event";

	// BACK BUTTON
	private final static String JSEventOnBackButtonPressed = "onBackButtonPressed";
	private final static String JSCallbackOnBackButtonPressed = "onBackButtonPressed";
		
	// LOG
	protected final static String JSTypeLog = "log";
	
	// NAVIGATION
	protected final static String JSTypeNavigation = "navigation";
	protected final static String JSActionNavigationPush = "push";
	protected final static String JSActionNavigationPop ="pop";
	protected final static String JSActionNavigationModal = "modal";
	protected final static String JSActionNavigationDismiss = "dismiss";
	protected final static String kJSNavigationController = "controller";
	protected final static String JSNavigationControllerDefault = "default";
	
	// UI
	protected final static String JSTypeUI = "ui";
	protected final static String kJSUIControl = "control";
		
	// ALERT
	protected final static String JSControlAlert = "alert";
	protected final static String kJSAlertTitle = "title";
	protected final static String kJSAlertMessage = "message";
	protected final static String kJSAlertButtons = "buttons";
	protected final static String kJSAlertCancelable = "cancelable";
	protected final static String kJSAlertButtonIndex  = "index";
	
	// DATE PICKER
	protected static final String JSControlPicker = "picker";
	protected static final String JSPickerDate = "date";
	protected static final String kJSDate = "date";
	protected static final String kJSDay = "day";
	protected static final String kJSMonth = "month";
	protected static final String kJSYear = "year";
	protected static final String kJSTexts = "texts";
	protected static final String kJSTitle ="title";
	protected static final String kJSDelete = "delete";
	protected static final String kJSCancel = "cancel";
	protected static final String kJSValidate = "validate"; 
	// TOAST
	protected final static String JSControlToast = "toast";

	// WEB LAYER
	protected final static String JSTypeWebLayer = "webLayer";
	protected final static String JSActionWebLayerShow = "show";
	protected final static String JSActionWebLayerDismiss = "dismiss";
	protected final static String kJSWebLayerFadeDuration = "fadeDuration";
	protected final static String JSEventWebLayerOnDismiss = "onWebLayerDismissed";
	
	// PULL TO REFRESH
	private static String JSEventPullToRefresh = "pullToRefresh";
	private static String JSCallbackPullToRefreshDidRefresh = "pullToRefreshDidRefresh";

	// INFINITE SCROLL
	private static String JSEventInfiniteScroll= "infiniteScroll";
	private static String JSCallbackInfiniteScrollDidRefresh = "infiniteScrollDidRefresh";


	/*********************************************************
	 * MEMBERS
	 ********************************************************/
	protected static boolean sDebug = false;
	protected static Context sContext;
	
	protected String mPage;
	
	protected OverScrollingWebView mWebView;
	// TODO: use ViewGroup instead of FrameLayout to allow using different layouts 
	protected FrameLayout mWebViewPlaceholder;
	protected boolean mWebViewContentHasBeenLoaded;
	
	protected Handler mHandler;

	private ArrayList<JSONObject> mWaitingJavaScriptCallsQueue;
	
	private boolean mPreloadOnCreateView;
	private boolean mWebViewLoaded;
	private boolean mCobaltIsReady;
	
	// Web view may having pull-to-refresh and/or infinite scroll features.
	protected PullToRefreshOverScrollWebview mPullToRefreshWebView;

	private boolean mInfiniteScrollRefreshing = false;
	private boolean mInfiniteScrollEnabled = false;
	private boolean mPullToRefreshActivate = false;
	
	/**************************************************************************************************************************
	 * LIFECYCLE
	 *************************************************************************************************************************/
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		
		if (sContext == null) {
			sContext = (Context) getActivity().getApplicationContext();
		}
		
		mHandler = new Handler();
		
		mWaitingJavaScriptCallsQueue = new ArrayList<JSONObject>();
		
		mPreloadOnCreateView = true;
		mWebViewLoaded = false;
		mWebViewContentHasBeenLoaded = false;
		mCobaltIsReady = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View view = inflater.inflate(getLayoutToInflate(), container, false);
		setUpViews(view);
		setUpListeners();
		return view;
	}	

	/**
	 * Restores Web view state.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (mWebView != null) {
			mWebView.restoreState(savedInstanceState);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();

		addWebView();
		preloadContent();
		
		setFeaturesWantedActive();
		
		// Web view has been added, set up its listener
		if (mPullToRefreshWebView != null) {		
			mPullToRefreshWebView.setOnRefreshListener(new OnRefreshListener<OverScrollingWebView>() {
				@Override
				public void onRefresh(PullToRefreshBase<OverScrollingWebView> refreshView) {
					refreshWebView();
				}
			});
		}
	}

	/**
	 * Saves the Web view state.
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if(mWebView != null) {
			mWebView.saveState(outState);
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		// Fragment will rotate or be destroyed, so we don't preload content defined in fragment's arguments again 
		mPreloadOnCreateView = false;
		
		removeWebViewFromPlaceholder();
	}
	
	/****************************************************************************************
	 * Helpers
	 ***************************************************************************************/
	
	/**
	 * This method should be overridden in subclasses.
	 * @return Layout id inflated by this fragment
	 */
	protected int getLayoutToInflate() {
		if (mPullToRefreshActivate) {
			return R.layout.html_ptr_fragment;
		}
		return R.layout.html_fragment;
	}

	/**
	 * Sets up the fragment's properties according to the inflated layout.
	 * This method should be overridden in subclasses. 
	 * @param rootView: parent view
	 */
	protected void setUpViews(View rootView) {
		// TODO: and if webViewPlaceholder id is null?
		mWebViewPlaceholder = ((FrameLayout) rootView.findViewById(R.id.webViewPlaceholder));
	}

	/**
	 * Sets up listeners for components inflated from the given layout and the parent view.
	 * This method should be overridden in subclasses.
	 */
	protected void setUpListeners() { }
	
	/**
	 * Called to add the Web view in the placeholder (and creates it if necessary).
	 * This method SHOULD NOT be overridden in subclasses.
	 */
	protected void addWebView() {
				
		if(mWebView == null) {
			mWebView = new OverScrollingWebView(sContext);
			setWebViewSettings(this);
		}	
		
		mPullToRefreshActivate = pullToRefreshActivate();
		if (mPullToRefreshActivate) {
			if (mPullToRefreshWebView == null) {
				mPullToRefreshWebView = new PullToRefreshOverScrollWebview(sContext);
				mWebView = mPullToRefreshWebView.getRefreshableView();
				setWebViewSettings(this);
			}
		}
		
		if(mWebViewPlaceholder != null) {
			if (mPullToRefreshActivate) {
				mWebViewPlaceholder.addView(mPullToRefreshWebView);
			}
			else mWebViewPlaceholder.addView(mWebView);
		}	
		else  {
			if(sDebug) Log.e(TAG, "addWebView: you must set up Web view placeholder in setUpViews!");
		}		
	}
	
	private void preloadContent() {
		// TODO: mPage setting. Do we keep this behavior?
		Bundle arguments = getArguments();
		
		if (arguments != null) {
			mPage = arguments.getString(kPage);
		}
		mPage = (mPage != null) ? mPage : "index.html";
		
		if (mPreloadOnCreateView) {
			loadFileFromAssets(Cobalt.getResourcePath(), mPage);
		}
	}
	
	/**
	 * Called when fragment is about to rotate or be destroyed
	 * This method SHOULD NOT be overridden in subclasses.
	 */
	protected void removeWebViewFromPlaceholder() {
		if (mWebViewPlaceholder != null) {
			if (mPullToRefreshWebView != null) {
				mWebViewPlaceholder.removeView(mPullToRefreshWebView);
			}
			if (mWebView != null) {
				mWebViewPlaceholder.removeView(mWebView);
			}
		}
		else if(sDebug) Log.e(TAG, "removeWebViewFromPlaceholder: you must set up Web view placeholder in setUpViews!");
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	protected void setWebViewSettings(Object javascriptInterface) {
		if(mWebView != null) {		
			mWebView.setScrollListener(this);
			mWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
			
			// Enables JS
			WebSettings webSettings = mWebView.getSettings();
			webSettings.setJavaScriptEnabled(true);

			// Enables and setup JS local storage
			webSettings.setDomStorageEnabled(true); 
			webSettings.setDatabaseEnabled(true);
			//@deprecated since API 19. But calling this method have simply no effect for API 19+
			webSettings.setDatabasePath(sContext.getFilesDir().getParentFile().getPath()+"/databases/");
			
			// Enables cross-domain calls for Ajax
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				allowAjax();
			}
			
			// Fix some focus issues on old devices like HTC Wildfire
			// keyboard was not properly showed on input touch.
			mWebView.requestFocus(View.FOCUS_DOWN);
			mWebView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent event) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
						case MotionEvent.ACTION_UP:
							if (! view.hasFocus()) {
								view.requestFocus();
							}
							break;
						default:
							break;
					}
					
					return false;
				}
			});

			// Add JavaScript interface so JavaScript can call native functions.
			mWebView.addJavascriptInterface(javascriptInterface, "Android");
			mWebView.addJavascriptInterface(new LocalStorageJavaScriptInterface(sContext), "LocalStorage");

			ScaleWebViewClient scaleWebViewClient = new ScaleWebViewClient() {
				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					
					mWebViewLoaded = false;
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					mWebViewLoaded = true;
					
					executeWaitingCalls();
				}
			};
			scaleWebViewClient.setScaleListener(this);

			mWebView.setWebViewClient(scaleWebViewClient);
		}
		else {
			if(sDebug) Log.e(TAG, "setWebViewSettings: Web view is null.");
		}
	}
	
	@TargetApi(android.os.Build.VERSION_CODES.JELLY_BEAN) // 16
	private void allowAjax() {
		try {
			// TODO: see how to restrict only to local files
			mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
		} 
		catch(NullPointerException exception) {
			exception.printStackTrace();
		}
	}
	
	/**
	 * Load the given file in the Web view 
	 * @param path: path in assets folder where the file is located.
	 * @param file: file name to load.
	 * @warning All application HTML files should be found in the same subfolder in ressource path
	 */
	public void loadFileFromAssets(String path, String file) {
		if(mWebView != null) {
			mWebView.loadUrl(ASSETS_PATH + path + file);
			mWebViewContentHasBeenLoaded = true;
		}
	}
	
	/*****************************************
	 * LOGGING
	 ****************************************/
	
	public static boolean isLoggingEnabled() {
		return sDebug;
	}

	public static void enableLogging(boolean debug) {
		sDebug = debug;
	}
	
	/****************************************************************************************
	 * SCRIPT EXECUTION
	 ***************************************************************************************/
	// TODO: find a way to keep in the queue not sent messages
	/**
	 * Sends script to be executed by JavaScript in Web view
	 * @param jsonObj: JSONObject containing script.
	 */
	public void executeScriptInWebView(final JSONObject jsonObj) {
		if (jsonObj != null) {
			if(mCobaltIsReady) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						// Line & paragraph separators are not JSON compliant but supported by JSONObject
						String message = jsonObj.toString().replaceAll("[\u2028\u2029]", "");
						String url = "javascript:cobalt.execute(" + message + ");";
						
						if(mWebView != null) {
							Log.i(TAG, "Send event: " + message);
							mWebView.loadUrl(url);		
						}
						else {
							if(sDebug) Log.e(TAG, "executeScriptInWebView: message cannot been sent to empty Web view");
						}						
					}
				});
			}
			else {
				Log.i(TAG, "Adding event to queue: " + jsonObj);
				mWaitingJavaScriptCallsQueue.add(jsonObj);
			}
		}
	}

	private void executeWaitingCalls() {
		int mWaitingJavaScriptCallsQueueLength = mWaitingJavaScriptCallsQueue.size();
		
		for (int i = 0 ; i < mWaitingJavaScriptCallsQueueLength ; i++) {
			if (sDebug) Log.i(TAG, "executeWaitingCalls: execute " + mWaitingJavaScriptCallsQueue.get(i).toString());
			executeScriptInWebView(mWaitingJavaScriptCallsQueue.get(i));
		}
		
		mWaitingJavaScriptCallsQueue.clear();
	}

	/****************************************************************************************
	 * MESSAGE SENDING
	 ***************************************************************************************/
	/**
	 * Calls the Web callback with an object containing response fields
	 * @param callbackId: the Web callback.
	 * @param data: the object containing response fields
	 */
	public void sendCallback(final String callbackId, final JSONObject data) {
		if(	callbackId != null 
			&& callbackId.length() > 0) {
			try {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put(kJSType, JSTypeCallBack);
				jsonObj.put(kJSCallback, callbackId);
				jsonObj.put(kJSData, data);	
				executeScriptInWebView(jsonObj);
			} 
			catch (JSONException exception) {
				exception.printStackTrace();
			}
		}
	}
	
	public void sendEvent(final String event, final JSONObject data, final String callbackID) {
		if (event != null
			&& event.length() > 0) {
			try {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put(kJSType, JSTypeEvent);
				jsonObj.put(kJSEvent, event);
				jsonObj.put(kJSData, data);
				jsonObj.put(kJSCallback, callbackID);
				executeScriptInWebView(jsonObj);
			}
			catch (JSONException exception) {
				exception.printStackTrace();
			}
		}
	}
	
	/****************************************************************************************
	 * MESSAGE HANDLING
	 ***************************************************************************************/
	/**
	 * This method is called when the JavaScript sends a message to the native side.
	 * This method should be overridden in subclasses.
	 * @param messageJS : the JSON-message sent by JavaScript.
	 * @return true if the message was handled by the native, false otherwise
	 * @details some basic operations are already implemented : navigation, logs, toasts, native alerts, web alerts
	 * @details this method may be called from a secondary thread.
	 */
	// This method must be public !!!
	@JavascriptInterface
	public boolean handleMessageSentByJavaScript(String message) {
		try {
			final JSONObject jsonObj = new JSONObject(message);
			
			// TYPE
			if (jsonObj.has(kJSType)) {
				String type = jsonObj.getString(kJSType);
				
				//CALLBACK
				if (type.equals(JSTypeCallBack)) {
					String callbackID = jsonObj.getString(kJSCallback);
					JSONObject data = jsonObj.optJSONObject(kJSData);
					
					return handleCallback(callbackID, data);		
				}
				
				// COBALT IS READY
				else if (type.equals(JSTypeCobaltIsReady)) {
					onReady();
					return true;
				}
				
				// EVENT
				if (type.equals(JSTypeEvent)) {
					String event = jsonObj.getString(kJSEvent);
					JSONObject data = jsonObj.optJSONObject(kJSData);
					String callback = jsonObj.optString(kJSCallback, null);
					
					return handleEvent(event, data, callback);			
				}
				
				// LOG
				else if (type.equals(JSTypeLog)) {
					String text = jsonObj.getString(kJSValue);
					Log.d("JS LOG", text);
					return true;
				}
				
				// NAVIGATION
				else if (type.equals(JSTypeNavigation)) {
					String action = jsonObj.getString(kJSAction);
					
					// PUSH
					if (action.equals(JSActionNavigationPush)) {
						JSONObject data = jsonObj.getJSONObject(kJSData);
						String page = data.getString(kJSPage);
						String controller = data.optString(kJSNavigationController, null);
						push(controller, page);
						return true;
					}
					
					// POP
					else if (action.equals(JSActionNavigationPop)) {
						pop();
						return true;
					}
					
					// MODAL
					else if (action.equals(JSActionNavigationModal)) {
						JSONObject data = jsonObj.getJSONObject(kJSData);
						String page = data.getString(kJSPage);
						String controller = data.optString(kJSNavigationController, null);
						String callBackId = jsonObj.optString(kJSCallback, null);
						presentModal(controller, page, callBackId);
						return true;
					}
					
					// DISMISS
					else if (action.equals(JSActionNavigationDismiss)) {
						// TODO: not present in iOS
						JSONObject data = jsonObj.getJSONObject(kJSData);
						String controller = data.getString(kJSNavigationController);
						String page = data.getString(kJSPage);
						dismissModal(controller, page);
						return true;
					}
					
					// UNHANDLED NAVIGATION
					else {
						onUnhandledMessage(jsonObj);
					}
				}
				
				// UI
		    	else if (type.equals(JSTypeUI)) {
			    	String control = jsonObj.getString(kJSUIControl);
					JSONObject data = jsonObj.getJSONObject(kJSData);
					String callback = jsonObj.optString(kJSCallback, null);

					return handleUi(control, data, callback);
		    	}
				
				// WEB LAYER
				else if (type.equals(JSTypeWebLayer)) {
					String action = jsonObj.getString(kJSAction);
					
					// SHOW
					if (action.equals(JSActionWebLayerShow)) {
						final JSONObject data = jsonObj.getJSONObject(kJSData);
						
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								showWebLayer(data);
							}
						});
						
						return true;
					}
					
					// UNHANDLED ACTION
					else {
						onUnhandledMessage(jsonObj);
					}
				}
				
				// UNHANDLED TYPE
				else {
					onUnhandledMessage(jsonObj);
				}
			}
			
			// UNHANDLED MESSAGE
			else {
				onUnhandledMessage(jsonObj);
			}
		} 
		catch (JSONException exception) {
			exception.printStackTrace();
		}
		
		return false;
	}
	
	protected void onReady() {
		if (sDebug) Log.i(TAG, "onReady");

		mCobaltIsReady = true;
		executeWaitingCalls();
	}
	
	private boolean handleCallback(String callback, JSONObject data) {
		try {
			if(callback.equals(JSCallbackOnBackButtonPressed)) {
				onBackPressed(data.getBoolean(kJSValue));
				return true;
			}
			else if (callback.equals(JSCallbackPullToRefreshDidRefresh)) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						onPullToRefreshDidRefresh();
					}
				});
				return true;
			}
			else if (callback.equals(JSCallbackInfiniteScrollDidRefresh)) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						onInfiniteScrollDidRefresh();
					}
				});
				return true;
			}
			else {
				return onUnhandledCallback(callback, data);
			}
		} 
		catch (JSONException exception) {
			exception.printStackTrace();
		}
		
		return false;
	}
	
	protected abstract boolean onUnhandledCallback(String callback, JSONObject data);
	
	private boolean handleEvent(String event, JSONObject data, String callback) {
		return onUnhandledEvent(event, data, callback);
	}
	
	protected abstract boolean onUnhandledEvent(String event, JSONObject data, String callback);
	
	private boolean handleUi(String control, JSONObject data, String callback) {
		try {
			// PICKER
			if (control.equals(JSControlPicker)) {
				String type = data.getString(kJSType);
				
				//DATE
				if (type.equals(JSPickerDate)) {
					JSONObject date = data.optJSONObject(kJSDate);

					Calendar calendar = Calendar.getInstance();
					int year = calendar.get(Calendar.YEAR);
					int month = calendar.get(Calendar.MONTH);
					int day = calendar.get(Calendar.DAY_OF_MONTH);

					if (date != null
						&& date.has(kJSYear)
						&& date.has(kJSMonth)
						&& date.has(kJSDay)) {
						year = date.getInt(kJSYear);
						month = date.getInt(kJSMonth);
						month--;
						day = date.getInt(kJSDay);
					}
					
					JSONObject texts = data.optJSONObject(kJSTexts);
					String title = texts.optString(kJSTitle, null);
					String delete = texts.optString(kJSDelete, null);
					String cancel = texts.optString(kJSCancel, null);
					String validate = texts.optString(kJSValidate, null);
					
					showDatePickerDialog(year, month, day, title, delete, cancel, validate, callback);
					
					return true;
				}
			}
			
			// ALERT
			else if(control.equals(JSControlAlert)) {
				showAlertDialog(data, callback);
				return true;
			}
			
			// TOAST
			else if(control.equals(JSControlToast)) {
				String message = data.getString(kJSAlertMessage);
				Toast.makeText(sContext, message, Toast.LENGTH_SHORT).show();
				return true;
			}
		} 
		catch (JSONException exception) {
			exception.printStackTrace();
		}
		
		// UNHANDLED UI
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(kJSType, JSTypeUI);
			jsonObj.put(kJSUIControl, control);
			jsonObj.put(kJSData, data);
			jsonObj.put(kJSCallback, callback);
			onUnhandledMessage(jsonObj);
		}
		catch (JSONException exception) {
			exception.printStackTrace();
		}
		
		return false;
	}
	
	protected abstract void onUnhandledMessage(JSONObject message);

	/*******************************************************************************************************
	 * LOCAL STORAGE
	 ******************************************************************************************************/
	/**
	 * Local storage substitution for Web views
	 * @author Diane
	 */
	private class LocalStorageJavaScriptInterface {
		
		private Context mContext;
		private LocalStorage mLocalStorage;
		private SQLiteDatabase mDatabase;

		LocalStorageJavaScriptInterface(Context context) {
			mContext = context;
			mLocalStorage = LocalStorage.getInstance(mContext);
		}

		/**
		 * Gets item for the given key
		 * @param key: key to look for
		 * @return item corresponding to the given key
		 */
		@JavascriptInterface
		public String getItem(String key) {
			String value = null;
			
			if (key != null) {
				mDatabase = mLocalStorage.getReadableDatabase();
				Cursor cursor = mDatabase.query(LocalStorage.LOCALSTORAGE_TABLE_NAME,
												null, 
												LocalStorage.LOCALSTORAGE_ID + " = ?",  new String [] {key}, 
												null, null, null);
				if (cursor.moveToFirst()) {
					value = cursor.getString(1);
				}
				cursor.close();
				mDatabase.close();
			}
			
			return value;
		}

		/**
		 * Sets value for the given key.
		 * @param key
		 * @param value
		 */
		@JavascriptInterface
		public void setItem(String key, String value) {
			if (key != null 
				&& value != null) {
				mDatabase = mLocalStorage.getWritableDatabase();
				
				ContentValues values = new ContentValues();
				values.put(LocalStorage.LOCALSTORAGE_ID, key);
				values.put(LocalStorage.LOCALSTORAGE_VALUE, value);
				
				if (getItem(key) != null) {
					mDatabase.update(	LocalStorage.LOCALSTORAGE_TABLE_NAME, 
										values, 
										LocalStorage.LOCALSTORAGE_ID + " = " + key, 
										null);
				}
				else {
					mDatabase.insert(	LocalStorage.LOCALSTORAGE_TABLE_NAME, null, 
										values);
				}
				mDatabase.close();
			}
		}

		/**
		 * Removes item corresponding to the given key
		 * @param key
		 */
		@JavascriptInterface
		public void removeItem(String key) {
			if(key != null) {
				mDatabase = mLocalStorage.getWritableDatabase();
				mDatabase.delete(	LocalStorage.LOCALSTORAGE_TABLE_NAME, 
									LocalStorage.LOCALSTORAGE_ID + " = " + key, 
									null);
				mDatabase.close();
			}
		}

		/**
		 * Clears local storage.
		 */
		@JavascriptInterface
		public void clear() {
			mDatabase = mLocalStorage.getWritableDatabase();
			mDatabase.delete(LocalStorage.LOCALSTORAGE_TABLE_NAME, null, null);
			mDatabase.close();
		}
	}
	
	/********************************************************************************************************************
	 * CONFIGURATION FILE
	 *******************************************************************************************************************/
	
	public static HTMLFragment getFragmentForController(Context applicationContext, Class<?> HTMLFragmentClass, String controller, String page) {
		HTMLFragment fragment = null;
		
		if (sContext == null) {
			sContext = applicationContext;
		}
		
		try {
			if (HTMLFragment.class.isAssignableFrom(HTMLFragmentClass)) {
				fragment = (HTMLFragment) HTMLFragmentClass.newInstance();
				
				Bundle configuration = getConfigurationForController(controller);
				
				if (configuration != null) {
					configuration.putString(kPage, page);
					fragment.setArguments(configuration);
				}
			}
			else {
				if (sDebug) Log.e(TAG, "getFragmentForController: " + HTMLFragmentClass.getSimpleName() + " does not inherit from HTMLFragment!");
			}
		} 
		catch (java.lang.InstantiationException exception) {
			exception.printStackTrace();
		} 
		catch (IllegalAccessException exception) {
			exception.printStackTrace();
		}
		
		return fragment;
	}
	
	private Intent getIntentForController(String controller, String page) {
		Intent intent = null;
		
		Bundle configuration = getConfigurationForController(controller);
		
		if (configuration != null) {
			String activity = configuration.getString(kActivity);
			
			if (activity != null) {
				// Creates intent
				Class<?> pClass;
				try {
					pClass = Class.forName(activity);
					// Instantiates intent only if class inherits from Activity
					if (Activity.class.isAssignableFrom(pClass)) {
						configuration.putString(kPage, page);
						
						intent = new Intent(sContext, pClass);
						intent.putExtra(kExtras, configuration);
					}
					else {
						if (sDebug) Log.e(TAG, "getIntentForController: " + activity + " does not inherit from Activity!");
					}
				} 
				catch (ClassNotFoundException exception) {
					if (sDebug) Log.e(TAG, "getIntentForController: " + activity + " class not found for id " + controller + "!");
					exception.printStackTrace();
				}
			}
		}
		
		return intent;
	}
	
	protected static Bundle getConfigurationForController(String controller) {
		Bundle bundle = null;
		
		JSONObject configuration = getConfiguration();
		String activity = null;
		boolean enablePullToRefresh = false;
		boolean enableInfiniteScroll = false;
		// TODO: add enableGesture
		
		// Gets configuration
		try {
			if (controller != null
				&& configuration.has(controller)) {
				activity = configuration.getJSONObject(controller).getString(kAndroidController);
				enablePullToRefresh = configuration.getJSONObject(controller).optBoolean(kPullToRefresh);
				enableInfiniteScroll = configuration.getJSONObject(controller).optBoolean(kInfiniteScroll);
			}
			else {
				activity = configuration.getJSONObject(JSNavigationControllerDefault).getString(kAndroidController);
				enablePullToRefresh = configuration.getJSONObject(JSNavigationControllerDefault).optBoolean(kPullToRefresh);
				enableInfiniteScroll = configuration.getJSONObject(JSNavigationControllerDefault).optBoolean(kInfiniteScroll);
			}
		}
		catch (JSONException exception) {
			Log.e(TAG, 	"getConfigurationForController: check cobalt.conf. Known issues: \n "
												+ "- \t" + controller + " controller not found and no " + JSNavigationControllerDefault + " controller defined \n "
												+ "- \t" + controller + " or " + JSNavigationControllerDefault + "controller found but no " + kAndroidController + "defined \n ");
			exception.printStackTrace();
			return bundle; // null
		}
		
		bundle = new Bundle();
		bundle.putString(kActivity, activity);
		bundle.putBoolean(kPullToRefresh, enablePullToRefresh);
		bundle.putBoolean(kInfiniteScroll, enableInfiniteScroll);
		
		return bundle;
	}
	
	private static JSONObject getConfiguration() {
		String configuration = readFileFromAssets(Cobalt.getResourcePath() + CONF_FILE);

		try {
			JSONObject jsonObj = new JSONObject(configuration);
			return jsonObj;
		} 
		catch (JSONException exception) {
			if (sDebug) Log.e(TAG, "getConfiguration: check cobalt.conf. File is missing or not at " + ASSETS_PATH + Cobalt.getResourcePath() + CONF_FILE);
			exception.printStackTrace();
		}
		
		return new JSONObject();
	}
	
	private static String readFileFromAssets(String file) {
		try {
			AssetManager assetManager = sContext.getAssets();
			InputStream inputStream = assetManager.open(file);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder fileContent = new StringBuilder();
			int character;
			
			while ((character = bufferedReader.read()) != -1) {
				fileContent.append((char) character) ;  
			}
			
			return fileContent.toString();
		} 
		catch (FileNotFoundException exception) {
			if (sDebug) Log.e(TAG, "getFileContentFromAssets: " + file + "not found.");
		} 
		catch (IOException exception) {
			exception.printStackTrace();
		}
		
		return new String();
	}
	
	/*****************************************************************************************************************
	 * NAVIGATION
	 ****************************************************************************************************************/
	private void push(String controller, String page) {
		Intent intent = getIntentForController(controller, page);
		if(intent != null) {
			getActivity().startActivity(intent);
		}
		else if (sDebug) Log.w(TAG, "push: Unable to push " + controller + " controller");
	}
	
	private void pop() {
		onBackPressed(true);
	}
	
	private void presentModal(String controller, String page, String callBackID) {
		Intent intent = getIntentForController(controller, page);
		
		if (intent != null) {
			getActivity().startActivity(intent);
			// Sends callback to store current activity & HTML page for dismiss
			try {
				JSONObject data = new JSONObject();
				data.put(kJSPage, mPage);
				data.put(kJSNavigationController, getActivity() != null ? getActivity().getClass().getName() : null);		
				sendCallback(callBackID, data);
			} 
			catch (JSONException exception) {
				exception.printStackTrace();
			}
		}
		else if (sDebug) Log.e(TAG, "presentModal: Unable to present modal " + controller + " controller");
	}

	private void dismissModal(String controller, String page) {
		try {
			Class<?> pClass = Class.forName(controller);

			// Instantiates intent only if class inherits from Activity
			if (Activity.class.isAssignableFrom(pClass)) {
				Bundle bundle = new Bundle();
				bundle.putString(kPage, page);

				Intent intent = new Intent(sContext, pClass);
				intent.putExtra(kExtras, bundle);

				NavUtils.navigateUpTo(getActivity(), intent);
			}
			else if(sDebug) Log.e(TAG, "dismissModal: unable to dismiss modal since " + controller + " does not inherit from Activity");
		} 
		catch (ClassNotFoundException exception) {
			if (sDebug) Log.e(TAG, "dismissModal: " + controller + "not found");
			exception.printStackTrace();
		}
	}
	
	/**
	 * Called when onBackPressed event is fired. Asks the Web view for back permission.
	 * This method should NOT be overridden in subclasses.
	 */
	public void askWebViewForBackPermission() {
		sendEvent(JSEventOnBackButtonPressed, null, JSCallbackOnBackButtonPressed);
	}
	
	/**
	 * Called when the Web view allowed or not the onBackPressed event.
	 * @param allowedToBack: 	if true, the onBackPressed method of activity will be called, 
	 * 							onBackDenied() will be called otherwise
	 * @details This method should not be overridden in subclasses
	 */
	protected void onBackPressed(boolean allowedToBack) {
		if (allowedToBack) {
			HTMLActivity activity = (HTMLActivity) getActivity();
			if (activity != null) {
				activity.back();
			}
			else if(sDebug) Log.e(TAG, "onBackButtonPressed: activity is null, cannot call back");
		}
		else {
			onBackDenied();
		}
	}
	
	/**
	 * Called when onBackPressed event is denied by the Web view.
	 * @details This method may be overridden in subclasses.
	 */
	protected void onBackDenied() {
		if(sDebug) Log.i(TAG, "onBackDenied: onBackPressed event denied by Web view");
	}
	
	/***********************************************************************************************************************************
	 * WEB LAYER
	 **********************************************************************************************************************************/
	private void showWebLayer(JSONObject data) {
		if (getActivity() != null) {
			try {
				String page = data.getString(kJSPage);
				double fadeDuration = data.optDouble(kJSWebLayerFadeDuration, 0.3);

				Bundle bundle = new Bundle();
				bundle.putString(kPage, page);
				
				HTMLWebLayerFragment webLayerFragment = getWebLayerFragment();
				webLayerFragment.setArguments(bundle);

				android.support.v4.app.FragmentTransaction fragmentTransition;
				fragmentTransition = getActivity().getSupportFragmentManager().beginTransaction();

				if (fadeDuration > 0) {
					fragmentTransition.setCustomAnimations(	android.R.anim.fade_in, android.R.anim.fade_out, 
															android.R.anim.fade_in, android.R.anim.fade_out);
				}
				else {
					fragmentTransition.setTransition(FragmentTransaction.TRANSIT_NONE);
				}

				if (HTMLActivity.class.isAssignableFrom(getActivity().getClass())) {
					// Dismiss current Web layer if one is already shown
					HTMLActivity activity = (HTMLActivity) getActivity();
					Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(activity.getFragmentContainerId());
					if (HTMLWebLayerFragment.class.isAssignableFrom(currentFragment.getClass())) {
						HTMLWebLayerFragment webLayerToDismiss = (HTMLWebLayerFragment) currentFragment;
						webLayerToDismiss.dismissWebLayer(null);
					}

					// Shows Web layer
					if (activity.findViewById(activity.getFragmentContainerId()) != null) {
						fragmentTransition.add(activity.getFragmentContainerId(), webLayerFragment);
						fragmentTransition.commit();
					}
					else if(sDebug) Log.e(TAG, "showWebLayer: fragment container not found");
				}
			} 
			catch (JSONException exception) {
				exception.printStackTrace();
			}
		}
		else if(sDebug) Log.e(TAG, "showWebLayer: unable to show a Web layer from a fragment not attached to an activity!");
	}
	
	/**
	 * Returns new instance of a {@link HTMLWebLayerFragment}
	 * @return a new instance of a {@link HTMLWebLayerFragment}
	 * This method may be overridden in subclasses if the {@link HTMLWebLayerFragment} must implement customized stuff.
	 */
	protected HTMLWebLayerFragment getWebLayerFragment() {
		return new HTMLWebLayerFragment();
	}
	
	/**
	 * Called from the corresponding {@link HTMLWebLayerFragment} when dismissed.
	 * This method may be overridden in subclasses.
	 */
	public void onWebLayerDismiss(final String page, final JSONObject data) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put(kJSPage, page);
					jsonObj.put(kJSData, data);

					sendEvent(JSEventWebLayerOnDismiss, jsonObj, null);
				} 
				catch (JSONException exception) {
					exception.printStackTrace();
				}
			}
		});
	}
	
	/******************************************************************************************************************
	 * ALERT DIALOG
	 *****************************************************************************************************************/
	private void showAlertDialog(JSONObject data, final String callback) {		
		try {
			String title = data.optString(kJSAlertTitle);
			String message = data.optString(kJSAlertMessage);
			boolean cancelable = data.optBoolean(kJSAlertCancelable, false);
			JSONArray buttons = data.has(kJSAlertButtons) ? data.getJSONArray(kJSAlertButtons) : new JSONArray();

			AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
			alert.setTitle(title);
			alert.setMessage(message);

			AlertDialog mAlert = alert.create();
			mAlert.setCancelable(cancelable);
			
			if (buttons.length() == 0) {
				mAlert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (callback != null) {
							try {
								JSONObject data = new JSONObject();
								data.put(kJSAlertButtonIndex, 0);
								sendCallback(callback, data);
							} 
							catch (JSONException exception) {
								exception.printStackTrace();
							}								
						}
					}
				});
			}
			else {
				int realSize = Math.min(buttons.length(), 3);
				for (int i = 1 ; i <= realSize ; i++) {
					mAlert.setButton(-i, buttons.getString(i-1), new DialogInterface.OnClickListener() {	
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (callback != null) {
								try {
									JSONObject data = new JSONObject();
									data.put(kJSAlertButtonIndex, -which-1);
									sendCallback(callback, data);
								} 
								catch (JSONException exception) {
									exception.printStackTrace();
								}
							}
						}
					});
				}
			}
			
			mAlert.show();
		} 
		catch (JSONException exception) {
			exception.printStackTrace();
		}
	}
	
	/*************************************************************************************
     * DATE PICKER
     ************************************************************************************/
    protected void showDatePickerDialog(int year, int month, int day, String title, String delete, String cancel, String validate, String callbackID) {
    	Bundle args = new Bundle();
    	args.putInt(HTMLDatePickerFragment.ARG_YEAR, year);
    	args.putInt(HTMLDatePickerFragment.ARG_MONTH, month);
    	args.putInt(HTMLDatePickerFragment.ARG_DAY, day);
    	args.putString(HTMLDatePickerFragment.ARG_TITLE, title);
    	args.putString(HTMLDatePickerFragment.ARG_DELETE, delete);
    	args.putString(HTMLDatePickerFragment.ARG_CANCEL, cancel);
    	args.putString(HTMLDatePickerFragment.ARG_VALIDATE, validate);
    	args.putString(HTMLDatePickerFragment.ARG_CALLBACK_ID, callbackID);
    	
    	HTMLDatePickerFragment newFragment = new HTMLDatePickerFragment();
        newFragment.setArguments(args);
        newFragment.setListener(this);
        
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }
    
    protected void sendDate(int year, int month, int day, String callbackID) {
    	try {
    		JSONObject jsonDate = new JSONObject();
    		if (year != 0 || month != 0 || day != 0) {	
    			jsonDate.put(kJSYear, year);
    			month++;
    			jsonDate.put(kJSMonth, month);
    			jsonDate.put(kJSDay, day);
    		}
    		JSONObject jsonResponse = new JSONObject();
    		jsonResponse.put(kJSType, JSTypeCallBack);
			jsonResponse.put(kJSCallback, callbackID);
			jsonResponse.put(kJSData, jsonDate);
			executeScriptInWebView(jsonResponse);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
    
    /******************************************************************************************************************************
	 * PULL TO REFRESH
	 *****************************************************************************************************************************/
	
	/**
	 * Enables pull-to-refresh feature
	 * mPullRefreshWebView must be set
	 */
	
	public void enablePullToRefresh() {
		if (mPullToRefreshWebView != null) {
			mPullToRefreshWebView.setMode(Mode.PULL_FROM_START);
		}
		else if(sDebug) Log.e(TAG, "Unable to enable pull-to-refresh feature. mPullToRefreshWebView must be set.");
	}
	
	/**
	 * Disables pull-to-refresh feature
	 * mPullRefreshWebView must be set 
	 */
	public void disablePullToRefresh() {
		if(mPullToRefreshWebView != null) {
			mPullToRefreshWebView.setMode(Mode.DISABLED);
			mPullToRefreshActivate = false;
		}
		else if(sDebug && mPullToRefreshActivate) Log.e(TAG, "Unable to disable pull-to-refresh feature. mPullToRefreshWebView must be set.");
	}
	
	/**
	 * Returns a boolean to know if pull-to-refresh feature is enabled
	 * @return 	true if pull-to-refresh is enabled, 
	 * 			false otherwise
	 */
	public boolean isPullToRefreshEnabled() {
		return ! (mPullToRefreshWebView.getMode() == Mode.DISABLED);
	}
	
	/**
	 * Customizes pull-to-refresh loading view.
	 * @param pullLabel: text shown when user pulling
	 * @param refreshingLabel: text shown while refreshing
	 * @param releaseLabel: text shown when refreshed
	 * @param lastUpdatedLabel: text shown for the last update
	 * @param loadingDrawable: drawable shown when user pulling
	 * @details loadingDrawable animation or labels text color customization must be done in the layout.
	 * @example ptr:ptrAnimationStyle="flip|rotate"
	 */
	protected void setCustomTitlesAndImage(	String pullLabel, String refreshingLabel, String releaseLabel, String lastUpdatedLabel, 
											Drawable loadingDrawable, Typeface typeface) {
		LoadingLayoutProxy loadingLayoutProxy = ((LoadingLayoutProxy) mPullToRefreshWebView.getLoadingLayoutProxy());
		if (lastUpdatedLabel != null) {
			loadingLayoutProxy.setLastUpdatedLabel(lastUpdatedLabel);
		}
		if (pullLabel != null) {
			loadingLayoutProxy.setPullLabel(pullLabel);
		}
		if (refreshingLabel != null) {
			loadingLayoutProxy.setRefreshingLabel(refreshingLabel);
		}
		if (releaseLabel != null) {
			loadingLayoutProxy.setReleaseLabel(releaseLabel);
		}
		if (loadingDrawable != null) {
			loadingLayoutProxy.setLoadingDrawable(loadingDrawable);
		}
		if (typeface != null) {
			loadingLayoutProxy.setTextTypeface(typeface);
		}
	}
	
	/**
	 * Customizes pull-to-refresh last updated label
	 * @param text: text of last updated label
	 */
	protected void setLastUpdatedLabel(String text) {
		LoadingLayoutProxy loadingLayoutProxy = (LoadingLayoutProxy) mPullToRefreshWebView.getLoadingLayoutProxy();
		if (text != null) {
			loadingLayoutProxy.setLastUpdatedLabel(text);
		}
	}
	
	private void refreshWebView() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put(kJSType, JSTypeEvent);
					jsonObj.put(kJSEvent, JSEventPullToRefresh);
					jsonObj.put(kJSCallback, JSCallbackPullToRefreshDidRefresh);
					executeScriptInWebView(jsonObj);
				} 
				catch (JSONException exception) {
					exception.printStackTrace();
				}
			}
		});
	}
	
	private void onPullToRefreshDidRefresh() {
		mPullToRefreshWebView.onRefreshComplete();
		onPullToRefreshRefreshed();
	}

	/**
	 * This method may be overridden in subclasses.
	 */
	protected abstract void onPullToRefreshRefreshed();

	
	/************************************************************************************
	 * INFINITE SCROLL
	 ***********************************************************************************/
	
	@Override
	public void onOverScrolled(int scrollX, int scrollY,int oldscrollX, int oldscrollY) {
		float density = sContext.getResources().getDisplayMetrics().density;
		// Round density in case it is too precise (and big)
		if (density > 1) {
			density = (float) (Math.floor(density * 10) / 10.0);
		}
		
		int yPosition = (int) ((mWebView.getScrollY() + mWebView.getHeight()) / density);
		if (yPosition >= mWebView.getContentHeight()) {
			infiniteScrollRefresh();
		}
	}
	
	/**
	 * Enables infinite scroll feature
	 */
	public void enableInfiniteScroll() {
		mInfiniteScrollEnabled = true;
	}

	/**
	 * Disables infinite scroll feature
	 */
	public void disableInfiniteScroll() {
		mInfiniteScrollEnabled = false;
	}

	/**
	 * Returns a boolean to know if the infinite scroll feature is enabled
	 * @return 	true if infinite scroll is enabled, 
	 * 			false otherwise
	 */
	public boolean isInfiniteScrollEnabled() {
		return mInfiniteScrollEnabled;
	}

	private void infiniteScrollRefresh() {
		if (mInfiniteScrollEnabled 
			&& ! mInfiniteScrollRefreshing) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					try {
						JSONObject jsonObj = new JSONObject();
						jsonObj.put(kJSType,JSTypeEvent);
						jsonObj.put(kJSEvent, JSEventInfiniteScroll);
						jsonObj.put(kJSCallback, JSCallbackInfiniteScrollDidRefresh);
						executeScriptInWebView(jsonObj);
						mInfiniteScrollRefreshing = true;
					} 
					catch (JSONException exception) {
						exception.printStackTrace();
					}
				}
			});
		}
	}
	
	private void onInfiniteScrollDidRefresh() {
		mInfiniteScrollRefreshing = false;
		onInfiniteScrollRefreshed();
	}

	/**
	 * This method may be overridden in subclasses.
	 */
	protected abstract void onInfiniteScrollRefreshed();
	
	
    /*****************************************************************
	 * HELPERS
	 ****************************************************************/
	
	private void setFeaturesWantedActive() {
		Boolean argsPTR = pullToRefreshActivate();	
		if(argsPTR) {
			enablePullToRefresh();
		}
		else {
			disablePullToRefresh();
		}
		
		mInfiniteScrollEnabled = infiniteScrollEnabled();
	}
	
	private boolean pullToRefreshActivate() {
		Bundle args = getArguments();
		if (args != null)	{
			return args.getBoolean(kPullToRefresh);
		}
		else {
			disablePullToRefresh();
			return false;
		}
	}
	
	private boolean infiniteScrollEnabled() {
		Bundle args = getArguments();
		if (args != null) {
			return args.getBoolean(kInfiniteScroll);
		}
		else {
			mInfiniteScrollEnabled = false;
			return false;
		}
	}
	/**
	 * Fired by {@link ScaleWebViewClient} to inform Web view its scale changed (pull-to-refresh need to know that to show its header appropriately).
	 */
	public void notifyScaleChange(float oldScale, float newScale) {
		mPullToRefreshWebView.setWebviewScale(newScale);
	}
}