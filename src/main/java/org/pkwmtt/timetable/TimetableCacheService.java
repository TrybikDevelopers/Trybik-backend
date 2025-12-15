package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.pkwmtt.exceptions.CacheContentNotAvailableException;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.pkwmtt.timetable.parser.TimetableParserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

/**
 * Service for caching and retrieving timetable data.
 * This service interacts with a remote timetable source, parses the data,
 * and caches the results for efficient retrieval.
 */
@Service
public class TimetableCacheService {
    /**
     * Dependencies
     */
    private final TimetableParserService parser;
    private final ObjectMapper mapper;
    private final Cache cache;
    
    /**
     * Base URL for the timetable source
     */
    @Value("${main.url:https://podzial.mech.pk.edu.pl/stacjonarne/html/}")
    private String mainUrl;
    
    public TimetableCacheService (TimetableParserService parser, ObjectMapper mapper, CacheManager cacheManager) {
        this.parser = parser;
        this.mapper = mapper;
        cache = cacheManager.getCache("timetables");
    }
    
    /**
     * Retrieves the timetable for a specified general group.
     *
     * @param generalGroupName the name of the general group
     * @return TimetableDTO containing the timetable data
     * @throws WebPageContentNotAvailableException        if the timetable page can't be fetched
     * @throws SpecifiedGeneralGroupDoesntExistsException if the specified general group doesn't exist
     */
    public TimetableDTO getGeneralGroupSchedule (String generalGroupName)
      throws WebPageContentNotAvailableException, SpecifiedGeneralGroupDoesntExistsException, JsonProcessingException {
        Map<String, String> generalGroupMap = getGeneralGroupsMap();
        
        if (!generalGroupMap.containsKey(generalGroupName)) {
            throw new SpecifiedGeneralGroupDoesntExistsException(generalGroupName);
        }
        
        String url = mainUrl + generalGroupMap.get(generalGroupName);
        String cacheKey = "timetable_" + generalGroupName;
        
        String json = cache.get(
          cacheKey,
          () -> mapper.writeValueAsString(new TimetableDTO(generalGroupName, parser.parse(fetchData(url))))
        );
        
        return getMappedValue(
          json, cacheKey, cache, new TypeReference<>() {
          }
        );
    }
    
    /**
     * Retrieves a map of general group names to their corresponding timetable URLs.
     *
     * @return Map where keys are general group names and values are their timetable URLs
     * @throws WebPageContentNotAvailableException if the general groups page can't be loaded
     */
    public Map<String, String> getGeneralGroupsMap ()
      throws WebPageContentNotAvailableException, JsonProcessingException {
        String url = mainUrl + "lista.html";
        String json = cache.get(
          "generalGroupMap",
          () -> mapper.writeValueAsString(parser.parseGeneralGroups(fetchData(url)))
        );
        
        return getMappedValue(
          json, "generalGroupList", cache, new TypeReference<>() {
          }
        );
    }
    
    /**
     * Retrieves a hard-coded list of timetable hours.
     *
     * @return List of strings representing timetable hours
     * @throws WebPageContentNotAvailableException if there were trouble with fetching data
     */
    public List<String> getListOfHours () throws WebPageContentNotAvailableException {
        
        //Hard coded values for hours, caused by inconsistent timetable hours range
        return List.of(
          "7:30-8:15", "8:15-9:00", "9:15-10:00", "10:00-10:45", "11:00-11:45", "11:45-12:30", "12:45-13:30",
          "13:30-14:15", "14:30-15:15", "15:15-16:00", "16:15-17:00", "17:00-17:45", "18:00-18:45", "18:45-19:30",
          "19:45-20:30", "20:30-21:15"
        );
    }
    
    /**
     * Fetches and parses the list of timetable hours from the remote source.
     *
     * @return List of strings representing timetable hours
     * @throws WebPageContentNotAvailableException if there were trouble with fetching data
     */
    @SuppressWarnings("unused")
    private List<String> fetchListOfHours () throws JsonProcessingException {
        String url = mainUrl + "plany/o25.html";
        String json = cache.get("hourList", () -> mapper.writeValueAsString(parser.parseHours(fetchData(url))));
        
        List<String> result = getMappedValue(
          json, "hourList", cache, new TypeReference<>() {
          }
        );
        
        //Delete useless spaces
        return result.stream().map(item -> item.replaceAll(" ", "")).toList();
        
    }
    
    /**
     * Maps a JSON string to a specified type, evicting the cache entry on failure.
     *
     * @param json    the JSON string to be mapped
     * @param key     the cache key associated with the JSON string
     * @param cache   the cache instance
     * @param typeRef the TypeReference indicating the target type for mapping
     * @param <T>     the type of the mapped object
     * @return the mapped object of type T
     * @throws CacheContentNotAvailableException if mapping fails
     */
    private <T> T getMappedValue (String json, String key, Cache cache, TypeReference<T> typeRef)
      throws JsonProcessingException {
        try {
            return mapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            cache.evict(key);
            throw e;
        }
    }
    
//    /**
//     * Fetches the HTML content of the specified URL using Jsoup.
//     *
//     * <p>This method performs a blocking HTTP GET request and returns the raw
//     * HTML content as a String. Any IO-related error encountered while
//     * connecting to or reading from the remote resource is translated into a
//     * {@link WebPageContentNotAvailableException} to decouple callers from
//     * low-level IO exceptions.</p>
//     *
//     * @param url the target URL to fetch HTML from
//     * @return the HTML content of the page as a String
//     * @throws WebPageContentNotAvailableException when an I/O error occurs while fetching the page
//     */
//    private static String fetchData (String url) throws WebPageContentNotAvailableException {
//        try {
//            return Jsoup.connect(url).get().html();
//        } catch (IOException ioe) {
//            throw new WebPageContentNotAvailableException();
//        }
//    }

    /**
     * Temporary solution for issues with university certificate.
     * Resolve problem by disabling certification verification
     * Should be replaced with better solution in the future
     * @param url the target URL to fetch HTML from
     * @return the HTML content of the page as a String
     * @throws WebPageContentNotAvailableException when an I/O error occurs while fetching the page
     */
//    FIXME: Replace with better solution
    private static String fetchData(String url) throws WebPageContentNotAvailableException {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }};

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());

            return Jsoup.connect(url)
                    .sslSocketFactory(sc.getSocketFactory())
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get()
                    .html();

        } catch (Exception e) {
            e.printStackTrace();
            throw new WebPageContentNotAvailableException();
        }
    }
    
}
