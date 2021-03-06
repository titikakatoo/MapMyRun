package de.dhbw.contents;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;

import de.dhbw.container.MenuContainerActivity;
import de.dhbw.container.R;
import de.dhbw.database.AnalysisCategory;
import de.dhbw.database.Coordinates;
import de.dhbw.database.DataBaseHandler;
import de.dhbw.database.Workout;
import de.dhbw.helpers.TrackService;
import de.dhbw.tracking.CustomTimerTask;
import de.dhbw.tracking.DistanceSegment;
import de.dhbw.tracking.GPSTracker;
import de.dhbw.tracking.MyItemizedOverlay;

public class LiveTrackingFragment extends SherlockFragment {

	public Context mContext;
	public GPSTracker gps;
	private DataBaseHandler db;
    public View mView;
    public LinearLayout mWorkoutLayout;
    public ListView mListView;
    
    public Timer timer = new Timer();	//Timer f�r Dauer
    public List<DistanceSegment> mSegmentList = new ArrayList<DistanceSegment>();
    
    //leerer Konstruktor
	public LiveTrackingFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Layout erstellen
		View v = inflater.inflate(R.layout.live_tracking_fragment, container,
				false);
		
		//Container Acivity
		mContext = getActivity();
		
		//Live Tracking Layout anzeigen
		mWorkoutLayout = (LinearLayout) v.findViewById(R.id.workout_layout);
		mWorkoutLayout.setVisibility(View.VISIBLE);
		
		//Karte ausblenden
		MapView mMapView = (MapView) v.findViewById(R.id.mapview);
		mMapView.setVisibility(View.GONE);
		
		//(Tracking-)Kategorieliste (Dauer,Distanz etc.) ausblenden
		mListView = (ListView) v.findViewById(R.id.category_list);
		mListView.setVisibility(View.GONE);
		
		db = new DataBaseHandler(mContext);

