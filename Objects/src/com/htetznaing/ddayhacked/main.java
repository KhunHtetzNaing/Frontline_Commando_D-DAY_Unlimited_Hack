package com.htetznaing.ddayhacked;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.htetznaing.ddayhacked", "com.htetznaing.ddayhacked.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.htetznaing.ddayhacked", "com.htetznaing.ddayhacked.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.htetznaing.ddayhacked.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        Object[] o;
        if (permissions.length > 0)
            o = new Object[] {permissions[0], grantResults[0] == 0};
        else
            o = new Object[] {"", false};
        processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static String _link1 = "";
public static anywheresoftware.b4a.objects.Timer _t = null;
public static int _theme_value = 0;
public anywheresoftware.b4a.objects.ProgressBarWrapper _progressbar1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnobb = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btncancel = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnresume = null;
public anywheresoftware.b4a.objects.ButtonWrapper _install = null;
public anywheresoftware.b4a.objects.ButtonWrapper _tutotial = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper _banner = null;
public mobi.mindware.admob.interstitial.AdmobInterstitialsAds _interstitial = null;
public anywheresoftware.b4a.object.XmlLayoutBuilder _res = null;
public anywheresoftware.b4a.objects.collections.List _lis = null;
public com.maximus.id.id _idd = null;
public static int _idd_int = 0;
public anywheresoftware.b4a.phone.Phone _p = null;
public com.htetznaing.ddayhacked.httputils2service _httputils2service = null;
public com.htetznaing.ddayhacked.downloadservice _downloadservice = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 38;BA.debugLine="p.SetScreenOrientation(1)";
mostCurrent._p.SetScreenOrientation(processBA,(int) (1));
 //BA.debugLineNum = 39;BA.debugLine="If p.SdkVersion > 19 Then";
if (mostCurrent._p.getSdkVersion()>19) { 
 //BA.debugLineNum = 40;BA.debugLine="Banner.Initialize(\"Banner\",\"ca-app-pub-4173348573";
mostCurrent._banner.Initialize(mostCurrent.activityBA,"Banner","ca-app-pub-4173348573252986/9809481351");
 //BA.debugLineNum = 41;BA.debugLine="Banner.LoadAd";
mostCurrent._banner.LoadAd();
 //BA.debugLineNum = 42;BA.debugLine="Activity.AddView(Banner,0%x,90%y,100%x,10%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._banner.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (90),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 44;BA.debugLine="Interstitial.Initialize(\"Interstitial\",\"ca-app-pu";
mostCurrent._interstitial.Initialize(mostCurrent.activityBA,"Interstitial","ca-app-pub-4173348573252986/2286214551");
 //BA.debugLineNum = 45;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd(mostCurrent.activityBA);
 };
 //BA.debugLineNum = 47;BA.debugLine="t.Initialize(\"t\",15000)";
_t.Initialize(processBA,"t",(long) (15000));
 //BA.debugLineNum = 48;BA.debugLine="t.Enabled = True";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 50;BA.debugLine="Install.Initialize(\"install\")";
mostCurrent._install.Initialize(mostCurrent.activityBA,"install");
 //BA.debugLineNum = 51;BA.debugLine="Install.Text = \"Install\"";
mostCurrent._install.setText((Object)("Install"));
 //BA.debugLineNum = 52;BA.debugLine="Tutotial.Initialize(\"Tutorial\")";
mostCurrent._tutotial.Initialize(mostCurrent.activityBA,"Tutorial");
 //BA.debugLineNum = 53;BA.debugLine="Tutotial.Text = \"Tutorial\"";
mostCurrent._tutotial.setText((Object)("Tutorial"));
 //BA.debugLineNum = 55;BA.debugLine="btnOBB.Initialize(\"btnOBB\")";
mostCurrent._btnobb.Initialize(mostCurrent.activityBA,"btnOBB");
 //BA.debugLineNum = 56;BA.debugLine="btnOBB.Text = \"Add Obb File\"";
mostCurrent._btnobb.setText((Object)("Add Obb File"));
 //BA.debugLineNum = 57;BA.debugLine="btnCancel.Initialize(\"btnCancel\")";
mostCurrent._btncancel.Initialize(mostCurrent.activityBA,"btnCancel");
 //BA.debugLineNum = 58;BA.debugLine="btnCancel.Text = \"Cancel\"";
