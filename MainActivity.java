package org.techtown.sdsd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    TextView address_view;
    String gu = "";
    String dong = "";
    public static final int REQUEST_CODE_MENU = 101;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        address_view = findViewById(R.id.textLocation);

        ImageButton button = findViewById(R.id.imageButton);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if(user == null){
            myStartActivity(SignUpActivity.class);
        }else{
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document != null){
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                                myStartActivity(MemberActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.logoutButton:
                    FirebaseAuth.getInstance().signOut();
                    myStartActivity(SignUpActivity.class);
                    break;
            }
        }
    };




    public void onClickLoc(View V){
        Intent intent = new Intent(this, LocActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MENU);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_MENU){
            Toast.makeText(getApplicationContext(), "위치를 설정했습니다.", Toast.LENGTH_LONG).show();
        }
        if(resultCode == RESULT_OK){
            int count = 0;
            String address = data.getStringExtra("addText");
            for (int i = 0; i < address.length(); i++){
                char ch = address.charAt(i);
                if(Character.isWhitespace(ch) && count == 0){
                    address = address.substring(i+1);
                    count++;
                }
                else if(Character.isWhitespace(ch) && count == 1){ count++; }
                else if(count == 2){
                    if(!Character.isWhitespace(ch)){
                        gu += ch;
                    }
                    else{ count++; }
                }
                else if(count == 3){
                    if(!Character.isWhitespace(ch)){
                        dong += ch;
                    }
                    else{break;}
                }
            }
            address_view.setText(address);
        }
    }


    public void onClickSearch(View view){
        Intent intent = new Intent(this, org.techtown.sdsd.SearchActivity.class);
        startActivity(intent);
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }

}