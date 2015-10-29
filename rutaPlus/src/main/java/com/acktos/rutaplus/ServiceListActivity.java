package com.acktos.rutaplus;

import com.acktos.rutaplus.ServiceListFragment.OnServiceSelectedListener;
import com.acktos.rutaplus.controllers.PaymentController;
import com.acktos.rutaplus.entities.Service;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

/**
 * Container for {@link ServiceListFragment}
 */
public class ServiceListActivity extends Activity implements OnServiceSelectedListener{

	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_service_list);

		//add commit
		actionBar=getActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new ServiceListFragment()).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if(id==android.R.id.home){
			launchSelectService();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {

		launchSelectService();
	}

	@Override
	public void onServiceSelected(Service service) {

		String dataService=service.toJson();
		Intent i=new Intent(this,ServiceDetailActivity.class);
		i.putExtra(ServiceDetailActivity.TAG_JSON_DETAIL, dataService);

		this.startActivity(i);		
	}

	private void launchSelectService(){
		Intent intent = new Intent(this, SelectServiceTypeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
}