mostCurrent._btncancel.setText((Object)("Cancel"));
 //BA.debugLineNum = 59;BA.debugLine="btnResume.Initialize(\"btnResume\")";
mostCurrent._btnresume.Initialize(mostCurrent.activityBA,"btnResume");
 //BA.debugLineNum = 60;BA.debugLine="btnResume.Text = \"Download\"";
mostCurrent._btnresume.setText((Object)("Download"));
 //BA.debugLineNum = 62;BA.debugLine="Label1.Initialize(\"Label1\")";
mostCurrent._label1.Initialize(mostCurrent.activityBA,"Label1");
 //BA.debugLineNum = 63;BA.debugLine="ProgressBar1.Initialize(\"ProgressBar1\")";
mostCurrent._progressbar1.Initialize(mostCurrent.activityBA,"ProgressBar1");
 //BA.debugLineNum = 65;BA.debugLine="Activity.AddView(Install,20%x,30%y,60%x,10%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._install.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (30),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 66;BA.debugLine="Activity.AddView(btnOBB,20%x,(Install.Top+Install";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._btnobb.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),(int) ((mostCurrent._install.getTop()+mostCurrent._install.getHeight())+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (2),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 67;BA.debugLine="Activity.AddView(Tutotial,20%x,(btnOBB.Top+btnOBB";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._tutotial.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),(int) ((mostCurrent._btnobb.getTop()+mostCurrent._btnobb.getHeight())+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (2),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 69;BA.debugLine="Activity.AddMenuItem(\"Stop Showing Ads!\",\"rad\")";
mostCurrent._activity.AddMenuItem("Stop Showing Ads!","rad");
 //BA.debugLineNum = 70;BA.debugLine="Activity.AddMenuItem(\"Change Theme\",\"ct\")";
mostCurrent._activity.AddMenuItem("Change Theme","ct");
 //BA.debugLineNum = 71;BA.debugLine="Activity.AddMenuItem3(\"Share This App\",\"share\",Loa";
mostCurrent._activity.AddMenuItem3("Share This App","share",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"share.png").getObject()),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 73;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
int _answ = 0;
 //BA.debugLineNum = 250;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 251;BA.debugLine="Dim Answ As Int";
_answ = 0;
 //BA.debugLineNum = 252;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 253;BA.debugLine="Answ = Msgbox2(\"Do you want to Exit App?\", \"At";
_answ = anywheresoftware.b4a.keywords.Common.Msgbox2("Do you want to Exit App?","Attention!","Yes","","No",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);
 //BA.debugLineNum = 254;BA.debugLine="If Answ = DialogResponse.NEGATIVE Then";
if (_answ==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 255;BA.debugLine="If File.Exists(File.DirRootExternal,\"D_Day_3.0.4";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"D_Day_3.0.4.apk")) { 
 //BA.debugLineNum = 256;BA.debugLine="File.Delete(File.DirRootExternal,\"D_Day_3.0.4.ap";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"D_Day_3.0.4.apk");
 //BA.debugLineNum = 257;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 };
 };
 };
 //BA.debugLineNum = 261;BA.debugLine="If Answ = DialogResponse.POSITIVE Then";
