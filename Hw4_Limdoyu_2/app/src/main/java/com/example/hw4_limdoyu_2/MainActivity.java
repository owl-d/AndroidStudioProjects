package com.example.hw4_limdoyu_2;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private CheckBox cb_membership;
    private EditText et_pizza, et_spagetti, et_salad;
    private Button btn_order_finish;
    private TextView tv_num_menu, tv_price, tv_option;
    private RadioGroup rg_option;
    private RadioButton rb_pickle, rb_source;
    private ImageView img_option;

    private String option = "피클";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_pizza = (EditText)findViewById(R.id.et_pizza);
        et_spagetti = (EditText)findViewById(R.id.et_spagetti);
        et_salad = (EditText)findViewById(R.id.et_salad);

        //소스 옵션 선택하면
        rg_option = (RadioGroup)findViewById(R.id.radioGroup);
        rb_pickle = (RadioButton)findViewById(R.id.rb_pickle);
        rb_source = (RadioButton)findViewById(R.id.rb_source);
        img_option = (ImageView)findViewById(R.id.image);

        rg_option.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {

                String pickle;
                String source;

                switch (checkId) {
                    case R.id.rb_pickle:
                        Log.d("TAG", "Select Pickle");
                        img_option.setImageResource(R.drawable.pickle);
                        option = "피클";
                        break;
                    case R.id.rb_source:
                        Log.d("TAG", "Select Source");
                        img_option.setImageResource(R.drawable.source);
                        option = "소스";
                        break;
                }
            }
        });

        //주문 완료 버튼이 눌리면
        btn_order_finish = (Button)findViewById(R.id.btn_order_finish);
        btn_order_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Finish Ordering");

                int price = 0;
                int dish = 0;

                tv_num_menu = (TextView)findViewById(R.id.tv_order_num);
                tv_price = (TextView)findViewById(R.id.tv_price);
                tv_option = (TextView)findViewById(R.id.tv_radio_check);

                //주문 개수 출력
                Log.d("TAG", "dish : " + dish);
                int dish_pizza = Integer.parseInt(et_pizza.getText().toString());
                int dish_spagetti = Integer.parseInt(et_spagetti.getText().toString());
                int dish_salad = Integer.parseInt(et_salad.getText().toString());
                dish = dish_pizza + dish_spagetti + dish_salad;
                tv_num_menu.setText("주문 개수 : "+ dish + "개");

                //주문 금액 출력
                Log.d("TAG", "price : " + price);
                price = dish_pizza*16000 + dish_spagetti*11000 + dish_salad*4000;
                //멤버십 있다고 체크하면 7% 할인
                cb_membership = (CheckBox)findViewById(R.id.cb_membership);
                if (cb_membership.isChecked()) price = price*93/100;
                tv_price.setText("주문 금액 : "+ price + "원");

                //옵션 출력
                Log.d("TAG", "option : " + option);
                tv_option.setText(option +"을 선택하셨습니다");

            }
        });
    }
}