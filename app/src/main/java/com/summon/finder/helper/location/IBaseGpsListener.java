package com.summon.finder.helper.location;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public interface IBaseGpsListener extends LocationListener, GpsStatus.Listener {
    public void onLocationChanged(Location location);

    public void onProviderDisabled(String provider);

    public  void onStatusChanged(String provider, int staatus, Bundle extras);

    public  void onGpsStatusChanged(int event);
}