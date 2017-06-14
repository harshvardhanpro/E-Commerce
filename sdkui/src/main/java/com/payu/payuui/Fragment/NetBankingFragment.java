package com.payu.payuui.Fragment;


import android.content.Intent;
import android.os.Bundle;

import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.payu.india.Model.PaymentDetails;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.india.PostParams.PaymentPostParams;
import com.payu.payuui.Activity.PayUBaseActivity;
import com.payu.payuui.Activity.PaymentsActivity;
import com.payu.payuui.R;
import com.payu.payuui.SdkuiUtil.SdkUIConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NetBankingFragment extends Fragment implements View.OnClickListener {

    private String bankcode;
    private Bundle mBundle;
    private ArrayList<PaymentDetails> netBankingList;
    private Spinner spinnerNetbanking;
    private PaymentParams mPaymentParams;
    private PayuHashes payuHashes;
    private ArrayAdapter<String> mAdapter;
    private PayuConfig payuConfig;
    private PostData postData;
    private View view;
    private HashMap<String, Integer> valueAddedHashMap;

    private ImageButton axisImageButton;
    private ImageButton hdfcImageButton;
    private ImageButton citiImageButton;
    private ImageButton sbiImageButton;
    private ImageButton iciciImageButton;
    private TextView bankDownText;

    public NetBankingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        view = inflater.inflate(R.layout.fragment_net_banking, container, false);

        bankDownText = (TextView) view.findViewById(R.id.text_view_bank_down_error);

        (axisImageButton = (ImageButton) view.findViewById(R.id.image_button_axis)).setOnClickListener(this);
        (hdfcImageButton = (ImageButton) view.findViewById(R.id.image_button_hdfc)).setOnClickListener(this);
        (citiImageButton = (ImageButton) view.findViewById(R.id.image_button_citi)).setOnClickListener(this);
        (sbiImageButton = (ImageButton) view.findViewById(R.id.image_button_sbi)).setOnClickListener(this);
        (iciciImageButton = (ImageButton) view.findViewById(R.id.image_button_icici)).setOnClickListener(this);


        mBundle = ((PayUBaseActivity) getActivity()).bundle;
        mPaymentParams = mBundle.getParcelable(PayuConstants.PAYMENT_PARAMS);
        payuHashes = mBundle.getParcelable(PayuConstants.PAYU_HASHES);
        payuConfig = mBundle.getParcelable(PayuConstants.PAYU_CONFIG);
        payuConfig = null != payuConfig ? payuConfig : new PayuConfig();
        postData = new PostData();


        if (netBankingList != null) {

            List<String> spinnerArray = new ArrayList<String>();
            for (int i = 0; i < netBankingList.size(); i++) {
                spinnerArray.add(netBankingList.get(i).getBankName());
                switch (netBankingList.get(i).getBankCode()){
                    case "AXIB":
                        ((LinearLayout)view.findViewById(R.id.layout_axis)).setVisibility(View.VISIBLE);
                        break;
                    case "HDFB":
                        ((LinearLayout)view.findViewById(R.id.layout_hdfc)).setVisibility(View.VISIBLE);
                        break;
                    case "SBIB":
                        ((LinearLayout)view.findViewById(R.id.layout_sbi)).setVisibility(View.VISIBLE);
                        break;
                    case "ICIB":
                        ((LinearLayout)view.findViewById(R.id.layout_icici)).setVisibility(View.VISIBLE);
                        break;
                }

            }


            spinnerNetbanking = (Spinner) view.findViewById(R.id.spinner);
            mAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
            mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerNetbanking.setAdapter(mAdapter);
            spinnerNetbanking.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                    bankcode = netBankingList.get(index).getBankCode();
                    if( valueAddedHashMap != null && valueAddedHashMap.get(netBankingList.get(index).getBankCode()) != null && getActivity() != null) {

                        int statusCode = valueAddedHashMap.get(bankcode);
                        if(statusCode == 0){
                            bankDownText.setVisibility(View.VISIBLE);
                            bankDownText.setText(netBankingList.get(index).getBankName()+" is temporarily down");
                        }
                        else{
                            bankDownText.setVisibility(View.GONE);
                        }

                    }else{
                        bankDownText.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            Toast.makeText(this.getActivity(), "Could not get netbanking list Data from the previous activity", Toast.LENGTH_LONG).show();
        }

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            getActivity().setResult(resultCode, data);
            getActivity().finish();
        }
    }




    @Override
    public void onClick(View view) {

        int id = view.getId();

        mPaymentParams.setHash(payuHashes.getPaymentHash());

    /*
    * Cannot use switch on android Resource ids, so using if else
    * */
        if (id == R.id.image_button_axis) {
            mPaymentParams.setBankCode("AXIB");

        } else if (id == R.id.image_button_hdfc) {
            mPaymentParams.setBankCode("HDFB");

        } else if (id == R.id.image_button_citi) {

            ViewPager mPager = (ViewPager) getActivity().findViewById(R.id.pager);

            mPager.setCurrentItem(mPager.getCurrentItem()-1);
            return;

        } else if (id == R.id.image_button_sbi) {
            mPaymentParams.setBankCode("SBIB");

        } else if (id == R.id.image_button_icici) {
            mPaymentParams.setBankCode("ICIB");

        }


        try {
            postData = new PaymentPostParams(mPaymentParams, PayuConstants.NB).getPaymentPostParams();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuConfig.setData(postData.getResult());
            Intent intent = new Intent(getActivity(), PaymentsActivity.class);
            intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
            startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
        } else {
            Toast.makeText(this.getActivity(), postData.getResult(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        netBankingList = getArguments().getParcelableArrayList(PayuConstants.NETBANKING);
        valueAddedHashMap = (HashMap<String, Integer>) getArguments().getSerializable(SdkUIConstants.VALUE_ADDED);
    }
}