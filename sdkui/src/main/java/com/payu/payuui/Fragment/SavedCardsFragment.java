package com.payu.payuui.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.payu.india.Interfaces.DeleteCardApiListener;
import com.payu.india.Model.CardStatus;
import com.payu.india.Model.MerchantWebService;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PayuResponse;
import com.payu.india.Model.PostData;
import com.payu.india.Model.StoredCard;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.india.Payu.PayuUtils;
import com.payu.india.PostParams.MerchantWebServicePostParams;
import com.payu.india.Tasks.DeleteCardTask;
import com.payu.payuui.Activity.PayUBaseActivity;
import com.payu.payuui.Adapter.PagerAdapter;
import com.payu.payuui.Adapter.SavedCardItemFragmentAdapter;
import com.payu.payuui.SdkuiUtil.SdkUIConstants;
import com.payu.payuui.Widget.CirclePageIndicator;
import com.payu.payuui.R;

import com.payu.payuui.Widget.ZoomOutPageTransformer;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavedCardsFragment extends Fragment implements View.OnClickListener, DeleteCardApiListener {

    private SavedCardItemFragmentAdapter mAdapter;
    private ViewPager mPager;
    private ArrayList<StoredCard> mStoreCards;
    private ImageButton deleteButton;
    private CirclePageIndicator indicator;
    private View mView;
    private TextView titleText;

    private PayuHashes payuHashes;
    private PaymentParams mPaymentParams;
    private PayuConfig payuConfig;
    private Bundle mBundle;
    private HashMap<String, CardStatus> valueAddedHashMap;
    private PayuUtils payuUtils;
    private HashMap<String, String> oneClickCardTokens;


    public SavedCardsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle fragmentBundle = getArguments();
        mStoreCards = fragmentBundle.getParcelableArrayList(PayuConstants.STORED_CARD);
        valueAddedHashMap = (HashMap<String, CardStatus>) fragmentBundle.getSerializable(SdkUIConstants.VALUE_ADDED);
        oneClickCardTokens = (HashMap<String, String>) fragmentBundle.getSerializable(PayuConstants.ONE_CLICK_CARD_TOKENS);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_saved_cards, container, false);
        mBundle = ((PayUBaseActivity) getActivity()).bundle;
        payuHashes = mBundle.getParcelable(PayuConstants.PAYU_HASHES);
        mPaymentParams = mBundle.getParcelable(PayuConstants.PAYMENT_PARAMS);
        payuConfig = mBundle.getParcelable(PayuConstants.PAYU_CONFIG);

        payuUtils = new PayuUtils();



        mAdapter = new SavedCardItemFragmentAdapter(getChildFragmentManager(), mStoreCards, valueAddedHashMap, oneClickCardTokens);
        mPager = (ViewPager) mView.findViewById(R.id.pager_saved_card);
        mPager.setAdapter(mAdapter);
        mPager.setClipToPadding(false);
        (deleteButton = (ImageButton) mView.findViewById(R.id.button_delete)).setOnClickListener(this);
        titleText = (TextView) mView.findViewById(R.id.edit_text_title);






        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        indicator = (CirclePageIndicator)mView.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);



        final float density = getResources().getDisplayMetrics().density;
        indicator.setBackgroundColor(0xFFFFFFFF);
        indicator.setRadius(3 * density);
        indicator.setPageColor(0xFFC6C6C6);
        indicator.setFillColor(0xFF363535);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                ViewPager activityViewPager = (ViewPager) getActivity().findViewById(R.id.pager);
                PagerAdapter activityAdapter = (PagerAdapter) activityViewPager.getAdapter();
                if(activityAdapter != null && activityAdapter.getPageTitle(activityViewPager.getCurrentItem()).toString().equals(SdkUIConstants.SAVED_CARDS)) {

              if (mStoreCards.get(position).getEnableOneClickPayment() == 1 && mStoreCards.get(position).getOneTapCard() == 1) {

                        getActivity().findViewById(R.id.button_pay_now).setEnabled(true);
                    } else if (mStoreCards.get(position).getCardType().equals("SMAE")) {
                        getActivity().findViewById(R.id.button_pay_now).setEnabled(true);
                    } else {
                        SavedCardItemFragmentAdapter mSaveAdapter = (SavedCardItemFragmentAdapter) mPager.getAdapter();
                        SavedCardItemFragment mSaveFragment = mSaveAdapter.getFragment(mPager.getCurrentItem());
                        if (mSaveFragment != null && mSaveFragment.cvvValidation()) {
                            getActivity().findViewById(R.id.button_pay_now).setEnabled(true);
                        } else {
                            getActivity().findViewById(R.id.button_pay_now).setEnabled(false);
                        }
                    }
                }




            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if(mStoreCards.size() == 0){
            deleteButton.setVisibility(View.GONE);
            mPager.setVisibility(View.GONE);
            indicator.setVisibility(View.GONE);
            titleText.setText("You have no Stored Cards");

        }


        ViewPager activityViewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        PagerAdapter activityAdapter = (PagerAdapter) activityViewPager.getAdapter();

        if(activityAdapter != null && activityAdapter.getPageTitle(activityViewPager.getCurrentItem()).toString().equals(SdkUIConstants.SAVED_CARDS) && mPager.getCurrentItem() == 0) {
            if (mStoreCards.size() != 0 && mStoreCards.get(0).getCardType().equals("SMAE")) {
                getActivity().findViewById(R.id.button_pay_now).setEnabled(true);
            }

            if (mStoreCards != null && mStoreCards.size() != 0 && mStoreCards.get(0).getEnableOneClickPayment() == 1  && mStoreCards.get(0).getOneTapCard() == 1 ) {
                getActivity().findViewById(R.id.button_pay_now).setEnabled(true);
            }
        }

        return mView;
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.button_delete){
            final int position = mPager.getCurrentItem();

            new AlertDialog.Builder(getActivity())
                    .setTitle("Delete")
                    .setMessage("Are you sure you want to delete this card?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            deleteCard(mStoreCards.get(position));
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }


    private void deleteCard(StoredCard storedCard) {
        deleteButton.setEnabled(false);
        MerchantWebService merchantWebService = new MerchantWebService();
        merchantWebService.setKey(mPaymentParams.getKey());
        merchantWebService.setCommand(PayuConstants.DELETE_USER_CARD);
        merchantWebService.setVar1(mPaymentParams.getUserCredentials());
        merchantWebService.setVar2(storedCard.getCardToken());
        merchantWebService.setHash(payuHashes.getDeleteCardHash());

        PostData postData = null;
        postData = new MerchantWebServicePostParams(merchantWebService).getMerchantWebServicePostParams();

        if (postData.getCode() == PayuErrors.NO_ERROR) {
            // ok we got the post params, let make an api call to payu to fetch
            // the payment related details
            payuConfig.setData(postData.getResult());
            payuConfig.setEnvironment(payuConfig.getEnvironment());

            DeleteCardTask deleteCardTask = new DeleteCardTask(this);
            deleteCardTask.execute(payuConfig);
        } else {
            Toast.makeText(getActivity(), postData.getResult(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDeleteCardApiResponse(PayuResponse payuResponse) {

        if (payuResponse.isResponseAvailable()) {
            Toast.makeText(getActivity(), payuResponse.getResponseStatus().getResult(), Toast.LENGTH_LONG).show();
        }
        if (payuResponse.getResponseStatus().getCode() == PayuErrors.NO_ERROR) {
            // there is no error, lets fetch te cards list.

            ((EditText)mView.findViewById(R.id.edit_text_cvv)).getText().clear();
            ((CheckBox)mView.findViewById(R.id.check_box_save_card_enable_one_click_payment)).setChecked(false );
            mStoreCards.remove(mPager.getCurrentItem());
            mPager.getAdapter().notifyDataSetChanged();
            if(mStoreCards.size() == 0){
                deleteButton.setVisibility(View.GONE);
                mPager.setVisibility(View.GONE);
                indicator.setVisibility(View.GONE);
                titleText.setText("You have no Stored Cards");
                getActivity().findViewById(R.id.button_pay_now).setEnabled(false);

            }

        } else {
                Toast.makeText(getActivity(), "Error While Deleting Card", Toast.LENGTH_LONG).show();

        }
        deleteButton.setEnabled(true);

    }

}
