package com.shareyour.antonio.sdcloud.callbacksinterface;


import com.appspot.sd_app_970.clientAPI.model.AmbitoProxy;

/**
 * Created by antonio on 24/06/15.
 */
public interface AmbitoClickListener {

    void onAmbitoSelected(AmbitoProxy ambito);

    void onAmbitoLongClicked(AmbitoProxy ambito);
}
