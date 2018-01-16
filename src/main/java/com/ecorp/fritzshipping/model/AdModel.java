
package com.ecorp.fritzshipping.model;

import com.ecorp.fritzshipping.service.external.AdHelperIF;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@RequestScoped
@Named
public class AdModel implements Serializable {
    @Inject
    private AdHelperIF adHelper;
    
    public String getHorizontalBannerUrl() {
        return adHelper.getHorizontalBanner();
    }
}
