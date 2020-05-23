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
  VisitedUrlsRepository visitedUrlsRepository;
  @Autowired
  SuggestionsRepository suggestionsRepository;
  @Autowired
  private TrendsService trendsService;
  @Autowired
  private SuggestionsService suggestionsService;
 
 
  @GetMapping("/Pages")
  public ResponseEntity<List<Page>> getPages (@RequestParam String query,
		  									  @RequestParam String country,
		  									  @RequestParam String pageNumber) {
	  try {
		  	CustomQuery _query = new CustomQuery(query, country, Integer.valueOf(pageNumber));
		    trendsService.extractTrends(_query);
		    suggestionsService.saveSuggestion(query);
		    List<Page> Pages = new ArrayList<Page>();
		    /** 
		     * Run Query Processor and Ranker Here
		     *  then return array of Pages 
		     */

		    if (Pages.isEmpty()) {
		      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		    }

		    return new ResponseEntity<>(Pages, HttpStatus.OK);
		  } catch (Exception e) {
			System.out.println(e);
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		  }
  }
  
  @GetMapping("/Images")
  public ResponseEntity<List<Image>> getImages(@RequestParam String query,
			  									@RequestParam String country,
			  									@RequestParam String pageNumber) {
	  try {
		    CustomQuery _query = new CustomQuery(query, country, Integer.valueOf(pageNumber));
		    List<Image> Images = new ArrayList<Image>();
		    /** 
		     * Run Query Processor and Ranker Here
		     *  then return array of Images 
		     */
		    
		    if (Images.isEmpty()) {
		      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		    }
		    return new ResponseEntity<>(Images, HttpStatus.OK);
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
  public ResponseEntity<VisitedUrl> createTutorial(@RequestBody VisitedUrl visitedUrl) {
	  try {
		  VisitedUrl _visitedURL = visitedUrlsRepository.save(new VisitedUrl(visitedUrl.getId(), visitedUrl.getVisitedUrl()));
		  return new ResponseEntity<>(_visitedURL, HttpStatus.CREATED);
		  } catch (Exception e) {
		    return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
		  }
  }
  
}

  