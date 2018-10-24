package com.jk51.modules.common.gaode;

import com.jk51.modules.common.pojo.Point;
import com.jk51.modules.common.pojo.WalkResultDTO;

import java.util.LinkedList;
import java.util.List;

/**
 * @author
 * 步行距离计算
 */
public class DirectionWalkingWebApi extends AbstractWebApi<WalkResultDTO> {
    private static final String PATH = "/v3/direction/walking";
    private final Point origin;
    private final Point destination;

    // region implements
    @Override
    public String getPath() {
        return PATH;
    }

    @Override
    public List<GaoDeWebParameter> getQueryNamesAndValues() {
        List<GaoDeWebParameter> parameters = new LinkedList<>();
        parameters.add(new GaoDeWebParameter("origin", origin.toString()));
        parameters.add(new GaoDeWebParameter("destination", destination.toString()));

        return parameters;
    }
    // endregion

    public DirectionWalkingWebApi(Point origin, Point destination) {
        this.origin = origin;
        this.destination = destination;
    }
}
