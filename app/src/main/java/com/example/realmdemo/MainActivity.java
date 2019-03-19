package com.example.realmdemo;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.realmdemo.model.Person;

import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Realm realm;
    private EditText ed_name,ed_id;
    private  Button btn_save,btn_update,btn_delete,btn_deleteall;
    private String nameString;
    private long Id;
    private  TextView tv_result;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         realm = Realm.getDefaultInstance(); // opens "myrealm.realm"
            init();
            setListner();
        }

    private void readData() {

        String output="";
        RealmResults<Person> realmResults =realm.where(Person.class)
                .findAll();

        for (Person person : realmResults)
        {
            output+=person.toString();
        }

        tv_result.setText(output);
    }

    private void saveIntoDatabase(final String nameString) {

          realm.executeTransactionAsync(new Realm.Transaction() {
              @Override
              public void execute(Realm realm) {
                //  Person person = realm.createObject(Person.class, System.currentTimeMillis() / 1000);
                //  person.setName(nameString);
                 // person.setId((int) );
                 //person.set("Kunal");

                  Number maxId = realm.where(Person.class).max("id");
                  // If there are no rows, currentId is null, so the next id must be 1
                  // If currentId is not null, increment it by 1
                  int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
                  // User object created with the new Primary key
                  Person person = realm.createObject(Person.class, nextId);
                  person.setName(nameString);

              }
          }, new Realm.Transaction.OnSuccess() {
              @Override
              public void onSuccess() {
                  Log.d(TAG, "onSuccess: ");
                  readData();

              }
          }, new Realm.Transaction.OnError() {
              @Override
              public void onError(Throwable error) {
                  Log.d(TAG, "onError: "+error.getMessage());

              }
          });
    }


    public void updateData(final long id, final String name)
    {

        hideKeyboard();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Person obj = realm.where(Person.class).equalTo("id", id).findFirst();
                if (obj == null) {
                    // obj = realm.createObject(Person.class, id);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "No Id Found", Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    obj.setName(name);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess()
            {
                Toast.makeText(MainActivity.this, "SucessFully Updated", Toast.LENGTH_SHORT).show();
                readData();

            }
        });
    }

    public void deleteSingle(long id)
    {
        final RealmResults<Person> students = realm.where(Person.class).findAll();

        Person userdatabase = students .where().equalTo("id",id).findFirst();

        if(userdatabase!=null){

            if (!realm.isInTransaction())
            {
                realm.beginTransaction();
            }

            userdatabase.deleteFromRealm();

            realm.commitTransaction();
        }

    }

    public void deleteAll()
    {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Person.class);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

                Toast.makeText(MainActivity.this, "Table Truncate", Toast.LENGTH_SHORT).show();
                readData();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {

            }
        });
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onClick(View v) {

        hideKeyboard();


        if (v.getId()==R.id.btn_save)
        {

            if (!isEmpty(ed_id)) {
                Id = Long.parseLong(ed_id.getText().toString());
                //String newSrtredf = ed_id.getText().toString();
                //Toast.makeText(this,newSrtredf , Toast.LENGTH_SHORT).show();
            }
            if (!isEmpty(ed_name)) {
                nameString = ed_name.getText().toString();
                //Toast.makeText(this, "Empty Method Worked", Toast.LENGTH_SHORT).show();
            }

            if (ed_name.getText().toString()!= null) {
                saveIntoDatabase(nameString);
            }
            else
            {
                ed_name.setError("Enter Valid Name");
                ed_name.setFocusable(true);
            }
            readData();
        }

        if (v.getId()==R.id.btn_update)
        {
            if (ed_name.getText().toString()!= null && ed_id.getText().toString()!=null) {
                // saveIntoDatabase(ed_name.getText().toString());
                // deleteSingle(1552977831);
                updateData(Id, nameString);
                //  deleteAll();
                readData();
            }
            else
            {
                ed_id.setError("Enter Valid Id");
                ed_id.setFocusable(true);
            }
        }

        if (v.getId()==R.id.btn_delete)
        {
            if (ed_id.getText().toString()!=null) {
                deleteSingle(Id);
                readData();
            }
            else
            {
                ed_id.setError("Enter Valid Id");
                ed_id.setFocusable(true);
            }
        }

        if (v.getId()==R.id.btn_deleteall)
        {

            deleteAll();

        }

    }

    private void init()
    {
        ed_name = findViewById(R.id.ed_name);
        ed_id = findViewById(R.id.ed_id);
        btn_save = findViewById(R.id.btn_save);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);
        btn_deleteall = findViewById(R.id.btn_deleteall);
        tv_result = findViewById(R.id.tv_result);
    }

    private  void setListner()
    {
        btn_save.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_deleteall.setOnClickListener(this);

    }

    private void hideKeyboard()
    {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    public boolean isEmpty(EditText editText) {
        boolean isEmptyResult = false;
        if (editText.getText().length() == 0) {
            isEmptyResult = true;
        }
        return isEmptyResult;
    }
}
