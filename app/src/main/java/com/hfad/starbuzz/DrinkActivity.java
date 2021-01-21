package com.hfad.starbuzz;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DrinkActivity extends Activity {
    public static final String EXTRA_DRINKID = "drinkId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        int drinkId = (int)getIntent().getExtras().get(EXTRA_DRINKID);

        /* Find the drink from the database. */
        StarbuzzDatabaseHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelper(this);
        try {
            SQLiteDatabase db = starbuzzDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("DRINK",
                    new String[] {"NAME", "DESCRIPTION", "IMAGE_RESOURCE_ID", "FAVORITE"},
                    "_id = ?",
                    new String[] {Integer.toString(drinkId)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                /* Get the drink details from the cursor. */
                String nameText = cursor.getString(0);
                String descriptionText = cursor.getString(1);
                int photoId = cursor.getInt(2);
                boolean isFavorite = (cursor.getInt(3) == 1);

                /* Populate the drink name. */
                TextView textView = findViewById(R.id.name);
                textView.setText(nameText);

                /* Populate the drink description. */
                textView = findViewById(R.id.description);
                textView.setText(descriptionText);

                /* Populate the drink image. */
                ImageView photo = findViewById(R.id.photo);
                photo.setImageResource(photoId);
                photo.setContentDescription(nameText);

                /* Populate the favorite checkbox. */
                CheckBox favorite = findViewById(R.id.favorite);
                favorite.setChecked(isFavorite);
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onFavoriteClicked(View view) {
        int drinkId = (int)getIntent().getExtras().get(EXTRA_DRINKID);
        CheckBox favorite = findViewById(R.id.favorite);
        ContentValues drinkValues = new ContentValues();
        drinkValues.put("FAVORITE", favorite.isChecked());
        StarbuzzDatabaseHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelper(this);
        try {
            SQLiteDatabase db = starbuzzDatabaseHelper.getWritableDatabase();
            db.update("DRINK", drinkValues,
                    "_id = ?", new String[] {Integer.toString(drinkId)});
            db.close();
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