if (_answ==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 262;BA.debugLine="If File.Exists(File.DirRootExternal,\"D_Day_3.0.";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"D_Day_3.0.4.apk")) { 
 //BA.debugLineNum = 263;BA.debugLine="File.Delete(File.DirRootExternal,\"D_Day_3.0.4.ap";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"D_Day_3.0.4.apk");
 //BA.debugLineNum = 264;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 };
 //BA.debugLineNum = 267;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 246;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 248;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 242;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 244;BA.debugLine="End Sub";
return "";
}
public static String  _btncancel_click() throws Exception{
 //BA.debugLineNum = 230;BA.debugLine="Sub btnCancel_Click";
 //BA.debugLineNum = 231;BA.debugLine="CallSubDelayed2(DownloadService, \"CancelDownload";
anywheresoftware.b4a.keywords.Common.CallSubDelayed2(mostCurrent.activityBA,(Object)(mostCurrent._downloadservice.getObject()),"CancelDownload",(Object)(_link1));
 //BA.debugLineNum = 232;BA.debugLine="End Sub";
return "";
}
public static String  _btnobb_click() throws Exception{
long _size = 0L;
int _a = 0;
com.htetznaing.ddayhacked.downloadservice._downloaddata _dd = null;
 //BA.debugLineNum = 175;BA.debugLine="Sub btnOBB_Click";
 //BA.debugLineNum = 176;BA.debugLine="Dim size As Long";
_size = 0L;
 //BA.debugLineNum = 177;BA.debugLine="size = File.Size(File.DirRootExternal &\"/Android";
_size = anywheresoftware.b4a.keywords.Common.File.Size(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Android/obb/com.glu.flcn_new","main.304.com.glu.flcn_new.obb");
 //BA.debugLineNum = 178;BA.debugLine="If size > 328612 Then";
if (_size>328612) { 
 //BA.debugLineNum = 179;BA.debugLine="Msgbox(\"You are already installed hacked! if yo";
anywheresoftware.b4a.keywords.Common.Msgbox("You are already installed hacked! if you have any problem? Unistall current D Day Game App and try again.","Attention!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 181;BA.debugLine="Msgbox(\"Firstly! Turn On Your internet!\",\"Need";
anywheresoftware.b4a.keywords.Common.Msgbox("Firstly! Turn On Your internet!","Need Internet Connection!",mostCurrent.activityBA);
 //BA.debugLineNum = 182;BA.debugLine="Dim a As Int";
_a = 0;
 //BA.debugLineNum = 183;BA.debugLine="a = 	Msgbox2(\"You NEED Download HACKED Unlimted";
_a = anywheresoftware.b4a.keywords.Common.Msgbox2("You NEED Download HACKED Unlimted OBB File. CLICK 'OK' button below to Start Download File and after you will get D-Day Unlimited Hacked","Attention!","OK","","Cancel",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);
 //BA.debugLineNum = 184;BA.debugLine="If a = DialogResponse.POSITIVE Then";
if (_a==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 185;BA.debugLine="Activity.AddView(btnCancel,10%x,15%y,35%x,10%y";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._btncancel.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (10),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (15),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (35),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 186;BA.debugLine="Activity.AddView(btnResume,55%x,15%y,35%x,10%y";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._btnresume.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (55),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (15),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (35),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 187;BA.debugLine="Activity.AddView(ProgressBar1,5%x,5%y,90%x,2%y";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._progressbar1.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (90),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (2),mostCurrent.activityBA));
 //BA.debugLineNum = 188;BA.debugLine="Activity.AddView(Label1,5%x,(ProgressBar1.Top+Pro";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._label1.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),(int) ((mostCurrent._progressbar1.getTop()+mostCurrent._progressbar1.getHeight())+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (2),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (90),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 189;BA.debugLine="Dim dd As DownloadData";
_dd = new com.htetznaing.ddayhacked.downloadservice._downloaddata();
 //BA.debugLineNum = 190;BA.debugLine="dd.url = link1";
_dd.url = _link1;
 //BA.debugLineNum = 191;BA.debugLine="dd.EventName = \"dd\"";
_dd.EventName = "dd";
 //BA.debugLineNum = 192;BA.debugLine="dd.Target = Me";
_dd.Target = main.getObject();
 //BA.debugLineNum = 193;BA.debugLine="CallSubDelayed2(DownloadService, \"StartDownload\",";
anywheresoftware.b4a.keywords.Common.CallSubDelayed2(mostCurrent.activityBA,(Object)(mostCurrent._downloadservice.getObject()),"StartDownload",(Object)(_dd));
 };
 };
 //BA.debugLineNum = 196;BA.debugLine="End Sub";
return "";
}
public static String  _btnresume_click() throws Exception{
com.htetznaing.ddayhacked.downloadservice._downloaddata _dd = null;
 //BA.debugLineNum = 234;BA.debugLine="Sub btnResume_Click";
 //BA.debugLineNum = 235;BA.debugLine="Dim dd As DownloadData";
_dd = new com.htetznaing.ddayhacked.downloadservice._downloaddata();
 //BA.debugLineNum = 236;BA.debugLine="dd.url = link1";
_dd.url = _link1;
 //BA.debugLineNum = 237;BA.debugLine="dd.EventName = \"dd\"";
_dd.EventName = "dd";
 //BA.debugLineNum = 238;BA.debugLine="dd.Target = Me";
_dd.Target = main.getObject();
 //BA.debugLineNum = 239;BA.debugLine="CallSubDelayed2(DownloadService, \"StartDownload\",";
anywheresoftware.b4a.keywords.Common.CallSubDelayed2(mostCurrent.activityBA,(Object)(mostCurrent._downloadservice.getObject()),"StartDownload",(Object)(_dd));
 //BA.debugLineNum = 240;BA.debugLine="End Sub";
return "";
}
public static String  _ct_click() throws Exception{
 //BA.debugLineNum = 88;BA.debugLine="Sub ct_Click";
 //BA.debugLineNum = 89;BA.debugLine="lis.Initialize";
mostCurrent._lis.Initialize();
 //BA.debugLineNum = 90;BA.debugLine="lis.AddAll(Array As String(\"Holo\",\"Holo Light\",\"H";
mostCurrent._lis.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"Holo","Holo Light","Holo Light Dark","Old Android","Material","Material Light","Material Light Dark","Transparent","Transparent No Title Bar"}));
 //BA.debugLineNum = 91;BA.debugLine="idd_int = idd.InputList1(lis,\"Choose Themes!\")";
