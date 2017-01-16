package com.example.justynaa.kampusaghmapa;

import java.util.List;

/**
 * Created by Justynaa on 2017-01-13.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> routes);
}
