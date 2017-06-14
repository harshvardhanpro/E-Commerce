package com.payu.payuui.Adapter;

/**
 * Created by ankur on 8/24/15.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import android.widget.EditText;

import com.payu.india.Model.CardStatus;
import com.payu.india.Model.StoredCard;
import com.payu.india.Payu.PayuConstants;
import com.payu.payuui.Fragment.SavedCardItemFragment;
import com.payu.payuui.SdkuiUtil.SdkUIConstants;

import java.util.ArrayList;
import java.util.HashMap;

public class SavedCardItemFragmentAdapter extends FragmentStatePagerAdapter {

    ArrayList<StoredCard> mStoredCards;
    SavedCardItemFragment mSavedCardItemFragment;
    HashMap<String, CardStatus> mValueAddedHashMap;
    Bundle mBundle;
    String bankStatus = "";
    FragmentManager mFragmentManager;
    HashMap<String, String> mOneClickCardTokens;
    private static HashMap <Integer, SavedCardItemFragment> mPageReferencce = new HashMap<Integer, SavedCardItemFragment>();

    public SavedCardItemFragmentAdapter(FragmentManager fm , ArrayList<StoredCard> storedCards, HashMap<String, CardStatus> valueAddedHashMap, HashMap<String, String> oneClickCardTokens) {
        super(fm);
        mFragmentManager = fm;
        mStoredCards = null;
        mStoredCards = storedCards;
        mValueAddedHashMap = valueAddedHashMap ;
        mOneClickCardTokens = oneClickCardTokens;

    }

    @Override
    public Fragment getItem(final int position) {
        mBundle = new Bundle();
        mBundle.putParcelable(PayuConstants.STORED_CARD, mStoredCards.get(position));
        if(mValueAddedHashMap != null && mValueAddedHashMap.get(mStoredCards.get(position).getCardBin()) != null && mValueAddedHashMap.get(mStoredCards.get(position).getCardBin()).getStatusCode() == 0){
            bankStatus = mStoredCards.get(position).getIssuingBank()+" is temporarily down";
        }else {
            bankStatus = "";
        }
        mBundle.putString(SdkUIConstants.ISSUING_BANK_STATUS, bankStatus);
        mBundle.putInt(SdkUIConstants.POSITION, position);
        mBundle.putSerializable(PayuConstants.ONE_CLICK_CARD_TOKENS, mOneClickCardTokens);
        mSavedCardItemFragment = new SavedCardItemFragment();
        mSavedCardItemFragment.setArguments(mBundle);

        if(mPageReferencce.get(position) != null){
            mPageReferencce.remove(position);
        }
        mPageReferencce.put(position, mSavedCardItemFragment);

        return mSavedCardItemFragment;
    }



    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        if(mPageReferencce.get(position) != null){
            mPageReferencce.remove(position);
        }
        mPageReferencce.put(position, (SavedCardItemFragment)fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        if(null != mStoredCards) return mStoredCards.size();
        return 0;
    }



    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }


    public SavedCardItemFragment getFragment(int key){
        return mPageReferencce.get(key);
    }

}