_idd_int = mostCurrent._idd.InputList1(mostCurrent._lis,"Choose Themes!",mostCurrent.activityBA);
 //BA.debugLineNum = 92;BA.debugLine="If idd_int = 0 Then";
if (_idd_int==0) { 
 //BA.debugLineNum = 93;BA.debugLine="SetTheme(res.GetResourceId(\"style\", \"android:sty";
_settheme(mostCurrent._res.GetResourceId("style","android:style/Theme.Holo"));
 };
 //BA.debugLineNum = 96;BA.debugLine="If idd_int = 1 Then";
if (_idd_int==1) { 
 //BA.debugLineNum = 97;BA.debugLine="SetTheme(res.GetResourceId(\"style\", \"android:sty";
_settheme(mostCurrent._res.GetResourceId("style","android:style/Theme.Holo.Light"));
 };
 //BA.debugLineNum = 100;BA.debugLine="If idd_int = 2 Then";
if (_idd_int==2) { 
 //BA.debugLineNum = 101;BA.debugLine="SetTheme(res.GetResourceId(\"style\", \"android:sty";
_settheme(mostCurrent._res.GetResourceId("style","android:style/Theme.Holo.Light.DarkActionBar"));
 };
 //BA.debugLineNum = 104;BA.debugLine="If idd_int = 3 Then";
if (_idd_int==3) { 
 //BA.debugLineNum = 105;BA.debugLine="SetTheme(16973829)";
_settheme((int) (16973829));
 };
 //BA.debugLineNum = 108;BA.debugLine="If idd_int = 4 Then";
if (_idd_int==4) { 
 //BA.debugLineNum = 109;BA.debugLine="SetTheme(res.GetResourceId(\"style\", \"android:sty";
_settheme(mostCurrent._res.GetResourceId("style","android:style/Theme.Material"));
 };
 //BA.debugLineNum = 112;BA.debugLine="If idd_int = 5 Then";
if (_idd_int==5) { 
 //BA.debugLineNum = 113;BA.debugLine="SetTheme(res.GetResourceId(\"style\", \"android:sty";
_settheme(mostCurrent._res.GetResourceId("style","android:style/Theme.Material.Light"));
 };
 //BA.debugLineNum = 116;BA.debugLine="If idd_int = 6 Then";
if (_idd_int==6) { 
 //BA.debugLineNum = 117;BA.debugLine="SetTheme(res.GetResourceId(\"style\", \"android:sty";
_settheme(mostCurrent._res.GetResourceId("style","android:style/Theme.Material.Light.DarkActionBar"));
 };
 //BA.debugLineNum = 120;BA.debugLine="If idd_int = 7 Then";
if (_idd_int==7) { 
 //BA.debugLineNum = 121;BA.debugLine="SetTheme(res.GetResourceId(\"style\", \"android:styl";
_settheme(mostCurrent._res.GetResourceId("style","android:style/Theme.Translucent"));
 };
 //BA.debugLineNum = 124;BA.debugLine="If idd_int = 8 Then";
if (_idd_int==8) { 
 //BA.debugLineNum = 125;BA.debugLine="SetTheme(res.GetResourceId(\"style\", \"android:styl";
_settheme(mostCurrent._res.GetResourceId("style","android:style/Theme.Translucent.NoTitleBar"));
 };
 //BA.debugLineNum = 128;BA.debugLine="End Sub";
