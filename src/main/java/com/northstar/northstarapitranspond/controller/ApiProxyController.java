package com.northstar.northstarapitranspond.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


/**
 * @author 李嘉豪
 * @date 2024/11/11 上午12:14
 * @version 1.0
 */
@RestController
@RequestMapping("/proxy") // 定义根路径 /proxy
public class ApiProxyController {

    private final RestTemplate restTemplate;

    public ApiProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 接收查询参数 path 作为目标路径
    @RequestMapping(value = "", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public String getBinanceApiData(@RequestParam String path, @RequestParam Map<String, String> allParams) {
        // 构造基础URL，根据 path 选择正确的URL前缀
        String baseUrl = path.startsWith("fapi") ? "https://fapi.binance.com/" : "https://api.binance.com/";
        String url = baseUrl + path;
        allParams.remove("path");
        // 返回接口的响应体
        return restTemplate.getForObject(buildUrlWithParams(url, allParams), String.class);
    }

    // 辅助方法：构建带查询参数的 URL
    private String buildUrlWithParams(String url, Map<String, String> params) {
        if (params.isEmpty()) {
            return url;
        }

        StringBuilder fullUrl = new StringBuilder(url);
        fullUrl.append("?");

        // 遍历参数并附加到 URL
        params.forEach((key, value) -> fullUrl.append(key).append("=").append(value).append("&"));

        // 删除最后的多余的 '&'
        fullUrl.deleteCharAt(fullUrl.length() - 1);

        return fullUrl.toString();
    }
}
