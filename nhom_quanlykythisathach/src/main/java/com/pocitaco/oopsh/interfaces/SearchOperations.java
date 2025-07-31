package com.pocitaco.oopsh.interfaces;

import java.util.List;

/**
 * Generic search operations interface
 * Provides search and filtering capabilities
 * 
 * @param <T> The entity type
 */
public interface SearchOperations<T> {
    
    /**
     * Search entities by keyword
     * 
     * @param keyword The search keyword
     * @return List of matching entities
     */
    List<T> search(String keyword);
    
    /**
     * Search entities by keyword with pagination
     * 
     * @param keyword The search keyword
     * @param page The page number (0-based)
     * @param size The page size
     * @return List of matching entities for the specified page
     */
    List<T> search(String keyword, int page, int size);
    
    /**
     * Find entities by field value
     * 
     * @param field The field name
     * @param value The field value
     * @return List of matching entities
     */
    List<T> findByField(String field, Object value);
    
    /**
     * Find entities by multiple field values
     * 
     * @param criteria The search criteria map
     * @return List of matching entities
     */
    List<T> findByCriteria(java.util.Map<String, Object> criteria);
    
    /**
     * Get total count of search results
     * 
     * @param keyword The search keyword
     * @return Total count of matching entities
     */
    long countSearchResults(String keyword);
    
    /**
     * Get suggestions for search autocomplete
     * 
     * @param partial The partial search term
     * @param limit Maximum number of suggestions
     * @return List of suggestions
     */
    List<String> getSearchSuggestions(String partial, int limit);
    
    /**
     * Advanced search with multiple parameters
     * 
     * @param searchParams The search parameters
     * @return List of matching entities
     */
    List<T> advancedSearch(SearchParams searchParams);
    
    /**
     * Search parameters class
     */
    class SearchParams {
        private String keyword;
        private java.util.Map<String, Object> filters;
        private String sortBy;
        private String sortOrder;
        private int page;
        private int size;
        
        public SearchParams() {
            this.filters = new java.util.HashMap<>();
            this.page = 0;
            this.size = 10;
            this.sortOrder = "ASC";
        }
        
        // Getters and setters
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        
        public java.util.Map<String, Object> getFilters() { return filters; }
        public void setFilters(java.util.Map<String, Object> filters) { this.filters = filters; }
        
        public String getSortBy() { return sortBy; }
        public void setSortBy(String sortBy) { this.sortBy = sortBy; }
        
        public String getSortOrder() { return sortOrder; }
        public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
        
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        
        public void addFilter(String field, Object value) {
            this.filters.put(field, value);
        }
    }
} 