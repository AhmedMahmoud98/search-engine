package com.SE.SearchEngineAPI;
import java.util.*;

import Models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class ApiController {

  @Autowired
  SuggestionsRepository suggestionsRepository;
  @Autowired
  private TrendsService trendsService;
  @Autowired
  private SuggestionsService suggestionsService;
  @Autowired
  private RankingService rankingService;
  @Autowired
  private VisitedUrlsService visitedUrlsService;
  @Autowired
  private PageGenerationService pageGenerationService;
  @Autowired
  private ImagesService imagesService;
 
  @GetMapping("/Pages")
  public ResponseEntity<Pages> getPages (@RequestParam String query,
		  								 @RequestParam String country,
		  								 @RequestParam String pageNumber) {
	  try {
		  	CustomQuery _query = new CustomQuery(query, country, Integer.parseInt(pageNumber));
		  	/* Extract Query Trends */
		    trendsService.extractTrends(_query);
		    /* Save Query at Suggestion Table */
		    suggestionsService.saveSuggestion(query);
		    /* Start Ranking Process */
			ArrayList<String> sortedLinks = rankingService.rank(_query);

		    if (sortedLinks.isEmpty()) {
			      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			 }
		    /* Send the Required Pages based on the Page Number Required */
			int sizeOfPage = 10;
			int fromIdx = (Integer.parseInt(pageNumber) - 1) * sizeOfPage;
		    int toIdx = Math.min(fromIdx + sizeOfPage, sortedLinks.size());
		    List<Page> pagesList = new ArrayList<Page>();
		    
		    /* Extract The Pages Summary and Title */
		    pagesList = pageGenerationService.generatPages(sortedLinks.subList(fromIdx, toIdx), query);
		    
		    /* Wrap the Page List and the Pages size into the model that will be sent */
		    Pages pages = new Pages(pagesList, sortedLinks.size());

		    return new ResponseEntity<>(pages, HttpStatus.OK);
		  } catch (Exception e) {
			System.out.println(e);
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		  }
  }
  
  @GetMapping("/Images")
  public ResponseEntity<Images> getImages(@RequestParam String query,
			  							  @RequestParam String country,
			  							  @RequestParam String pageNumber) {
	  try {
		  	CustomQuery _query = new CustomQuery(query, country, Integer.parseInt(pageNumber));

		    trendsService.extractTrends(_query);
		    suggestionsService.saveSuggestion(query);

		    List<Image> imagesList = imagesService.getImages(query);
		    if (imagesList.isEmpty()) {
		      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		    }
		    
		    int sizeOfPage = 20;
		    int fromIdx = (Integer.parseInt(pageNumber) - 1) * sizeOfPage;
		    int toIdx = Math.min(fromIdx + sizeOfPage, imagesList.size());

		    Images images = new Images(imagesList.subList(fromIdx, toIdx), imagesList.size());

		    if (imagesList.isEmpty()) {
		      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		    }
		    return new ResponseEntity<>(images, HttpStatus.OK);
		  } catch (Exception e) {
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		  }
  }

  @GetMapping("/Trends")
  public ResponseEntity<List<Trend>> getTrends (@RequestParam String country) {
	  try {
		    List<Trend> trends = new ArrayList<Trend>();
		    trends = trendsService.getTrends(country);
		    
		    if (trends.isEmpty()) {
		      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		    }

		    return new ResponseEntity<>(trends, HttpStatus.OK);
		  } catch (Exception e) {
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		  }
  }
  
  @GetMapping("/Suggestions")
  public ResponseEntity<List<String>> getSuggestions (@RequestParam String query) {
	  
	  try {
		    List<Suggestion> suggestions = new ArrayList<>();
		    suggestions = suggestionsRepository.findBySearchStringStartingWithOrderByFrequencyDesc(query.toLowerCase() , PageRequest.of(0, 7) );
		    if (suggestions.isEmpty()) {
		      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		    }

		    return new ResponseEntity<>(Suggestion.searchStrings(suggestions), HttpStatus.OK);
		  } catch (Exception e) {
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		  }
  }

  @PostMapping("/VisitedUrls")
  public ResponseEntity<VisitedUrl> createVisitedUrl(@RequestParam String query,
												   	 @RequestParam String visitedUrl) {
	  try {
		  visitedUrlsService.saveVisitedUrl(visitedUrl, query);
		  return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		  } catch (Exception e) {
		    return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
		  }
  }
  
}

  