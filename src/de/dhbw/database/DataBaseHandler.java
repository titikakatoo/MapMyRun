package de.dhbw.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class DataBaseHandler extends SQLiteOpenHelper{
	   SimpleDateFormat df = new SimpleDateFormat("yyyy-mm");
	   public DataBaseHandler(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	   
	   public DataBaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//Datenbankversion
    private static final int DATABASE_VERSION = 33;
 
    // Datenbankname
    private static final String DATABASE_NAME = "workoutsManager";
 
    // Workouts Tabellenname
    private static final String TABLE_WORKOUTS = "workouts";
    //Coordinates Tabellenname
    private static final String TABLE_COORDINATES = "coordinates";
    // Achievement Tabellenname
    private static final String TABLE_ACHIEVEMENTS = "achievements";
    // AnalysisCategory Tabellenname
    private static final String TABLE_ANALYSIS_CATEGORY = "analysiscategory";
    // CategoryPositions Tabellenname
    private static final String TABLE_CATEGORY_POSITIONS = "categorypositions";
 
    //Gemeinsame Spaltennamen
    private static final String KEY_ID = "id";
	//Workouts
    private static final String KEY_DURATION = "duration";
    private static final String KEY_PACE = "pace";
    private static final String KEY_ELEVATION_DOWNWARDS = "elevation_downwards";
    private static final String KEY_ELEVATION_UPWARDS = "elevation_upwards";
    private static final String KEY_CALORIES_BURNED = "calories_burned";
    private static final String KEY_DISTANCE = "distance";
    private static final String KEY_DATE = "date";
    //Coordinates
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_ALTITUDE = "altitude";
    private static final String KEY_TIMESTAMP = "timestamp";
    //Achievements
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGENAME = "imagename";
    private static final String KEY_REQUIREMENT_NUMBER = "requirednumber";
    private static final String KEY_REQUIREMENT_UNIT = "unit";
    private static final String KEY_ACHIEVED = "achieved";
    //AnalysisCategory
    private static final String KEY_FORMAT = "format";
    //CategoryPositions
    private static final String KEY_POSITION = "position";
    private static final String KEY_CATEGORY_ID = "categoryid";
    
    //SQL Statements zum Tabellen Anlegen
    //Erstellt Workout Tabelle
    String CREATE_WORKOUTS_TABLE = "CREATE TABLE " + TABLE_WORKOUTS + "("
    		+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_DURATION + " TEXT,"
    		+ KEY_PACE + " REAL," + KEY_ELEVATION_DOWNWARDS + 
    		" REAL," + KEY_ELEVATION_UPWARDS + " REAL,"
    		+ KEY_CALORIES_BURNED + " REAL," + KEY_DISTANCE + " REAL," + KEY_DATE + " INTEGER" + ");";
    //Erstellt Koordinatentabelle
    String CREATE_COORDINATES_TABLE = "CREATE TABLE " + TABLE_COORDINATES + "("
    		+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_LONGITUDE + " REAL,"
    		+ KEY_LATITUDE + " REAL," + KEY_ALTITUDE + " REAL," + KEY_TIMESTAMP + " REAL" +");";
    //Erstellt Achievements Tabelle
    String CREATE_ACHIEVEMENT_TABLE = "CREATE TABLE " + TABLE_ACHIEVEMENTS + "("
    		+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DESCRIPTION + " TEXT,"
    		+ KEY_IMAGENAME + " TEXT," + KEY_REQUIREMENT_NUMBER + " INTEGER," + KEY_REQUIREMENT_UNIT
    		+" TEXT," + KEY_ACHIEVED + " INTEGER" + ");";
    //Erstellt AnalysisCategroy Tabelle
    String CREATE_ANALYSIS_CATEGORY_TABLE = "CREATE TABLE " + TABLE_ANALYSIS_CATEGORY + "("
    		+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_IMAGENAME + " TEXT,"
    		+ KEY_FORMAT + " TEXT" + ");";
    //Erstellt CategoryPositions Tabelle
    String CREATE_CATEGORY_POSITIONS_TABLE = "CREATE TABLE " + TABLE_CATEGORY_POSITIONS + "("
    		+ KEY_POSITION + " INTEGER PRIMARY KEY," + KEY_CATEGORY_ID + " INTEGER" + ");";
    
    // Legt Tabellen an
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WORKOUTS_TABLE);
        db.execSQL(CREATE_COORDINATES_TABLE);
        Log.d("Workout Query", CREATE_WORKOUTS_TABLE);
        Log.d("Coordinates Query", CREATE_COORDINATES_TABLE);
        db.execSQL(CREATE_ACHIEVEMENT_TABLE);
        initAchievements(db);
        Log.d("Achievement Query", CREATE_ACHIEVEMENT_TABLE);
        db.execSQL(CREATE_ANALYSIS_CATEGORY_TABLE);
        initAnalysisCategories(db);
        
        db.execSQL(CREATE_CATEGORY_POSITIONS_TABLE);
        initCategoryPositions(db);
    }
    
    
    public void clearCoordinates(){
      SQLiteDatabase db = this.getWritableDatabase();
      db.delete("COORDINATES", null, null);
    }
    
    // Datenbankversion hochzaehlen
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACHIEVEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANALYSIS_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY_POSITIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COORDINATES);
        // Create tables again
        onCreate(db);
    }
    
    
    
    //Neues Workout erstellen
    public void addWorkout(Workout workout) {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues values = new ContentValues();
	    values.put(KEY_DURATION, workout.getDuration()); 
	    values.put(KEY_PACE, workout.getPace()); 
	    values.put(KEY_ELEVATION_DOWNWARDS, workout.getElevationDownwards()); 
	    values.put(KEY_ELEVATION_UPWARDS, workout.getElevationUpwards()); 
	    values.put(KEY_CALORIES_BURNED, workout.getCaloriesBurned()); 
	    values.put(KEY_DISTANCE, workout.get_distance()); 
	    values.put(KEY_DATE, workout.get_date().getTime());
	    // Neue Zeile einfügen
	    db.insert(TABLE_WORKOUTS, null, values);
	    db.close(); // Datenbankverbindung schließen
    }
     
    // Gibt einzelne Workouts zurück
    public Workout getWorkout(int id){
    	SQLiteDatabase db = this.getReadableDatabase();
    	  
	    Cursor cursor = db.query(TABLE_WORKOUTS, new String[] { KEY_ID,
	    		KEY_DURATION, 
	    		KEY_PACE,
	    		KEY_ELEVATION_DOWNWARDS,
	    		KEY_ELEVATION_UPWARDS, 
	    		KEY_CALORIES_BURNED,
	    		KEY_DISTANCE,
	    		KEY_DATE}, KEY_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    Workout workout = new Workout(
	    		Integer.parseInt(cursor.getString(0)),
	            cursor.getString(1), 
	            Double.parseDouble(cursor.getString(2)), 
	            Double.parseDouble(cursor.getString(3)), 
	            Double.parseDouble(cursor.getString(4)),
	            Double.parseDouble(cursor.getString(5)),
	            Double.parseDouble(cursor.getString(6)),
	            new Date(Long.parseLong(cursor.getString(7))));
	    return workout;
		}
     
    // Alle Workouts zurückgeben
	 public List <Workout> getAllWorkouts() {
	    List <Workout> workoutList = new ArrayList<Workout>();
	    // Alle Abfragen Query
	    String selectQuery = "SELECT  * FROM " + TABLE_WORKOUTS;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    if (cursor.moveToFirst()) {
	        do {
	            Workout workout = new Workout();
	            workout.setID(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
	            workout.setDuration(cursor.getString(cursor.getColumnIndex(KEY_DURATION)));
	            workout.setPace(cursor.getDouble(cursor.getColumnIndex(KEY_PACE)));
	            workout.setElevationDownwards(cursor.getDouble(cursor.getColumnIndex(KEY_ELEVATION_DOWNWARDS)));
	            workout.setElevationUpwards(cursor.getDouble(cursor.getColumnIndex(KEY_ELEVATION_UPWARDS)));
	            workout.setCaloriesBurned(cursor.getDouble(cursor.getColumnIndex(KEY_CALORIES_BURNED)));
	            workout.set_distance(cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE)));
	            workout.set_date(new Date((cursor.getLong(cursor.getColumnIndex(KEY_DATE)))));
	            // Workout zur Liste hinzufügen
	            workoutList.add(workout);
	        } while (cursor.moveToNext());
	    }
	    
	    db.close();
	    // Alle Workouts zur Liste zurückgeben
	    return workoutList;
	}
	 
	 
    // Gibt die Anzahl von Workouts in der Datenbank zurück 
    public int getWorkoutsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_WORKOUTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
		}
   
    
    
    // Aktualisiert einzelne Workouts
    public int updateWorkout(Workout workout) {
    	SQLiteDatabase db = this.getWritableDatabase();
	  
	    ContentValues values = new ContentValues();
	    values.put(KEY_DURATION, workout.getDuration());
	    values.put(KEY_PACE, workout.getPace());
	    values.put(KEY_ELEVATION_DOWNWARDS, workout.getElevationDownwards());
	    values.put(KEY_ELEVATION_UPWARDS, workout.getElevationUpwards());
	    values.put(KEY_CALORIES_BURNED, workout.getCaloriesBurned());
	    values.put(KEY_DISTANCE, workout.get_distance());
	    values.put(KEY_DATE, workout.get_date().getTime());
	    
	    // Aktualisiert Zeile
	    int dbUpdate = db.update(TABLE_WORKOUTS, values, KEY_ID + " = ?",
	            new String[] { String.valueOf(workout.getID()) });
	    db.close();
	    
	    return dbUpdate;
		}
     
    // Löscht einzelne Workouts
    public void deleteWorkout(Workout workout) {
    	SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_WORKOUTS, KEY_ID + " = ?",
	            new String[] { String.valueOf(workout.getID()) });
	    db.close();
    }   
    
    // Fügt Koordinatenpaare in die Datenbank ein
    public void addCoordinates(SQLiteDatabase db, Coordinates coordinate) {
    	ContentValues values = new ContentValues();
	    values.put(KEY_LONGITUDE, coordinate.get_longitude());
	    values.put(KEY_LATITUDE, coordinate.get_latitude());
	    values.put(KEY_ALTITUDE, coordinate.get_altitude());
	    values.put(KEY_TIMESTAMP, coordinate.get_timestamp());
	    // Fügt Zeile ein
	    db.insert(TABLE_COORDINATES, null, values);
    }
    
    public void addCoordinates(Coordinates coordinate) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	addCoordinates(db, coordinate);
	    db.close();
    }
    
    // Gibt die Anzahl aller Koordinatenpaare zurück
    public int getCoordinatePairsCount() {
        String countQuery = "SELECT * FROM " + TABLE_COORDINATES;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        
        return count;
	}
  
    
    // Gibt einzelne Koordinatenpaare zurück
    public Coordinates getCoordinatePair(int id) {
    	SQLiteDatabase db = this.getReadableDatabase();
    	  
	    Cursor cursor = db.query(TABLE_COORDINATES, new String[] { KEY_ID,
	    		KEY_LONGITUDE,
	    		KEY_LATITUDE,
	    		KEY_ALTITUDE,
	    		KEY_TIMESTAMP}, KEY_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);

	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    Coordinates coordinates = new Coordinates(
	    	    Integer.parseInt(cursor.getString(0)),
	            Double.parseDouble(cursor.getString(1)), 
	            Double.parseDouble(cursor.getString(2)),
	            Double.parseDouble(cursor.getString(3)),
	            Long.parseLong(cursor.getString(4)));
	    cursor.close();
	    db.close();
	    
	    return coordinates;
		}
    
    
    	// Gibt alle Koordinatenpaare zurück
    	 public List <Coordinates> getAllCoordinatePairs() {
    	    List <Coordinates> coordinateList = new ArrayList<Coordinates>();
    	    String selectQuery = "SELECT  * FROM " + TABLE_COORDINATES;
    	 
    	    SQLiteDatabase db = this.getWritableDatabase();
    	    Cursor cursor = db.rawQuery(selectQuery, null);
    	 
    	    //Geht alle Zeilen durch und fügt einzelne Koordinatenpaare in die Liste ein
    	    if (cursor.moveToFirst()) {
    	        do {
    	            Coordinates coordinates = new Coordinates();
    	            coordinates.set_id(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
    	            coordinates.set_longitude(cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)));
    	            coordinates.set_latitude(cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)));
    	            coordinates.set_altitude(cursor.getDouble(cursor.getColumnIndex(KEY_ALTITUDE)));
    	            coordinates.set_timestamp(cursor.getLong(cursor.getColumnIndex(KEY_TIMESTAMP)));
    	            coordinateList.add(coordinates);
    	        } while (cursor.moveToNext());
    	    }
    	    
    	    cursor.close();
    	    db.close();
    	 
    	    // Liste mit Koordinatenparen zurückgeben
    	    return coordinateList;
		}
 
    // Fügt neues Achievement in die Datenbank ein
    public void addAchievement(SQLiteDatabase db, Achievement achievement) {
    	ContentValues values = new ContentValues();
	    values.put(KEY_NAME, achievement.getName());
	    values.put(KEY_DESCRIPTION, achievement.getDescription());
	    values.put(KEY_IMAGENAME, achievement.getImageName());
	    values.put(KEY_REQUIREMENT_NUMBER, achievement.getRequiredNumber());
	    values.put(KEY_REQUIREMENT_UNIT, achievement.getRequiredUnit());
	    values.put(KEY_ACHIEVED, achievement.isAchieved());
	    // Inserting Row
	    db.insert(TABLE_ACHIEVEMENTS, null, values);
    }
    
    public void addAchievement(Achievement achievement) {
    	SQLiteDatabase db = getWritableDatabase();	
    	addAchievement(db, achievement);
	    db.close(); // Datenbankverbindung schließen
    }
    
    public int setAchieved(int id) {
    	SQLiteDatabase db = this.getWritableDatabase();
	  
	    ContentValues values = new ContentValues();
	    values.put(KEY_ACHIEVED, 1);
	    
	    // Aktualisiert Zeile
	    int dbUpdate = db.update(TABLE_ACHIEVEMENTS, values, KEY_ID + " = ?",
	            new String[] { String.valueOf(id) });
	    db.close();
	    
	    return dbUpdate;
	}
    
    // Gibt die Anzahl aller achievements aus der Datenbank zurück
    public int getAchievementCount() {
        String countQuery = "SELECT * FROM " + TABLE_ACHIEVEMENTS;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        
        return count;
	}
    
    // Gibt Anzahl von Achievements pro Einheit zurück (z.B. km/h, Sekunden etc.)
    public int getAchievementCount(String unit) {
        return getAchievementCount(unit, false);
	}
    
    public int getAchievementCount(String unit, boolean achieved) {
    	String countQuery;
    	if (achieved)
	        countQuery = "SELECT * FROM " + TABLE_ACHIEVEMENTS + " WHERE " + KEY_REQUIREMENT_UNIT + "='" + unit + "' AND ACHIEVED = '1'";
    	else
    		countQuery = "SELECT * FROM " + TABLE_ACHIEVEMENTS + " WHERE " + KEY_REQUIREMENT_UNIT + "='" + unit + "'";
    	SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
	 
        return count;
	}
    
    // Gibt Achievement anhand der ID zurück
    public Achievement getAchievement(int id) {
    	SQLiteDatabase db = getReadableDatabase();
    	  
	    Cursor cursor = db.query(TABLE_ACHIEVEMENTS, null, KEY_ID + "= ?", new String[] {String.valueOf(id)}, null, null, null, null);

	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    Achievement achievement = new Achievement(
	    		Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))),
	            cursor.getString(cursor.getColumnIndex(KEY_NAME)), 
	            cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)), 
	            cursor.getString(cursor.getColumnIndex(KEY_IMAGENAME)), 
	            cursor.getString(cursor.getColumnIndex(KEY_REQUIREMENT_UNIT)),
	            cursor.getInt(cursor.getColumnIndex(KEY_REQUIREMENT_NUMBER)));
	    
	    cursor.close();
	    db.close();
	    
	    return achievement; // gibt Achievement zurück
	}
    
    // Gibt alle Achievements zurück
	public List <Achievement> getAllAchievements() {
	    List <Achievement> achievementList = new ArrayList<Achievement>();
	    // Select all Query
	    String selectQuery = "SELECT  * FROM " + TABLE_ACHIEVEMENTS;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // Geht alle Zeilen durch und fügt sie der Liste hinzu
	    if (cursor.moveToFirst()) {
	        do {
		            Achievement achievement = new Achievement();
		            achievement.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
		            achievement.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
		            achievement.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
		            achievement.setImageName(cursor.getString(cursor.getColumnIndex(KEY_IMAGENAME)));
		            achievement.setRequiredUnit(cursor.getString(cursor.getColumnIndex(KEY_REQUIREMENT_UNIT)));
		            achievement.setRequiredNumber(cursor.getInt(cursor.getColumnIndex(KEY_REQUIREMENT_NUMBER)));
		            if (cursor.getInt(cursor.getColumnIndex(KEY_ACHIEVED)) == 0)
		            	achievement.setAchieved(false);
		            else
		            	achievement.setAchieved(true);
		            achievementList.add(achievement);
	        } while (cursor.moveToNext());
	    }
	    
	    cursor.close();
	    db.close();
	 
	    // Gibt Achievements Liste zurück
	    return achievementList;
	}
	
	// Gibt alle Achievements einer Einheit zurück (z.B. km/h, Sekunden)
	public List <Achievement> getAchievementsByUnit(String unit) {
		return getAchievementsByUnit(unit, false);
	}
	
	// Gibt alle Achievements einer Einheit zurück (optional nur errungene Achievements)
	public List <Achievement> getAchievementsByUnit(String unit, Boolean achieved) {
	    List <Achievement> achievementList = new ArrayList<Achievement>();
	    // Select All Query
	    String selectQuery;
	    if (achieved)
	    	selectQuery = "SELECT  * FROM " + TABLE_ACHIEVEMENTS + " WHERE " + KEY_REQUIREMENT_UNIT + "='"+unit+"' AND ACHIEVED = '1'";
	    else
	    	selectQuery = "SELECT  * FROM " + TABLE_ACHIEVEMENTS + " WHERE " + KEY_REQUIREMENT_UNIT + "='"+unit+"'";
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // Geht alle Zeilen durch und fügt sie der Liste hinzu
	    if (cursor.moveToFirst()) {
	        do {
		            Achievement achievement = new Achievement();
		            achievement.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
		            achievement.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
		            achievement.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
		            achievement.setImageName(cursor.getString(cursor.getColumnIndex(KEY_IMAGENAME)));
		            achievement.setRequiredUnit(cursor.getString(cursor.getColumnIndex(KEY_REQUIREMENT_UNIT)));
		            achievement.setRequiredNumber(cursor.getInt(cursor.getColumnIndex(KEY_REQUIREMENT_NUMBER)));
		            achievementList.add(achievement);
	        } while (cursor.moveToNext());
	    }
	    
	    cursor.close();
	    db.close();
	 
	    // Gibt (Achievements-)Liste zurück
	    return achievementList;
	}
	
	public List <AnalysisCategory> getAllAnalysisCategories() {
	    List <AnalysisCategory> analysisCategoryList = new ArrayList<AnalysisCategory>();
	    // Select All Query
	    String selectQuery = "SELECT * FROM " + TABLE_ANALYSIS_CATEGORY;
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	    
	    //Geht alle Zeilen durch und fügt sie der Liste hinzu
	    if (cursor.moveToFirst()) {
	        do {
	        		AnalysisCategory analysisCategory = new AnalysisCategory();
	        		analysisCategory.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
	        		analysisCategory.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
		            analysisCategory.setImageName(cursor.getString(cursor.getColumnIndex(KEY_IMAGENAME)));
		            analysisCategory.setFormat(cursor.getString(cursor.getColumnIndex(KEY_FORMAT)));
		            analysisCategoryList.add(analysisCategory);
	        } while (cursor.moveToNext());
	    }
	    
	    cursor.close();
	    db.close();
	 
	    // gibt analysisCategoryList zurück
	    return analysisCategoryList;
	}
		
	// Fügt neue AnalysisCategory in die DB ein
    public void addAnalysisCategory(SQLiteDatabase db, AnalysisCategory analysisCategory) {
    	ContentValues values = new ContentValues();
	    values.put(KEY_NAME, analysisCategory.getName());
	    values.put(KEY_IMAGENAME, analysisCategory.getImageName());
	    values.put(KEY_FORMAT, analysisCategory.getFormat());
	    // Fügt neue Zeile ein
	    db.insert(TABLE_ANALYSIS_CATEGORY, null, values);
    }
    
    public void addAnalysisCategory(AnalysisCategory analysisCategory) {
    	SQLiteDatabase db = getWritableDatabase();
    	addAnalysisCategory(db, analysisCategory);
	    db.close(); // Schließt Datenbankverbindung 
    }
    
    // Gibt CategoryId anhand der ID zurück
  	public AnalysisCategory getAnalysisCategoryById(int id) {
  		
  		AnalysisCategory analysisCategory = new AnalysisCategory();
  		
  	    // Select All Query
  	    String selectQuery = "SELECT  * FROM " + TABLE_ANALYSIS_CATEGORY + " WHERE " + KEY_ID + "='"+id+"'";
  	 
  	    SQLiteDatabase db = this.getWritableDatabase();
  	    Cursor cursor = db.rawQuery(selectQuery, null);
  	 
  	    // Geht alle Zeilen durch und fügt sie der analysisCategory Liste hinzu
  	    if (cursor.moveToFirst()) {
  	        analysisCategory.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
  	        analysisCategory.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
  	        analysisCategory.setImageName(cursor.getString(cursor.getColumnIndex(KEY_IMAGENAME)));
  	        analysisCategory.setFormat(cursor.getString(cursor.getColumnIndex(KEY_FORMAT)));
  	    }
  	    else
  	    	analysisCategory = null;
  	    
  	    cursor.close();
  	    db.close();
  	    
  	    return analysisCategory;
  	}
    
    private void addCategoryPosition(SQLiteDatabase db, CategoryPosition categoryPosition)
    {
    	ContentValues values = new ContentValues();
	    values.put(KEY_CATEGORY_ID, categoryPosition.getCategoryId());
	    values.put(KEY_POSITION, categoryPosition.getPosition());
	    // Fügt neue Zeile ein
	    db.insert(TABLE_CATEGORY_POSITIONS, null, values);
    }
    
    // Aktualisiert einzelne CategoryPosition Einträge
    public int updateCategoryPosition(CategoryPosition categoryPosition) 
    {
    	SQLiteDatabase db = this.getWritableDatabase();
	  
	    ContentValues values = new ContentValues();
	    values.put(KEY_CATEGORY_ID, categoryPosition.getCategoryId());
	    
	    // Zeile aktualisieren
	    int dbUpdate = db.update(TABLE_CATEGORY_POSITIONS, values, KEY_POSITION + " = ?",
	            new String[] { String.valueOf(categoryPosition.getPosition()) });
	    
	    db.close();
	    
	    return dbUpdate;
	}
    
    public List<CategoryPosition> getAllCategoryPositions()
    {
    	 List <CategoryPosition> categoryPositionList = new ArrayList<CategoryPosition>();
 	    // Select All Query
 	    String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY_POSITIONS;
 	 
 	    SQLiteDatabase db = this.getReadableDatabase();
 	    Cursor cursor = db.rawQuery(selectQuery, null);
 	 
 	    // Geht alle Zeilen durch und fügt sie der categoryPositionList Liste hinzu
 	    if (cursor.moveToFirst()) {
 	        do {
 	        		CategoryPosition categoryPosition = new CategoryPosition();
 	        		categoryPosition.setPosition(cursor.getInt(cursor.getColumnIndex(KEY_POSITION)));
 	        		categoryPosition.setCategoryId(cursor.getInt(cursor.getColumnIndex(KEY_CATEGORY_ID)));
 		           categoryPositionList.add(categoryPosition);
 	        } while (cursor.moveToNext());
 	    }
 	    
 	    cursor.close();
 	    db.close();
 	 
 	    // Gibt analysisCategoryList zurück
 	    return categoryPositionList;
    }
    
    // Liefert CategoryId anhand der ID zurück
 	public int getCategoryIdByPosition(int position) {
 		int categoryId;
 	    // Select All Query
 	    String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY_POSITIONS + " WHERE " + KEY_POSITION + "='"+position+"'";
 	 
 	    SQLiteDatabase db = this.getWritableDatabase();
 	    Cursor cursor = db.rawQuery(selectQuery, null);
 	 
 	    if (cursor.moveToFirst()) {
 	        categoryId = cursor.getInt(cursor.getColumnIndex(KEY_CATEGORY_ID));
 	    }
 	    else
 	    	categoryId = -1;
 	    
 	    cursor.close();
 	    db.close();
 	    
 	    return categoryId;
 	}
 	
    //Initialisiere Positionen der Kategorien in der Liste
    private void initCategoryPositions (SQLiteDatabase db)
    {    	
    	for (int i=1; i<8; i++)
    		addCategoryPosition(db, new CategoryPosition(i, i));
    }
    
    public void checkAchievements(Workout aktWorkout, Context mContext)
    {
    	List<Achievement> achievementList = getAllAchievements();
    	for (Achievement achievement : achievementList)
    	{
    		if (achievement.isAchieved())
    			continue;
    		boolean achieved = false;
    		if (achievement.getRequiredUnit().equals("skm"))
    		{
    			if (aktWorkout.get_distance() > achievement.getRequiredNumber())
    			{
    				setAchieved(achievement.getId());
    				achieved = true;
    			}
    		}
    		else if (achievement.getRequiredUnit().equals("ss"))
    		{
    			String[] durationString = aktWorkout.getDuration().split(":");
    			int duration = Integer.parseInt(durationString[0].trim())*3600 + Integer.parseInt(durationString[1].trim())*60 + Integer.parseInt(durationString[2].trim());
    			if (duration > achievement.getRequiredNumber())
    			{
    				setAchieved(achievement.getId());
    				achieved = true;
    			}
    		}	
    		else if (achievement.getRequiredUnit().equals("tkm"))
    		{
    			int distance = 0;
    			List<Workout> workOutList = getAllWorkouts();
    			for (Workout workout : workOutList)
    				distance += workout.get_distance();
    			if (distance > achievement.getRequiredNumber())
    			{
    				setAchieved(achievement.getId());
    				achieved = true;
    			}
    		}		
    		else if (achievement.getRequiredUnit().equals("ts"))
    		{
    			String[] durationString = aktWorkout.getDuration().split(":");
    			int duration = Integer.parseInt(durationString[0].trim())*3600 + Integer.parseInt(durationString[1].trim())*60 + Integer.parseInt(durationString[2].trim());
    			
    			List<Workout> workOutList = getAllWorkouts();
    			for (Workout workout : workOutList)
    			{
    				durationString = workout.getDuration().split(":");
    				duration += Integer.parseInt(durationString[0].trim())*3600 + Integer.parseInt(durationString[1].trim())*60 + Integer.parseInt(durationString[2].trim());
    			}
    				
    			if (duration > achievement.getRequiredNumber())
    			{
    				setAchieved(achievement.getId());
    				achieved = true;
    			}
    		}
    		
    		if (achieved)
    			Toast.makeText(mContext, "Achievement earned!\n"+achievement.getName()+"\n("+achievement.getDescription()+")", Toast.LENGTH_LONG).show();
    	}
    }
    
    private void initAnalysisCategories (SQLiteDatabase db)
    {
    	//add analysis categories
    	addAnalysisCategory(db, new AnalysisCategory("Dauer", "clock", "hh:mm:ss"));
    	addAnalysisCategory(db, new AnalysisCategory("Distanz", "road", "km"));
    	addAnalysisCategory(db, new AnalysisCategory("Hoehenmeter aufwaerts", "elevation", "m"));
    	addAnalysisCategory(db, new AnalysisCategory("Hoehenmeter abwaerts", "elevation", "m"));
    	addAnalysisCategory(db, new AnalysisCategory("Kalorien", "calories_burned", "kcal"));
    	addAnalysisCategory(db, new AnalysisCategory("Durchschnittsgeschwindigkeit", "speed_2", "kmh"));
    	addAnalysisCategory(db, new AnalysisCategory("Zeit", "clock", "hh:mm"));
    }
    
    private void initAchievements(SQLiteDatabase db)
    {
    	// Gesamtentfernung Achievements
        addAchievement(db, new Achievement("Beginner", "Laufe insgesamt 5 Kilometer", "ic_achievement_trophy", "tkm", 5));
        addAchievement(db, new Achievement("Advanced", "Laufe insgesamt 10 Kilometer", "ic_achievement_trophy", "tkm", 10));
        addAchievement(db, new Achievement("Marathon", "Laufe insgesamt 42 Kilometer", "ic_achievement_trophy", "tkm", 42));
        addAchievement(db, new Achievement("Runner", "Laufe insgesamt 50 Kilometer", "ic_achievement_trophy", "tkm", 50));
        addAchievement(db, new Achievement("Always running", "Laufe insgesamt 75 Kilometer", "ic_achievement_trophy", "tkm", 75));
        
        // Gesamtdauer Achievements
        addAchievement(db, new Achievement("And here...we...go!", "Laufe insgesamt 5 Minuten", "ic_achievement_trophy", "ts", 300));
        addAchievement(db, new Achievement("So far so good!", "Laufe insgesamt 30 Minuten", "ic_achievement_trophy", "ts", 1800));
        addAchievement(db, new Achievement("The first hour!", "Laufe insgesamt eine Stunde", "ic_achievement_trophy", "ts", 3600));
        addAchievement(db, new Achievement("Time goes by...", "Laufe insgesamt 5 Stunden", "ic_achievement_trophy", "ts", 18000));
        addAchievement(db, new Achievement("Indestructible!", "Laufe insgesamt 10 Stunden", "ic_achievement_trophy", "ts", 36000));
        
        // Einezelne Entfernungen Achievements
        addAchievement(db, new Achievement("So it begins...", "Laufe einen Kilometer am Stueck", "ic_achievement_trophy", "skm", 1));
        addAchievement(db, new Achievement("Double the distance!", "Laufe 2 Kilometer am Stueck", "ic_achievement_trophy", "skm", 2));
        addAchievement(db, new Achievement("High Five!", "Laufe 5 Kilometer am Stueck", "ic_achievement_trophy", "skm", 5));
        addAchievement(db, new Achievement("City Run", "Laufe 10 Kilometer am Stueck", "ic_achievement_trophy", "skm", 10));
        addAchievement(db, new Achievement("Run, Forrest, run!", "Laufe 20 Kilometer am Stueck", "ic_achievement_trophy", "skm", 20));
        addAchievement(db, new Achievement("You deserve a cookie.", "Laufe 30 Kilometer am Stueck", "ic_achievement_trophy", "skm", 30));
        addAchievement(db, new Achievement("Almost there...", "Laufe 40 Kilometer am Stueck", "ic_achievement_trophy", "skm", 40));
        addAchievement(db, new Achievement("Just ran a marathon.", "Laufe 42 Kilometer am Stueck", "ic_achievement_trophy", "skm", 42));
        
        // Einzeldauer achievements
        addAchievement(db, new Achievement("The first experiences", "Laufe 1 Minute am Stueck", "ic_achievement_trophy", "ss", 60));
        addAchievement(db, new Achievement("Training", "Laufe 10 Minuten am Stueck", "ic_achievement_trophy", "ss", 600));
        addAchievement(db, new Achievement("Walker", "Laufe 20 Minuten am Stueck", "ic_achievement_trophy", "ss", 1200));
        addAchievement(db, new Achievement("Runner", "Laufe 30 Minuten am Stueck", "ic_achievement_trophy", "ss", 1800));
        addAchievement(db, new Achievement("You are tired now...or are you?", "Laufe 1 Stunde am Stueck", "ic_achievement_trophy", "ss", 3600));
    }
}