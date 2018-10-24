package com.jk51.modules.map.controller;

import com.jk51.model.map.Coordinate;
import com.jk51.model.map.GeoJson;
import com.jk51.modules.map.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by chen on 2017/2/16.
 */

@Controller
@RequestMapping("/map")
public class MapController {

    @Autowired
    MapService mapService;

    @ResponseBody
    @RequestMapping("/distance")
    public String getCoordinatee(Coordinate from,Coordinate to ,@RequestParam(required =false,defaultValue = "true") boolean geo ){
        if(geo)
            return mapService.geoDistance(from,to);
        else
            return mapService.bdDistance(from,to);
    }

    @ResponseBody
    @RequestMapping("/coordinate")
    public Coordinate getCoordinate(String address ,@RequestParam(required =false,defaultValue = "true") boolean geo){
        if(geo){
            return mapService.geoCoordinate(address);
        }
        else{
            return mapService.bdCoordinate(address);
        }
    }

    @ResponseBody
    @RequestMapping("/regeo")
    public String regeo(Coordinate coordinate1,Coordinate coordinate2){
        return mapService.reGeo(coordinate1,coordinate2).toJSONString();
    }

    /**
     * map  storesCoo[List<Coordinate>]   mmemberCoo[Coordinate]
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("/moredistance")
    public Map<String,Object> getMoreDistance(@RequestBody Map<String,Object> param){
        return mapService.moreDistance(param);
    }


    @RequestMapping("/geoSearch")
    public @ResponseBody GeoJson geoSearch(String keywords){

        GeoJson result = mapService.geoSearch(keywords);

        return result;
    }


}

