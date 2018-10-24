package com.jk51.modules.common.gaode;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.modules.common.pojo.*;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toList;

/**
 * @author
 * 高德地图服务
 */
@Service
public class GaoDeService {
    public static final Logger logger = LoggerFactory.getLogger(GaoDeService.class);
    private static final int BATCH_MAX_SIZE = 20;
    private static final Executor executor;
    static {
        // 10个常驻 最多50个 非core线上200S空闲释放
        executor = new ThreadPoolExecutor(10, 50, 200, TimeUnit.SECONDS, new LinkedBlockingDeque(), r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
    }

    @Value("${mapConfig.GEO_KEY}")
    private String key;

    private final static String API_URL = "http://restapi.amap.com";

    public <T> T request(AbstractWebApi<T> webApi) {
        List<GaoDeWebParameter> queryNamesAndValues = webApi.getQueryNamesAndValues();
        HttpUrl.Builder builder = HttpUrl.parse(API_URL + webApi.getPath()).newBuilder();

        for (GaoDeWebParameter parameter : queryNamesAndValues) {
            builder.addQueryParameter(parameter.getKey(), parameter.getValue());
        }

        builder.addQueryParameter("key", key);

        HttpUrl url = builder.build();
        String res = OkHttpUtil.get(url.toString());

        return webApi.getResult(res);
    }

    /**
     * 批量接口调用
     * http://lbs.amap.com/api/webservice/guide/api/batchrequest
     * @param webApis
     * @return
     */
    public List<BatchResultDTO> batchRequest(List<? extends AbstractWebApi> webApis) {
        final int size = webApis.size();
        long st = System.currentTimeMillis();
        int start = 0;
        int end = size < BATCH_MAX_SIZE ? size : 20;
        List<CompletableFuture<List<BatchResultDTO>>> completableFutures = new LinkedList<>();
        do {
            CompletableFuture<List<BatchResultDTO>> temp = batchRequestAsync(webApis.subList(start, end), executor);
            completableFutures.add(temp);
            start = end;
            end += (end + BATCH_MAX_SIZE < size) ? BATCH_MAX_SIZE : size - end;
        } while (start < size);

        List<BatchResultDTO> collect = completableFutures
            .stream()
            .map(CompletableFuture::join)
            .flatMap(List::stream)
            .collect(toList());
        logger.info("高德批量接口 总共{}个 耗时 {}", size, System.currentTimeMillis() - st);

        return collect;
    }

    public CompletableFuture<List<BatchResultDTO>> batchRequestAsync(List<? extends AbstractWebApi> webApis, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            List<Map<String, String>> ops = new LinkedList<>();

            for (AbstractWebApi webApi : webApis) {
                Map<String, String> map = new HashMap<>(1);
                map.put("url", webApi.toUrlPath(key));
                ops.add(map);
            }

            HttpUrl httpUrl = HttpUrl.parse(API_URL).newBuilder()
                .addPathSegment("v3")
                .addPathSegment("batch")
                .addQueryParameter("key", key)
                .build();

            Map<String, List<Map<String, String>>> param = new HashMap<>(1);
            param.put("ops", ops);
            String res = OkHttpUtil.postJson(httpUrl.toString(), JacksonUtils.mapToJson(param));

            try {
                return JacksonUtils.json2list(res, BatchResultDTO.class);
            } catch (Exception e) {
                logger.error("序列化异常", e);
                return null;
            }
        }, executor);
    }

    public <T> T request(HttpUrl httpUrl, Class<T> clazz) {
        try {
            return JacksonUtils.json2pojo(OkHttpUtil.get(httpUrl.toString()), clazz);
        } catch (Exception e) {
            throw new GaoDeInvokingException(e);
        }
    }
    /**
     * 路线规划 @see http://lbs.amap.com/api/webservice/guide/api/direction/
     */
    public void driving(Point origin, Point destination) {
        //
        throw new RuntimeException("方法未实现");
    }

    /**
     * 步行路径规划
     */
    public WalkResultDTO walking(Point origin, Point destination) {
        return request(new DirectionWalkingWebApi(origin, destination));
    }

    public int calcWalkDistance(Point origin, Point destination) {
        return calcWalkDistance(walking(origin, destination));
    }


    public int calcWalkDistance(WalkResultDTO walkResult) {
        return calcWalkDistance(Optional.ofNullable(walkResult));
    }

    /**
     * 计算步行距离
     */
    public int calcWalkDistance(Optional<WalkResultDTO> walkResult) {

        int distance = walkResult
            .map(WalkResultDTO::getRoute)
            .map(Route::getPaths)
            .map(m -> m.get(0))
            .map(PathsItem::getDistance)
            .orElse(0);

        return distance;
    }

}
