package com.acktos.rutaplus;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.acktos.rutaplus.adapters.DrawableAdapter;
import com.acktos.rutaplus.controllers.PaymentController;
import com.acktos.rutaplus.controllers.UserController;

import java.util.ArrayList;

/**
 * Activity which display the three types of service and manages the side-menu options.
 */
public class SelectServiceTypeActivity extends Activity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;


	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private ArrayList<String> mMenuTitles;

	static final String DEBUG_TAG = "ServiceActivity debug";
	public static final String TYPE_SERVICE="TYPE_SERVICE";
	public static final int TYPE_VIP=1;
	public static final int TYPE_ELECTED=3;
	public static final int TYPE_VAN=2;
	public static final int TYPE_UNIVERSITY=4;
	public static final String KEY_REFERER="com.acktos.rutaplus.CLASS_NAME";


	// Applications
	UserController userController;
	PaymentController payment;
	private boolean isEnterprise=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_service_type);

		//initialize application components
		userController=new UserController(this);
		payment=new PaymentController(this);


		//check if user comes from enterprise
		isEnterprise=userController.isEnterprise();

		if(!isEnterprise && !payment.isPaymentSelected()){
			Intent i=new Intent(this,CardListActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
		}


		//fill drawable items
		mMenuTitles=generateDrawableItems();

		mDrawerTitle =getString(R.string.app_name);
		mTitle =getString(R.string.title_activity_select_service_type);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);


		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new DrawableAdapter(this,mMenuTitles,isEnterprise));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description for accessibility */
				R.string.drawer_close  /* "close drawer" description for accessibility */
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerSlide(View drawerView, float slideOffset)
			{
				super.onDrawerSlide(drawerView, slideOffset);
				mDrawerLayout.bringChildToFront(drawerView);
				mDrawerLayout.requestLayout();
				//mDrawerLayout.setScrimColor(Color.TRANSPARENT);

			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		if (savedInstanceState == null) {
			//selectItem(2);
		}
	}

	private ArrayList<String> generateDrawableItems(){

		ArrayList<String> items=new ArrayList<String>();

		if(isEnterprise){
			items.add(userController.getName()+" - "+userController.getEnterprise());
		}else{
			items.add(userController.getName());
		}

		items.add(userController.getUserEmail());
		items.add(userController.getCC());

		String[] menuArray=null;

		if(isEnterprise){
			menuArray=getResources().getStringArray(R.array.menu_enterprise);
		}else{
			menuArray=getResources().getStringArray(R.array.menu_array);
		}


		for(int i=0; i<menuArray.length;i++){

			try{
				items.add(menuArray[i]);
			}catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
			}
		}

		return items;
	}

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {


		if(isEnterprise){
			switch(position){

			case 3:
				launchServiceList();
				break;

			case 4:
				launchShareApp();
				break;

			case 5:
				launchChangePassword();
				break;
			case 6:
				logout();
				break;

			default:
				mDrawerList.setItemChecked(position, true);
				mDrawerLayout.closeDrawer(mDrawerList);
			}
		}else{

			switch(position){

			case 3:
				launchServiceList();
				break;
				/*case 4:
				launchCarList();
				break;*/
			case 4:
				launchCardList();
				break;

			case 5:
				launchRates();
				break;
			case 6:
				launchShareApp();
				break;

			case 7:
				launchChangePassword();
				break;
			case 8:
				logout();
				break;

			default:
				mDrawerList.setItemChecked(position, true);
				mDrawerLayout.closeDrawer(mDrawerList);
			}
		}



	}

	private void launchServiceList(){
		Intent i=new Intent(this,ServiceListActivity.class);
		startActivity(i);
	}

	private void launchCardList(){
		Intent i=new Intent(this,CardListActivity.class);
		i.putExtra(CardListActivity.FROM_MENU_INTENT, Activity.RESULT_OK);
		startActivity(i);
	}

	private void launchRates(){
		Intent i=new Intent(this,RatesActivity.class);
		startActivity(i);
	}

	private void launchChangePassword(){
		Intent i=new Intent(this,ChangePasswordActivity.class);
		startActivity(i);
	}

	private void launchShareApp(){
		Intent intent=new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		// Add data to the intent, the receiving app will decide what to do with it.
		intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
		intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.msg_share_app)+" "+getString(R.string.url_base));
		startActivity(Intent.createChooser(intent,getString(R.string.lbl_share)));
	}

	private void logout(){
		deleteFile(UserController.FILE_NAME_PROFILE);
		Intent i=new Intent(this,GoogleSignInActivity.class);
		startActivity(i);
		finish();
		Log.i(DEBUG_TAG,"success logout");
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		//boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		//menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		/*MenuItem sendAddress=menu.findItem(R.id.send_address);
		if(!sendAddress.isVisible()){
			sendAddress.setVisible(true);
		}*/
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// Handle action buttons
		switch(item.getItemId()) {
		case R.id.send_address:
			item.setVisible(false);
			//launchServiceForm(address);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		//return super.onOptionsItemSelected(item);
	}

	// invoked by elected service button
	public void launchVipService(View view){
		Intent i = new Intent(this, ServiceActivity.class);
		i.putExtra(TYPE_SERVICE,TYPE_VIP);
		startActivity(i);
	}

	// invoked by elected service button
	public void launchVanService(View view){
		Intent i = new Intent(this, ServiceActivity.class);
		i.putExtra(TYPE_SERVICE,TYPE_VAN);
		startActivity(i);
	}

	// invoked by elected service button
	public void launchElectedService(View view){
		Intent i = new Intent(this, ServiceActivity.class);
		i.putExtra(TYPE_SERVICE,TYPE_ELECTED);
		startActivity(i);
	}

	// invoked by university service button
	public void launchUniversityService(View view){
		Intent i = new Intent(this, ServiceActivity.class);
		i.putExtra(TYPE_SERVICE,TYPE_UNIVERSITY);
		startActivity(i);
	}




}