return "";
}
public static String  _dd_complete(com.htetznaing.ddayhacked.httpjob _job) throws Exception{
String _mypath = "";
anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper _o = null;
long _size = 0L;
 //BA.debugLineNum = 209;BA.debugLine="Sub dd_Complete(Job As HttpJob)";
 //BA.debugLineNum = 210;BA.debugLine="Dim MyPath As String";
_mypath = "";
 //BA.debugLineNum = 211;BA.debugLine="MyPath = File.DirRootExternal & \"/Android/obb/com";
_mypath = anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Android/obb/com.glu.flcn_new";
 //BA.debugLineNum = 212;BA.debugLine="If 	File.Exists(MyPath,\"\") = False Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(_mypath,"")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 213;BA.debugLine="File.MakeDir(File.DirRootExternal &\"/Android/obb\"";
anywheresoftware.b4a.keywords.Common.File.MakeDir(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Android/obb","com.glu.flcn_new");
 };
 //BA.debugLineNum = 215;BA.debugLine="Dim o As OutputStream";
_o = new anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper();
 //BA.debugLineNum = 216;BA.debugLine="o = File.OpenOutput(MyPath, \"main.304.com.glu.";
_o = anywheresoftware.b4a.keywords.Common.File.OpenOutput(_mypath,"main.304.com.glu.flcn_new.obb",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 217;BA.debugLine="File.Copy2(Job.GetInputStream, o)";
anywheresoftware.b4a.keywords.Common.File.Copy2((java.io.InputStream)(_job._getinputstream().getObject()),(java.io.OutputStream)(_o.getObject()));
 //BA.debugLineNum = 218;BA.debugLine="o.Close";
_o.Close();
 //BA.debugLineNum = 219;BA.debugLine="Log(\"Job completed: \" & Job.Success)";
anywheresoftware.b4a.keywords.Common.Log("Job completed: "+BA.ObjectToString(_job._success));
 //BA.debugLineNum = 220;BA.debugLine="Job.Release";
_job._release();
 //BA.debugLineNum = 221;BA.debugLine="Dim size As Long";
_size = 0L;
 //BA.debugLineNum = 222;BA.debugLine="size = File.Size(File.DirRootExternal &\"/Android";
_size = anywheresoftware.b4a.keywords.Common.File.Size(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Android/obb/com.glu.flcn_new","main.304.com.glu.flcn_new.obb");
 //BA.debugLineNum = 223;BA.debugLine="If size > 328612 Then";
if (_size>328612) { 
 //BA.debugLineNum = 224;BA.debugLine="Msgbox2(\"D-Day Unlimited Hacked Install Finished.";
anywheresoftware.b4a.keywords.Common.Msgbox2("D-Day Unlimited Hacked Install Finished. Enjoy :)","Completed","Ok","","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 226;BA.debugLine="Msgbox(\"You download File not completed, please";
anywheresoftware.b4a.keywords.Common.Msgbox("You download File not completed, please Download again!","ERROR!",mostCurrent.activityBA);
 };
 //BA.debugLineNum = 228;BA.debugLine="End Sub";
return "";
}
public static String  _dd_progress(long _progress,long _total) throws Exception{
 //BA.debugLineNum = 203;BA.debugLine="Sub dd_Progress(Progress As Long, Total As Long)";
 //BA.debugLineNum = 204;BA.debugLine="ProgressBar1.Progress = Progress / Total * 100";
mostCurrent._progressbar1.setProgress((int) (_progress/(double)_total*100));
 //BA.debugLineNum = 205;BA.debugLine="Label1.Text = NumberFormat(Progress / 1024, 0, 0)";
mostCurrent._label1.setText((Object)(anywheresoftware.b4a.keywords.Common.NumberFormat(_progress/(double)1024,(int) (0),(int) (0))+"KB / "+anywheresoftware.b4a.keywords.Common.NumberFormat(_total/(double)1024,(int) (0),(int) (0))+"KB"));
 //BA.debugLineNum = 207;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 22;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 23;BA.debugLine="Dim ProgressBar1 As ProgressBar";
mostCurrent._progressbar1 = new anywheresoftware.b4a.objects.ProgressBarWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim Label1 As Label";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim btnOBB As Button";
mostCurrent._btnobb = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim btnCancel,btnResume As Button";
mostCurrent._btncancel = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._btnresume = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim Install,Tutotial As Button";
mostCurrent._install = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._tutotial = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim Banner As AdView";
mostCurrent._banner = new anywheresoftware.b4a.admobwrapper.AdViewWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim Interstitial As mwAdmobInterstitial";
mostCurrent._interstitial = new mobi.mindware.admob.interstitial.AdmobInterstitialsAds();
 //BA.debugLineNum = 30;BA.debugLine="Dim res As XmlLayoutBuilder";
mostCurrent._res = new anywheresoftware.b4a.object.XmlLayoutBuilder();
 //BA.debugLineNum = 31;BA.debugLine="Dim lis As List";
mostCurrent._lis = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 32;BA.debugLine="Dim idd As id";
mostCurrent._idd = new com.maximus.id.id();
 //BA.debugLineNum = 33;BA.debugLine="Dim idd_int As Int";
_idd_int = 0;
 //BA.debugLineNum = 34;BA.debugLine="Dim p As Phone";
mostCurrent._p = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _install_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 166;BA.debugLine="Sub Install_Click";
 //BA.debugLineNum = 167;BA.debugLine="File.Copy(File.DirAssets,\"D-Day_3.0.4.apk\",File.Di";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"D-Day_3.0.4.apk",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"D_Day_3.0.4.apk");
 //BA.debugLineNum = 168;BA.debugLine="Msgbox(\"1. Click 'OK' Below to Install App!\" & CRL";
anywheresoftware.b4a.keywords.Common.Msgbox("1. Click 'OK' Below to Install App!"+anywheresoftware.b4a.keywords.Common.CRLF+"2. After Install Finished!"+anywheresoftware.b4a.keywords.Common.CRLF+"Do not click 'Open' button"+anywheresoftware.b4a.keywords.Common.CRLF+"Click 'Done' button to come back here and"+anywheresoftware.b4a.keywords.Common.CRLF+"click 'Add Obb File' button below!","WARNING!",mostCurrent.activityBA);
 //BA.debugLineNum = 169;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 170;BA.debugLine="i.Initialize(i.ACTION_VIEW,\"file:///\"&File.DirRoot";
_i.Initialize(_i.ACTION_VIEW,"file:///"+anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/D_Day_3.0.4.apk");
 //BA.debugLineNum = 171;BA.debugLine="i.SetType(\"application/vnd.android.package-archive";
_i.SetType("application/vnd.android.package-archive");
 //BA.debugLineNum = 172;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 //BA.debugLineNum = 173;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
httputils2service._process_globals();
downloadservice._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 17;BA.debugLine="Private link1 As String = \"https://github.com/MgH";
_link1 = "https://github.com/MgHtetzNaing/DDay/releases/download/v1.0/dday.obb";
 //BA.debugLineNum = 18;BA.debugLine="Dim t As Timer";
_t = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 19;BA.debugLine="Dim Theme_Value As Int";
_theme_value = 0;
 //BA.debugLineNum = 20;BA.debugLine="End Sub";
return "";
}
public static String  _rad_click() throws Exception{
 //BA.debugLineNum = 149;BA.debugLine="Sub rad_Click";
 //BA.debugLineNum = 150;BA.debugLine="t.Enabled = False";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 151;BA.debugLine="End Sub";
return "";
}
public static String  _settheme(int _theme) throws Exception{
 //BA.debugLineNum = 130;BA.debugLine="Private Sub SetTheme (Theme As Int)";
 //BA.debugLineNum = 131;BA.debugLine="If Theme = 0 Then";
if (_theme==0) { 
 //BA.debugLineNum = 132;BA.debugLine="ToastMessageShow(\"Theme not available.\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Theme not available.",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 133;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 135;BA.debugLine="If Theme = Theme_Value Then Return";
if (_theme==_theme_value) { 
if (true) return "";};
 //BA.debugLineNum = 136;BA.debugLine="Theme_Value = Theme";
_theme_value = _theme;
 //BA.debugLineNum = 137;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 138;BA.debugLine="StartActivity(Me)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,main.getObject());
 //BA.debugLineNum = 139;BA.debugLine="End Sub";
return "";
}
public static String  _share_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _shareit = null;
b4a.util.BClipboard _copy = null;
 //BA.debugLineNum = 75;BA.debugLine="Sub share_Click";
 //BA.debugLineNum = 76;BA.debugLine="Dim ShareIt As Intent";
_shareit = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 77;BA.debugLine="Dim copy As BClipboard";
_copy = new b4a.util.BClipboard();
 //BA.debugLineNum = 78;BA.debugLine="copy.clrText";
_copy.clrText(mostCurrent.activityBA);
 //BA.debugLineNum = 79;BA.debugLine="copy.setText(\"#One_Click_Email_Generator! Do You L";
_copy.setText(mostCurrent.activityBA,"#One_Click_Email_Generator! Do You Like This App? So, Please Share!!! This Is www.temp-mail.org Android App! Temp Mail service Is Not just a one-off designed project. Temp Mail has ambitious plans To further improve its service. We strive For the ideal in order To achieve As close As possible perfection. Your suggestions And comments are very useful. We take everything into account And implement useful suggestions into standard practice. Do you like our anonymous e-mail service? You can suggest our service To other users in a reviews. If you can think of ways To assist us To achieve perfection, your comments will allow our joint efforts To make the service more convenient, user friendly, And meet the needs of other users requiring a temporary email address.We invite you To help us To improve our services. We offer you the most secure anonymous email And most comfortable user interface. Download Free at Google Play Store: https://play.google.com/store/apps/details?id=com.htetznaing.tempmail");
 //BA.debugLineNum = 80;BA.debugLine="ShareIt.Initialize (ShareIt.ACTION_SEND,\"\")";
_shareit.Initialize(_shareit.ACTION_SEND,"");
 //BA.debugLineNum = 81;BA.debugLine="ShareIt.SetType (\"text/plain\")";
_shareit.SetType("text/plain");
 //BA.debugLineNum = 82;BA.debugLine="ShareIt.PutExtra (\"android.intent.extra.TEXT\",";
_shareit.PutExtra("android.intent.extra.TEXT",(Object)(_copy.getText(mostCurrent.activityBA)));
 //BA.debugLineNum = 83;BA.debugLine="ShareIt.PutExtra (\"android.intent.extra.SUBJEC";
_shareit.PutExtra("android.intent.extra.SUBJECT",(Object)("#One_Click_Email_Generator!"));
 //BA.debugLineNum = 84;BA.debugLine="ShareIt.WrapAsIntentChooser(\"Share App Via...\"";
_shareit.WrapAsIntentChooser("Share App Via...");
 //BA.debugLineNum = 85;BA.debugLine="StartActivity (ShareIt)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_shareit.getObject()));
 //BA.debugLineNum = 86;BA.debugLine="End Sub";
return "";
}
public static String  _t_tick() throws Exception{
 //BA.debugLineNum = 153;BA.debugLine="Sub t_Tick";
 //BA.debugLineNum = 154;BA.debugLine="If p.SdkVersion > 19 Then";
if (mostCurrent._p.getSdkVersion()>19) { 
 //BA.debugLineNum = 155;BA.debugLine="If Interstitial.Status=Interstitial.Status_AdRead";
if (mostCurrent._interstitial.Status==mostCurrent._interstitial.Status_AdReadyToShow) { 
 //BA.debugLineNum = 156;BA.debugLine="Interstitial.Show";
mostCurrent._interstitial.Show(mostCurrent.activityBA);
 };
 //BA.debugLineNum = 159;BA.debugLine="If Interstitial.Status=Interstitial.Status_Dismis";
if (mostCurrent._interstitial.Status==mostCurrent._interstitial.Status_Dismissed) { 
 //BA.debugLineNum = 160;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd(mostCurrent.activityBA);
 };
 }else {
 };
 //BA.debugLineNum = 164;BA.debugLine="End Sub";
return "";
}
public static String  _tutorial_click() throws Exception{
anywheresoftware.b4a.phone.Phone.PhoneIntents _pi = null;
 //BA.debugLineNum = 198;BA.debugLine="Sub Tutorial_Click";
 //BA.debugLineNum = 199;BA.debugLine="Dim pi As PhoneIntents";
_pi = new anywheresoftware.b4a.phone.Phone.PhoneIntents();
 //BA.debugLineNum = 200;BA.debugLine="StartActivity(pi.OpenBrowser(\"http://ht3tzn4ing.b";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_pi.OpenBrowser("http://ht3tzn4ing.blogspot.com/2016/11/DDayUnlimitedHacked.html")));
 //BA.debugLineNum = 201;BA.debugLine="End Sub";
return "";
}
public void _onCreate() {
	if (_theme_value != 0)
		setTheme(_theme_value);
}
}