		//Button zum Starten, Anhalten und Auswerten von einer Live-Tracking
		Button trackingButton = (Button) v.findViewById(R.id.tracking);
		trackingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				changeTrackingState(view);
			}
		});
		
		return v;
	}
	
	//Sperre Zur�ck-Taste, da diese in den meisten F�llen die App beendet
	//Ausnahme: Wenn in Auswahlliste der kategorien, blende wieder Live-Tracking ein
	public void onBackPressed()
	{
		if (mListView.getVisibility() == View.VISIBLE)
		{
			mListView.setVisibility(View.GONE);
			mWorkoutLayout.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onResume() {
		//Setze Liste bei Fortsetzen des Fragments
		mView = getView();	
		setList();
		super.onResume();
	}
	
	//Setze Default-Werte f�r die einzelnen Kategorien
	public void formatCategoryHeadline(AnalysisCategory ac, TextView valueView ){
		String format = ac.getFormat();
		
		if (format.equals("hh:mm:ss") && valueView.getText().equals(""))
            valueView.setText("00:00:00");
		else if (format.equals("km"))
			valueView.setText("0");
		else if (format.equals("m"))
			valueView.setText("0");
		else if (format.equals("kcal"))
			valueView.setText("0");
		else if (format.equals("km/h"))
			valueView.setText("0,0");	
		else if (format.equals("hh:mm"))
		{
			Calendar c = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
			valueView.setText(sdf.format(c.getTime()));
		}

	}
	
	public void populateCategories(AnalysisCategory ac, TextView valueView, List <Coordinates> listContents){
		//Tracking Kategorien Werte zuordnen
		switch(ac.getId())
		{
			case 1:		//Dauer wird per Timer gesetzt
				break;
			case 2:		//Distanz
				valueView.setText(String.valueOf(TrackService.calcDistance(listContents)));
				break;
			case 3:		//Hoehenmeter aufwaerts
				valueView.setText(String.valueOf(TrackService.calcElevation(listContents)));
				break;
			case 4:		//Hoehenmeter abwaerts
				valueView.setText(String.valueOf(TrackService.calcDescent(listContents)));
				break;
			case 5:		//Verbrannte Kalorien
				valueView.setText(String.valueOf(TrackService.calcCaloriesBurned(listContents)));
				break;
			case 6:		//Durchschnittsgeschwindigkeit
				valueView.setText(String.valueOf(TrackService.calcPace(listContents)));
				break;
			case 7:		//Zeit wird per Timer gesetzt				
				break;
			default:	//Wird nie erreicht
				break;
		}
	}
	
	//Lese Liste des letzten zusammengestellten Listen-Layouts aus der Datenbank und �bernehme das Layout
	public void setList()
	{
		
		for (int i=0; i<7; i++)
		{
			db = new DataBaseHandler(mContext);
			AnalysisCategory ac = db.getAnalysisCategoryById(db.getCategoryIdByPosition(i+1));
			if (ac == null)
				continue;
			
			int viewId = getResources().getIdentifier("workout_element_"+i, "id", mContext.getPackageName());
			View listElement = mView.findViewById(viewId);
			listElement.setOnClickListener(new CustomListOnClickListener(this));
			TextView valueView = ((TextView) listElement.findViewById(R.id.live_tracking_element_value_text));
			
			//Pr�fen, ob Dauer und nicht oberes Feld (->Schriftgr��e)
			if (ac.getId() == 1 && i != 0)
				valueView.setTextSize(20);
			else
				valueView.setTextSize(30);
			
			List <Coordinates> listContents = new ArrayList<Coordinates>();
			listContents = db.getAllCoordinatePairs();
			formatCategoryHeadline(ac, valueView);
			populateCategories(ac, valueView, listContents);
			//Zu Kategorien Icons laden
			int imageId = getResources().getIdentifier(ac.getImageName(), "drawable", mContext.getPackageName());
			((ImageView) listElement.findViewById(R.id.live_tracking_element_value_icon)).setImageResource(imageId);
			//Zu Kategorien �berschriften laden		
			((TextView) listElement.findViewById(R.id.live_tracking_element_name)).setText(ac.getName() + " (" + ac.getFormat() + ")");
		
		}
	}

	//Starten, Anhalten und Auswerten von Tracking
	public void changeTrackingState(View view) {
		// Container Activity
		MenuContainerActivity ac = (MenuContainerActivity) getActivity();

		// Start Tracking
		if (view.getTag() == null) {
			
			//Koordinaten des vergangenen Tracking aus DB entfernen
			db.clearCoordinates();
			
			//Tracking über GPS oder Netzwerk starten
			gps = new GPSTracker(mContext, this);
			view.setTag(1);
			
			timer.schedule(new CustomTimerTask(this),0,1000);//Update text every second
			
			//Überschrift von Start auf Stop aendern
			((TextView) view).setText(getString(R.string.button_workout_stop));

			// Achievements und Menü sperren solange Tracking aktiv
			ac.setLocked(true);
			ac.invalidateOptionsMenu();

		} 
		else if ((Integer) view.getTag() == 1) {
			view.setTag(2);
			
			//Tracking anhalten
			gps.stopUsingGPS();
			
			//Route auf Karte anzeigen
			showTrackingRouteOnMap();

			//Neues Workout speicern
			Workout aktWorkout = populateWorkoutWithData();
			db.addWorkout(aktWorkout);
			db.checkAchievements(aktWorkout, mContext);
			((TextView) view)
					.setText(getString(R.string.button_workout_analyse));
		} 
		
		//Navigation zur Detailauswertung 
		else if ((Integer) view.getTag() == 2) {
			getToTrackingEvaluation();
			//Menü und Achievements reaktivieren
			ac.setLocked(false);
			ac.invalidateOptionsMenu();
		}
	}
	
	
	//Route auf Karte anzeigen
	public void showTrackingRouteOnMap(){
		//Karte einblenden
		mView.findViewById(R.id.workout_layout).setVisibility(View.GONE);
		((TextView) mView.findViewById(R.id.map_copyright)).setVisibility(View.VISIBLE);
		MapView mapView = (MapView) mView.findViewById(R.id.mapview);
		mapView.setVisibility(View.VISIBLE);
		
		//Zoom Funktionalität aktivieren
		mapView.setBuiltInZoomControls(true);
		
		//Markierungelement definieren
		Drawable marker = getResources().getDrawable(
				android.R.drawable.star_big_on);
		int markerWidth = marker.getIntrinsicWidth();
		int markerHeight = marker.getIntrinsicHeight();
		marker.setBounds(0, markerHeight, markerWidth, 0);
		
		
		//Markierungselement auf Karte positionieren
		ResourceProxy resourceProxy = new DefaultResourceProxyImpl(mContext);
		MyItemizedOverlay myItemizedOverlay = new MyItemizedOverlay(marker,
				resourceProxy);
		mapView.getOverlays().add(myItemizedOverlay);
		List<Coordinates> listContents = new ArrayList<Coordinates>();
		listContents = db.getAllCoordinatePairs();
		
		//Koordinaten als Markierungselement auf Karte anzeigen
		for (Coordinates i : listContents) {
			GeoPoint myPoint1 = new GeoPoint(i.get_latitude(),
					i.get_longitude());
			myItemizedOverlay.addItem(myPoint1, "myPoint1", "myPoint1");
		}
		
	}
	
	//Neues Workout aus aktuellen Trackingergebnissen erstellen
	public Workout populateWorkoutWithData(){
		List <Coordinates> listContents = new ArrayList<Coordinates>();
		listContents = db.getAllCoordinatePairs();
		String duration = TrackService.calcDuration(listContents);
		double pace = TrackService.calcPace(listContents);
		double elevation = TrackService.calcElevation(listContents);
		double descent = TrackService.calcDescent(listContents);
		double calories_burned = TrackService.calcCaloriesBurned(listContents);
		double distance = TrackService.calcDistance(listContents);
		return new Workout(duration, pace, descent, elevation, calories_burned, distance, new Date());
	}

	public void getToTracking(SherlockFragment single_evaluation) {
		((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
				.add(R.id.currentFragment, single_evaluation).commit();
	}

	
	//Zur Detailauswertung
	@SuppressWarnings("static-access")
	public void getToTrackingEvaluation() {
		
		//Speichere L�nge der Liste der Segmente
		Bundle bundle = new Bundle();
		bundle.putInt("segmentlength", mSegmentList.size());
		for (int i=0; i<mSegmentList.size(); i++)
			bundle.putStringArray("segment"+String.valueOf(i), mSegmentList.get(i).toStringArray());
		EvaluationViewPager evp = new EvaluationViewPager();
		evp.setArguments(bundle);
		((FragmentActivity) mContext)
				.getSupportFragmentManager()
				.beginTransaction()
			.replace(R.id.currentFragment,
						evp, evp.TAG).addToBackStack(null).commit();
	}
}