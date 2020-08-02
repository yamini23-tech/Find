package com.roommate.find.activities;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.roommate.find.R;
import com.roommate.find.models.AddPostDo;
import com.roommate.find.models.FilterDo;

public class PostFilterActivity extends BaseActivity {

    private View llPostFilter;
    private AddPostDo addPostDo;
    private Spinner spUnitType, spBedRooms, spRating;
    private EditText etPriceRange;
    private RadioButton rbHydro, rbHeating, rbParking, rbPet, rbFurniture, rbWifi;
    private TextView tvClose, tvReset, tvDone;
    private FilterDo filterDo;

    @Override
    public void initialise() {
        llPostFilter= inflater.inflate(R.layout.posts_filter_layout, null);
        addBodyView(llPostFilter);
        lockMenu();
        ivBack.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        tvTitle.setText("POST FILTER");
        llFooter.setVisibility(View.GONE);
        initialiseControls();

        spUnitType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position != 0){
                    filterDo.unitType = ((TextView)view).getText().toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spBedRooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position != 0){
                    filterDo.bedRooms = ((TextView)view).getText().toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spBedRooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position != 0){
                    filterDo.rating = Integer.parseInt(((TextView)view).getText().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDo = new FilterDo();
                if(!etPriceRange.getText().toString().trim().equalsIgnoreCase("")){
                    filterDo.priceRange = Integer.parseInt(etPriceRange.getText().toString().trim());
                }
                filterDo.hydro     = ""+rbHydro.isChecked();
                filterDo.heating    = ""+rbHeating.isChecked();
                filterDo.parking    = ""+rbParking.isChecked();
                filterDo.pet        = ""+rbPet.isChecked();
                filterDo.furniture  = ""+rbFurniture.isChecked();
                filterDo.wifi       = ""+rbWifi.isChecked();
                Intent intent = new Intent();
                intent.putExtra("filterDo", filterDo);
                setResult(321, intent);
                finish();
            }
        });
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initialiseControls(){
        tvClose                 = llPostFilter.findViewById(R.id.tvClose);
        tvReset                 = llPostFilter.findViewById(R.id.tvReset);
        tvDone                  = llPostFilter.findViewById(R.id.tvDone);

        rbHydro                 = llPostFilter.findViewById(R.id.rbHydro);
        rbHeating               = llPostFilter.findViewById(R.id.rbHeating);
        rbParking               = llPostFilter.findViewById(R.id.rbParking);
        rbPet                   = llPostFilter.findViewById(R.id.rbPet);
        rbFurniture             = llPostFilter.findViewById(R.id.rbFurniture);
        rbWifi                  = llPostFilter.findViewById(R.id.rbWifi);
        
        etPriceRange            = llPostFilter.findViewById(R.id.etPriceRange);
        spUnitType              = llPostFilter.findViewById(R.id.spUnitType);
        spBedRooms              = llPostFilter.findViewById(R.id.spBedRooms);
        spRating                = llPostFilter.findViewById(R.id.spRating);

    }

    @Override
    public void getData() {

    }
}
