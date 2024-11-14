package com.northstar.northstarapitranspond.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
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

    private static final Logger logger = LoggerFactory.getLogger(ApiProxyController.class);

    private final RestTemplate restTemplate;

    public ApiProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 接收查询参数 path 作为目标路径
    @RequestMapping(value = "", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<String> getBinanceApiData(@RequestParam String path, @RequestParam Map<String, String> allParams) {
        // 构造基础URL，根据 path 选择正确的URL前缀
        String baseUrl = path.startsWith("fapi") ? "https://fapi.binance.com/" : "https://api.binance.com/";
        String url = baseUrl + path;

        allParams.remove("path");

        String finalUrl = buildUrlWithParams(url, allParams);

        // 记录请求路径和参数
        logger.info("Requesting Binance API: URL={} Params={}", finalUrl, allParams);

        try {
            // 返回接口的响应体
            String response = restTemplate.getForObject(finalUrl, String.class);
            logger.info("Response from Binance API: {}", response);
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // 捕获 HTTP 客户端或服务器错误，并记录日志
            logger.error("HTTP error occurred while requesting Binance API: Status={} Response={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode()).body("Error: " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            // 捕获其他 REST 客户端错误，并记录日志
            logger.error("Error occurred while requesting Binance API: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        } catch (Exception e) {
            // 捕获所有其他异常，并记录日志
            logger.error("Unexpected error occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
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
