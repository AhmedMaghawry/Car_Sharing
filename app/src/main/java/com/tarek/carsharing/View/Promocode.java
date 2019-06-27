package com.tarek.carsharing.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tarek.carsharing.Control.Utils;
import com.tarek.carsharing.Model.Promocodes;
import com.tarek.carsharing.Model.Trip;
import com.tarek.carsharing.Model.User;
import com.tarek.carsharing.R;


public class Promocode extends AppCompatActivity implements View.OnClickListener{

    private TextView enterPromo;
    private Button addPromo;
    private String promo;
    private User currentUserr;
    private int  clickable=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promocode);

        enterPromo = findViewById(R.id.enterpromo);
        addPromo = findViewById(R.id.addpromo);

        findViewById(R.id.addpromo).setOnClickListener(this);


        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("Users");
        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentUserr = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(User.class);

                Utils.hideLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Promocode.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
                Utils.hideLoading();
            }

        });
    }

    @Override
    public void onClick(View v) {

        CheckPromo();

        //  Toast.makeText(this, "method", Toast.LENGTH_SHORT).show();

    }


    private void CheckPromo(){

        promo = enterPromo.getText().toString();
        final Promocodes test = new Promocodes();

        DatabaseReference datapromo = FirebaseDatabase.getInstance().getReference("Promocodes");

        datapromo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean flag=false;

                for (DataSnapshot datapro : dataSnapshot.getChildren()) {

                    Promocodes arraypromo = datapro.getValue(Promocodes.class);
                    String promoGeneral = arraypromo.getName();


                    if (promo.equals(promoGeneral)) {

                        flag=true;
                        int times = arraypromo.getTimes();
                        int uses =currentUserr.getPromocode();
                        int check = 0;
                        check = times - uses;
                        if (clickable == 0){
                            if(check > 0  ) {


                                currentUserr.setPromocode(uses + 1);
                                currentUserr.setPromovalue(arraypromo.getValue());
                                currentUserr.updateUser();
                                clickable = 1;

                            }
                            else
                            {

                                Toast.makeText(Promocode.this, "This promocode is expired", Toast.LENGTH_SHORT).show();
                            }
                        }




                        Toast.makeText(Promocode.this,arraypromo.getValue()+"" +"%",Toast.LENGTH_SHORT).show();


                    }


                }
                if(!flag){

                    Toast.makeText(Promocode.this,"Invalid promocode",Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }




}