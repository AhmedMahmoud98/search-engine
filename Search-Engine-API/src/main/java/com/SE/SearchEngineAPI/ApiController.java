package com.SE.SearchEngineAPI;
import java.util.*;

import Models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
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
  
  @GetMapping("/images")
  public ResponseEntity<List<Image>> getImages(@RequestBody Query query ) {
	  try {
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

  @GetMapping("/pages")
  public ResponseEntity<List<Page>> getPages (@RequestBody Query query ) {
	  try {
		    List<Page> Pages = new ArrayList<Page>();
		    /** 
		     * Run Query Processor and Ranker Here
		     *  then return array of Pages 
		     */
		    
		    
		    suggestionsRepository.incFreq(query.getQueryString());
		    if (Pages.isEmpty()) {
		      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		    }
		    
		    return new ResponseEntity<>(Pages, HttpStatus.OK);
		  } catch (Exception e) {
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		  }
  }
  
  @GetMapping("/Trends")
  public ResponseEntity<List<Trend>> getTrends () {
	  try {
		    List<Trend> Trends = new ArrayList<Trend>();
		    /**
		     * Get 1st 10 Trends From DB directly
		     * Collection => Trends
		     */
		    
		    if (Trends.isEmpty()) {
		      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		    }

		    return new ResponseEntity<>(Trends, HttpStatus.OK);
		  } catch (Exception e) {
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		  }
  }
  
  @GetMapping("/suggestions")
  public ResponseEntity<List<String>> getSuggestions (@RequestParam String query) {
	  
	  try {
		    List<Suggestion> suggestions = new ArrayList<>();
		    
		    System.out.println(query);
		    suggestions = suggestionsRepository.findBySearchStringStartingWithOrderByFrequencyDesc(query.toLowerCase() , PageRequest.of(0, 7) );
		    System.out.println(suggestions);
		    
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
		  /**
		   * The ID sent from client is usless, we should save the real document ID
		   * 34an Ali Khaled E3rf Egeb Byanat 2l document dh mn 2l table 2l tany elly feh 2lDoc IDS 
		   */
		  VisitedUrl _visitedURL = visitedUrlsRepository.save(new VisitedUrl(visitedUrl.getId(), visitedUrl.getVisitedUrl()));
		  return new ResponseEntity<>(_visitedURL, HttpStatus.CREATED);
		  } catch (Exception e) {
		    return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
		  }
  }
  
}

  